package me.bcoffield.golf.scorecard.ocr.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PromptDTO {
    private String model;
    private List<PromptMessageDTO> messages;
    private int max_tokens;
}
