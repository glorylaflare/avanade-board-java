package br.com.dio.board.persistence.dao;

import br.com.dio.board.persistence.entity.BoardColumnEntity;
import br.com.dio.board.persistence.entity.CardEntity;
import br.com.dio.board.dto.BoardColumnDTO;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.dio.board.persistence.entity.BoardColumnTypeEnum.findByName;
import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class BoardColumnDAO {

    private final Connection connection;

    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {
        var sql = "INSERT INTO BOARDS_COLUMNS (name, sort, type, board_id) " +
                "VALUES (?, ?, ?, ?);";

        try(PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getName());
            statement.setInt(2, entity.getOrder());
            statement.setString(3, entity.getType().name());
            statement.setLong(4, entity.getBoard().getId());

            int affectedRows = statement.executeUpdate();

            if(affectedRows == 0) {
                throw new SQLException("Falha ao inserir no banco de dados, nenhuma linha afetada para o BoardColumn: " + entity.getName());
            }

            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Falha ao obter a chave gerada para o novo BoardColumn.");
                }
            } catch (SQLException e) {
                throw new SQLException("Erro ao tentar inserir BoardColumn com nome: " + entity.getName());
            }
        }
        return entity;
    }

    public List<BoardColumnEntity> findByBoardId(final Long boardId) throws SQLException {
        List<BoardColumnEntity> entities = new ArrayList<>();
        var sql = "SELECT id, name, sort, type " +
                "FROM BOARDS_COLUMNS " +
                "WHERE board_id = ? " +
                "ORDER BY sort;";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);

            try(ResultSet resultSet = statement.executeQuery()) {
                while(resultSet.next()) {
                    BoardColumnEntity entity = new BoardColumnEntity();
                    entity.setId(resultSet.getLong("id"));
                    entity.setName(resultSet.getString("name"));
                    entity.setOrder(resultSet.getInt("sort"));
                    entity.setType(findByName(resultSet.getString("type")));
                    entities.add(entity);
                }
            }
            return entities;
        }
    }

    public List<BoardColumnDTO> findByBoardIdWithDetails(final Long boardId) throws SQLException {
        List<BoardColumnDTO> dtos = new ArrayList<>();
        var sql = "SELECT bc.id, bc.name, bc.type, " +
                "(SELECT COUNT(c.id) FROM CARDS c WHERE c.board_column_id = bc.id) AS cards_amount " +
                "FROM BOARDS_COLUMNS bc " +
                "WHERE board_id = ? " +
                "ORDER BY sort;";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);

            try(ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    BoardColumnDTO dto = new BoardColumnDTO(
                            resultSet.getLong("id"),
                            resultSet.getString("name"),
                            findByName(resultSet.getString("type")),
                            resultSet.getInt("cards_amount")
                    );
                    dtos.add(dto);
                }
            }
        }
        return dtos;
    }

    public Optional<BoardColumnEntity> findById(final Long boardId) throws SQLException {
        var sql = "SELECT bc.name, bc.type, c.id, c.title, c.description " +
                "FROM BOARDS_COLUMNS bc " +
                "LEFT JOIN CARDS c ON c.board_column_id = bc.id " +
                "WHERE bc.id = ?;";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);

            try(ResultSet resultSet = statement.executeQuery()) {
                if(resultSet.next()) {
                    BoardColumnEntity entity = new BoardColumnEntity();
                    entity.setName(resultSet.getString("name"));
                    entity.setType(findByName(resultSet.getString("type")));

                    while (resultSet.next()) {
                        String title = resultSet.getString("title");
                        if (isNull(title)) break;

                        CardEntity card = new CardEntity();
                        card.setId(resultSet.getLong("id"));
                        card.setTitle(title);
                        card.setDescription(resultSet.getString("description"));
                        entity.getCards().add(card);
                    }
                    return Optional.of(entity);
                }
            }
            return Optional.empty();
        }
    }
}
