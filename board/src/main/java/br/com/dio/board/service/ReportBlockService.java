package br.com.dio.board.service;

import br.com.dio.board.dto.BlockDetailsDTO;
import br.com.dio.board.persistence.dao.BlockDAO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
public class ReportBlockService {

    private final BlockDAO dao;
    private final ObjectMapper objectMapper;

    public void generateBlockReport(final Long cardId) throws SQLException, IOException {
        List<BlockDetailsDTO> movements = dao.getReportByCardId(cardId);

        if(movements.isEmpty()) {
            System.out.println("Nenhuma movimentação encontrada para o card " + cardId);
            return;
        }

        List<Map<String, String>> blockHistory = new ArrayList<>();

        for (BlockDetailsDTO details : movements) {
            Map<String, String> blockEntry = new LinkedHashMap<>();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            //BLOCK
            String blockedDate = details.block_date().toString();
            OffsetDateTime dataBlock = OffsetDateTime.parse(blockedDate);
            String formatedBlockDate = formatter.format(dataBlock);
            //UNBLOCK
            String unblockedDate = details.unblock_date().toString();
            OffsetDateTime dataUnblock = OffsetDateTime.parse(unblockedDate);
            String formatedUnblockDate = formatter.format(dataUnblock);

            blockEntry.put("Data do bloqueio:", formatedBlockDate);
            blockEntry.put("Motivo do bloqueio:", details.block_reason());
            blockEntry.put("Data de debloqueio:", formatedUnblockDate);
            blockEntry.put("Motivo de debloqueio:", details.unblock_reason());

            Duration duration = Duration.between(details.block_date(), details.unblock_date());
            long hours = duration.toHours();
            long minutes = duration.toMinutes() % 60;
            long seconds = duration.getSeconds() % 60;

            String blockedTime = (hours + " horas, " + minutes + " minutos e " + seconds + " segundos.");
            blockEntry.put("Tempo bloqueado:", blockedTime);

            blockHistory.add(blockEntry);
        }

        Map<String, Object> reportData = new LinkedHashMap<>();
        reportData.put("ID:", cardId);
        reportData.put("Histórico de bloqueios:", blockHistory);

        String reportPath = "src/main/resources/reports/blocks/block_" + cardId + "_report.json";

        Files.createDirectories(Paths.get("src/main/resources/reports/blocks"));
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(reportPath), reportData);
        System.out.println("Relatório gerado com sucesso " + reportPath);
    }
}
