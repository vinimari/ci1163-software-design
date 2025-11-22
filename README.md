# Sistema Seu Cantinho - Backend API

## 📋 Descrição

Sistema de gerenciamento de reservas de espaços para eventos, desenvolvido com arquitetura em camadas seguindo princípios SOLID e boas práticas de desenvolvimento.

## 🏗️ Arquitetura em Camadas

O projeto segue uma arquitetura em camadas bem definida:

```
┌─────────────────────────────────────────┐
│     Presentation Layer (Controllers)    │  ← API REST
├─────────────────────────────────────────┤
│      Business Layer (Services)          │  ← Lógica de Negócio
├─────────────────────────────────────────┤
│    Data Access Layer (Repositories)     │  ← Acesso a Dados
├─────────────────────────────────────────┤
│       Domain Layer (Entities)           │  ← Modelo de Domínio
└─────────────────────────────────────────┘
```

### 1. **Domain Layer** (`domain/entity`)
Contém as entidades JPA que representam o modelo de domínio:
- `Usuario` (classe abstrata)
  - `Administrador`
  - `Cliente`
  - `Funcionario`
- `Filial`
- `Espaco`
- `Reserva`
- `Pagamento`

**Enums**: `PerfilUsuarioEnum`, `StatusReservaEnum`, `TipoPagamentoEnum`

### 2. **Data Access Layer** (`repository`)
Interfaces Spring Data JPA para acesso ao banco de dados:
- `UsuarioRepository`
- `AdministradorRepository`
- `ClienteRepository`
- `FuncionarioRepository`
- `FilialRepository`
- `EspacoRepository`
- `ReservaRepository`
- `PagamentoRepository`

### 3. **Business Layer** (`service`)
Contém a lógica de negócio da aplicação:
- `ClienteService`
- `FilialService`
- `EspacoService`
- `ReservaService`
- `PagamentoService`

**Funcionalidades**:
- Validações de regras de negócio
- Conversão entre entidades e DTOs
- Gerenciamento de transações
- Cálculos e processamentos

### 4. **Presentation Layer** (`controller`)
Controllers REST que expõem os endpoints da API:
- `ClienteController`
- `FilialController`
- `EspacoController`
- `ReservaController`
- `PagamentoController`

## 📦 Estrutura de Pacotes

```
com.seucantinho.api/
├── config/              # Configurações (Security, OpenAPI)
├── controller/          # Controllers REST
├── dto/                 # Data Transfer Objects
│   ├── espaco/
│   ├── filial/
│   ├── pagamento/
│   ├── reserva/
│   └── usuario/
├── domain/
│   ├── entity/         # Entidades JPA
│   └── enums/          # Enumerações
├── exception/          # Exceções customizadas e handlers
├── repository/         # Repositories Spring Data JPA
└── service/            # Serviços de negócio
```

## 🔄 Fluxo de Requisição

```
Client → Controller → Service → Repository → Database
                ↓         ↓
              DTO     Entity
```

1. **Controller** recebe a requisição HTTP e valida o DTO
2. **Service** aplica regras de negócio e converte DTO → Entity
3. **Repository** persiste/recupera dados do banco
4. **Service** converte Entity → DTO de resposta
5. **Controller** retorna o DTO ao cliente

## 🚀 Endpoints Principais

### Filiais
- `GET /api/filiais` - Listar todas as filiais
- `GET /api/filiais/{id}` - Buscar filial por ID
- `POST /api/filiais` - Criar nova filial
- `PUT /api/filiais/{id}` - Atualizar filial
- `DELETE /api/filiais/{id}` - Excluir filial

### Espaços
- `GET /api/espacos` - Listar todos os espaços
- `GET /api/espacos/{id}` - Buscar espaço por ID
- `GET /api/espacos/filial/{filialId}` - Listar espaços por filial
- `GET /api/espacos/ativos` - Listar espaços ativos
- `GET /api/espacos/disponiveis?data={date}&capacidadeMinima={cap}` - Buscar disponíveis
- `POST /api/espacos` - Criar novo espaço
- `PUT /api/espacos/{id}` - Atualizar espaço
- `DELETE /api/espacos/{id}` - Excluir espaço

### Clientes
- `GET /api/clientes` - Listar todos os clientes
- `GET /api/clientes/{id}` - Buscar cliente por ID
- `POST /api/clientes` - Criar novo cliente
- `PUT /api/clientes/{id}` - Atualizar cliente
- `DELETE /api/clientes/{id}` - Excluir cliente

### Reservas
- `GET /api/reservas` - Listar todas as reservas
- `GET /api/reservas/{id}` - Buscar reserva por ID
- `GET /api/reservas/usuario/{usuarioId}` - Listar reservas por usuário
- `GET /api/reservas/espaco/{espacoId}` - Listar reservas por espaço
- `POST /api/reservas` - Criar nova reserva
- `PUT /api/reservas/{id}` - Atualizar reserva
- `PATCH /api/reservas/{id}/status?status={status}` - Atualizar status
- `DELETE /api/reservas/{id}` - Excluir reserva

### Pagamentos
- `GET /api/pagamentos` - Listar todos os pagamentos
- `GET /api/pagamentos/{id}` - Buscar pagamento por ID
- `GET /api/pagamentos/reserva/{reservaId}` - Listar pagamentos por reserva
- `POST /api/pagamentos` - Registrar novo pagamento
- `DELETE /api/pagamentos/{id}` - Excluir pagamento

## 🛠️ Tecnologias

- **Java 17**
- **Spring Boot 4.0.0**
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Segurança e autenticação
- **Spring Validation** - Validação de dados
- **PostgreSQL** - Banco de dados
- **Flyway** - Migração de banco de dados
- **Lombok** - Redução de boilerplate
- **SpringDoc OpenAPI** - Documentação da API

## 📚 Documentação da API

Após iniciar a aplicação, acesse:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## ⚙️ Configuração

### Banco de Dados

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/seucantinho
spring.datasource.username=user
spring.datasource.password=pass
```

### Flyway

As migrações são executadas automaticamente na inicialização:
- `V1__Create_Schema_Inicial.sql` - Criação das tabelas
- `V2__Insert_Data_Dev.sql` - Dados iniciais para desenvolvimento

## 🔐 Segurança

- Senhas são criptografadas com **BCrypt**
- Senha padrão dos usuários iniciais: `password123`
- Endpoints da API estão abertos para desenvolvimento (configurar autenticação JWT em produção)

## 🧪 Validações Implementadas

### Cliente
- Email único e válido
- CPF único
- Senha mínima de 6 caracteres

### Espaço
- Capacidade mínima de 1 pessoa
- Preço da diária >= 0
- Vinculado a uma filial existente

### Reserva
- Data do evento deve ser futura
- Validação de disponibilidade do espaço
- Não permite reservas duplicadas para o mesmo espaço/data
- Controle de status (AGUARDANDO_SINAL, CONFIRMADA, QUITADA, CANCELADA, FINALIZADA)

### Pagamento
- Valor > 0
- Não pode exceder o saldo da reserva
- Vinculado a uma reserva existente

## 🎯 Regras de Negócio

1. **Reservas Exclusivas**: Um espaço só pode ter uma reserva ativa por data
2. **Controle de Pagamentos**: O total de pagamentos não pode exceder o valor da reserva
3. **Espaços Ativos**: Apenas espaços ativos podem receber novas reservas
4. **Hierarquia de Usuários**: Admin, Funcionário (vinculado a filial) e Cliente
5. **Cálculo Automático**: Saldo da reserva = valor_total - soma_pagamentos

## 🔄 Tratamento de Erros

O sistema retorna respostas padronizadas para erros:

```json
{
  "timestamp": "2025-11-21T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Reserva não encontrada com ID: 123",
  "path": "/api/reservas/123"
}
```

Tipos de erros tratados:
- `404 Not Found` - Recurso não encontrado
- `400 Bad Request` - Validação ou regra de negócio violada
- `409 Conflict` - Recurso duplicado
- `500 Internal Server Error` - Erro inesperado

## 🚀 Como Executar

1. **Com Docker**:
```bash
docker-compose up
```

2. **Localmente**:
```bash
mvn clean install
mvn spring-boot:run
```

## 📝 Usuários Iniciais

| Email | Senha | Perfil |
|-------|-------|--------|
| admin@seucantinho.com | password123 | ADMIN |
| joao.pr@seucantinho.com | password123 | FUNCIONARIO |
| ana.sc@seucantinho.com | password123 | FUNCIONARIO |
| cliente@gmail.com | password123 | CLIENTE |
