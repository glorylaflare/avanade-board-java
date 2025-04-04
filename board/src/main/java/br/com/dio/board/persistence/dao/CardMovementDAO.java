package br.com.dio.board.persistence.dao;

import br.com.dio.board.dto.CardMovementDTO;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static br.com.dio.board.persistence.converter.OffsetDateTimeConverter.toOffsetDateTime;

@AllArgsConstructor
public class CardMovementDAO {

    private Connection connection;

    public List<CardMovementDTO> getMovementByCardId(final Long cardId) throws SQLException {
        var sql = "SELECT cmh.*, bc.name AS column_name, bc.type AS column_type " +
                "FROM CARD_MOVEMENT_HISTORY cmh " +
                "JOIN BOARDS_COLUMNS bc ON cmh.board_column_id = bc.id " +
                "WHERE cmh.card_id = ? " +
                "ORDER BY cmh.entered_at;";

        List<CardMovementDTO> movements = new ArrayList<>();

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, cardId);

            try(ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    CardMovementDTO dto = new CardMovementDTO(
                            resultSet.getLong("id"),
                            toOffsetDateTime(resultSet.getTimestamp("entered_at")),
                            toOffsetDateTime(resultSet.getTimestamp("lefted_at")),
                            resultSet.getLong("card_id"),
                            resultSet.getLong("board_column_id"),
                            resultSet.getString("column_name"),
                            resultSet.getString("column_type")
                    );

                    movements.add(dto);
                }
            }
        }
        return movements;
    }
}