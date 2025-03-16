package br.com.dio.board.persistence.dao;

import br.com.dio.board.persistence.entity.BoardColumnEntity;
import br.com.dio.board.persistence.entity.CardEntity;
import br.com.dio.dto.BoardColumnDTO;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static br.com.dio.board.persistence.entity.BoardColumnTypeEnum.findByNome;
import static java.util.Objects.isNull;

@RequiredArgsConstructor
public class BoardColumnDAO {

    private final Connection connection;

    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {
        var sql = "INSERT INTO BOARDS_COLUMNS (nome, ordem, tipo, board_id) VALUES (?, ?, ?, ?);";
        try(var statement = connection.prepareStatement(sql)) {
            var i = 1;
            statement.setString(i++, entity.getName());
            statement.setInt(i++, entity.getOrder());
            statement.setString(i++, entity.getType().name());
            statement.setLong(i, entity.getBoard().getId());
            statement.executeUpdate();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
        }
        return entity;
    }

    public List<BoardColumnEntity> findByBoardId(final Long boardId) throws SQLException {
        List<BoardColumnEntity> entities = new ArrayList<>();
        var sql = "SELECT id, nome, ordem, tipo FROM BOARDS_COLUMNS WHERE board_id = ? ORDER BY ordem;";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while(resultSet.next()) {
                var entity = new BoardColumnEntity();
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("nome"));
                entity.setOrder(resultSet.getInt("ordem"));
                entity.setType(findByNome(resultSet.getString("tipo")));
                entities.add(entity);
            }
            return entities;
        }
    }

    public List<BoardColumnDTO> findByBoardIdWithDetails(final Long boardId) throws SQLException {
        List<BoardColumnDTO> dtos = new ArrayList<>();
        var sql = "SELECT bc.id, bc.nome, bc.tipo (SELECT COUNT(c.id) FROM CARDS c WHERE c.board_column_id = bc.id) cards_amount FROM BOARDS_COLUMNS bc WHERE board_id = ? ORDER BY ordem;";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            while (resultSet.next()) {
                var dto = new BoardColumnDTO(
                        resultSet.getLong("bc.id"),
                        resultSet.getString("bc.nome"),
                        findByNome(resultSet.getString("bc.tipo")),
                        resultSet.getInt("cards_amount")
                );
                dtos.add(dto);
            }
        }
        return dtos;
    }

    public Optional<BoardColumnEntity> findById(final Long boardId) throws SQLException {
        var sql = "SELECT bc.nome, bc.tipo, c.id, c.titulo, c.descricao FROM BOARDS_COLUMNS bc LEFT JOIN CARDS c ON c.board_column_id = bc.id WHERE bc.id = ?;";
        try(var statement = connection.prepareStatement(sql)) {
            statement.setLong(1, boardId);
            statement.executeQuery();
            var resultSet = statement.getResultSet();
            if(resultSet.next()) {
                var entity = new BoardColumnEntity();
                entity.setName(resultSet.getString("bc.nome"));
                entity.setType(findByNome(resultSet.getString("bc.tipo")));
                do {
                    var card = new CardEntity();
                    if (isNull(resultSet.getString("c.titulo"))) break;

                    card.setId(resultSet.getLong("c.id"));
                    card.setTitle(resultSet.getString("c.titulo"));
                    card.setDescription(resultSet.getString("c.descricao"));
                    entity.getCards().add(card);
                } while (resultSet.next());

                return Optional.of(entity);
            }
            return Optional.empty();
        }
    }
}
