package br.com.dio.board.persistence.dao;

import br.com.dio.board.persistence.entity.CardEntity;
import br.com.dio.dto.CardDetailsDTO;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

import static br.com.dio.board.persistence.converter.OffsetDateTimeConverter.toOffsetDateTime;
import static java.util.Objects.nonNull;

@AllArgsConstructor
public class CardDAO {

    private Connection connection;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        var sql = "INSERT INTO CARDS (titulo, descricao, board_column_id) VALUES (?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)) {
            var i = 1;
            statement.setString(i++, entity.getTitle());
            statement.setString(i++, entity.getDescription());
            statement.setLong(i, entity.getBoardColumn().getId());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
        }
        return entity;
    }

    public void moveToColum(final Long columnId, final Long cardId) throws SQLException {
        var sql = "UPDATE CARDS SET board_column_id = ? WHERE id = ?;";
        try(var statement = connection.prepareStatement(sql)) {
            var i = 1;
            statement.setLong(i++, columnId);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        var sql = "SELECT c.id, c.titulo, c.descricao, b.data_bloqueio, b.motivo_bloqueio, c.board_column_id, bc.nome, (SELECT COUNT(sub_b.id) FROM BLOCKS sub_b WHERE sub_b.card_id = c.id) blocks_amount FROM CARDS c LEFT JOIN BLOCKS b ON c.id = b.card_id AND b.data_desbloqueio IS NULL INNER JOIN BOARDS_COLUMNS bc ON bc.id = c.board_column_id WHERE c.id = ?;";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if(resultSet.next()) {
                var dto = new CardDetailsDTO(
                        resultSet.getLong("c.id"),
                        resultSet.getString("c.titulo"),
                        resultSet.getString("c.descricao"),
                        nonNull(resultSet.getString("b.motivo_bloqueio")),
                        toOffsetDateTime(resultSet.getTimestamp("b.data_bloqueio")),
                        resultSet.getString("b.motivo_bloqueio"),
                        resultSet.getInt("blocks_amount"),
                        resultSet.getLong("c.board_column_id"),
                        resultSet.getString("bc.nome")
                );
                return Optional.of(dto);
            }
        }
        return Optional.empty();
    }

}
