package br.com.dio.board.service;

import br.com.dio.board.dto.BoardInfoDTO;
import br.com.dio.board.exception.EntityNotFoundException;
import br.com.dio.board.persistence.dao.BoardColumnDAO;
import br.com.dio.board.persistence.dao.BoardDAO;
import br.com.dio.board.persistence.entity.BoardEntity;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;

@AllArgsConstructor
public class BoardService {

    private final Connection connection;

    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        BoardDAO dao = new BoardDAO(connection);
        BoardColumnDAO boardColumnDAO = new BoardColumnDAO(connection);

        try {
            dao.insert(entity);
            var columns = entity.getBoardColumns().stream().map(c -> {
                c.setBoard(entity);
                return c;
            }).toList();
            for(var column : columns) {
                boardColumnDAO.insert(column);
            }
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                e.addSuppressed(rollbackException);
            }
            throw e;
        }
        return entity;
    }

    public boolean delete(final Long id) throws SQLException {
        BoardDAO dao = new BoardDAO(connection);

        try {
            if(!dao.exists(id)) return false;

            dao.delete(id);
            connection.commit();
            return true;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                e.addSuppressed(rollbackException);
            }
            throw e;
        }
    }

    public BoardInfoDTO getInfo(final Long id) throws SQLException {
        BoardDAO dao = new BoardDAO(connection);
        return dao.findById(id)
                .map(e -> new BoardInfoDTO(e.getId(), e.getName()))
                .orElseThrow(() -> new EntityNotFoundException("O board de id %s não foi encontrado.".formatted(id)));
    }
}
