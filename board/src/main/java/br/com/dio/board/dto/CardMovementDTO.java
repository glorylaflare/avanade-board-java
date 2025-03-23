package br.com.dio.board.dto;

import java.time.OffsetDateTime;

public record CardMovementDTO(
        Long id,
        OffsetDateTime entered_at,
        OffsetDateTime lefted_at,
        Long card_id,
        Long board_column_id,
        String column_name,
        String column_type) {
}
