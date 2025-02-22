package nl.vpro.validation;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;



@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = CountryValidator.class)
@Documented
public @interface Country {

    String message() default "{nl.vpro.constraints.country}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
