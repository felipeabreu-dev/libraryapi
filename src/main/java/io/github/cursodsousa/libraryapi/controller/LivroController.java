package io.github.cursodsousa.libraryapi.controller;

import io.github.cursodsousa.libraryapi.controller.common.GenericController;
import io.github.cursodsousa.libraryapi.controller.dto.CadastroLivroDTO;
import io.github.cursodsousa.libraryapi.controller.dto.ErroRespostaDTO;
import io.github.cursodsousa.libraryapi.controller.mappers.LivroMapper;
import io.github.cursodsousa.libraryapi.exception.RegistroDuplicadoException;
import io.github.cursodsousa.libraryapi.model.Livro;
import io.github.cursodsousa.libraryapi.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/livros")
public class LivroController implements GenericController {

    private final LivroService livroService;
    private final LivroMapper livroMapper;

    public LivroController(LivroService livroService, LivroMapper livroMapper) {
        this.livroService = livroService;
        this.livroMapper = livroMapper;
    }

    @PostMapping
    public ResponseEntity<?> salvar(@RequestBody @Valid CadastroLivroDTO dto) {
        try {
            Livro livro = livroMapper.paraEntidade(dto);
            livroService.salvar(livro);

            var location = gerarHeaderLocation(livro.getId());

            return ResponseEntity.status(HttpStatus.CREATED).location(location).build();
        } catch (RegistroDuplicadoException e) {
            var erroDTO = ErroRespostaDTO.conflito(e.getMessage());
            return ResponseEntity.status(erroDTO.status()).body(erroDTO);
        }
    }
}
