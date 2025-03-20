package br.com.dio.board.service;

import br.com.dio.board.persistence.dao.CardDAO;
import br.com.dio.board.dto.CardDetailsDTO;
import lombok.AllArgsConstructor;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@AllArgsConstructor
public class CardQueryService {

    private final Connection connection;

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        CardDAO dao = new CardDAO(connection);
        return dao.findById(id);
    }

}
