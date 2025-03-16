package br.com.dio.board.persistence.entity;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class BlockEntity {

    private Long id;
    private OffsetDateTime blackedAt;
    private String blockedReason;
    private OffsetDateTime unblockedAt;
    private String unblockedReason;

}
