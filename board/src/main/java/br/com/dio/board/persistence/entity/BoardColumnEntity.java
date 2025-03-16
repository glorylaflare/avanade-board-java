package br.com.dio.board.persistence.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BoardColumnEntity {

    private Long id;
    private String name;
    private Integer order;
    private BoardColumnTypeEnum type;
    private BoardEntity board = new BoardEntity();
    private List<CardEntity> cards = new ArrayList<>();
}
