package com.libsgh.mybatis.gen.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author Libs
 * @Description 混合参数校验（至少包含types中的一个或多个，多个类型,分隔）
 * @Date 2022/12/15 16:11
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Constraint(validatedBy = CommonValidation.InputMultiTypeCheckFunc.class)
public @interface InputMultiTypeCheck {
    String message() default "";

    String[] types() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
