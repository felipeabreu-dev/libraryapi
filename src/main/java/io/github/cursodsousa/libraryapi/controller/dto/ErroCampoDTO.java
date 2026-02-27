package io.github.cursodsousa.libraryapi.controller.dto;

public record ErroCampoDTO(
        String campo,
        String mensagem
) {
}
