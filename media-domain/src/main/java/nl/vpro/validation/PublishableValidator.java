/*
 * Copyright (C) 2011 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.vpro.domain.media.support.PublishableObject;

public class PublishableValidator implements ConstraintValidator<Publishable, PublishableObject> {

    @Override
    public void initialize(Publishable publishable) {
    }

    @Override
    public boolean isValid(PublishableObject value, ConstraintValidatorContext constraintValidatorContext) {
        return value.getPublishStartInstant() == null
            || value.getPublishStopInstant() == null
            || value.getPublishStartInstant().toEpochMilli() <= value.getPublishStopInstant().toEpochMilli();
    }
}
