package br.com.dio.board.service;

import br.com.dio.board.dto.CardDetailsDTO;
import br.com.dio.board.persistence.dao.BlockDAO;
import br.com.dio.board.persistence.dao.CardDAO;
import br.com.dio.board.persistence.entity.BoardColumnTypeEnum;
import br.com.dio.board.persistence.entity.CardEntity;
import br.com.dio.board.dto.BoardColumnInfoDTO;
import br.com.dio.board.exception.CardBlockedException;
import br.com.dio.board.exception.CardFinishedException;
import br.com.dio.board.exception.EntityNotFoundException;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
public class CardService {

    private final Connection connection;

    public CardEntity create(final CardEntity entity) throws SQLException {
        final CardDAO dao = new CardDAO(connection);

        try {
            dao.insert(entity);
            connection.commit();
            return entity;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                e.addSuppressed(rollbackException);
            }
            throw e;
        }
    }

    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        final CardDAO dao = new CardDAO(connection);

        try {
            final CardDetailsDTO dto = dao.findById(cardId).orElseThrow(
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado.".formatted(cardId)));

            if(dto.blocked()) {
                throw new CardBlockedException("O card %s está bloqueado, é necesário desbloquea-lo para mover".formatted(cardId));
            }

            final BoardColumnInfoDTO currentColumn = findCurrentColumn(dto, boardColumnsInfo);
            final BoardColumnInfoDTO nextColumn = findColumn(currentColumn, boardColumnsInfo);

            dao.moveToColum(nextColumn.id(), cardId);
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                e.addSuppressed(rollbackException);
            }
            throw e;
        }
    }

    public void cancel(final Long cardId, final Long cancelColumnId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            CardDAO dao = new CardDAO(connection);
            Optional<CardDetailsDTO> optional = dao.findById(cardId);
            CardDetailsDTO dto = optional.orElseThrow(
                    () -> new EntityNotFoundException("O card de id %s não foi encontrado.".formatted(cardId)));

            if(dto.blocked()) {
                throw new CardBlockedException("O card %s está bloqueado, é necesário desbloquea-lo para mover".formatted(cardId));
            }

            final BoardColumnInfoDTO currentColumn = findCurrentColumn(dto, boardColumnsInfo);
            findColumn(currentColumn, boardColumnsInfo);

            dao.moveToColum(cancelColumnId, cardId);
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                e.addSuppressed(rollbackException);
            }
            throw e;
        }
    }

    public void block(final Long id, final String reason, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        try {
            CardDetailsDTO dto = findCardOrThrow(id);

            if(dto.blocked()) {
                throw new CardBlockedException("O card %s está bloqueado".formatted(id));
            }

            final BoardColumnInfoDTO currentColumn = findCurrentColumn(dto, boardColumnsInfo);

            if(currentColumn.type().equals(BoardColumnTypeEnum.FINAL) || currentColumn.type().equals(BoardColumnTypeEnum.CANCEL)) {
                throw new IllegalStateException("O card está em uma coluna do tipo %s e não pode ser bloqueado.".formatted(currentColumn.type()));
            }

            BlockDAO blockDAO = new BlockDAO(connection);
            blockDAO.block(reason, id);
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                e.addSuppressed(rollbackException);
            }
            throw e;
        }
    }

    public void unblock(final Long id, final String reason) throws SQLException {
        try {
            CardDetailsDTO dto = findCardOrThrow(id);

            if(!dto.blocked()) {
                throw new CardBlockedException("O card %s não está bloqueado.".formatted(id));
            }

            BlockDAO blockDAO = new BlockDAO(connection);
            blockDAO.unblock(reason, id);
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackException) {
                e.addSuppressed(rollbackException);
            }
            throw e;
        }
    }

    public CardDetailsDTO findCardOrThrow(final Long id) throws SQLException {
        CardDAO dao = new CardDAO(connection);
        return dao.findById(id).orElseThrow(
                () -> new EntityNotFoundException("O card de id %s não foi encontrado.".formatted(id))
        );
    }

    private BoardColumnInfoDTO findCurrentColumn(final CardDetailsDTO dto, final List<BoardColumnInfoDTO> boardColumnsInfo) {
        return boardColumnsInfo.stream()
                .filter(bc -> Objects.equals(bc.id(), dto.columnId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board."));
    }

    private BoardColumnInfoDTO findColumn(final BoardColumnInfoDTO currentColumn, final List<BoardColumnInfoDTO> boardColumnsInfo) {
        if(currentColumn.type().equals(BoardColumnTypeEnum.FINAL)) {
            throw new CardFinishedException("O card já foi finalizado.");
        }

        return boardColumnsInfo.stream()
                .filter(bc -> bc.order() == currentColumn.order() + 1)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("O card está cancelado."));
    }
}
