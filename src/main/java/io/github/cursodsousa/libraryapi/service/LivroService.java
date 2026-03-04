package io.github.cursodsousa.libraryapi.service;

import io.github.cursodsousa.libraryapi.model.Livro;
import io.github.cursodsousa.libraryapi.model.enums.GeneroLivro;
import io.github.cursodsousa.libraryapi.repository.LivroRepository;
import io.github.cursodsousa.libraryapi.validator.LivroValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static io.github.cursodsousa.libraryapi.repository.specs.LivroSpecs.*;

@Service
public class LivroService {

    private final LivroRepository livroRepository;
    private final LivroValidator validator;

    public LivroService(LivroRepository livroRepository, LivroValidator validator) {
        this.livroRepository = livroRepository;
        this.validator = validator;
    }

    public Livro salvar(Livro livro) {
        validator.validar(livro);
        return livroRepository.save(livro);
    }

    public Optional<Livro> obterPorId(UUID id) {
        return livroRepository.findById(id);
    }

    public void deletar(Livro livro) {
        livroRepository.delete(livro);
    }

    public Page<Livro> pesquisa(
            String isbn,
            String nomeAutor,
            String titulo,
            GeneroLivro genero,
            Integer anoPublicacao,
            Integer pagina,
            Integer tamanhoPagina) {

        Specification<Livro> specs = Specification.where((root, query, cb) -> cb.conjunction());

        if(isbn != null){
            // query = query and isbn = :isbn
            specs = specs.and(isbnEqual(isbn));
        }

        if(titulo != null){
            specs = specs.and(tituloLike(titulo));
        }

        if(genero != null){
            specs = specs.and(generoEqual(genero));
        }

        if(anoPublicacao != null){
            specs = specs.and(anoPublicacaoEqual(anoPublicacao));
        }

        if(nomeAutor != null){
            specs = specs.and(nomeAutorLike(nomeAutor));
        }

        Pageable pageRequest = PageRequest.of(pagina, tamanhoPagina);

        return livroRepository.findAll(specs, pageRequest);
    }

    public void atualizar(Livro livro) {
        if(livro.getId() == null) {
            throw new IllegalArgumentException("Para atualizar é necessário que o autor já esteja salvo na base de dados");
        }

        validator.validar(livro);
        livroRepository.save(livro);
    }
}
