# Seu Cantinho - Sistema de Reserva de Espaços

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-green?logo=springboot&logoColor=white)
![Angular](https://img.shields.io/badge/Angular-19-red?logo=angular&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?logo=postgresql&logoColor=white)

![Backend Coverage](https://img.shields.io/badge/Backend_Coverage-86%25-brightgreen?style=for-the-badge&logo=jacoco)
![Frontend Coverage](https://img.shields.io/badge/Frontend_Coverage-90%25-brightgreen?style=for-the-badge&logo=jest)

Sistema completo para gerenciamento de reservas de espaços para eventos, desenvolvido com Spring Boot (backend) e Angular (frontend).

## 🚀 Como Executar o Projeto

### Pré-requisitos
- Docker e Docker Compose instalados
- Portas 5433, 8080 e 8081 disponíveis

### Execução com Docker (Recomendado)

1. Clone o repositório e navegue até a pasta do projeto:
```bash
cd project-meu-cantinho
```

2. Execute o projeto com Docker Compose:
```bash
docker-compose up -d
```

3. Aguarde alguns minutos para que todos os serviços sejam inicializados.

### Execução Manual (Desenvolvimento)

#### Backend (Spring Boot)
```bash
cd backend
./mvnw spring-boot:run
```

#### Frontend (Angular)
```bash
cd frontend
npm install
npm start
```

#### Banco de Dados (PostgreSQL)
```bash
# Usando Docker apenas para o banco
docker run -d \
  --name postgres-seucantinho \
  -e POSTGRES_DB=seucantinho \
  -e POSTGRES_USER=user \
  -e POSTGRES_PASSWORD=pass \
  -p 5433:5432 \
  -v ./backend/init.sql:/docker-entrypoint-initdb.d/init.sql \
  postgres:15-alpine
```

## 🌐 Portas e Acessos

### Aplicação
- **Frontend (Angular)**: http://localhost:8081
- **Backend (API)**: http://localhost:8080
- **Documentação da API (Swagger UI)**: http://localhost:8080/swagger-ui/index.html (também funciona: `/swagger-ui.html`)
- **OpenAPI JSON (spec)**: http://localhost:8080/v3/api-docs
- **Banco de Dados (PostgreSQL)**: localhost:5433

### Credenciais do Banco de Dados
- **Host**: localhost
- **Porta**: 5433
- **Database**: seucantinho
- **Usuário**: user
- **Senha**: pass

## 👥 Usuários Pré-cadastrados

Todos os usuários utilizam a senha: **`password123`**

### Administrador
- **Email**: `admin@seucantinho.com`
- **Perfil**: ADMIN
- **Nome**: Maria Proprietaria
- **Descrição**: Acesso total ao sistema

### Funcionários
- **Email**: `joao.pr@seucantinho.com`
- **Perfil**: FUNCIONARIO
- **Nome**: João Curitiba
- **Filial**: Seu Cantinho - Matriz (Curitiba)

- **Email**: `ana.sc@seucantinho.com`
- **Perfil**: FUNCIONARIO
- **Nome**: Ana Floripa
- **Filial**: Seu Cantinho - Ilha (Florianópolis)

### Clientes
- **Email**: `cliente1@gmail.com`
- **Perfil**: CLIENTE
- **Nome**: Cliente 1
- **CPF**: 123.456.789-00
- **Telefone**: (11) 90000-0000

- **Email**: `cliente2@gmail.com`
- **Perfil**: CLIENTE
- **Nome**: Cliente 2
- **CPF**: 123.456.789-01
- **Telefone**: (11) 90000-0001

## 🏢 Filiais Pré-cadastradas

1. **Seu Cantinho - Matriz**
   - Curitiba/PR
   - Av. Batel, 1000
   - (41) 99999-9999

2. **Seu Cantinho - Ilha**
   - Florianópolis/SC
   - Av. Beira Mar, 500
   - (48) 98888-8888

3. **Seu Cantinho - Serra**
   - Gramado/RS
   - Rua Coberta, 10
   - (54) 97777-7777

## 🏠 Espaços Disponíveis

1. **Salão Cristal**
   - Filial: Curitiba
   - Capacidade: 200 pessoas
   - Preço: R$ 1.500,00/dia
   - Descrição: Salão de luxo

2. **Espaço Vista Mar**
   - Filial: Florianópolis
   - Capacidade: 120 pessoas
   - Preço: R$ 2.000,00/dia
   - Descrição: Vista para a ponte

## 🛠️ Tecnologias Utilizadas

### Backend
- Spring Boot 4.0.0
- Java 17
- PostgreSQL
- Spring Security
- Spring Data JPA
- Swagger/OpenAPI

### Frontend
- Angular 19
- TypeScript
- RxJS
- Jest (testes)

### Infraestrutura
- Docker
- Docker Compose
- Nginx (proxy reverso)

## 📊 Funcionalidades

- **Autenticação e Autorização** com diferentes perfis de usuário
- **Gestão de Filiais** e espaços
- **Sistema de Reservas** com controle de status
- **Gestão de Pagamentos** (sinal, quitação, total)
- **Dashboard Administrativo**
- **Interface responsiva** para clientes

## 🧪 Executar Testes

### Frontend (Jest)
```bash
cd frontend
npm test
```

### Backend (JUnit)
```bash
cd backend
./mvnw test
```

## 📝 Logs e Troubleshooting

Para verificar logs dos containers:
```bash
docker-compose logs -f [service_name]
# Exemplo: docker-compose logs -f api
```

Para reiniciar um serviço específico:
```bash
docker-compose restart [service_name]
```

Para parar todos os serviços:
```bash
docker-compose down
```

## 📋 Status dos Serviços

Verifique se todos os serviços estão rodando:
```bash
docker-compose ps
```

O status deve mostrar todos os containers como "Up".
