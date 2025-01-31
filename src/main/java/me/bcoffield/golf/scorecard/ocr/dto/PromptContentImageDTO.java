package me.bcoffield.golf.scorecard.ocr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PromptContentImageDTO extends PromptContentDTO {
    private PromptContentImageURLDTO image_url;

    public PromptContentImageDTO() {
        super();
        setType("image_url");
    }
}
