package nl.vpro.nep.domain;

import org.junit.Test;

import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
public class NEPItemizeRequestTest {


    @Test
    public void json() throws Exception {
        NEPItemizeRequest request = NEPItemizeRequest.builder()
            .starttime("2018-05-09T16:03:01.121")
            .endtime("2018-05-09T16:53:01.122")
            .identifier("npo-1dvr")
            .build();

        Jackson2TestUtil.roundTripAndSimilar(Jackson2Mapper.getLenientInstance(), request, "{\n" +
            "  \"identifier\" : \"npo-1dvr\",\n" +
            "  \"starttime\" : \"2018-05-09T16:03:01.121\",\n" +
            "  \"endtime\" : \"2018-05-09T16:53:01.122\"\n" +
            "}");

        // {"starttime":"2018-05-09T14:59:25.405","endtime":"2018-05-09T14:59:31.548","identifier":"npo-1dvr"}
    }

}
