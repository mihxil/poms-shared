/**
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.page.bind;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import nl.vpro.domain.page.Crid;
import nl.vpro.domain.page.PageType;

/**
 * @author Roelof Jan Koekoek
 * @since 3.0
 */
public class PageTypeToDisplay {

    public static class Serializer extends JsonSerializer<PageType> {

        @Override
        public void serialize(PageType pageType, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
            jgen.writeString(pageType.getDisplayName());
        }
    }

    public static class Deserializer extends JsonDeserializer<PageType> {

        @Override
        public PageType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
            return PageType.valueOfDisplayName(jp.getValueAsString());
        }
    }
}
