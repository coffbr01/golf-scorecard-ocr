package me.bcoffield.golf.scorecard.ocr;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ScorecardConfig {
    private String url;
    private String model;
    private String apiKey;
    private int maxTokens;
}
