package io.github.cursodsousa.libraryapi.controller.common;

import io.github.cursodsousa.libraryapi.controller.dto.ErroCampoDTO;
import io.github.cursodsousa.libraryapi.controller.dto.ErroRespostaDTO;
import io.github.cursodsousa.libraryapi.exception.OperacaoNaoPermitidaException;
import io.github.cursodsousa.libraryapi.exception.RegistroDuplicadoException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GoblalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErroRespostaDTO handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        List<ErroCampoDTO> listaDeErros = fieldErrors
                .stream()
                .map(fe -> new ErroCampoDTO(fe.getField(), fe.getDefaultMessage()))
                .toList();

        return new ErroRespostaDTO(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Erro validação",
                listaDeErros
        );
    }

    @ExceptionHandler(RegistroDuplicadoException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErroRespostaDTO handleRegistroDuplicadoException(RegistroDuplicadoException e) {
        return ErroRespostaDTO.conflito(e.getMessage());
    }

    @ExceptionHandler(OperacaoNaoPermitidaException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErroRespostaDTO handleOperacaoNaoPermitidaException(OperacaoNaoPermitidaException e) {
        return ErroRespostaDTO.respostaPadrao(e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErroRespostaDTO handleErrosNaoTratados(RuntimeException e) {
        return new ErroRespostaDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocorreu um erro inesperado, entre em contato com a administração do sistema",
                List.of()
        );
    }

}
