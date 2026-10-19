-- ============================================================================
-- Etapa 2 - Esquema Relacional (DDL SQL)
-- Sistema: Monitoramento e Manutencao Colaborativa de Infraestrutura Urbana
-- Dialeto: Compativel com SQLite 3 e PostgreSQL (ANSI SQL)
-- ============================================================================

PRAGMA foreign_keys = ON;

-- 1. regiao_cep (Regra 1: Entidade Forte)
CREATE TABLE regiao_cep (
    cep                     CHAR(8)       NOT NULL,
    cidade                  VARCHAR(100) NOT NULL,
    bairro                  VARCHAR(100) NOT NULL,
    logradouro              VARCHAR(150) NOT NULL,
    ponto_referencia_regiao VARCHAR(200),
    CONSTRAINT pk_regiao_cep PRIMARY KEY (cep)
);

-- 2. cidadao (Regra 1: Entidade Forte + Regra 4: Relacionamento 1:N endereco_moradia)
CREATE TABLE cidadao (
    id_cidadao        INTEGER      NOT NULL,
    cep_moradia       CHAR(8)      NOT NULL,
    cpf               CHAR(11)     NOT NULL UNIQUE,
    nome_completo     VARCHAR(150) NOT NULL,
    email             VARCHAR(150) NOT NULL UNIQUE,
    telefone          VARCHAR(20)  NOT NULL,
    numero_residencia VARCHAR(20)  NOT NULL,
    CONSTRAINT pk_cidadao PRIMARY KEY (id_cidadao),
    CONSTRAINT fk_cidadao_cep_moradia FOREIGN KEY (cep_moradia)
        REFERENCES regiao_cep (cep)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 3. ocorrencia (Regra 1: Entidade Forte + Regra 4: Relacionamentos 1:N reporta e local_do_problema)
CREATE TABLE ocorrencia (
    id_ocorrencia       INTEGER      NOT NULL,
    id_cidadao          INTEGER      NOT NULL,
    cep_ocorrencia      CHAR(8)      NOT NULL,
    protocolo           VARCHAR(30)  NOT NULL UNIQUE,
    titulo              VARCHAR(120) NOT NULL,
    descricao_detalhada TEXT         NOT NULL,
    categoria_problema  VARCHAR(60)  NOT NULL,
    nivel_gravidade     VARCHAR(20)  NOT NULL,
    data_abertura       TIMESTAMP    NOT NULL,
    status_atual        VARCHAR(30)  NOT NULL,
    numero_aproximado   VARCHAR(20),
    CONSTRAINT pk_ocorrencia PRIMARY KEY (id_ocorrencia),
    CONSTRAINT fk_ocorrencia_cidadao FOREIGN KEY (id_cidadao)
        REFERENCES cidadao (id_cidadao)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_ocorrencia_cep FOREIGN KEY (cep_ocorrencia)
        REFERENCES regiao_cep (cep)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 4. anexo_midia (Regra 1: Entidade Forte + Regra 4: Relacionamento 1:N possui)
CREATE TABLE anexo_midia (
    id_anexo      INTEGER      NOT NULL,
    id_ocorrencia INTEGER      NOT NULL,
    url_arquivo   VARCHAR(500) NOT NULL,
    tipo_midia    VARCHAR(30)  NOT NULL,
    data_upload   TIMESTAMP    NOT NULL,
    CONSTRAINT pk_anexo_midia PRIMARY KEY (id_anexo),
    CONSTRAINT fk_anexo_midia_ocorrencia FOREIGN KEY (id_ocorrencia)
        REFERENCES ocorrencia (id_ocorrencia)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- 5. secretaria_orgao (Regra 1: Entidade Forte)
CREATE TABLE secretaria_orgao (
    id_secretaria               INTEGER        NOT NULL,
    nome_secretaria             VARCHAR(120)   NOT NULL UNIQUE,
    sigla                       VARCHAR(15)    NOT NULL UNIQUE,
    nome_secretario_responsavel VARCHAR(150)   NOT NULL,
    telefone_contato            VARCHAR(20)    NOT NULL,
    orcamento_mensal            DECIMAL(14, 2) NOT NULL CHECK (orcamento_mensal >= 0),
    CONSTRAINT pk_secretaria_orgao PRIMARY KEY (id_secretaria)
);

-- 6. equipe_manutencao (Regra 1: Entidade Forte + Regra 4: Relacionamento 1:N coordena)
CREATE TABLE equipe_manutencao (
    id_equipe                   INTEGER      NOT NULL,
    id_secretaria               INTEGER      NOT NULL,
    nome_equipe                 VARCHAR(100) NOT NULL UNIQUE,
    categoria_problema_atendida VARCHAR(60)  NOT NULL,
    turno_trabalho              VARCHAR(30)  NOT NULL,
    status_disponibilidade      VARCHAR(30)  NOT NULL,
    CONSTRAINT pk_equipe_manutencao PRIMARY KEY (id_equipe),
    CONSTRAINT fk_equipe_secretaria FOREIGN KEY (id_secretaria)
        REFERENCES secretaria_orgao (id_secretaria)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 7. funcionario (Regra 1: Entidade Forte + Regra 4: Relacionamento 1:N e_composta_por)
CREATE TABLE funcionario (
    matricula_funcionario VARCHAR(20)  NOT NULL,
    id_equipe             INTEGER      NOT NULL,
    cpf                   CHAR(11)     NOT NULL UNIQUE,
    nome_funcionario      VARCHAR(150) NOT NULL,
    cargo_funcao          VARCHAR(80)  NOT NULL,
    data_admissao         DATE         NOT NULL,
    telefone_corporativo  VARCHAR(20),
    CONSTRAINT pk_funcionario PRIMARY KEY (matricula_funcionario),
    CONSTRAINT fk_funcionario_equipe FOREIGN KEY (id_equipe)
        REFERENCES equipe_manutencao (id_equipe)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 8. veiculo_frota (Regra 1: Entidade Forte + Regra 4: Relacionamento 1:N mantem_frota)
CREATE TABLE veiculo_frota (
    placa_veiculo     CHAR(7)        NOT NULL,
    id_secretaria     INTEGER        NOT NULL,
    tipo_veiculo      VARCHAR(50)    NOT NULL,
    modelo            VARCHAR(80)    NOT NULL,
    ano               INTEGER        NOT NULL CHECK (ano >= 1950),
    capacidade_carga  DECIMAL(10, 2) NOT NULL CHECK (capacidade_carga >= 0),
    status_manutencao VARCHAR(30)    NOT NULL,
    CONSTRAINT pk_veiculo_frota PRIMARY KEY (placa_veiculo),
    CONSTRAINT fk_veiculo_secretaria FOREIGN KEY (id_secretaria)
        REFERENCES secretaria_orgao (id_secretaria)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 9. ordem_servico (Regra 1: Entidade Forte + Regra 4: Relacionamentos 1:N gera, executa e desloca_para)
CREATE TABLE ordem_servico (
    id_os                INTEGER        NOT NULL,
    id_ocorrencia        INTEGER        NOT NULL,
    id_equipe            INTEGER        NOT NULL,
    placa_veiculo        CHAR(7)        NOT NULL,
    data_emissao         TIMESTAMP      NOT NULL,
    data_inicio_execucao TIMESTAMP,
    data_conclusao       TIMESTAMP,
    relatorio_tecnico    TEXT,
    custo_mao_de_obra    DECIMAL(12, 2) CHECK (custo_mao_de_obra >= 0),
    status_os            VARCHAR(30)    NOT NULL,
    CONSTRAINT pk_ordem_servico PRIMARY KEY (id_os),
    CONSTRAINT fk_os_ocorrencia FOREIGN KEY (id_ocorrencia)
        REFERENCES ocorrencia (id_ocorrencia)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_os_equipe FOREIGN KEY (id_equipe)
        REFERENCES equipe_manutencao (id_equipe)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_os_veiculo FOREIGN KEY (placa_veiculo)
        REFERENCES veiculo_frota (placa_veiculo)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 10. material_insumo (Regra 1: Entidade Forte)
CREATE TABLE material_insumo (
    id_material              INTEGER        NOT NULL,
    nome_material            VARCHAR(120)   NOT NULL UNIQUE,
    unidade_medida           VARCHAR(20)    NOT NULL,
    quantidade_estoque_atual DECIMAL(12, 3) NOT NULL CHECK (quantidade_estoque_atual >= 0),
    custo_unitario_medio     DECIMAL(12, 2) NOT NULL CHECK (custo_unitario_medio >= 0),
    CONSTRAINT pk_material_insumo PRIMARY KEY (id_material)
);

-- 11. fornecedor (Regra 1: Entidade Forte)
CREATE TABLE fornecedor (
    id_fornecedor   INTEGER      NOT NULL,
    cnpj            CHAR(14)     NOT NULL UNIQUE,
    razao_social    VARCHAR(150) NOT NULL,
    telefone        VARCHAR(20)  NOT NULL,
    email_comercial VARCHAR(150) NOT NULL,
    cidade_sede     VARCHAR(100) NOT NULL,
    CONSTRAINT pk_fornecedor PRIMARY KEY (id_fornecedor)
);

-- 12. avaliacao_atendimento (Regra 1: Entidade Forte + Regra 3: Relacionamento 1:1 recebe + Regra 4: 1:N registra_avaliacao)
CREATE TABLE avaliacao_atendimento (
    id_avaliacao               INTEGER   NOT NULL,
    id_os                      INTEGER   NOT NULL UNIQUE,
    id_cidadao                 INTEGER   NOT NULL,
    nota_satisfacao            INTEGER   NOT NULL CHECK (nota_satisfacao BETWEEN 1 AND 5),
    comentario_cidadao         TEXT,
    problema_resolvido_boolean BOOLEAN   NOT NULL,
    data_avaliacao             TIMESTAMP NOT NULL,
    CONSTRAINT pk_avaliacao_atendimento PRIMARY KEY (id_avaliacao),
    CONSTRAINT fk_avaliacao_os FOREIGN KEY (id_os)
        REFERENCES ordem_servico (id_os)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_avaliacao_cidadao FOREIGN KEY (id_cidadao)
        REFERENCES cidadao (id_cidadao)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 13. consumo_material_os (Regra 5: Relacionamento N:M consome)
CREATE TABLE consumo_material_os (
    id_os                INTEGER        NOT NULL,
    id_material          INTEGER        NOT NULL,
    quantidade_utilizada DECIMAL(12, 3) NOT NULL CHECK (quantidade_utilizada > 0),
    data_baixa_estoque   TIMESTAMP      NOT NULL,
    CONSTRAINT pk_consumo_material_os PRIMARY KEY (id_os, id_material),
    CONSTRAINT fk_consumo_os FOREIGN KEY (id_os)
        REFERENCES ordem_servico (id_os)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT fk_consumo_material FOREIGN KEY (id_material)
        REFERENCES material_insumo (id_material)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 14. fornecimento_insumo (Regra 5: Relacionamento N:M fornece com discriminador de lote)
CREATE TABLE fornecimento_insumo (
    id_fornecedor         INTEGER        NOT NULL,
    id_material           INTEGER        NOT NULL,
    numero_lote_compra    VARCHAR(50)    NOT NULL,
    data_entrega          DATE           NOT NULL,
    quantidade_comprada   DECIMAL(12, 3) NOT NULL CHECK (quantidade_comprada > 0),
    preco_unitario_compra DECIMAL(12, 2) NOT NULL CHECK (preco_unitario_compra >= 0),
    CONSTRAINT pk_fornecimento_insumo PRIMARY KEY (id_fornecedor, id_material, numero_lote_compra),
    CONSTRAINT fk_fornecimento_fornecedor FOREIGN KEY (id_fornecedor)
        REFERENCES fornecedor (id_fornecedor)
        ON UPDATE CASCADE
        ON DELETE RESTRICT,
    CONSTRAINT fk_fornecimento_material FOREIGN KEY (id_material)
        REFERENCES material_insumo (id_material)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
