package io.github.cursodsousa.libraryapi.controller;

import io.github.cursodsousa.libraryapi.controller.common.GenericController;
import io.github.cursodsousa.libraryapi.controller.dto.CadastroLivroDTO;
import io.github.cursodsousa.libraryapi.controller.dto.ResultadoPesquisaLivroDTO;
import io.github.cursodsousa.libraryapi.controller.mappers.LivroMapper;
import io.github.cursodsousa.libraryapi.model.Livro;
import io.github.cursodsousa.libraryapi.model.enums.GeneroLivro;
import io.github.cursodsousa.libraryapi.service.LivroService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

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
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    public ResponseEntity<?> salvar(@RequestBody @Valid CadastroLivroDTO dto) {
        Livro livro = livroMapper.paraEntidade(dto);
        livroService.salvar(livro);

        var location = gerarHeaderLocation(livro.getId());

        return ResponseEntity.status(HttpStatus.CREATED).location(location).build();
    }


    @GetMapping("{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    public ResponseEntity<?> obterDetalhes(@PathVariable String id) {
        return livroService.obterPorId(UUID.fromString(id))
                .map(livro -> {
                    var dto = livroMapper.paraDTO(livro);
                    return ResponseEntity.status(HttpStatus.OK).body(dto);
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    public ResponseEntity<?> deletar(@PathVariable String id) {
        return livroService.obterPorId(UUID.fromString(id))
                .map(livro -> {
                    livroService.deletar(livro);
                    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    public ResponseEntity<?> pesquisa(
            @RequestParam(value = "isbn", required = false) String isbn,
            @RequestParam(value = "nomeAutor",required = false) String nomeAutor,
            @RequestParam(value = "titulo", required = false) String titulo,
            @RequestParam(value = "genero", required = false) GeneroLivro genero,
            @RequestParam(value = "anoPublicacao", required = false) Integer anoPublicacao,
            @RequestParam(value = "pagina", defaultValue = "0") Integer pagina,
            @RequestParam(value = "tamanho-pagina", defaultValue = "10") Integer tamanhoPagina
            ) {
        var resultado = livroService
                .pesquisa(isbn, nomeAutor, titulo, genero, anoPublicacao, pagina, tamanhoPagina)
                .map(livroMapper::paraDTO);

        return ResponseEntity.status(HttpStatus.OK).body(resultado);
    }

    @PutMapping("{id}")
    @PreAuthorize("hasAnyRole('OPERADOR', 'GERENTE')")
    public ResponseEntity<?> atualizar(@PathVariable("id") String id,
                                       @RequestBody CadastroLivroDTO dto) {
        return livroService.obterPorId(UUID.fromString(id))
                .map(livro -> {
                    var entidadeAuxiliar = livroMapper.paraEntidade(dto);
                    livro.setIsbn(entidadeAuxiliar.getIsbn());
                    livro.setTitulo(entidadeAuxiliar.getTitulo());
                    livro.setDataPublicacao(entidadeAuxiliar.getDataPublicacao());
                    livro.setGenero(entidadeAuxiliar.getGenero());
                    livro.setPreco(entidadeAuxiliar.getPreco());
                    livro.setAutor(entidadeAuxiliar.getAutor());

                    livroService.atualizar(livro);

                    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
                }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }
}
