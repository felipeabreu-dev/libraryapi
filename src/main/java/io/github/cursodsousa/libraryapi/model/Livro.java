package io.github.cursodsousa.libraryapi.model;

import io.github.cursodsousa.libraryapi.model.enums.GeneroLivro;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "livro")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Livro {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "isbn", length = 20, nullable = false)
    private String isbn;

    @Column(name = "titulo", length = 150, nullable = false)
    private String titulo;

    @Column(name = "data_publicacao", nullable = false)
    private LocalDate dataPublicacao;

    @Column(name = "preco", precision = 18, scale = 2)
    private BigDecimal preco;

    @Column(name = "genero", length = 30, nullable = false)
    @Enumerated(EnumType.STRING)
    private GeneroLivro genero;

    @ManyToOne
    @JoinColumn(name = "id_autor")
    private Autor autor;
}
