/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint;

import javax.annotation.Nullable;
import javax.el.ELContext;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;
import java.util.Locale;

import static nl.vpro.domain.constraint.PredicateTestResult.FACTORY;


/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlTransient
public abstract class AbstractNot<T> implements Constraint<T> {

    protected Constraint<T> constraint;

    protected AbstractNot() {
    }

    protected AbstractNot(Constraint<T> constraint) {
        this.constraint = constraint;
    }

    public Constraint<T> getConstraint() {
        return constraint;
    }

    public void setConstraint(Constraint<T> constraint) {
        this.constraint = constraint;
    }

    @Override
    public boolean test(@Nullable T t) {
        return constraint == null || constraint.negate().test(t);
    }


    @Override
    public NotPredicateTestResult<T> testWithReason(@Nullable T t) {
        PredicateTestResult<T> result = constraint.testWithReason(t);
        return new NotPredicateTestResult<>(this, t, ! result.applies(), result);
    }

    @Override
    public void setELContext(ELContext ctx, T value, Locale locale, PredicateTestResult<T> result) {
        Constraint.super.setELContext(ctx, value, locale, result);
        NotPredicateTestResult<T> notResult = (NotPredicateTestResult<T>) result;
        ctx.getVariableMapper().setVariable("clause",
            FACTORY.createValueExpression(notResult.getClause().getDescription(locale), String.class));
    }

    @Override
    public List<String> getDefaultBundleKey() {
        List<String> result = Constraint.super.getDefaultBundleKey();
        result.add(0, "Not");
        return result;
    }

}
