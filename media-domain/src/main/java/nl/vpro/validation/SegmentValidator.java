package nl.vpro.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import nl.vpro.domain.media.Segment;

/**
 * @author Michiel Meeuwissen
 * @since 2.3.1
 */
public class SegmentValidator implements ConstraintValidator<SegmentValidation, Segment> {

    @Override
    public void initialize(SegmentValidation constraintAnnotation) {


    }

    @Override
    public boolean isValid(Segment value, ConstraintValidatorContext context) {
        return value.getMidRef() != null || value.getParent() != null;
    }
}
