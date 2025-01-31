package me.bcoffield.golf.scorecard.ocr;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bcoffield.golf.scorecard.ocr.dto.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class ScorecardOCR {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final ScorecardConfig config;

    public ScorecardDTO readScorecard(InputStream imageIS) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            PromptDTO bodyDto = createPromptDTO(imageIS);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(config.getUrl()))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(bodyDto)))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), ScorecardDTO.class);
            } else {
                log.error("Failed to read scorecard. Status code: {}", response.statusCode());
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    private PromptDTO createPromptDTO(InputStream imageIS) throws IOException {
        PromptDTO prompt = new PromptDTO();
        prompt.setModel(config.getModel());
        prompt.setMax_tokens(config.getMaxTokens());
        PromptMessageDTO message = new PromptMessageDTO();
        message.setRole("user");
        List<PromptContentDTO> content = getPromptContentDTOs(imageIS);
        message.setContent(content);
        List<PromptMessageDTO> messages = Collections.singletonList(message);
        prompt.setMessages(messages);
        return prompt;
    }

    private static List<PromptContentDTO> getPromptContentDTOs(InputStream imageIS) throws IOException {
        List<PromptContentDTO> content = new ArrayList<>();
        PromptContentTextDTO textContent = new PromptContentTextDTO();
        String promptText = getPromptText();
        textContent.setText(promptText);
        content.add(textContent);

        PromptContentImageDTO imageContent = new PromptContentImageDTO();
        PromptContentImageURLDTO imageUrl = new PromptContentImageURLDTO();
        String base64Str = ImageUtil.convertImageToBase64(imageIS);
        imageUrl.setUrl("data:image/jpeg;base64," + base64Str);
        imageContent.setImage_url(imageUrl);
        content.add(imageContent);
        return content;
    }

    private static String getPromptText() {
        return """
            Inspect this photo of a golf scorecard and return JSON of the scores.
            The scores will be the larger number in each cell.
            If there is also a smaller number in the cell, it represents the number of putts.
            Sometimes putts will not be present in the photo. In this case, return putts as -1.
            Return your response in the following format:
            ```json
            {
               "scores": [
                   {
                      "player": "Player 1",
                      "holes": [
                          {
                              "hole": 1,
                              "score": 3,
                              "putts": 2
                          },
                          {
                              "hole": 2,
                              "score": 5,
                              "putts": 3
                          }
                      ]
                   },
                   {
                      "player": "Player 2",
                      "holes": [
                          {
                              "hole": 1,
                              "score": 4,
                              "putts": 2
                          },
                          {
                              "hole": 2,
                              "score": 4,
                              "putts": 2
                          }
                      ]
                   }
               ]
            }
            ```
            """;
    }
}