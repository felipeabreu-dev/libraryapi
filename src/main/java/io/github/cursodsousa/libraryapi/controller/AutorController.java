package io.github.cursodsousa.libraryapi.controller;

import io.github.cursodsousa.libraryapi.controller.dto.AutorDTO;
import io.github.cursodsousa.libraryapi.controller.dto.AutorRespostaDTO;
import io.github.cursodsousa.libraryapi.model.Autor;
import io.github.cursodsousa.libraryapi.service.AutorService;
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
    public ResponseEntity<?> salvarAutor(@RequestBody AutorDTO autor) {
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
}
