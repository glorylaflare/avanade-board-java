package br.com.dio.board.persistence.dao;

import br.com.dio.board.persistence.entity.CardEntity;
import br.com.dio.board.dto.CardDetailsDTO;
import lombok.AllArgsConstructor;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

import static br.com.dio.board.persistence.converter.OffsetDateTimeConverter.toOffsetDateTime;
import static java.util.Objects.nonNull;

@AllArgsConstructor
public class CardDAO {

    private Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        var sql = "INSERT INTO CARDS (title, description, board_column_id) " +
                "VALUES (?, ?, ?);";

        try(PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getTitle());
            statement.setString(2, entity.getDescription());
            statement.setLong(3, entity.getBoardColumn().getId());
            int affectedRows = statement.executeUpdate();

            if(affectedRows == 0) {
                throw new SQLException("Falha ao inserir no banco de dados, nenhuma linha afetada para o CardBoard: " + entity.getId());
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                    System.out.println("O CardBoard foi criado com sucesso!");
                } else {
                    throw new SQLException("Falha ao obter a chave gerada para o novo CardBoard.");
                }
            }
        }
        return entity;
    }

    public void moveToColum(final Long columnId, final Long cardId) throws SQLException {
        var sql = "UPDATE CARDS SET board_column_id = ?, date_moved = ? " +
                "WHERE id = ?;";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, columnId);
            statement.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            statement.setLong(3, cardId);
            statement.executeUpdate();
            System.out.println("O CardBoard de id " + cardId + " foi movido com sucesso!");
        } catch (SQLException e) {
            throw new SQLException("Erro ao mover o cartão para a coluna.", e);
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
