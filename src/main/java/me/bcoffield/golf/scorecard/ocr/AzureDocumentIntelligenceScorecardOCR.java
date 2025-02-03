package me.bcoffield.golf.scorecard.ocr;

import com.azure.ai.documentintelligence.DocumentIntelligenceClient;
import com.azure.ai.documentintelligence.DocumentIntelligenceClientBuilder;
import com.azure.ai.documentintelligence.models.*;
import com.azure.core.credential.AzureKeyCredential;
import com.azure.core.util.polling.SyncPoller;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bcoffield.golf.scorecard.ocr.dto.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
public class AzureDocumentIntelligenceScorecardOCR implements IScorecardOCR {
    private final ScorecardConfig config;

    public ScorecardDTO readScorecard(InputStream imageIS) {
        DocumentIntelligenceClient client = new DocumentIntelligenceClientBuilder()
                .credential(new AzureKeyCredential(config.getApiKey()))
                .endpoint(config.getUrl())
                .buildClient();

        String modelId = "prebuilt-layout";

        byte[] imageBytes;
        try {
            imageBytes = ImageUtil.convertImageToBytes(imageIS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        AnalyzeDocumentOptions analyzeDocumentOptions = new AnalyzeDocumentOptions(imageBytes);
        SyncPoller<AnalyzeOperationDetails, AnalyzeResult> analyzeLayoutPoller = client.beginAnalyzeDocument(modelId, analyzeDocumentOptions);


        DocumentTable table = analyzeLayoutPoller.getFinalResult().getTables().stream().findFirst().orElseThrow(() -> new RuntimeException("No table found"));
        return fillScorecard(table);
    }

    private ScorecardDTO fillScorecard(DocumentTable documentTable) {
        ScorecardDTO result = new ScorecardDTO();

        List<DocumentTableCell> cells = documentTable.getCells();
        final int nameColumnIndex = 0;

        List<Integer> nameRowIndicesList = Arrays.asList(5, 6, 9, 10);

        Map<Integer, Integer> holeByColumnIndex = new HashMap<>();
        List<DocumentTableCell> headerCells = cells.stream().filter(cell -> DocumentTableCellKind.COLUMN_HEADER == cell.getKind()).toList();
        for (DocumentTableCell cell : headerCells) {
            try {
                int holeNumber = Integer.parseInt(cell.getContent());
                holeByColumnIndex.put(cell.getColumnIndex(), holeNumber);
            } catch (NumberFormatException e) {
                // This is fine
                log.debug("Unable to parse int {}", cell.getContent());
            }
        }

        Map<Integer, List<DocumentTableCell>> cellsByRow = new HashMap<>();
        for (DocumentTableCell cell : cells) {
            int rowIndex = cell.getRowIndex();
            cellsByRow.putIfAbsent(rowIndex, new ArrayList<>());
            cellsByRow.get(rowIndex).add(cell);
        }

        result.setScores(new ArrayList<>());

        for (Map.Entry<Integer, List<DocumentTableCell>> entry : cellsByRow.entrySet()) {
            int rowIndex = entry.getKey();
            if (nameRowIndicesList.contains(rowIndex)) {
                List<DocumentTableCell> rowCells = entry.getValue();
                ScorecardScoreDTO scorecardScoreDTO = new ScorecardScoreDTO();
                scorecardScoreDTO.setHoles(new ArrayList<>());
                for (DocumentTableCell rowCell : rowCells) {
                    int columnIndex = rowCell.getColumnIndex();
                    if (rowCell.getColumnIndex() == nameColumnIndex) {
                        scorecardScoreDTO.setPlayer(rowCell.getContent().split("\\n")[0]);
                    } else if (holeByColumnIndex.containsKey(columnIndex)) {
                        ScorecardHoleDTO scorecardHoleDTO = new ScorecardHoleDTO();
                        int hole = holeByColumnIndex.get(columnIndex);
                        scorecardHoleDTO.setHole(hole);
                        int score = -1;
                        try {
                            score = Integer.parseInt(rowCell.getContent());
                        } catch (NumberFormatException e) {
                            log.debug("Could not parse player {}, hole {}, score {}", scorecardScoreDTO.getPlayer(), hole, rowCell.getContent());
                        }
                        scorecardHoleDTO.setScore(score);
                        scorecardScoreDTO.getHoles().add(scorecardHoleDTO);
                    }
                }
                result.getScores().add(scorecardScoreDTO);
            }

        }

        return result;
    }

}