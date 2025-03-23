package br.com.dio.board.persistence.dao;

import br.com.dio.board.dto.CardDetailsDTO;
import br.com.dio.board.persistence.entity.CardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Optional;

import static br.com.dio.board.persistence.converter.OffsetDateTimeConverter.toOffsetDateTime;
import static br.com.dio.board.persistence.converter.OffsetDateTimeConverter.toTimestamp;
import static java.util.Objects.nonNull;

@AllArgsConstructor
public class CardDAO {

    private Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        var sql = "INSERT INTO CARDS (title, description, board_column_id) " +
                "VALUES (?, ?, ?);";
        var insertMovementSql = "INSERT INTO CARD_MOVEMENT_HISTORY (card_id, board_column_id, entered_at) " +
                "VALUES (?, ?, ?);";

        try {
            connection.setAutoCommit(false);

            try(PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

                statement.setString(1, entity.getTitle());
                statement.setString(2, entity.getDescription());
                statement.setLong(3, entity.getBoardColumn().getId());
                statement.executeUpdate();

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        entity.setId(generatedKeys.getLong(1));

                        try(PreparedStatement insertMovementStatement = connection.prepareStatement(insertMovementSql)) {
                            insertMovementStatement.setLong(1, entity.getId());
                            insertMovementStatement.setLong(2, entity.getBoardColumn().getId());
                            insertMovementStatement.setTimestamp(3, toTimestamp(OffsetDateTime.now()));
                            insertMovementStatement.executeUpdate();
                        }
                    } else {
                        throw new SQLException("Falha ao obter a chave gerada para o novo Card.");
                    }
                }
            }
            connection.commit();
            System.out.println("O Card foi criado com sucesso!");
        } catch (SQLException e) {
            connection.rollback();
            throw new SQLException("Erro ao criar o card.", e);
        } finally {
            connection.setAutoCommit(false);
        }
        return entity;
    }

    public void moveToColum(final Long columnId, final Long cardId) throws SQLException {
        var updateCardSql = "UPDATE CARDS SET board_column_id = ? " +
                "WHERE id = ?;";
        var updateMovementSql = "UPDATE CARd_MOVEMENT_HISTORY SET lefted_at = ? " +
                "WHERE card_id = ? " +
                "AND lefted_at IS NULL";
        var insertMovementSql = "INSERT INTO CARD_MOVEMENT_HISTORY (card_id, board_column_id, entered_at) " +
                "VALUES (?, ?, ?);";

        try(PreparedStatement updateCardStatement = connection.prepareStatement(updateCardSql);
            PreparedStatement updateMovementStatement = connection.prepareStatement(updateMovementSql);
            PreparedStatement insertMovementStatement = connection.prepareStatement(insertMovementSql)) {

            connection.setAutoCommit(false);

            updateCardStatement.setLong(1, columnId);
            updateCardStatement.setLong(2, cardId);
            updateCardStatement.executeUpdate();

            updateMovementStatement.setTimestamp(1, toTimestamp(OffsetDateTime.now()));
            updateMovementStatement.setLong(2, cardId);
            updateMovementStatement.executeUpdate();

            insertMovementStatement.setLong(1, cardId);
            insertMovementStatement.setLong(2, columnId);
            insertMovementStatement.setTimestamp(3, toTimestamp(OffsetDateTime.now()));
            insertMovementStatement.executeUpdate();

            connection.commit();

            System.out.println("O card de id [" + cardId + "] foi movido e sua movimentação foi registrada com sucesso!");
        } catch (SQLException e) {
            connection.rollback();
            throw new SQLException("Erro ao mover o cartão para a coluna.", e);
        } finally {
            connection.setAutoCommit(false);
        }
    }

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        var sql = "SELECT c.id, c.title, c.description, b.block_date, b.block_reason, c.board_column_id, bc.name, " +
                "(SELECT COUNT(sub_b.id) FROM BLOCKS sub_b WHERE sub_b.card_id = c.id) blocks_amount FROM CARDS c " +
                "LEFT JOIN BLOCKS b ON c.id = b.card_id AND b.unblock_date IS NULL " +
                "INNER JOIN BOARDS_COLUMNS bc ON bc.id = c.board_column_id " +
                "WHERE c.id = ?;";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try(ResultSet resultSet = statement.executeQuery()) {
                if(resultSet.next()) {
                    CardDetailsDTO dto = new CardDetailsDTO(
                            resultSet.getLong("id"),
                            resultSet.getString("title"),
                            resultSet.getString("description"),
                            nonNull(resultSet.getString("block_reason")),
                            toOffsetDateTime(resultSet.getTimestamp("block_date")),
                            resultSet.getString("block_reason"),
                            resultSet.getInt("blocks_amount"),
                            resultSet.getLong("board_column_id"),
                            resultSet.getString("name")
                    );
                    return Optional.of(dto);
                }
            }
        }
        return Optional.empty();
    }
}
