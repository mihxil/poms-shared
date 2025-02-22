/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RestrictionValidator implements ConstraintValidator<Restriction, nl.vpro.domain.media.Restriction> {

    @Override
    public void initialize(Restriction publishable) {
    }

    @Override
    public boolean isValid(nl.vpro.domain.media.Restriction value, ConstraintValidatorContext constraintValidatorContext) {
        return value.getStart() == null
            || value.getStop() == null
            || (!value.getStart().isAfter(value.getStop()));

    }
}
