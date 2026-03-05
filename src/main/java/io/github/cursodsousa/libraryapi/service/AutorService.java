package io.github.cursodsousa.libraryapi.service;

import io.github.cursodsousa.libraryapi.exception.OperacaoNaoPermitidaException;
import io.github.cursodsousa.libraryapi.model.Autor;
import io.github.cursodsousa.libraryapi.repository.AutorRepository;
import io.github.cursodsousa.libraryapi.repository.LivroRepository;
import io.github.cursodsousa.libraryapi.security.SecurityService;
import io.github.cursodsousa.libraryapi.validator.AutorValidator;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AutorService {

    private final AutorRepository autorRepository;
    private final LivroRepository livroRepository;
    private final AutorValidator validator;
    private final SecurityService securityService;

    public AutorService(AutorRepository autorRepository, LivroRepository livroRepository, AutorValidator validator, SecurityService securityService) {
        this.autorRepository = autorRepository;
        this.livroRepository = livroRepository;
        this.validator = validator;
        this.securityService = securityService;
    }

    public Autor salvar(Autor autor) {
        validator.validar(autor);
        autor.setUsuario(securityService.obterUsuarioLogado());
        return autorRepository.save(autor);
    }

    public void atualizar(Autor autor) {
        if (autor.getId() == null) {
            throw new IllegalArgumentException("Para atualizar é necessário que o autor já esteja salvo na base de dados");
        }

        validator.validar(autor);
        autorRepository.save(autor);
    }

    public Optional<Autor> obterPorId(UUID id) {
        return autorRepository.findById(id);
    }

    public void deletar(Autor autor) {
        if (possuiLivro(autor)) {
            throw new OperacaoNaoPermitidaException(
                    "Não é permitido excluir um autor que possui livros cadastrados!");
        }
        autorRepository.delete(autor);
    }

    public List<Autor> pesquisa(String nome, String nacionalidade) {
        if (nome != null && nacionalidade != null) {
            return autorRepository.findByNomeAndNacionalidade(nome, nacionalidade);
        }

        if (nome != null) {
            return autorRepository.findByNome(nome);
        }

        if (nacionalidade != null) {
            return autorRepository.findByNacionalidade(nacionalidade);
        }

        return autorRepository.findAll();
    }

    public List<Autor> pesquisaByExample(String nome, String nacionalidade) {
        Autor autor = new Autor();
        autor.setNome(nome);
        autor.setNacionalidade(nacionalidade);

        ExampleMatcher exampleMatcher = ExampleMatcher
                .matching()
                .withIgnoreNullValues()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Autor> autorExample = Example.of(autor, exampleMatcher);

        return autorRepository.findAll(autorExample);
    }

    boolean possuiLivro(Autor autor) {
        return livroRepository.existsByAutor(autor);
    }
}
