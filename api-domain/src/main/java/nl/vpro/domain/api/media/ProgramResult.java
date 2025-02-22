package nl.vpro.domain.api.media;

import nl.vpro.domain.api.Result;
import nl.vpro.domain.media.Program;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collections;
import java.util.List;

/**
 * Exists only because of https://jira.vpro.nl/browse/API-118
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlRootElement(name = "programResult")
@XmlType(name = "programResultType")
public class ProgramResult extends Result<Program> {

    public static ProgramResult emptyResult(Long offset, Integer max) {
        return new ProgramResult(Collections.emptyList(), offset, max, 0L);
    }

    public ProgramResult() {

    }
    public ProgramResult(List<Program> programs, Long offset, Integer max, long listSizes) {
        super(programs,  offset, max, listSizes);
    }

    public ProgramResult(Result<Program> programs) {
        super(programs);
    }
}
