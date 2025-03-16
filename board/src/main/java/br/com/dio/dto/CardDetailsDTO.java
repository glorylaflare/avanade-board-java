package br.com.dio.dto;

import java.time.OffsetDateTime;

public record CardDetailsDTO(Long id,
                             String title,
                             String description,
                             Boolean blocked,
                             OffsetDateTime blockedAt,
                             String blockReason,
                             Integer blocksAmount,
                             Long columnId,
                             String columnName) {
}
