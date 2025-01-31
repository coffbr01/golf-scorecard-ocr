package me.bcoffield.golf.scorecard.ocr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromptContentTextDTO extends PromptContentDTO {
    private String text;

    public PromptContentTextDTO() {
        super();
        setType("text");
    }
}
