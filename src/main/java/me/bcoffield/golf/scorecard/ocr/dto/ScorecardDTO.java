package me.bcoffield.golf.scorecard.ocr.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScorecardDTO {
    private List<ScorecardScoreDTO> scores;
}
