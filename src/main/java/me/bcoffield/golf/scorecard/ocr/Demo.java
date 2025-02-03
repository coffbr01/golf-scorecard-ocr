package me.bcoffield.golf.scorecard.ocr;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.bcoffield.golf.scorecard.ocr.dto.ScorecardDTO;

import java.io.InputStream;

@Slf4j
public class Demo {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        ScorecardConfig config = ScorecardConfig.builder()
                .url(System.getenv("URL"))
                .apiKey(System.getenv("API_KEY"))
                .build();
        IScorecardOCR scorecardOCR = new AzureDocumentIntelligenceScorecardOCR(config);
        try (InputStream is = Demo.class.getResourceAsStream("/demo-scorecard.jpg")) {
            ScorecardDTO scorecardDTO = scorecardOCR.readScorecard(is);
            log.info("{}", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(scorecardDTO));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
