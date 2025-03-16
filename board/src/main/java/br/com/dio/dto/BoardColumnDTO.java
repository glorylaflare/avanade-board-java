package br.com.dio.dto;

import br.com.dio.board.persistence.entity.BoardColumnTypeEnum;

public record BoardColumnDTO(Long id, String name, BoardColumnTypeEnum types, int cardsAmount) {}
