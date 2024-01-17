package org.churk.telegrampibot.reader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.churk.telegrampibot.config.BotConfig;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StickerPackLoader {
    private static final String csvPath = "src/main/resources/stickers.csv";
    private final BotConfig botConfig;

    public StickerPackLoader(BotConfig botConfig) {
        this.botConfig = botConfig;
    }

    public void loadStickerPacks() {
        List<String> stickerSetNames = botConfig.getStickerSets();
        String botToken = botConfig.getToken();

        ObjectMapper mapper = new ObjectMapper();
        try {
            Files.deleteIfExists(Paths.get(csvPath));
            for (String stickerSetName : stickerSetNames) {
                String url = "https://api.telegram.org/bot" + botToken + "/getStickerSet?name=" + stickerSetName;

                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    HttpGet request = new HttpGet(url);
                    HttpResponse response = httpClient.execute(request);
                    String json = EntityUtils.toString(response.getEntity());

                    if (response.getStatusLine().getStatusCode() == 200) {
                        TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {
                        };
                        Map<String, Object> responseData = mapper.readValue(json, typeRef);
                        Map<String, Object> resultData = (Map<String, Object>) responseData.get("result");

                        TypeReference<List<Map<String, Object>>> stickersTypeRef = new TypeReference<>() {
                        };
                        List<Map<String, Object>> stickers = mapper.convertValue(resultData.get("stickers"), stickersTypeRef);

                        try (FileWriter writer = new FileWriter(csvPath, true)) {
                            for (Map<String, Object> sticker : stickers) {
                                String fileId = (String) sticker.get("file_id");
                                writer.write(fileId + "\n");
                            }
                        }

                        log.info("Sticker IDs from set " + stickerSetName + " appended to " + csvPath + ".");
                    } else {
                        log.error("Failed to fetch data from the API for set " + stickerSetName + ".");
                        log.error("HTTP Status Code: " + response.getStatusLine().getStatusCode());
                    }
                } catch (IOException e) {
                    log.error("Failed to fetch data from the API for set " + stickerSetName + ".", e);
                }
            }
        } catch (IOException e) {
            log.error("Failed to load YAML configuration.", e);
        }
    }
}
