package com.seucantinho.api.shared.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

@DisplayName("Testes do OpenApiConfig")
class OpenApiConfigTest {

    private final OpenApiConfig openApiConfig = new OpenApiConfig();

    @Test
    @DisplayName("Deve criar bean OpenAPI")
    void deveCriarBeanOpenAPI() {
        // Act
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        // Assert
        assertThat(openAPI).isNotNull();
    }

    @Test
    @DisplayName("Deve configurar informações da API")
    void deveConfigurarInformacoesDaApi() {
        // Act
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        Info info = openAPI.getInfo();

        // Assert
        assertThat(info).isNotNull();
        assertThat(info.getTitle()).isEqualTo("API Seu Cantinho");
        assertThat(info.getVersion()).isEqualTo("1.0.0");
        assertThat(info.getDescription()).isEqualTo("Sistema de gerenciamento de reservas de espaços para eventos");
    }

    @Test
    @DisplayName("Deve configurar contato da API")
    void deveConfigurarContatoDaApi() {
        // Act
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        Contact contact = openAPI.getInfo().getContact();

        // Assert
        assertThat(contact).isNotNull();
        assertThat(contact.getName()).isEqualTo("Seu Cantinho");
        assertThat(contact.getEmail()).isEqualTo("contato@seucantinho.com");
    }

    @Test
    @DisplayName("Deve configurar licença da API")
    void deveConfigurarLicencaDaApi() {
        // Act
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        License license = openAPI.getInfo().getLicense();

        // Assert
        assertThat(license).isNotNull();
        assertThat(license.getName()).isEqualTo("MIT License");
        assertThat(license.getUrl()).isEqualTo("https://opensource.org/licenses/MIT");
    }

    @Test
    @DisplayName("Deve ter título não vazio")
    void deveTerTituloNaoVazio() {
        // Act
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        // Assert
        assertThat(openAPI.getInfo().getTitle()).isNotBlank();
    }

    @Test
    @DisplayName("Deve ter versão não vazia")
    void deveTerVersaoNaoVazia() {
        // Act
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        // Assert
        assertThat(openAPI.getInfo().getVersion()).isNotBlank();
    }

    @Test
    @DisplayName("Deve ter descrição não vazia")
    void deveTerDescricaoNaoVazia() {
        // Act
        OpenAPI openAPI = openApiConfig.customOpenAPI();

        // Assert
        assertThat(openAPI.getInfo().getDescription()).isNotBlank();
    }

    @Test
    @DisplayName("Deve configurar todas as propriedades de contato")
    void deveConfigurarTodasAsPropriedadesDeContato() {
        // Act
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        Contact contact = openAPI.getInfo().getContact();

        // Assert
        assertThat(contact.getName()).isNotBlank();
        assertThat(contact.getEmail()).isNotBlank();
        assertThat(contact.getEmail()).contains("@");
    }

    @Test
    @DisplayName("Deve configurar todas as propriedades de licença")
    void deveConfigurarTodasAsPropriedadesDeLicenca() {
        // Act
        OpenAPI openAPI = openApiConfig.customOpenAPI();
        License license = openAPI.getInfo().getLicense();

        // Assert
        assertThat(license.getName()).isNotBlank();
        assertThat(license.getUrl()).isNotBlank();
        assertThat(license.getUrl()).startsWith("http");
    }

    @Test
    @DisplayName("Deve criar instância nova a cada chamada")
    void deveCriarInstanciaNovaACadaChamada() {
        // Act
        OpenAPI openAPI1 = openApiConfig.customOpenAPI();
        OpenAPI openAPI2 = openApiConfig.customOpenAPI();

        // Assert
        assertThat(openAPI1).isNotSameAs(openAPI2);
        assertThat(openAPI1.getInfo().getTitle()).isEqualTo(openAPI2.getInfo().getTitle());
    }
}
