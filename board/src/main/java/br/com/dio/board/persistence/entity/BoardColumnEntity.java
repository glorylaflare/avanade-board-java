package br.com.dio.board.persistence.entity;

import lombok.Data;

@Data
public class BoardColumnEntity {

    private Long id;
    private String name;
    private Integer order;
    private BoardColumnTypes tyoes;
}
