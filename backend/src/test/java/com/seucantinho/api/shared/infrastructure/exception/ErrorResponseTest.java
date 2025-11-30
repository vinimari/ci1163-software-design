package com.seucantinho.api.shared.infrastructure.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes do ErrorResponse")
class ErrorResponseTest {

    @Test
    @DisplayName("Deve criar ErrorResponse com todos os campos")
    void deveCriarErrorResponseComTodosCampos() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now();
        Integer status = 404;
        String error = "Not Found";
        String message = "Recurso não encontrado";
        String path = "/api/clientes/999";
        List<ErrorResponse.FieldError> fieldErrors = Arrays.asList(
                new ErrorResponse.FieldError("nome", "não pode ser vazio")
        );

        // Act
        ErrorResponse errorResponse = new ErrorResponse(timestamp, status, error, message, path, fieldErrors);

        // Assert
        assertThat(errorResponse.getTimestamp()).isEqualTo(timestamp);
        assertThat(errorResponse.getStatus()).isEqualTo(status);
        assertThat(errorResponse.getError()).isEqualTo(error);
        assertThat(errorResponse.getMessage()).isEqualTo(message);
        assertThat(errorResponse.getPath()).isEqualTo(path);
        assertThat(errorResponse.getFieldErrors()).hasSize(1);
        assertThat(errorResponse.getFieldErrors().get(0).getField()).isEqualTo("nome");
        assertThat(errorResponse.getFieldErrors().get(0).getMessage()).isEqualTo("não pode ser vazio");
    }

    @Test
    @DisplayName("Deve criar ErrorResponse sem fieldErrors usando construtor simplificado")
    void deveCriarErrorResponseSemFieldErrors() {
        // Arrange
        LocalDateTime timestamp = LocalDateTime.now();
        Integer status = 400;
        String error = "Bad Request";
        String message = "Erro de negócio";
        String path = "/api/reservas";

        // Act
        ErrorResponse errorResponse = new ErrorResponse(timestamp, status, error, message, path);

        // Assert
        assertThat(errorResponse.getTimestamp()).isEqualTo(timestamp);
        assertThat(errorResponse.getStatus()).isEqualTo(status);
        assertThat(errorResponse.getError()).isEqualTo(error);
        assertThat(errorResponse.getMessage()).isEqualTo(message);
        assertThat(errorResponse.getPath()).isEqualTo(path);
        assertThat(errorResponse.getFieldErrors()).isNull();
    }

    @Test
    @DisplayName("Deve criar ErrorResponse usando construtor vazio")
    void deveCriarErrorResponseUsandoConstrutorVazio() {
        // Act
        ErrorResponse errorResponse = new ErrorResponse();

        // Assert
        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getTimestamp()).isNull();
        assertThat(errorResponse.getStatus()).isNull();
        assertThat(errorResponse.getError()).isNull();
        assertThat(errorResponse.getMessage()).isNull();
        assertThat(errorResponse.getPath()).isNull();
        assertThat(errorResponse.getFieldErrors()).isNull();
    }

    @Test
    @DisplayName("Deve modificar campos usando setters")
    void deveModificarCamposUsandoSetters() {
        // Arrange
        ErrorResponse errorResponse = new ErrorResponse();
        LocalDateTime timestamp = LocalDateTime.now();

        // Act
        errorResponse.setTimestamp(timestamp);
        errorResponse.setStatus(500);
        errorResponse.setError("Internal Server Error");
        errorResponse.setMessage("Erro interno");
        errorResponse.setPath("/api/test");
        errorResponse.setFieldErrors(Arrays.asList());

        // Assert
        assertThat(errorResponse.getTimestamp()).isEqualTo(timestamp);
        assertThat(errorResponse.getStatus()).isEqualTo(500);
        assertThat(errorResponse.getError()).isEqualTo("Internal Server Error");
        assertThat(errorResponse.getMessage()).isEqualTo("Erro interno");
        assertThat(errorResponse.getPath()).isEqualTo("/api/test");
        assertThat(errorResponse.getFieldErrors()).isEmpty();
    }

    @Test
    @DisplayName("Deve criar FieldError com todos os campos")
    void deveCriarFieldErrorComTodosCampos() {
        // Arrange & Act
        ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError("email", "formato inválido");

        // Assert
        assertThat(fieldError.getField()).isEqualTo("email");
        assertThat(fieldError.getMessage()).isEqualTo("formato inválido");
    }

    @Test
    @DisplayName("Deve criar FieldError usando construtor vazio")
    void deveCriarFieldErrorUsandoConstrutorVazio() {
        // Act
        ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError();

        // Assert
        assertThat(fieldError).isNotNull();
        assertThat(fieldError.getField()).isNull();
        assertThat(fieldError.getMessage()).isNull();
    }

    @Test
    @DisplayName("Deve modificar FieldError usando setters")
    void deveModificarFieldErrorUsandoSetters() {
        // Arrange
        ErrorResponse.FieldError fieldError = new ErrorResponse.FieldError();

        // Act
        fieldError.setField("cpf");
        fieldError.setMessage("CPF inválido");

        // Assert
        assertThat(fieldError.getField()).isEqualTo("cpf");
        assertThat(fieldError.getMessage()).isEqualTo("CPF inválido");
    }

    @Test
    @DisplayName("Deve criar ErrorResponse com múltiplos FieldErrors")
    void deveCriarErrorResponseComMultiplosFieldErrors() {
        // Arrange
        List<ErrorResponse.FieldError> fieldErrors = Arrays.asList(
                new ErrorResponse.FieldError("nome", "não pode ser vazio"),
                new ErrorResponse.FieldError("email", "formato inválido"),
                new ErrorResponse.FieldError("cpf", "CPF inválido")
        );

        // Act
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(), 400, "Bad Request", 
                "Erro de validação", "/api/clientes", fieldErrors
        );

        // Assert
        assertThat(errorResponse.getFieldErrors()).hasSize(3);
        assertThat(errorResponse.getFieldErrors().get(0).getField()).isEqualTo("nome");
        assertThat(errorResponse.getFieldErrors().get(1).getField()).isEqualTo("email");
        assertThat(errorResponse.getFieldErrors().get(2).getField()).isEqualTo("cpf");
    }

    @Test
    @DisplayName("Deve aceitar fieldErrors vazio")
    void deveAceitarFieldErrorsVazio() {
        // Act
        ErrorResponse errorResponse = new ErrorResponse(
                LocalDateTime.now(), 400, "Bad Request",
                "Erro", "/api/test", Arrays.asList()
        );

        // Assert
        assertThat(errorResponse.getFieldErrors()).isEmpty();
    }

    @Test
    @DisplayName("Deve manter timestamp consistente")
    void deveManterTimestampConsistente() {
        // Arrange
        LocalDateTime before = LocalDateTime.now();
        
        // Act
        ErrorResponse errorResponse = new ErrorResponse(
                before, 404, "Not Found", "Não encontrado", "/api/test"
        );

        // Assert
        assertThat(errorResponse.getTimestamp()).isEqualTo(before);
    }
}
