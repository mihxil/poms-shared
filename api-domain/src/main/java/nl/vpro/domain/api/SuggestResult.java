/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * @author Roelof Jan Koekoek
 * @since 3.2
 */
@XmlRootElement(name = "suggestResult")
@XmlType(name = "suggestResultType")
public class SuggestResult extends Result<Suggestion> {

    public SuggestResult() {
    }

    public static SuggestResult emptyResult() {
        return new SuggestResult(Collections.emptyList(), 0, 0);
    }

    public SuggestResult(List<Suggestion> list, Integer max, long listSizes) {
        super(list, null, max, listSizes);
    }
}