package io.github.cursodsousa.libraryapi.controller;

import io.github.cursodsousa.libraryapi.controller.common.GenericController;
import io.github.cursodsousa.libraryapi.controller.dto.AutorDTO;
import io.github.cursodsousa.libraryapi.controller.mappers.AutorMapper;
import io.github.cursodsousa.libraryapi.service.AutorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/autores")
public class AutorController implements GenericController {

    private final AutorService autorService;
    private final AutorMapper autorMapper;

    public AutorController(AutorService autorService, AutorMapper autorMapper) {
        this.autorService = autorService;
        this.autorMapper = autorMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<?> salvarAutor(@RequestBody @Valid AutorDTO autor) {
        var autorEntidade = autorMapper.dtoParaEntidade(autor);
        autorService.salvar(autorEntidade);

        var location = gerarHeaderLocation(autorEntidade.getId());

        return ResponseEntity.status(HttpStatus.CREATED).location(location).build();
    }

    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    public ResponseEntity<?> obterDetalhes(@PathVariable String id) {
        var idAutor = UUID.fromString(id);
        var autor = autorService.obterPorId(idAutor);

        if(autor.isPresent()) {
            var dto = autorMapper.entidadeParaDTO(autor.get());

            return ResponseEntity.status(HttpStatus.OK).body(dto);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasRole('GERENTE')")
    public ResponseEntity<?> deletar(@PathVariable String id) {
        var idAutor = UUID.fromString(id);
        var autor = autorService.obterPorId(idAutor);

        if(autor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        autorService.deletar(autor.get());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    public ResponseEntity<?> pesquisar(@RequestParam(value = "nome", required = false) String nome,
                                       @RequestParam(value = "nacionalidade", required = false) String nacionalidade) {

        var autores = autorService.pesquisaByExample(nome, nacionalidade)
                .stream()
                .map(autorMapper::entidadeParaDTO)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(autores);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> atualizar(
            @PathVariable String id,
            @RequestBody AutorDTO autorDTO) {

        var idAutor = UUID.fromString(id);
        var autor = autorService.obterPorId(idAutor);

        if(autor.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        autor.get().setNome(autorDTO.nome());
        autor.get().setDataNascimento(autorDTO.dataNascimento());
        autor.get().setNacionalidade(autorDTO.nacionalidade());

        autorService.atualizar(autor.get());

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
