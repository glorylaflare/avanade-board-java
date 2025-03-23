package br.com.dio.board.persistence.entity;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class CardMovementEntity {

    private Long id;
    private Timestamp enteredAt;
    private Timestamp leftedAt;
    private CardEntity cardId = new CardEntity();
    private BoardColumnEntity columnId = new BoardColumnEntity();
}
