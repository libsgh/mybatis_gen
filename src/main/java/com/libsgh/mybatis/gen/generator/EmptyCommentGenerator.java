package com.libsgh.mybatis.gen.generator;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.dom.java.*;
import org.mybatis.generator.api.dom.kotlin.KotlinFile;
import org.mybatis.generator.api.dom.kotlin.KotlinFunction;
import org.mybatis.generator.api.dom.kotlin.KotlinProperty;
import org.mybatis.generator.api.dom.kotlin.KotlinType;
import org.mybatis.generator.api.dom.xml.XmlElement;

import java.util.Properties;
import java.util.Set;

/**
 * @ClassName EmptyCommentGenerator
 * @Description 空的注释生成器
 * @Author Libs
 * @Date 2022/12/12 15:03
 * @Version 1.0
 */
public class EmptyCommentGenerator implements CommentGenerator {
    @Override
    public void addConfigurationProperties(Properties properties) {

    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        CommentGenerator.super.addFieldComment(field, introspectedTable, introspectedColumn);
    }

    @Override
    public void addFieldComment(Field field, IntrospectedTable introspectedTable) {
        CommentGenerator.super.addFieldComment(field, introspectedTable);
    }

    @Override
    public void addModelClassComment(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        CommentGenerator.super.addModelClassComment(topLevelClass, introspectedTable);
    }

    @Override
    public void addModelClassComment(KotlinType modelClass, IntrospectedTable introspectedTable) {
        CommentGenerator.super.addModelClassComment(modelClass, introspectedTable);
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable) {
        CommentGenerator.super.addClassComment(innerClass, introspectedTable);
    }

    @Override
    public void addClassComment(InnerClass innerClass, IntrospectedTable introspectedTable, boolean markAsDoNotDelete) {
        CommentGenerator.super.addClassComment(innerClass, introspectedTable, markAsDoNotDelete);
    }

    @Override
    public void addEnumComment(InnerEnum innerEnum, IntrospectedTable introspectedTable) {
        CommentGenerator.super.addEnumComment(innerEnum, introspectedTable);
    }

    @Override
    public void addGetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        CommentGenerator.super.addGetterComment(method, introspectedTable, introspectedColumn);
    }

    @Override
    public void addSetterComment(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn) {
        CommentGenerator.super.addSetterComment(method, introspectedTable, introspectedColumn);
    }

    @Override
    public void addGeneralMethodComment(Method method, IntrospectedTable introspectedTable) {
        CommentGenerator.super.addGeneralMethodComment(method, introspectedTable);
    }

    @Override
    public void addJavaFileComment(CompilationUnit compilationUnit) {
        CommentGenerator.super.addJavaFileComment(compilationUnit);
    }

    @Override
    public void addComment(XmlElement xmlElement) {
        CommentGenerator.super.addComment(xmlElement);
    }

    @Override
    public void addRootComment(XmlElement rootElement) {
        CommentGenerator.super.addRootComment(rootElement);
    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> imports) {
        CommentGenerator.super.addGeneralMethodAnnotation(method, introspectedTable, imports);
    }

    @Override
    public void addGeneralMethodAnnotation(Method method, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
        CommentGenerator.super.addGeneralMethodAnnotation(method, introspectedTable, introspectedColumn, imports);
    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> imports) {
        CommentGenerator.super.addFieldAnnotation(field, introspectedTable, imports);
    }

    @Override
    public void addFieldAnnotation(Field field, IntrospectedTable introspectedTable, IntrospectedColumn introspectedColumn, Set<FullyQualifiedJavaType> imports) {
        CommentGenerator.super.addFieldAnnotation(field, introspectedTable, introspectedColumn, imports);
    }

    @Override
    public void addClassAnnotation(InnerClass innerClass, IntrospectedTable introspectedTable, Set<FullyQualifiedJavaType> imports) {
        CommentGenerator.super.addClassAnnotation(innerClass, introspectedTable, imports);
    }

    @Override
    public void addFileComment(KotlinFile kotlinFile) {
        CommentGenerator.super.addFileComment(kotlinFile);
    }

    @Override
    public void addGeneralFunctionComment(KotlinFunction kf, IntrospectedTable introspectedTable, Set<String> imports) {
        CommentGenerator.super.addGeneralFunctionComment(kf, introspectedTable, imports);
    }

    @Override
    public void addGeneralPropertyComment(KotlinProperty property, IntrospectedTable introspectedTable, Set<String> imports) {
        CommentGenerator.super.addGeneralPropertyComment(property, introspectedTable, imports);
    }
}
