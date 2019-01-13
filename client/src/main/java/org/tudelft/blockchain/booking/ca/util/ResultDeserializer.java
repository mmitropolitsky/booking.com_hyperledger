package org.tudelft.blockchain.booking.ca.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tudelft.blockchain.booking.ca.model.CAInfo;
import org.tudelft.blockchain.booking.ca.model.Result;

import java.io.IOException;

@Component
public class ResultDeserializer extends StdDeserializer<Result> {

//    @Autowired
//    ObjectMapper objectMapper;

    public ResultDeserializer() {
        this(null);
    }

    protected ResultDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Result deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonNode resultNode = p.getCodec().readTree(p);
        Result result = new Result();
        result.setAttrs(resultNode.get("Attrs").asText());
        result.setCredential(resultNode.get("Credential").asText());
        result.setCri(resultNode.get("CRI").asText());
        result.setNonce(resultNode.get("Nonce").asText());
        result.setCaInfo(objectMapper.readValue(resultNode.get("CAInfo").toString(), CAInfo.class));

        return result;
    }
}
