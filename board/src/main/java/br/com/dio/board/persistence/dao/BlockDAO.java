package br.com.dio.board.persistence.dao;

import br.com.dio.board.dto.BlockDetailsDTO;
import br.com.dio.board.persistence.entity.BlockEntity;
import lombok.AllArgsConstructor;
import org.springframework.cglib.core.Block;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static br.com.dio.board.persistence.converter.OffsetDateTimeConverter.toOffsetDateTime;
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

    public List<BlockDetailsDTO> getReportByCardId(final Long cardId) throws SQLException {
        var sql = "SELECT * FROM BLOCKS " +
                "WHERE card_id = ?;";

        List<BlockDetailsDTO> details = new ArrayList<>();

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, cardId);
            try(ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    BlockDetailsDTO dto = new BlockDetailsDTO(
                            resultSet.getLong("id"),
                            toOffsetDateTime(resultSet.getTimestamp("block_date")),
                            resultSet.getString("block_reason"),
                            toOffsetDateTime(resultSet.getTimestamp("unblock_date")),
                            resultSet.getString("unblock_reason")
                    );

                    details.add(dto);
                }
            }
        }
        return details;
    }
}
