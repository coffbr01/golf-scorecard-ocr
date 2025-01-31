package me.bcoffield.golf.scorecard.ocr.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScorecardHoleDTO {
    private int hole;
    private int score;
    private int putts;
}
