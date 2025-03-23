package br.com.dio.board.dto;

import java.time.OffsetDateTime;

public record BlockDetailsDTO(
        Long id,
        OffsetDateTime block_date,
        String block_reason,
        OffsetDateTime unblock_date,
        String unblock_reason) {
}
