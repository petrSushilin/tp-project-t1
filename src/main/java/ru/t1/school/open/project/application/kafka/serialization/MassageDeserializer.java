package ru.t1.school.open.project.application.kafka.serialization;

import org.apache.kafka.common.header.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class MassageDeserializer<T> extends JsonDeserializer<T> {
    Logger logger = LoggerFactory.getLogger(MassageDeserializer.class);

    private static String getMassage(byte[] data) {
        return new String(data, StandardCharsets.UTF_8);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            return super.deserialize(topic, data);
        } catch (Exception e) {
            logger.warn("Error deserializing message: {}", new String(data, StandardCharsets.UTF_8), e);
            throw new RuntimeException("Error deserializing", e);
        }
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] data) {
        try {
            return super.deserialize(topic, headers, data);
        } catch (Exception e) {
            logger.warn("Error deserializing message: {}", new String(data, StandardCharsets.UTF_8), e);
            throw new RuntimeException("Error deserializing", e);
        }
    }
}
