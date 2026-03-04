package io.github.cursodsousa.libraryapi.validator;

import io.github.cursodsousa.libraryapi.exception.CampoInvalidoException;
import io.github.cursodsousa.libraryapi.exception.RegistroDuplicadoException;
import io.github.cursodsousa.libraryapi.model.Livro;
import io.github.cursodsousa.libraryapi.repository.LivroRepository;
import org.springframework.stereotype.Component;

@Component
public class LivroValidator {

    private final LivroRepository livroRepository;
    private static final Integer ANO_EXIGENCIA_PRECO = 2020;

    public LivroValidator(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    public void validar(Livro livro) {
        if(existeLivroComIsbn(livro)) {
            throw new RegistroDuplicadoException("ISBN já cadastrado");
        }

        if(isPrecoObrigatorioNulo(livro)) {
            throw new CampoInvalidoException("Livros publicados a partir de 2020 devem ter preço cadastrado no sistema", "preco");
        }
    }

    public boolean existeLivroComIsbn(Livro livro) {
        var livroEncontrado = livroRepository.findByIsbn(livro.getIsbn());

        if(livro.getId() == null) {
            return livroEncontrado.isPresent();
        }

        return livroEncontrado
                .map(Livro::getId)
                .stream()
                .anyMatch(id -> !id.equals(livro.getId()));
    }

    public boolean isPrecoObrigatorioNulo(Livro livro) {
        return livro.getPreco() == null &&
                livro.getDataPublicacao().getYear() >= ANO_EXIGENCIA_PRECO;
    }
}
