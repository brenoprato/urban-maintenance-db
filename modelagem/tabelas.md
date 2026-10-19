# Mapeamento ER → Relacional: Lista de Tabelas (`tabelas.md`)

**Sistema:** Monitoramento e Manutenção Colaborativa de Infraestrutura Urbana (ODS 11 e ODS 9)  
**Total de Tabelas:** 14 tabelas (12 originadas de entidades fortes com absorção de relacionamentos 1:1 e 1:N + 2 tabelas associativas originadas de relacionamentos N:M)

---

## 1. Regras de Mapeamento Aplicadas (Elmasri & Navathe)

1. **Regra 1 — Entidade forte:** Cada entidade forte do diagrama ER origina uma tabela própria contendo seus atributos simples como colunas e seu atributo identificador como Chave Primária (**PK**).
2. **Regra 2 — Entidade fraca:** Não há entidade fraca pura (sem chave primária própria) na especificação. A entidade `anexo_midia` possui chave primária própria (`id_anexo`) e dependência existencial com participação total `(1,1)` em `possui`, sendo mapeada pela **Regra 1 + Regra 4** (com `NOT NULL` e `ON DELETE CASCADE`).
3. **Regra 3 — Relacionamento binário 1:1:** No relacionamento `recebe` entre `ordem_servico` `(0,1)` e `avaliacao_atendimento` `(1,1)`, a PK de `ordem_servico` (`id_os`) migra como Chave Estrangeira (**FK**) para `avaliacao_atendimento` (lado de participação total `(1,1)`), com restrições `NOT NULL` e `UNIQUE` para assegurar a cardinalidade 1:1 sem gerar valores nulos nas ordens de serviço ainda não avaliadas.
4. **Regra 4 — Relacionamento binário 1:N:** Para cada um dos 11 relacionamentos 1:N do modelo, a PK da entidade do lado `1` migra como **FK** para a tabela do lado `N`. Como todos os lados `N` possuem participação total `(1,1)` na especificação, todas as FKs resultantes recebem a restrição `NOT NULL`.
5. **Regra 5 — Relacionamento binário N:M:** Cada relacionamento N:M (`consome` e `fornece`) origina uma nova **tabela associativa** (`consumo_material_os` e `fornecimento_insumo`), cuja PK composta é formada pelas FKs das entidades participantes (acrescida do atributo discriminador `numero_lote_compra` no caso de `fornecimento_insumo`, permitindo múltiplos lotes de compra do mesmo material junto ao mesmo fornecedor) e cujos atributos próprios do relacionamento tornam-se colunas da tabela associativa.
6. **Regras 6, 7, 8 e 9 (Atributos multivalorados, Relacionamentos n-ários, Especialização/Generalização e Atributos derivados):** Não aplicáveis ao modelo especificado em `spec_bd.md`, pois todos os atributos foram definidos na forma atômica/simples e todos os relacionamentos são binários sem hierarquias de herança.

---

## 2. Detalhamento das Tabelas Geradas

### A. Tabelas Originadas de Entidades (Relacionamentos 1:1 e 1:N absorvidos via FK)

### 1. `regiao_cep` (Regra 1: entidade forte)
| Coluna | Tipo | Restrições |
|---|---|---|
| `cep` | `CHAR(8)` | **PK** |
| `cidade` | `VARCHAR(100)` | `NOT NULL` |
| `bairro` | `VARCHAR(100)` | `NOT NULL` |
| `logradouro` | `VARCHAR(150)` | `NOT NULL` |
| `ponto_referencia_regiao` | `VARCHAR(200)` | `NULL` (opcional) |

---

### 2. `cidadao` (Regra 1: entidade forte + Regra 4: relacionamento 1:N `endereco_moradia`)
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_cidadao` | `INTEGER` | **PK** |
| `cpf` | `CHAR(11)` | `NOT NULL`, `UNIQUE` |
| `nome_completo` | `VARCHAR(150)` | `NOT NULL` |
| `email` | `VARCHAR(150)` | `NOT NULL`, `UNIQUE` |
| `telefone` | `VARCHAR(20)` | `NOT NULL` |
| `numero_residencia` | `VARCHAR(20)` | `NOT NULL` |
| `cep_moradia` | `CHAR(8)` | **FK** → `regiao_cep(cep)`, `NOT NULL` |

---

### 3. `ocorrencia` (Regra 1: entidade forte + Regra 4: relacionamentos 1:N `reporta` e `local_do_problema`)
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_ocorrencia` | `INTEGER` | **PK** |
| `protocolo` | `VARCHAR(30)` | `NOT NULL`, `UNIQUE` |
| `titulo` | `VARCHAR(120)` | `NOT NULL` |
| `descricao_detalhada` | `TEXT` | `NOT NULL` |
| `categoria_problema` | `VARCHAR(60)` | `NOT NULL` |
| `nivel_gravidade` | `VARCHAR(20)` | `NOT NULL` |
| `data_abertura` | `TIMESTAMP` | `NOT NULL` |
| `status_atual` | `VARCHAR(30)` | `NOT NULL` |
| `numero_aproximado` | `VARCHAR(20)` | `NULL` (opcional) |
| `id_cidadao` | `INTEGER` | **FK** → `cidadao(id_cidadao)`, `NOT NULL` |
| `cep_ocorrencia` | `CHAR(8)` | **FK** → `regiao_cep(cep)`, `NOT NULL` |

---

### 4. `anexo_midia` (Regra 1: entidade forte + Regra 4: relacionamento 1:N `possui`)
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_anexo` | `INTEGER` | **PK** |
| `url_arquivo` | `VARCHAR(500)` | `NOT NULL` |
| `tipo_midia` | `VARCHAR(30)` | `NOT NULL` |
| `data_upload` | `TIMESTAMP` | `NOT NULL` |
| `id_ocorrencia` | `INTEGER` | **FK** → `ocorrencia(id_ocorrencia)`, `NOT NULL` (`ON DELETE CASCADE`) |

---

### 5. `secretaria_orgao` (Regra 1: entidade forte)
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_secretaria` | `INTEGER` | **PK** |
| `nome_secretaria` | `VARCHAR(120)` | `NOT NULL`, `UNIQUE` |
| `sigla` | `VARCHAR(15)` | `NOT NULL`, `UNIQUE` |
| `nome_secretario_responsavel` | `VARCHAR(150)` | `NOT NULL` |
| `telefone_contato` | `VARCHAR(20)` | `NOT NULL` |
| `orcamento_mensal` | `DECIMAL(14,2)` | `NOT NULL`, `CHECK (orcamento_mensal >= 0)` |

---

### 6. `equipe_manutencao` (Regra 1: entidade forte + Regra 4: relacionamento 1:N `coordena`)
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_equipe` | `INTEGER` | **PK** |
| `nome_equipe` | `VARCHAR(100)` | `NOT NULL` |
| `categoria_problema_atendida` | `VARCHAR(60)` | `NOT NULL` |
| `turno_trabalho` | `VARCHAR(30)` | `NOT NULL` |
| `status_disponibilidade` | `VARCHAR(30)` | `NOT NULL` |
| `id_secretaria` | `INTEGER` | **FK** → `secretaria_orgao(id_secretaria)`, `NOT NULL` |

---

### 7. `funcionario` (Regra 1: entidade forte + Regra 4: relacionamento 1:N `e_composta_por`)
| Coluna | Tipo | Restrições |
|---|---|---|
| `matricula_funcionario` | `VARCHAR(20)` | **PK** |
| `cpf` | `CHAR(11)` | `NOT NULL`, `UNIQUE` |
| `nome_funcionario` | `VARCHAR(150)` | `NOT NULL` |
| `cargo_funcao` | `VARCHAR(80)` | `NOT NULL` |
| `data_admissao` | `DATE` | `NOT NULL` |
| `telefone_corporativo` | `VARCHAR(20)` | `NULL` (opcional) |
| `id_equipe` | `INTEGER` | **FK** → `equipe_manutencao(id_equipe)`, `NOT NULL` |

---

### 8. `veiculo_frota` (Regra 1: entidade forte + Regra 4: relacionamento 1:N `mantem_frota`)
| Coluna | Tipo | Restrições |
|---|---|---|
| `placa_veiculo` | `CHAR(7)` | **PK** |
| `tipo_veiculo` | `VARCHAR(50)` | `NOT NULL` |
| `modelo` | `VARCHAR(80)` | `NOT NULL` |
| `ano` | `INTEGER` | `NOT NULL`, `CHECK (ano >= 1950)` |
| `capacidade_carga` | `DECIMAL(10,2)` | `NOT NULL`, `CHECK (capacidade_carga >= 0)` |
| `status_manutencao` | `VARCHAR(30)` | `NOT NULL` |
| `id_secretaria` | `INTEGER` | **FK** → `secretaria_orgao(id_secretaria)`, `NOT NULL` |

---

### 9. `ordem_servico` (Regra 1: entidade forte + Regra 4: relacionamentos 1:N `gera`, `executa` e `desloca_para`)
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_os` | `INTEGER` | **PK** |
| `data_emissao` | `TIMESTAMP` | `NOT NULL` |
| `data_inicio_execucao` | `TIMESTAMP` | `NULL` (preenchido ao iniciar execução) |
| `data_conclusao` | `TIMESTAMP` | `NULL` (preenchido ao concluir execução) |
| `relatorio_tecnico` | `TEXT` | `NULL` (preenchido na execução/conclusão) |
| `custo_mao_de_obra` | `DECIMAL(12,2)` | `NULL`, `CHECK (custo_mao_de_obra >= 0)` |
| `status_os` | `VARCHAR(30)` | `NOT NULL` |
| `id_ocorrencia` | `INTEGER` | **FK** → `ocorrencia(id_ocorrencia)`, `NOT NULL` |
| `id_equipe` | `INTEGER` | **FK** → `equipe_manutencao(id_equipe)`, `NOT NULL` |
| `placa_veiculo` | `CHAR(7)` | **FK** → `veiculo_frota(placa_veiculo)`, `NOT NULL` |

---

### 10. `material_insumo` (Regra 1: entidade forte)
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_material` | `INTEGER` | **PK** |
| `nome_material` | `VARCHAR(120)` | `NOT NULL`, `UNIQUE` |
| `unidade_medida` | `VARCHAR(20)` | `NOT NULL` |
| `quantidade_estoque_atual` | `DECIMAL(12,3)` | `NOT NULL`, `CHECK (quantidade_estoque_atual >= 0)` |
| `custo_unitario_medio` | `DECIMAL(12,2)` | `NOT NULL`, `CHECK (custo_unitario_medio >= 0)` |

---

### 11. `fornecedor` (Regra 1: entidade forte)
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_fornecedor` | `INTEGER` | **PK** |
| `cnpj` | `CHAR(14)` | `NOT NULL`, `UNIQUE` |
| `razao_social` | `VARCHAR(150)` | `NOT NULL` |
| `telefone` | `VARCHAR(20)` | `NOT NULL` |
| `email_comercial` | `VARCHAR(150)` | `NOT NULL` |
| `cidade_sede` | `VARCHAR(100)` | `NOT NULL` |

---

### 12. `avaliacao_atendimento` (Regra 1: entidade forte + Regra 3: relacionamento 1:1 `recebe` + Regra 4: relacionamento 1:N `registra_avaliacao`)
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_avaliacao` | `INTEGER` | **PK** |
| `nota_satisfacao` | `INTEGER` | `NOT NULL`, `CHECK (nota_satisfacao BETWEEN 1 AND 5)` |
| `comentario_cidadao` | `TEXT` | `NULL` (opcional) |
| `problema_resolvido_boolean` | `BOOLEAN` | `NOT NULL` |
| `data_avaliacao` | `TIMESTAMP` | `NOT NULL` |
| `id_os` | `INTEGER` | **FK** → `ordem_servico(id_os)`, `NOT NULL`, `UNIQUE` (garante 1:1) |
| `id_cidadao` | `INTEGER` | **FK** → `cidadao(id_cidadao)`, `NOT NULL` |

---

### B. Tabelas Associativas Originadas de Relacionamentos N:M

### 13. `consumo_material_os` (Regra 5: relacionamento N:M `consome` entre `ordem_servico` e `material_insumo`)
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_os` | `INTEGER` | **PK**, **FK** → `ordem_servico(id_os)`, `NOT NULL` |
| `id_material` | `INTEGER` | **PK**, **FK** → `material_insumo(id_material)`, `NOT NULL` |
| `quantidade_utilizada` | `DECIMAL(12,3)` | `NOT NULL`, `CHECK (quantidade_utilizada > 0)` |
| `data_baixa_estoque` | `TIMESTAMP` | `NOT NULL` |

---

### 14. `fornecimento_insumo` (Regra 5: relacionamento N:M `fornece` entre `fornecedor` e `material_insumo`)
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_fornecedor` | `INTEGER` | **PK**, **FK** → `fornecedor(id_fornecedor)`, `NOT NULL` |
| `id_material` | `INTEGER` | **PK**, **FK** → `material_insumo(id_material)`, `NOT NULL` |
| `numero_lote_compra` | `VARCHAR(50)` | **PK** (discriminador de lote), `NOT NULL` |
| `data_entrega` | `DATE` | `NOT NULL` |
| `quantidade_comprada` | `DECIMAL(12,3)` | `NOT NULL`, `CHECK (quantidade_comprada > 0)` |
| `preco_unitario_compra` | `DECIMAL(12,2)` | `NOT NULL`, `CHECK (preco_unitario_compra >= 0)` |

---

## 3. Resumo Final (Formato Texto Compacto — Linha Única)

### 3.1. Notação Explícita (`PK`, `FK → tabela.coluna`)

```text
1.  regiao_cep(PK cep, cidade, bairro, logradouro, ponto_referencia_regiao)
2.  cidadao(PK id_cidadao, cpf, nome_completo, email, telefone, numero_residencia, FK cep_moradia → regiao_cep.cep)
3.  ocorrencia(PK id_ocorrencia, protocolo, titulo, descricao_detalhada, categoria_problema, nivel_gravidade, data_abertura, status_atual, numero_aproximado, FK id_cidadao → cidadao.id_cidadao, FK cep_ocorrencia → regiao_cep.cep)
4.  anexo_midia(PK id_anexo, url_arquivo, tipo_midia, data_upload, FK id_ocorrencia → ocorrencia.id_ocorrencia)
5.  secretaria_orgao(PK id_secretaria, nome_secretaria, sigla, nome_secretario_responsavel, telefone_contato, orcamento_mensal)
6.  equipe_manutencao(PK id_equipe, nome_equipe, categoria_problema_atendida, turno_trabalho, status_disponibilidade, FK id_secretaria → secretaria_orgao.id_secretaria)
7.  funcionario(PK matricula_funcionario, cpf, nome_funcionario, cargo_funcao, data_admissao, telefone_corporativo, FK id_equipe → equipe_manutencao.id_equipe)
8.  veiculo_frota(PK placa_veiculo, tipo_veiculo, modelo, ano, capacidade_carga, status_manutencao, FK id_secretaria → secretaria_orgao.id_secretaria)
9.  ordem_servico(PK id_os, data_emissao, data_inicio_execucao, data_conclusao, relatorio_tecnico, custo_mao_de_obra, status_os, FK id_ocorrencia → ocorrencia.id_ocorrencia, FK id_equipe → equipe_manutencao.id_equipe, FK placa_veiculo → veiculo_frota.placa_veiculo)
10. material_insumo(PK id_material, nome_material, unidade_medida, quantidade_estoque_atual, custo_unitario_medio)
11. fornecedor(PK id_fornecedor, cnpj, razao_social, telefone, email_comercial, cidade_sede)
12. avaliacao_atendimento(PK id_avaliacao, nota_satisfacao, comentario_cidadao, problema_resolvido_boolean, data_avaliacao, FK/UNIQUE id_os → ordem_servico.id_os, FK id_cidadao → cidadao.id_cidadao)
13. consumo_material_os(PK/FK id_os → ordem_servico.id_os, PK/FK id_material → material_insumo.id_material, quantidade_utilizada, data_baixa_estoque)
14. fornecimento_insumo(PK/FK id_fornecedor → fornecedor.id_fornecedor, PK/FK id_material → material_insumo.id_material, PK numero_lote_compra, data_entrega, quantidade_comprada, preco_unitario_compra)
```

### 3.2. Notação Clássica da Especificação (Negrito/Sublinhado = PK | `#` = FK)

1. **regiao_cep** (<u>**cep**</u>, cidade, bairro, logradouro, ponto_referencia_regiao)
2. **cidadao** (<u>**id_cidadao**</u>, cpf, nome_completo, email, telefone, numero_residencia, `#cep_moradia`)
3. **ocorrencia** (<u>**id_ocorrencia**</u>, protocolo, titulo, descricao_detalhada, categoria_problema, nivel_gravidade, data_abertura, status_atual, numero_aproximado, `#id_cidadao`, `#cep_ocorrencia`)
4. **anexo_midia** (<u>**id_anexo**</u>, url_arquivo, tipo_midia, data_upload, `#id_ocorrencia`)
5. **secretaria_orgao** (<u>**id_secretaria**</u>, nome_secretaria, sigla, nome_secretario_responsavel, telefone_contato, orcamento_mensal)
6. **equipe_manutencao** (<u>**id_equipe**</u>, nome_equipe, categoria_problema_atendida, turno_trabalho, status_disponibilidade, `#id_secretaria`)
7. **funcionario** (<u>**matricula_funcionario**</u>, cpf, nome_funcionario, cargo_funcao, data_admissao, telefone_corporativo, `#id_equipe`)
8. **veiculo_frota** (<u>**placa_veiculo**</u>, tipo_veiculo, modelo, ano, capacidade_carga, status_manutencao, `#id_secretaria`)
9. **ordem_servico** (<u>**id_os**</u>, data_emissao, data_inicio_execucao, data_conclusao, relatorio_tecnico, custo_mao_de_obra, status_os, `#id_ocorrencia`, `#id_equipe`, `#placa_veiculo`)
10. **material_insumo** (<u>**id_material**</u>, nome_material, unidade_medida, quantidade_estoque_atual, custo_unitario_medio)
11. **fornecedor** (<u>**id_fornecedor**</u>, cnpj, razao_social, telefone, email_comercial, cidade_sede)
12. **avaliacao_atendimento** (<u>**id_avaliacao**</u>, nota_satisfacao, comentario_cidadao, problema_resolvido_boolean, data_avaliacao, `#id_os` [UNIQUE], `#id_cidadao`)
13. **consumo_material_os** (<u>**`#id_os`**</u>, <u>**`#id_material`**</u>, quantidade_utilizada, data_baixa_estoque)
14. **fornecimento_insumo** (<u>**`#id_fornecedor`**</u>, <u>**`#id_material`**</u>, <u>**numero_lote_compra**</u>, data_entrega, quantidade_comprada, preco_unitario_compra)

---

## 4. Matriz de Rastreabilidade (Diagrama ER → Modelo Relacional)

| # | Elemento no Diagrama ER | Tipo no MER | Cardinalidade / Participação | Tabela(s) Afetada(s) / Gerada(s) | Efeito no Esquema Relacional | Regra Aplicada (Elmasri & Navathe) |
|---|---|---|---|---|---|---|
| E01 | `regiao_cep` | Entidade Forte | — | `regiao_cep` | Cria tabela com `PK(cep)` e 4 atributos descritivos | **Regra 1** (Entidade forte) |
| E02 | `cidadao` | Entidade Forte | — | `cidadao` | Cria tabela com `PK(id_cidadao)` e 5 atributos próprios | **Regra 1** (Entidade forte) |
| E03 | `ocorrencia` | Entidade Forte | — | `ocorrencia` | Cria tabela com `PK(id_ocorrencia)` e 8 atributos próprios | **Regra 1** (Entidade forte) |
| E04 | `anexo_midia` | Entidade Forte (dependente) | — | `anexo_midia` | Cria tabela com `PK(id_anexo)` e 3 atributos próprios | **Regra 1** (Entidade forte) |
| E05 | `secretaria_orgao` | Entidade Forte | — | `secretaria_orgao` | Cria tabela com `PK(id_secretaria)` e 5 atributos próprios | **Regra 1** (Entidade forte) |
| E06 | `equipe_manutencao` | Entidade Forte | — | `equipe_manutencao` | Cria tabela com `PK(id_equipe)` e 4 atributos próprios | **Regra 1** (Entidade forte) |
| E07 | `funcionario` | Entidade Forte | — | `funcionario` | Cria tabela com `PK(matricula_funcionario)` e 5 atributos próprios | **Regra 1** (Entidade forte) |
| E08 | `veiculo_frota` | Entidade Forte | — | `veiculo_frota` | Cria tabela com `PK(placa_veiculo)` e 5 atributos próprios | **Regra 1** (Entidade forte) |
| E09 | `ordem_servico` | Entidade Forte | — | `ordem_servico` | Cria tabela com `PK(id_os)` e 6 atributos próprios | **Regra 1** (Entidade forte) |
| E10 | `material_insumo` | Entidade Forte | — | `material_insumo` | Cria tabela com `PK(id_material)` e 4 atributos próprios | **Regra 1** (Entidade forte) |
| E11 | `fornecedor` | Entidade Forte | — | `fornecedor` | Cria tabela com `PK(id_fornecedor)` e 5 atributos próprios | **Regra 1** (Entidade forte) |
| E12 | `avaliacao_atendimento` | Entidade Forte | — | `avaliacao_atendimento` | Cria tabela com `PK(id_avaliacao)` e 4 atributos próprios | **Regra 1** (Entidade forte) |
| R01 | `endereco_moradia` | Relacionamento 1:N | `regiao_cep (0,N)` — `(1,1) cidadao` | `cidadao` | Absorvido via `FK cep_moradia NOT NULL` → `regiao_cep(cep)` | **Regra 4** (Relacionamento 1:N) |
| R02 | `local_do_problema` | Relacionamento 1:N | `regiao_cep (0,N)` — `(1,1) ocorrencia` | `ocorrencia` | Absorvido via `FK cep_ocorrencia NOT NULL` → `regiao_cep(cep)` | **Regra 4** (Relacionamento 1:N) |
| R03 | `reporta` | Relacionamento 1:N | `cidadao (0,N)` — `(1,1) ocorrencia` | `ocorrencia` | Absorvido via `FK id_cidadao NOT NULL` → `cidadao(id_cidadao)` | **Regra 4** (Relacionamento 1:N) |
| R04 | `possui` | Relacionamento 1:N | `ocorrencia (0,N)` — `(1,1) anexo_midia` | `anexo_midia` | Absorvido via `FK id_ocorrencia NOT NULL` → `ocorrencia(id_ocorrencia)` | **Regra 4** (Relacionamento 1:N) |
| R05 | `coordena` | Relacionamento 1:N | `secretaria_orgao (1,N)` — `(1,1) equipe_manutencao` | `equipe_manutencao` | Absorvido via `FK id_secretaria NOT NULL` → `secretaria_orgao(id_secretaria)` | **Regra 4** (Relacionamento 1:N) |
| R06 | `mantem_frota` | Relacionamento 1:N | `secretaria_orgao (0,N)` — `(1,1) veiculo_frota` | `veiculo_frota` | Absorvido via `FK id_secretaria NOT NULL` → `secretaria_orgao(id_secretaria)` | **Regra 4** (Relacionamento 1:N) |
| R07 | `e_composta_por` | Relacionamento 1:N | `equipe_manutencao (1,N)` — `(1,1) funcionario` | `funcionario` | Absorvido via `FK id_equipe NOT NULL` → `equipe_manutencao(id_equipe)` | **Regra 4** (Relacionamento 1:N) |
| R08 | `gera` | Relacionamento 1:N | `ocorrencia (0,N)` — `(1,1) ordem_servico` | `ordem_servico` | Absorvido via `FK id_ocorrencia NOT NULL` → `ocorrencia(id_ocorrencia)` | **Regra 4** (Relacionamento 1:N) |
| R09 | `executa` | Relacionamento 1:N | `equipe_manutencao (0,N)` — `(1,1) ordem_servico` | `ordem_servico` | Absorvido via `FK id_equipe NOT NULL` → `equipe_manutencao(id_equipe)` | **Regra 4** (Relacionamento 1:N) |
| R10 | `desloca_para` | Relacionamento 1:N | `veiculo_frota (0,N)` — `(1,1) ordem_servico` | `ordem_servico` | Absorvido via `FK placa_veiculo NOT NULL` → `veiculo_frota(placa_veiculo)` | **Regra 4** (Relacionamento 1:N) |
| R11 | `recebe` | Relacionamento 1:1 | `ordem_servico (0,1)` — `(1,1) avaliacao_atendimento` | `avaliacao_atendimento` | Absorvido no lado total `(1,1)` via `FK id_os NOT NULL UNIQUE` → `ordem_servico(id_os)` | **Regra 3** (Relacionamento 1:1) |
| R12 | `registra_avaliacao` | Relacionamento 1:N | `cidadao (0,N)` — `(1,1) avaliacao_atendimento` | `avaliacao_atendimento` | Absorvido via `FK id_cidadao NOT NULL` → `cidadao(id_cidadao)` | **Regra 4** (Relacionamento 1:N) |
| R13 | `consome` | Relacionamento N:M | `ordem_servico (0,N)` — `(0,N) material_insumo` | `consumo_material_os` | Gera nova tabela associativa com `PK(id_os, id_material)` + atributos `quantidade_utilizada`, `data_baixa_estoque` | **Regra 5** (Relacionamento N:M) |
| R14 | `fornece` | Relacionamento N:M | `fornecedor (1,N)` — `(1,N) material_insumo` | `fornecimento_insumo` | Gera nova tabela associativa com `PK(id_fornecedor, id_material, numero_lote_compra)` + atributos `data_entrega`, `quantidade_comprada`, `preco_unitario_compra` | **Regra 5** (Relacionamento N:M) |
