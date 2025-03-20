package br.com.dio.board.persistence.dao;

import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static br.com.dio.board.persistence.converter.OffsetDateTimeConverter.toTimestamp;

@AllArgsConstructor
public class BlockDAO {

    private final Connection connection;

    public void block(final String reason, final Long cardId) throws SQLException {
        var sql = "INSERT INTO BLOCKS (block_date, block_reason, card_id) " +
                "VALUES (?, ?, ?);";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, toTimestamp(OffsetDateTime.now()));
            statement.setString(2, reason);
            statement.setLong(3, cardId);
            statement.executeUpdate();
            System.out.println("O card " + cardId + " foi BLOQUEADO com sucesso!");
        }
    }

    public void unblock(final String reason, final Long cardId) throws SQLException {
        var sql = "UPDATE BLOCKS SET unblock_date = ?, unblock_reason = ? " +
                "WHERE card_id = ? " +
                "AND unblock_reason IS NULL;";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, toTimestamp(OffsetDateTime.now()));
            statement.setString(2, reason);
            statement.setLong(3, cardId);
            statement.executeUpdate();
            System.out.println("O card " + cardId + " foi DESBLOQUEADO com sucesso!");
        }
    }
}
