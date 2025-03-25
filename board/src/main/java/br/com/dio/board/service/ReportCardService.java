package br.com.dio.board.service;

import br.com.dio.board.dto.CardMovementDTO;
import br.com.dio.board.persistence.dao.CardMovementDAO;
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
import java.util.*;

@AllArgsConstructor
public class ReportCardService {

    private final CardMovementDAO dao;
    private final ObjectMapper objectMapper;

    public void generateCardReport(final Long cardId) throws SQLException, IOException {
        List<CardMovementDTO> movements = dao.getMovementByCardId(cardId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String formatedLeftedAt = "";

        if(movements.isEmpty()) {
            System.out.println("Nenhuma movimentação encontrada para o card " + cardId);
            return;
        }

        List<Map<String, String>> movementList = new ArrayList<>();

        for (CardMovementDTO movement : movements) {
            var enteredAt = movement.entered_at();
            var leftedAt = movement.lefted_at();

            String timeSpent;

            if (leftedAt != null) {
                //LEFTED AT
                String leftedAtDate = leftedAt.toString();
                OffsetDateTime dataLefted = OffsetDateTime.parse(leftedAtDate);
                formatedLeftedAt = formatter.format(dataLefted);

                Duration duration = Duration.between(enteredAt, leftedAt);
                long hours = duration.toHours();
                long minutes = duration.toMinutes() % 60;
                long seconds = duration.toSeconds() % 60;

                timeSpent = (hours + " horas, " + minutes + " minutos e " + seconds + " segundos.");
            } else {
                timeSpent = "Ainda em andamento";
            }

            //ENTERED AT
            String enteredAtDate = enteredAt.toString();
            OffsetDateTime dataEntered = OffsetDateTime.parse(enteredAtDate);
            String formatedenteredAtDate = formatter.format(dataEntered);

            Map<String, String> movementData = new HashMap<>();
            movementData.put("ID da coluna:", movement.board_column_id().toString());
            movementData.put("Nome da coluna:", movement.column_name());
            movementData.put("Data de entrada:", formatedenteredAtDate);
            if (!"FINAL".equals(movement.column_type())) {
                movementData.put("Data de saída:", leftedAt != null ? formatedLeftedAt : "Em andamento");
                movementData.put("Tempo gasto:", timeSpent);
            }
            movementList.add(movementData);
        }

        var firstEntry = movements.getFirst().entered_at();

        //CREATED DATE
        String createdAtDate = firstEntry.toString();
        OffsetDateTime dataCreated = OffsetDateTime.parse(createdAtDate);
        String formatedCreatedAt = formatter.format(dataCreated);

        Map<String, Object> reportData = new LinkedHashMap<>();
        reportData.put("ID:", cardId);
        reportData.put("Data de criação:", formatedCreatedAt);
        reportData.put("Movimentação:", movementList);

        String reportPath = "src/main/resources/reports/cards/card_" + cardId + "_report.json";

        Files.createDirectories(Paths.get("src/main/resources/reports/cards"));
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(reportPath), reportData);
        System.out.println("Relatório gerado com sucesso " + reportPath);
    }
}
