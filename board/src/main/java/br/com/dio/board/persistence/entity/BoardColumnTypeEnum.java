package br.com.dio.board.persistence.entity;

import java.util.stream.Stream;

public enum BoardColumnTypeEnum {

    INITIAL, FINAL, CANCEL, PENDING;

    public static BoardColumnTypeEnum findByNome(final String name) {
        return Stream.of(BoardColumnTypeEnum.values()).filter(b -> b.name().equals(name)).findFirst().orElseThrow();
    }

}
