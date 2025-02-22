/*
 * Copyright (C) 2014 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api.jackson;

import java.io.IOException;
import java.time.Duration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import nl.vpro.domain.api.*;
import nl.vpro.jackson2.Jackson2Mapper;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class DurationRangeFacetsToJson {

    private static final ObjectMapper mapper = Jackson2Mapper.getInstance();

    public static class Serializer extends JsonSerializer<DurationRangeFacets<?>> {

        @Override
        public void serialize(DurationRangeFacets<?> value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
            jgen.writeStartArray();
            for(RangeFacet<Duration> item : value.getRanges()) {
                jgen.writeObject(item);
            }
            jgen.writeEndArray();
        }
    }

    public static class Deserializer extends JsonDeserializer<DurationRangeFacets<?>> {

        @Override
        public DurationRangeFacets deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
            DurationRangeFacets result = new DurationRangeFacets();

            TreeNode treeNode = mapper.readTree(jp);
            if(treeNode instanceof ArrayNode) {
                ArrayNode arrayNode = (ArrayNode)treeNode;
                for(JsonNode jsonNode : arrayNode) {
                    if(jsonNode.isTextual()) {
                        result.addRanges(new DurationRangeInterval(jsonNode.textValue()));
                    } else {
                        result.addRanges(mapper.readValue(jsonNode.toString(), DurationRangeFacetItem.class));
                    }
                }
            } else if(treeNode instanceof ObjectNode) {
                result.addRanges(mapper.readValue((treeNode).toString(), DurationRangeFacetItem.class));
            } else if(treeNode instanceof TextNode) {
                result.addRanges(new DurationRangeInterval(((TextNode)treeNode).textValue()));
            } else {
                throw new IllegalArgumentException("Unsupported node: " + treeNode.toString());
            }

            return result;
        }
    }
}
