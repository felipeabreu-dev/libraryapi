package io.github.cursodsousa.libraryapi.controller.dto;

import io.github.cursodsousa.libraryapi.model.Autor;

import java.time.LocalDate;
import java.util.UUID;

public record AutorRespostaDTO(
        UUID id,
        String nome,
        LocalDate dataNascimento,
        String nacionalidade
) {

    public static AutorRespostaDTO paraDTO(Autor autor) {
        return new AutorRespostaDTO(
                autor.getId(),
                autor.getNome(),
                autor.getDataNascimento(),
                autor.getNacionalidade()
        );
    }
}
