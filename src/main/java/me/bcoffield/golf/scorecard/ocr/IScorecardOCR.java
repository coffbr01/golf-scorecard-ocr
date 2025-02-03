package me.bcoffield.golf.scorecard.ocr;

import me.bcoffield.golf.scorecard.ocr.dto.ScorecardDTO;

import java.io.InputStream;

public interface IScorecardOCR {
    ScorecardDTO readScorecard(InputStream imageIS);
}
