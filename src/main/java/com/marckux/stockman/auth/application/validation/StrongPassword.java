package com.marckux.stockman.auth.application.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
  String message() default "La password debe tener al menos 8 caracteres" +
      " una mayúscula, una minúscula y un número";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
