package com.libsgh.mybatis.gen.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author Libs
 * @Description 参数包含校验（必须是指定类型中的一种）
 * @Date 2022/12/15 16:11
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Constraint(validatedBy = CommonValidation.InputTypeCheckFunc.class)
public @interface InputTypeCheck {
    String message() default "";

    String[] types() default {};

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
