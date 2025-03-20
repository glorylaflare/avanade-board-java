package br.com.dio.board.persistence.dao;

import br.com.dio.board.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class BoardDAO {

    private final Connection connection;

    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        var sql = "INSERT INTO BOARDS (name) " +
                "VALUES (?);";

        //Utilizando o RETURN_GENERATED_KEYS para que o valor do id seja recuperado após a criação do board.
        try(PreparedStatement statement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getName());
            int affectedRows = statement.executeUpdate();

            if(affectedRows == 0) {
                throw new SQLException("Falha ao inserir no banco de dados, nenhuma linha afetada.");
            }

            //Criando o board através do id gerado, em caso de erro, lança uma exceção
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                    System.out.println("O Board foi criado com sucesso!");
                } else {
                    throw new SQLException("Falha ao obter a chave gerada para o novo Board.");
                }
            }
        }
        return entity;
    }

    public List<BoardEntity> findAll() throws SQLException {
        List<BoardEntity> boards = new ArrayList<>();
        var sql = "SELECT id, name FROM BOARDS;";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            try(ResultSet resultSet = statement.executeQuery()) {
               while(resultSet.next()) {
                   BoardEntity entity = new BoardEntity();
                   entity.setId(resultSet.getLong("id"));
                   entity.setName(resultSet.getString("name"));
                   boards.add(entity);
               }
            }
        }
        return boards;
    }

    public void delete(final Long id) throws SQLException {
        var sql = "DELETE FROM BOARDS " +
                "WHERE id = ?;";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao tentar deletar o registro: " + e.getMessage());
            throw e;
        }
    }

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var sql = "SELECT id, name FROM BOARDS " +
                "WHERE id = ?;";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            //Faz a busca pelo id e retorna o que foi encontrado através do resultSet
            try(ResultSet resultSet = statement.executeQuery()) {
                if(resultSet.next()) {
                    BoardEntity entity = new BoardEntity();
                    entity.setId(resultSet.getLong("id"));
                    entity.setName(resultSet.getString("name"));
                    return Optional.of(entity);
                }
            }
        }
        return Optional.empty();
    }

    public boolean exists(final Long id) throws SQLException {
        var sql = "SELECT 1 FROM BOARDS " +
                "WHERE id = ?;";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try(ResultSet resultSet = statement.executeQuery()){
                return resultSet.next();
            }
        }
    }
}
