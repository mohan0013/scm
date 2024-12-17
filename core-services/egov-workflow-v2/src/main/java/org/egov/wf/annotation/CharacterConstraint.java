package org.egov.wf.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

@Documented
@Constraint(validatedBy = AdditionalDetailValidator.class)
@Target( {ElementType.METHOD,ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CharacterConstraint {

    String message() default "Invalid Additional Details";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    int size();


}