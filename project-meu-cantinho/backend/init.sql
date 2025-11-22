-- ==============================================================
-- 1. ESTRUTURA (SCHEMA)
-- ==============================================================
-- Recriamos a estrutura aqui para garantir que o Docker consiga inserir os dados logo em seguida
-- sem depender do Java iniciar.

CREATE TYPE status_reserva_enum AS ENUM ('AGUARDANDO_SINAL', 'CONFIRMADA', 'QUITADA', 'CANCELADA', 'FINALIZADA');
CREATE TYPE tipo_pagamento_enum AS ENUM ('SINAL', 'QUITACAO', 'TOTAL');
CREATE TYPE perfil_usuario_enum AS ENUM ('ADMIN', 'FUNCIONARIO', 'CLIENTE');

CREATE TABLE tb_filial (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    cidade VARCHAR(100) NOT NULL,
    estado VARCHAR(2) NOT NULL,
    endereco VARCHAR(255),
    telefone VARCHAR(20),
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE tb_usuario (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha_hash VARCHAR(255) NOT NULL,
    perfil perfil_usuario_enum NOT NULL,
    cpf VARCHAR(14) UNIQUE,
    telefone VARCHAR(20),
    matricula VARCHAR(50) UNIQUE,
    filial_id INTEGER,
    ativo BOOLEAN DEFAULT TRUE,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_filial FOREIGN KEY (filial_id) REFERENCES tb_filial(id)
);

CREATE TABLE tb_espaco (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    descricao TEXT,
    capacidade INTEGER NOT NULL CHECK (capacidade > 0),
    preco_diaria DECIMAL(10, 2) NOT NULL CHECK (preco_diaria >= 0),
    ativo BOOLEAN DEFAULT TRUE,
    url_foto_principal VARCHAR(255),
    filial_id INTEGER NOT NULL,
    CONSTRAINT fk_espaco_filial FOREIGN KEY (filial_id) REFERENCES tb_filial(id)
);

CREATE TABLE tb_reserva (
    id SERIAL PRIMARY KEY,
    data_criacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    data_evento DATE NOT NULL,
    valor_total DECIMAL(10, 2) NOT NULL,
    observacoes TEXT,
    status status_reserva_enum NOT NULL DEFAULT 'AGUARDANDO_SINAL',
    usuario_id INTEGER NOT NULL,
    espaco_id INTEGER NOT NULL,
    CONSTRAINT fk_reserva_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id),
    CONSTRAINT fk_reserva_espaco FOREIGN KEY (espaco_id) REFERENCES tb_espaco(id)
);

CREATE TABLE tb_pagamento (
    id SERIAL PRIMARY KEY,
    data_pagamento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    valor DECIMAL(10, 2) NOT NULL CHECK (valor > 0),
    tipo tipo_pagamento_enum NOT NULL,
    forma_pagamento VARCHAR(50),
    codigo_transacao_gateway VARCHAR(100),
    reserva_id INTEGER NOT NULL,
    CONSTRAINT fk_pagamento_reserva FOREIGN KEY (reserva_id) REFERENCES tb_reserva(id)
);

-- Constraints e Indices Especiais
CREATE UNIQUE INDEX idx_reserva_unica_ativa ON tb_reserva (espaco_id, data_evento) WHERE status <> 'CANCELADA';
CREATE INDEX idx_reserva_data ON tb_reserva(data_evento);

-- ==============================================================
-- 2. DADOS INICIAIS (SEED)
-- ==============================================================

INSERT INTO tb_filial (nome, cidade, estado, endereco, telefone) VALUES
('Seu Cantinho - Matriz', 'Curitiba', 'PR', 'Av. Batel, 1000', '41999999999'),
('Seu Cantinho - Ilha', 'Florianopolis', 'SC', 'Av. Beira Mar, 500', '48988888888'),
('Seu Cantinho - Serra', 'Gramado', 'RS', 'Rua Coberta, 10', '54977777777');

INSERT INTO tb_usuario (nome, email, senha_hash, perfil, ativo, filial_id) VALUES
('Maria Proprietaria', 'admin@seucantinho.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', true, NULL),
('Joao Curitiba', 'joao.pr@seucantinho.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'FUNCIONARIO', true, 1),
('Ana Floripa', 'ana.sc@seucantinho.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'FUNCIONARIO', true, 2);

INSERT INTO tb_usuario (nome, email, senha_hash, perfil, cpf, telefone, ativo) VALUES
('Cliente Teste', 'cliente@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'CLIENTE', '12345678900', '11900000000', true);

INSERT INTO tb_espaco (nome, descricao, capacidade, preco_diaria, filial_id, ativo, url_foto_principal) VALUES
('Salao Cristal', 'Salao de luxo.', 200, 1500.00, 1, true, 'http://img.com/1'),
('Espaco Vista Mar', 'Vista para a ponte.', 120, 2000.00, 2, true, 'http://img.com/2');
