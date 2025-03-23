package br.com.dio.board.service;

import br.com.dio.board.dto.BlockDetailsDTO;
import br.com.dio.board.persistence.dao.BlockDAO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class ReportBlockService {

    private final BlockDAO dao;
    private final ObjectMapper objectMapper;

    public void generateBlockReport(final Long cardId) throws SQLException, IOException {
        List<BlockDetailsDTO> blocks = dao.getReportByCardId(cardId);
        String reportPath = "src/main/resources/reports/blocks/block_" + cardId + "_report.json";

        Map<String, Object> reportData;

        File reportFile = new File(reportPath);
        if(reportFile.exists()) {
            reportData = objectMapper.readValue(reportFile, new TypeReference<>() {});
        } else {
            reportData = new LinkedHashMap<>();
            reportData.put("ID:", cardId);
            reportData.put("Histórico de bloqueios:", new ArrayList<Map<String, Object>>());
        }

        @SuppressWarnings("uncheked")
        List<Map<String, Object>> blockHistory = (List<Map<String, Object>>) reportData.get("Historico de bloqueios");

        if(blockHistory == null) blockHistory = new ArrayList<>();

        for(BlockDetailsDTO detail : blocks) {
            Map<String, Object> blockEntry = new LinkedHashMap<>();

            blockEntry.put("Data do bloqueio:", detail.block_date().toString());
            blockEntry.put("Motivo do bloqueio:", detail.block_reason());
            blockEntry.put("Data de debloqueio:", detail.unblock_date().toString());
            blockEntry.put("Motivo de debloqueio:", detail.unblock_reason());

            Duration duration = Duration.between(detail.block_date(), detail.unblock_date());
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            long seconds = duration.toSeconds() % 60;

            String blockedTime = (hours + " horas, " + minutes + " minutos e " + seconds + " segundos.");

            blockEntry.put("Tempo bloqueado:", blockedTime);

            blockHistory.add(blockEntry);
        }

        reportData.put("Histórico de bloqueios:", blockHistory);

        Files.createDirectories(Paths.get("src/main/resources/reports/blocks"));
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(reportFile, reportData);
        System.out.println("Relatório gerado com sucesso " + reportPath);
    }
}
