package br.com.dio.dto;

import br.com.dio.board.persistence.entity.BoardColumnTypeEnum;

public record BoardColumnInfoDTO(Long id, Integer order, BoardColumnTypeEnum type) {
}
