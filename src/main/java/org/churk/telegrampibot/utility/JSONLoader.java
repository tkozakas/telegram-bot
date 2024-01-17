package org.churk.telegrampibot.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class JSONLoader {
    public List<Map<String, Object>> readFromJSON(String fileName) {
        try {
            String jsonContent = new String(Files.readAllBytes(Paths.get(fileName)));
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(jsonContent, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            log.error("JSON processing exception", e);
            return Collections.emptyList();
        } catch (IOException e) {
            log.error("Error reading JSON file", e);
            return Collections.emptyList();
        }
    }
}
