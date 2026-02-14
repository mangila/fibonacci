package com.github.mangila.fibonacci.web.shared;

import io.github.mangila.ensure4j.Ensure;
import org.intellij.lang.annotations.Language;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.node.ObjectNode;


@Component
public class RedisMessageParser {

    private final JsonMapper jsonMapper;

    public RedisMessageParser(JsonMapper jsonMapper) {
        this.jsonMapper = jsonMapper;
    }

    public enum OptionType {
        STREAM_OPTION,
        ID_OPTION,
        UNKNOWN
    }

    public record OptionNode(OptionType optionType, ObjectNode node) {

        public OptionNode {
            Ensure.notNull(optionType);
            Ensure.notNull(node);
        }
    }

    public OptionNode determineOption(@Language("JSON") String json) {
        var node = jsonMapper.readTree(json);
        Ensure.isTrue(node.isObject(), "json node must be an object");
        if (isStreamOption(node)) {
            return new OptionNode(OptionType.STREAM_OPTION, (ObjectNode) node);
        } else if (isIdOption(node)) {
            return new OptionNode(OptionType.ID_OPTION, (ObjectNode) node);
        } else {
            return new OptionNode(OptionType.UNKNOWN, jsonMapper.createObjectNode());
        }
    }

    private static boolean isStreamOption(JsonNode node) {
        return node.hasNonNull("offset") && node.hasNonNull("limit");
    }

    private static boolean isIdOption(JsonNode node) {
        return node.hasNonNull("id");
    }

}
