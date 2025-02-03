package me.bcoffield.golf.scorecard.ocr.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class ScorecardScoreDTO {
    private String player;
    private List<ScorecardHoleDTO> holes;
}
