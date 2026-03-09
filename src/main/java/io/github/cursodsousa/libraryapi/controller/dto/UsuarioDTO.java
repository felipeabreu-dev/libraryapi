package io.github.cursodsousa.libraryapi.controller.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UsuarioDTO(
        @NotBlank(message = "Campo Obrigatório")
        @Size(min = 3, max = 20, message = "O campo precisa ter entre 3 e 20 caracteres")
        String login,
        @Email(message = "campo obrigatório")
        String email,
        @NotBlank(message = "Campo Obrigatório")
        String senha,
        List<String> roles
) {
}
