package br.com.dio.board.exception;

public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(final String message) {
        super(message);
    }
}
