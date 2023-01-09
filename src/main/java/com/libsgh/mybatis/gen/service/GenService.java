package com.libsgh.mybatis.gen.service;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.db.Db;
import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.dialect.mysql.parser.MySqlStatementParser;
import com.alibaba.druid.sql.dialect.mysql.visitor.MySqlSchemaStatVisitor;
import com.alibaba.druid.stat.TableStat;
import com.google.common.base.CaseFormat;
import com.libsgh.mybatis.gen.model.FileInfo;
import com.libsgh.mybatis.gen.model.GenConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.config.*;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName GenService
 * @Description 代码生成服务
 * @Author Libs
 * @Date 2022/12/9 17:31
 * @Version 1.0
 */
@Service
@Slf4j
public class GenService {

    public static Map<String, Long> timedCache = new HashMap<>();

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassname;

    @Value("${gen_path}")
    private String genPath;

    @Resource
    private DruidDataSource ds;

    public Map<String, Object> execGen(GenConfig config) {
        Map<String, Object> result = new HashMap<>();
        Boolean createResult = this.createTable(config.getSql());
        long id = IdUtil.getSnowflakeNextId();
        String path = genPath + File.separator + id;
        FileUtil.mkdir(path);
        if(!createResult){
            result.put("code", -1);
            result.put("msg", "请输入正确的SQL语句");
            return result;
        }
        List<String> warnings = new ArrayList<String>();
        boolean overwrite = true;
        Configuration mybatisConfig = new Configuration();
        //所有内容（主键，blob）等全部生成在一个对象中.
        Context context = new Context(ModelType.FLAT);
        context.setTargetRuntime(config.getGen_type());
        context.setId("mysql");
        context.addProperty("autoDelimitKeywords", "false");
        //生成的Java文件的编码
        context.addProperty("javaFileEncoding", "UTF-8");
        //格式化java代码
        context.addProperty("javaFormatter", "org.mybatis.generator.api.dom.DefaultJavaFormatter");
        //格式化XML代码
        context.addProperty("xmlFormatter", "org.mybatis.generator.api.dom.DefaultXmlFormatter");
        //用于标记数据库对象名的符号， mysql是`，ORACLE就是双引号
        context.addProperty("beginningDelimiter", "`");
        context.addProperty("endingDelimiter", "`");
        //设置数据库连接
        JDBCConnectionConfiguration jdbcConnectionConfiguration = new JDBCConnectionConfiguration();
        jdbcConnectionConfiguration.setDriverClass("com.mysql.cj.jdbc.Driver");
        jdbcConnectionConfiguration.setConnectionURL(dbUrl);
        jdbcConnectionConfiguration.setUserId(userName);
        jdbcConnectionConfiguration.setPassword(password);
        jdbcConnectionConfiguration.addProperty("useInformationSchema", "true");
        context.setJdbcConnectionConfiguration(jdbcConnectionConfiguration);
        //java模型创建配置
        JavaModelGeneratorConfiguration javaModelGeneratorConfiguration = new JavaModelGeneratorConfiguration();
        javaModelGeneratorConfiguration.setTargetPackage(config.getDefault_model_package_name());
        //todo 路径
        javaModelGeneratorConfiguration.setTargetProject(path);
        javaModelGeneratorConfiguration.addProperty("constructorBased", "false");
        //在targetPackage的基础上，根据数据库的schema再生成一层package，最终生成的类放在这个package下
        javaModelGeneratorConfiguration.addProperty("enableSubPackages", "true");
        //是否创建一个不可变的类（无setter方法）
        javaModelGeneratorConfiguration.addProperty("immutable", "false");
        //设置父类
        //javaModelGeneratorConfiguration.addProperty("rootClass", "com.libsgh.mybatis.base.BaseModel");
        //设置是否在getter方法中，对String类型字段调用trim()方法
        javaModelGeneratorConfiguration.addProperty("trimStrings", "false");
        context.setJavaModelGeneratorConfiguration(javaModelGeneratorConfiguration);

        SqlMapGeneratorConfiguration sqlMapGeneratorConfiguration = new SqlMapGeneratorConfiguration();
        sqlMapGeneratorConfiguration.setTargetPackage(config.getDefault_mapper_package_name());
        sqlMapGeneratorConfiguration.setTargetProject(path);
        sqlMapGeneratorConfiguration.addProperty("enableSubPackages", "true");
        context.setSqlMapGeneratorConfiguration(sqlMapGeneratorConfiguration);

        //Mapper 接口配置
        JavaClientGeneratorConfiguration javaClientGeneratorConfiguration = new JavaClientGeneratorConfiguration();
        //类型：ANNOTATEDMAPPER 接口注解，MIXEDMAPPER 混合配置，会生成Mapper接口，并适当添加合适的Annotation，XMLMAPPER 会生成Mapper接口，接口完全依赖XML
        if(config.hasPlugin("ann")){
            javaClientGeneratorConfiguration.setConfigurationType("ANNOTATEDMAPPER");
        }else{
            javaClientGeneratorConfiguration.setConfigurationType("XMLMAPPER");
        }
        javaClientGeneratorConfiguration.setTargetPackage(config.getDefault_mapper_package_name());
        javaClientGeneratorConfiguration.setTargetProject(path);
        //在targetPackage的基础上，根据数据库的schema再生成一层package
        javaClientGeneratorConfiguration.addProperty("enableSubPackages", "true");
        //所有生成的接口添加一个父接口
        //javaClientGeneratorConfiguration.addProperty("rootInterface", "");
        context.setJavaClientGeneratorConfiguration(javaClientGeneratorConfiguration);
        if(config.hasPlugin("lombok")){
            //添加lombok插件
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("com.softwareloop.mybatis.generator.plugins.LombokPlugin");
            pluginConfiguration.addProperty("builder", "true");
            pluginConfiguration.addProperty("data", "true");
            pluginConfiguration.addProperty("allArgsConstructor", "true");
            pluginConfiguration.addProperty("noArgsConstructor", "true");
            context.addPluginConfiguration(pluginConfiguration);
        }
        if(config.hasPlugin("swagger")){
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("mybatis.generator.plugins.GenerateSwagger3Doc");
            pluginConfiguration.addProperty("apiModelAnnotationPackage", "io.swagger.annotations.ApiModel");
            pluginConfiguration.addProperty("apiModelPropertyAnnotationPackage", "io.swagger.annotations.ApiModelProperty");
            pluginConfiguration.addProperty("apiModelJavaDoc", "false");
            pluginConfiguration.addProperty("generatorJavaDoc", "false");
            pluginConfiguration.addProperty("generatorSwaggerDoc", "true");
            pluginConfiguration.addProperty("useFullPathName", "false");
            context.addPluginConfiguration(pluginConfiguration);
        }
        if(config.hasPlugin("ser")){
            PluginConfiguration pluginConfiguration = new PluginConfiguration();
            pluginConfiguration.setConfigurationType("org.mybatis.generator.plugins.SerializablePlugin");
            context.addPluginConfiguration(pluginConfiguration);
        }
        List<String> tables = this.getTableNameFromCreateSql(config.getSql());
        for (String table : tables) {
            //添加表配置
            TableConfiguration tableConfiguration = new TableConfiguration(context);
            //表名
            tableConfiguration.setTableName(table);
            //设置为true，在生成的SQL中，table名字不会加上catalog或schema
            tableConfiguration.addProperty("ignoreQualifiersAtRuntime","false");
            //tableConfiguration.addProperty("immutable","false");
            //tableConfiguration.addProperty("modelOnly","false");
            //tableConfiguration.addProperty("rootClass","");
            //tableConfiguration.addProperty("rootInterface","");
            tableConfiguration.addProperty("runtimeCatalog","");
            tableConfiguration.addProperty("runtimeSchema","");
            tableConfiguration.addProperty("runtimeTableName","");
            tableConfiguration.addProperty("selectAllOrderByClause","");
            tableConfiguration.addProperty("useActualColumnNames","false");
            DomainObjectRenamingRule domainObjectRenamingRule = new DomainObjectRenamingRule();
            domainObjectRenamingRule.setSearchString("^"+CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, config.getIgnore_prefix()));
            domainObjectRenamingRule.setReplaceString("");
            tableConfiguration.setDomainObjectRenamingRule(domainObjectRenamingRule);
            if(config.getIs_gen_example().equals("N")){
                tableConfiguration.setCountByExampleStatementEnabled(false);
                tableConfiguration.setSelectByExampleStatementEnabled(false);
                tableConfiguration.setUpdateByExampleStatementEnabled(false);
                tableConfiguration.setDeleteByExampleStatementEnabled(false);
            }
            context.addTableConfiguration(tableConfiguration);
        }
        //注释配置
        //CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        //commentGeneratorConfiguration.addProperty("suppressDate", "true");
        //commentGeneratorConfiguration.addProperty("suppressAllComments", "true");
        CommentGeneratorConfiguration commentGeneratorConfiguration = new CommentGeneratorConfiguration();
        if(config.hasPlugin("note")){
            commentGeneratorConfiguration.setConfigurationType("com.libsgh.mybatis.gen.generator.MyCommentGenerator");
        }else{
            commentGeneratorConfiguration.addProperty("suppressAllComments", "true");
        }
        //用于处理DB中的类型到Java中的类型
        JavaTypeResolverConfiguration javaTypeResolverConfiguration = new JavaTypeResolverConfiguration();
        javaTypeResolverConfiguration.addProperty("forceBigDecimals", "false");
        if(config.getIs_jsr310().equalsIgnoreCase("Y")){
            javaTypeResolverConfiguration.addProperty("useJSR310Types", "true");
        }else{
            javaTypeResolverConfiguration.addProperty("useJSR310Types", "false");
        }
        context.setJavaTypeResolverConfiguration(javaTypeResolverConfiguration);
        //添加 db 表中字段的注释
        context.setCommentGeneratorConfiguration(commentGeneratorConfiguration);
        mybatisConfig.addContext(context);
        DefaultShellCallback callback = new DefaultShellCallback(overwrite);
        MyBatisGenerator myBatisGenerator = null;
        result.put("code", 0);
        result.put("msg", "success");
        try {
            myBatisGenerator = new MyBatisGenerator(mybatisConfig, callback, warnings);
            myBatisGenerator.generate(null);
            this.deleteTable(tables);
            Map<String, Object> data = this.handleFiles(id+"", path);
            timedCache.put(path, DateUtil.offset(new Date(), DateField.HOUR, 24).getTime());
            result.put("data", data);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.put("code", -1);
            result.put("msg", "程序执行异常");
        }
        return result;
    }
    private void loopAllFiles(String path, List<File> genFiles){
        List<File> fileList = FileUtil.loopFiles(path);
        for (File file : fileList) {
            if(file.isFile()){
                genFiles.add(file);
            }else{
                this.loopAllFiles(file.getAbsolutePath(), genFiles);
            }
        }
    }

    private Map<String, Object> handleFiles(String id, String path) {
        //1. 递归找到目录下所有文件
        Map<String, Object> map = new HashMap<>();
        Map<String, String> contentMap = new HashMap<>();
        List<File> genFiles = new ArrayList<>();
        List<FileInfo> files = new ArrayList<>();
        this.loopAllFiles(path, genFiles);
        for (File file : genFiles) {
            if(FileUtil.extName(file).equalsIgnoreCase("xml")){
                contentMap.put("xml_mapper", FileUtil.readUtf8String(file));
                files.add(FileInfo.builder().
                        icon("xml").
                        name(file.getName()).
                        path(id + "/" + file.getName()).
                        size(file.length()).
                        modif_time(DateUtil.formatDateTime(DateUtil.convertTimeZone(new Date(file.lastModified()), ZoneId.of("Asia/Shanghai")))).build());
            }else{
                String content = FileUtil.readUtf8String(file);
                if(StrUtil.endWith(FileUtil.getPrefix(file.getName()), "DynamicSqlSupport")){
                    contentMap.put("dynamic_sql", content);
                }else if(StrUtil.endWith(FileUtil.getPrefix(file.getName()), "Mapper")){
                    contentMap.put("java_mapper", content);
                }else if(StrUtil.endWith(FileUtil.getPrefix(file.getName()), "Example")){
                    contentMap.put("java_example", content);
                }else{
                    contentMap.put("java_model", content);
                }
                files.add(FileInfo.builder().
                        icon("file-code-outline").
                        path(id + "/" + file.getName()).
                        name(file.getName()).
                        size(file.length()).
                        modif_time(DateUtil.formatDateTime(DateUtil.convertTimeZone(new Date(file.lastModified()), ZoneId.of("Asia/Shanghai")))).build());

            }
            FileUtil.move(file, FileUtil.file(path), true);
        }
        List<File> fs = FileUtil.loopFiles(path).stream().filter(f->f.isFile()).collect(Collectors.toList());
        File zipFile = new File(path + File.separator + FileUtil.getName(path) + ".zip");
        ZipUtil.zip(zipFile, false, fs.toArray(new File[fs.size()]));
        files.add(FileInfo.builder().
                icon("zip-box-outline").
                name(zipFile.getName()).
                path(id + "/" + zipFile.getName()).
                size(zipFile.length()).
                modif_time(DateUtil.formatDateTime(DateUtil.convertTimeZone(new Date(zipFile.lastModified()), ZoneId.of("Asia/Shanghai")))).build());
        for (File f : new File(path).listFiles()) {
            if(FileUtil.isDirectory(f)){
                FileUtil.del(f);
            }
        }
        map.put("content", contentMap);
        map.put("files", files);
        return map;
    }

    private Boolean deleteTable(List<String> tables) {
        for(String table: tables){
            try {
                Db.use(ds).execute("drop table " + table);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
                return false;
            }
        }
        return true;
    }

    private Boolean createTable(String sql) {
        List<SQLStatement> list = null;
        try {
            list = SQLUtils.parseStatements(sql, DbType.mysql);
        }catch (Exception e){
            return false;
        }
        if(list.size() > 1){
            return false;
        }
        for(SQLStatement sqlStatement: list){
            try {
                if(StrUtil.startWith(sqlStatement.toString(), "CREATE TABLE", true)){
                    Db.use(ds).execute(sqlStatement.toString());
                }
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
                return false;
            }
        }
        return true;
    }

    private List<String> getTableNameFromCreateSql(String sql) {
        MySqlStatementParser parser = new MySqlStatementParser(sql);
        SQLStatement sqlStatement = parser.parseStatement();
        MySqlSchemaStatVisitor visitor = new MySqlSchemaStatVisitor();
        sqlStatement.accept(visitor);
        Map<TableStat.Name, TableStat> tableStatMap = visitor.getTables();
        return tableStatMap.entrySet().parallelStream().map(r->r.getKey().getName()).collect(Collectors.toList());
    }

    public Map<String, Object> getPrefix(String sql) {
        Map<String, Object> map = new HashMap<>();
        List<String> tables = this.getTableNameFromCreateSql(sql);
        if(tables.size() > 1){
            map.put("code", -1);
            map.put("msg", "检测到多条SQL语句");
            map.put("data", "");
        }else{
            String tableName = tables.get(0);
            String prefix = "";
            int separatorIndex = tableName.lastIndexOf("_");
            int separatorIndex2 = tableName.lastIndexOf("-");
            if(separatorIndex > 0){
                prefix = StrUtil.subPre(tableName, separatorIndex + 1);
            }else{
                if(separatorIndex2 > 0){
                    prefix = StrUtil.subPre(tableName, separatorIndex2 + 1);
                }
            }
            map.put("code", 0);
            map.put("msg", "ok");
            map.put("data", prefix);
        }
        return map;
    }
}
