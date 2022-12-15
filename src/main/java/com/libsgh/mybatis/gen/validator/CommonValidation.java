package com.libsgh.mybatis.gen.validator;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommonValidation<T extends Annotation> implements ConstraintValidator<T, Object> {

	protected Predicate<Object> predicate = c -> true;

	@Override
	public boolean isValid(Object obj, ConstraintValidatorContext constraintValidatorContext) {
		constraintValidatorContext.getDefaultConstraintMessageTemplate();
		boolean result = predicate.test(obj);
		return result;
	}


	public static class InputTypeCheckFunc extends CommonValidation<InputTypeCheck> {
		@Override
		public void initialize(InputTypeCheck inputTypeCheck) {
			predicate = c -> {
				String str = (String) c;
				if (str != null) {
					if (!StrUtil.equalsAny(str, inputTypeCheck.types())) {
						return false;
					}
				}
				return true;
			};
		}
	}

	public static class InputMultiTypeCheckFunc extends CommonValidation<InputMultiTypeCheck> {
		@Override
		public void initialize(InputMultiTypeCheck inputMultiTypeCheck) {
			predicate = c -> {
				List<String> strs = (List<String>) c;
				if (strs != null && strs.size() > 0) {
					List<String> checkResult = strs.stream().
							filter(s -> !StrUtil.equalsAny(s, inputMultiTypeCheck.types())).
							collect(Collectors.toList());
					if (checkResult.size() > 0) {
						return false;
					}
				}
				return true;
			};
		}
	}

}
