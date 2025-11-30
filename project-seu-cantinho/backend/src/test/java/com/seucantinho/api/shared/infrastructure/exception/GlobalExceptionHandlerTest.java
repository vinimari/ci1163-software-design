package com.seucantinho.api.shared.infrastructure.exception;

import com.seucantinho.api.shared.domain.exception.BusinessException;
import com.seucantinho.api.shared.domain.exception.DuplicateResourceException;
import com.seucantinho.api.shared.domain.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

    @Mock
    private HttpServletRequest request;

    @Mock
    private MethodArgumentNotValidException validationException;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        when(request.getRequestURI()).thenReturn("/api/test");
    }

    @Test
    @DisplayName("Deve tratar ResourceNotFoundException")
    void deveTratarResourceNotFoundException() {
        // Arrange
        ResourceNotFoundException exception = new ResourceNotFoundException("Cliente não encontrado");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFound(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(404);
        assertThat(response.getBody().getError()).isEqualTo("Not Found");
        assertThat(response.getBody().getMessage()).isEqualTo("Cliente não encontrado");
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Deve tratar BusinessException")
    void deveTratarBusinessException() {
        // Arrange
        BusinessException exception = new BusinessException("Regra de negócio violada");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBusinessException(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Business Rule Violation");
        assertThat(response.getBody().getMessage()).isEqualTo("Regra de negócio violada");
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Deve tratar DuplicateResourceException")
    void deveTratarDuplicateResourceException() {
        // Arrange
        DuplicateResourceException exception = new DuplicateResourceException("Email já cadastrado");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleDuplicateResource(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(409);
        assertThat(response.getBody().getError()).isEqualTo("Conflict");
        assertThat(response.getBody().getMessage()).isEqualTo("Email já cadastrado");
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Deve tratar MethodArgumentNotValidException")
    void deveTratarMethodArgumentNotValidException() {
        // Arrange
        FieldError fieldError1 = new FieldError("cliente", "nome", "não pode ser vazio");
        FieldError fieldError2 = new FieldError("cliente", "email", "formato inválido");
        List<FieldError> fieldErrors = Arrays.asList(fieldError1, fieldError2);
        
        when(validationException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(fieldErrors);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(validationException, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(400);
        assertThat(response.getBody().getError()).isEqualTo("Validation Failed");
        assertThat(response.getBody().getMessage()).isEqualTo("Erro de validação nos campos");
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
        assertThat(response.getBody().getFieldErrors()).hasSize(2);
        assertThat(response.getBody().getFieldErrors().get(0).getField()).isEqualTo("nome");
        assertThat(response.getBody().getFieldErrors().get(0).getMessage()).isEqualTo("não pode ser vazio");
        assertThat(response.getBody().getFieldErrors().get(1).getField()).isEqualTo("email");
        assertThat(response.getBody().getFieldErrors().get(1).getMessage()).isEqualTo("formato inválido");
    }

    @Test
    @DisplayName("Deve tratar Exception genérica")
    void deveTratarExceptionGenerica() {
        // Arrange
        Exception exception = new Exception("Erro inesperado");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleGenericException(exception, request);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo(500);
        assertThat(response.getBody().getError()).isEqualTo("Internal Server Error");
        assertThat(response.getBody().getMessage()).isEqualTo("Ocorreu um erro interno no servidor");
        assertThat(response.getBody().getPath()).isEqualTo("/api/test");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("Deve incluir URI correta no erro")
    void deveIncluirUriCorretaNoErro() {
        // Arrange
        when(request.getRequestURI()).thenReturn("/api/clientes/999");
        ResourceNotFoundException exception = new ResourceNotFoundException("Cliente não encontrado");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFound(exception, request);

        // Assert
        assertThat(response.getBody().getPath()).isEqualTo("/api/clientes/999");
    }

    @Test
    @DisplayName("Deve tratar validação com lista vazia de erros")
    void deveTratarValidacaoComListaVaziaDeErros() {
        // Arrange
        when(validationException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(Arrays.asList());

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleValidationException(validationException, request);

        // Assert
        assertThat(response.getBody().getFieldErrors()).isEmpty();
    }

    @Test
    @DisplayName("Deve manter status HTTP correto para cada tipo de exceção")
    void deveManterStatusHttpCorretoParaCadaTipoDeExcecao() {
        // Arrange
        ResourceNotFoundException notFound = new ResourceNotFoundException("Not found");
        BusinessException business = new BusinessException("Business error");
        DuplicateResourceException duplicate = new DuplicateResourceException("Duplicate");
        Exception generic = new Exception("Generic");

        // Act & Assert
        assertThat(exceptionHandler.handleResourceNotFound(notFound, request).getStatusCode())
                .isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exceptionHandler.handleBusinessException(business, request).getStatusCode())
                .isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exceptionHandler.handleDuplicateResource(duplicate, request).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);
        assertThat(exceptionHandler.handleGenericException(generic, request).getStatusCode())
                .isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Deve preservar mensagem original da exceção")
    void devePreservarMensagemOriginalDaExcecao() {
        // Arrange
        String mensagemOriginal = "Mensagem de erro específica";
        BusinessException exception = new BusinessException(mensagemOriginal);

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleBusinessException(exception, request);

        // Assert
        assertThat(response.getBody().getMessage()).isEqualTo(mensagemOriginal);
    }

    @Test
    @DisplayName("Deve criar timestamp válido para todos os erros")
    void deveCriarTimestampValidoParaTodosOsErros() {
        // Arrange
        ResourceNotFoundException exception = new ResourceNotFoundException("Erro");

        // Act
        ResponseEntity<ErrorResponse> response = exceptionHandler.handleResourceNotFound(exception, request);

        // Assert
        assertThat(response.getBody().getTimestamp()).isNotNull();
        assertThat(response.getBody().getTimestamp()).isBeforeOrEqualTo(java.time.LocalDateTime.now());
    }
}
