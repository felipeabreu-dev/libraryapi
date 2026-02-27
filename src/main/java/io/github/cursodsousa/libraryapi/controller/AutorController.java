package io.github.cursodsousa.libraryapi.controller;

import io.github.cursodsousa.libraryapi.controller.dto.AutorDTO;
import io.github.cursodsousa.libraryapi.controller.dto.AutorRespostaDTO;
import io.github.cursodsousa.libraryapi.service.AutorService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/autores")
public class AutorController {

    private final AutorService autorService;

    public AutorController(AutorService autorService) {
        this.autorService = autorService;
    }

    @PostMapping
    public ResponseEntity<?> salvarAutor(@RequestBody @Valid AutorDTO autor) {
        var autorEntidade = autor.mapearParaAutor();
        autorService.salvar(autorEntidade);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(autorEntidade.getId())
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("{id}")
    public ResponseEntity<?> obterDetalhes(@PathVariable String id) {
        var idAutor = UUID.fromString(id);
        var autor = autorService.obterPorId(idAutor);

        if(autor.isPresent()) {
            var dto = AutorRespostaDTO.paraDTO(autor.get());

            return ResponseEntity.status(HttpStatus.OK).body(dto);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @DeleteMapping("{id}")
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
    public ResponseEntity<?> pesquisar(@RequestParam(value = "nome", required = false) String nome,
                                       @RequestParam(value = "nacionalidade", required = false) String nacionalidade) {

        var autores = autorService.pesquisa(nome, nacionalidade)
                .stream()
                .map(AutorRespostaDTO::paraDTO)
                .toList();

        return ResponseEntity.status(HttpStatus.OK).body(autores);
    }

    @PutMapping("{id}")
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
