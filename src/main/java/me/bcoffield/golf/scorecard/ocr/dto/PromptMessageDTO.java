package me.bcoffield.golf.scorecard.ocr.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PromptMessageDTO {
    private String role;
    private List<PromptContentDTO> content;
}
