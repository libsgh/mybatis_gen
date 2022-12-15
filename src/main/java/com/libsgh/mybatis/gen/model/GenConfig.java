package com.libsgh.mybatis.gen.model;

import com.libsgh.mybatis.gen.validator.InputMultiTypeCheck;
import com.libsgh.mybatis.gen.validator.InputTypeCheck;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName GenConfig
 * @Description TODO
 * @Author Libs
 * @Date 2022/12/9 17:06
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenConfig {
    /**
     * 生成模式
     **/
    @NotBlank(message = "生成模式不能为空")
    @InputTypeCheck(message = "无效的生成模式", types = {"MyBatis3", "MyBatis3Simple", "MyBatis3DynamicSql"})
    private String gen_type;
    /**
     * 是否生成example
     **/
    @InputTypeCheck(message = "无效的Example配置", types = {"Y", "N"})
    @NotBlank(message = "Example配置不能为空")
    private String is_gen_example;
    /**
     * 是否支持jsr310日期格式
     **/
    @NotBlank(message = "JSR310配置不能为空")
    @InputTypeCheck(message = "无效的JSR310配置", types = {"Y", "N"})
    private String is_jsr310;
    /**
     * 启用的插件列表
     **/
    @InputMultiTypeCheck(message = "无效的插件类型", types = {"note", "lombok", "swagger", "ann", "ser"})
    private List<String> plugin;
    /**
     * 默认model包名
     **/
    @NotBlank(message = "Model包名不能为空")
    private String default_model_package_name;
    /**
     * 忽略表前缀
     **/
    private String ignore_prefix;
    /**
     * 默认mapper包名
     **/
    @NotBlank(message = "Mapper包名不能为空")
    private String default_mapper_package_name;
    /**
     * 建表语句DDL
     **/
    @NotBlank(message = "建表语句不能为空")
    private String sql;

    public Boolean hasPlugin(String plugin){
        List<String> list = this.getPlugin();
        return list.contains(plugin);
    }
}
