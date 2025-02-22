/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint;

import java.util.Locale;
import java.util.Objects;

import javax.el.ELContext;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlValue;


/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlTransient
public abstract class AbstractTextConstraint<T> implements WildTextConstraint<T> {


    @XmlTransient
    protected CaseHandling caseHandling = CaseHandling.LOWER;

    @XmlTransient
    protected String value;

    protected AbstractTextConstraint() {
    }

    protected AbstractTextConstraint(String value) {
        this.value = value;
    }



    @Override
    @XmlValue
    public final String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "/" + getESPath() + "{value='" + value + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) {
            return false;
        }
        if(this == o) {
            return true;
        }

        if(!this.getClass().equals(o.getClass())) {
            return false;
        }

        AbstractTextConstraint<?> that = (AbstractTextConstraint<?>)o;

        return value != null ? value.equals(that.value) : that.value == null;
    }

    @Override
    public int hashCode() {
        return value != null ? value.hashCode() : 0;
    }


    @Override
    public void setELContext(ELContext ctx, T v, Locale locale, PredicateTestResult<T> result) {
        WildTextConstraint.super.setELContext(ctx, v, locale, result);
    }


    protected boolean applyValue(String compareTo) {
        switch (getCaseHandling()) {
            case ASIS:
                return Objects.equals(value, compareTo);
            case LOWER:
                return Objects.equals(value == null ? null : value.toLowerCase(), compareTo);
            case UPPER:
                return Objects.equals(value == null ? null : value.toUpperCase(), compareTo);
            default:
                return Objects.equals(value == null ? null : value.toUpperCase(), compareTo.toUpperCase());
        }
    }


    @Override
    public CaseHandling getCaseHandling() {
        return caseHandling;
    }
}
