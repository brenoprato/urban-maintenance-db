# Modelo Lógico Relacional e Tabelas

## 1. Modelo Lógico Relacional

**Convenção:** `TABELA(<u>chave_primaria</u>, #chave_estrangeira, atributo_1, atributo_2, ...)`

1. **regiao_cep**(<u>cep</u>, cidade, bairro, logradouro, ponto_referencia_regiao)
2. **cidadao**(<u>id_cidadao</u>, #cep_moradia, cpf, nome_completo, email, telefone, numero_residencia)
   - `#cep_moradia` → `regiao_cep(cep)`
3. **ocorrencia**(<u>id_ocorrencia</u>, #id_cidadao, #cep_ocorrencia, protocolo, titulo, descricao_detalhada, categoria_problema, nivel_gravidade, data_abertura, status_atual, numero_aproximado)
   - `#id_cidadao` → `cidadao(id_cidadao)`
   - `#cep_ocorrencia` → `regiao_cep(cep)`
4. **anexo_midia**(<u>id_anexo</u>, #id_ocorrencia, url_arquivo, tipo_midia, data_upload)
   - `#id_ocorrencia` → `ocorrencia(id_ocorrencia)`
5. **secretaria_orgao**(<u>id_secretaria</u>, nome_secretaria, sigla, nome_secretario_responsavel, telefone_contato, orcamento_mensal)
6. **equipe_manutencao**(<u>id_equipe</u>, #id_secretaria, nome_equipe, categoria_problema_atendida, turno_trabalho, status_disponibilidade)
   - `#id_secretaria` → `secretaria_orgao(id_secretaria)`
7. **funcionario**(<u>matricula_funcionario</u>, #id_equipe, cpf, nome_funcionario, cargo_funcao, data_admissao, telefone_corporativo)
   - `#id_equipe` → `equipe_manutencao(id_equipe)`
8. **veiculo_frota**(<u>placa_veiculo</u>, #id_secretaria, tipo_veiculo, modelo, ano, capacidade_carga, status_manutencao)
   - `#id_secretaria` → `secretaria_orgao(id_secretaria)`
9. **ordem_servico**(<u>id_os</u>, #id_ocorrencia, #id_equipe, #placa_veiculo, data_emissao, data_inicio_execucao, data_conclusao, relatorio_tecnico, custo_mao_de_obra, status_os)
   - `#id_ocorrencia` → `ocorrencia(id_ocorrencia)`
   - `#id_equipe` → `equipe_manutencao(id_equipe)`
   - `#placa_veiculo` → `veiculo_frota(placa_veiculo)`
10. **material_insumo**(<u>id_material</u>, nome_material, unidade_medida, quantidade_estoque_atual, custo_unitario_medio)
11. **fornecedor**(<u>id_fornecedor</u>, cnpj, razao_social, telefone, email_comercial, cidade_sede)
12. **avaliacao_atendimento**(<u>id_avaliacao</u>, #id_os, #id_cidadao, nota_satisfacao, comentario_cidadao, problema_resolvido_boolean, data_avaliacao)
    - `#id_os` → `ordem_servico(id_os)` *(UNIQUE)*
    - `#id_cidadao` → `cidadao(id_cidadao)`
13. **consumo_material_os**(<u>#id_os</u>, <u>#id_material</u>, quantidade_utilizada, data_baixa_estoque)
    - `#id_os` → `ordem_servico(id_os)`
    - `#id_material` → `material_insumo(id_material)`
14. **fornecimento_insumo**(<u>#id_fornecedor</u>, <u>#id_material</u>, <u>numero_lote_compra</u>, data_entrega, quantidade_comprada, preco_unitario_compra)
    - `#id_fornecedor` → `fornecedor(id_fornecedor)`
    - `#id_material` → `material_insumo(id_material)`

---

## 2. Tabelas

### 1. `regiao_cep`
| Coluna | Tipo | Restrições |
|---|---|---|
| `cep` | `CHAR(8)` | **PK** |
| `cidade` | `VARCHAR(100)` | `NOT NULL` |
| `bairro` | `VARCHAR(100)` | `NOT NULL` |
| `logradouro` | `VARCHAR(150)` | `NOT NULL` |
| `ponto_referencia_regiao` | `VARCHAR(200)` | `NULL` |

### 2. `cidadao`
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_cidadao` | `INTEGER` | **PK** |
| `cep_moradia` | `CHAR(8)` | **FK** → `regiao_cep(cep)`, `NOT NULL` |
| `cpf` | `CHAR(11)` | `NOT NULL`, `UNIQUE` |
| `nome_completo` | `VARCHAR(150)` | `NOT NULL` |
| `email` | `VARCHAR(150)` | `NOT NULL`, `UNIQUE` |
| `telefone` | `VARCHAR(20)` | `NOT NULL` |
| `numero_residencia` | `VARCHAR(20)` | `NOT NULL` |

### 3. `ocorrencia`
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_ocorrencia` | `INTEGER` | **PK** |
| `id_cidadao` | `INTEGER` | **FK** → `cidadao(id_cidadao)`, `NOT NULL` |
| `cep_ocorrencia` | `CHAR(8)` | **FK** → `regiao_cep(cep)`, `NOT NULL` |
| `protocolo` | `VARCHAR(30)` | `NOT NULL`, `UNIQUE` |
| `titulo` | `VARCHAR(120)` | `NOT NULL` |
| `descricao_detalhada` | `TEXT` | `NOT NULL` |
| `categoria_problema` | `VARCHAR(60)` | `NOT NULL` |
| `nivel_gravidade` | `VARCHAR(20)` | `NOT NULL` |
| `data_abertura` | `TIMESTAMP` | `NOT NULL` |
| `status_atual` | `VARCHAR(30)` | `NOT NULL` |
| `numero_aproximado` | `VARCHAR(20)` | `NULL` |

### 4. `anexo_midia`
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_anexo` | `INTEGER` | **PK** |
| `id_ocorrencia` | `INTEGER` | **FK** → `ocorrencia(id_ocorrencia)`, `NOT NULL`, `ON DELETE CASCADE` |
| `url_arquivo` | `VARCHAR(500)` | `NOT NULL` |
| `tipo_midia` | `VARCHAR(30)` | `NOT NULL` |
| `data_upload` | `TIMESTAMP` | `NOT NULL` |

### 5. `secretaria_orgao`
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_secretaria` | `INTEGER` | **PK** |
| `nome_secretaria` | `VARCHAR(120)` | `NOT NULL`, `UNIQUE` |
| `sigla` | `VARCHAR(15)` | `NOT NULL`, `UNIQUE` |
| `nome_secretario_responsavel` | `VARCHAR(150)` | `NOT NULL` |
| `telefone_contato` | `VARCHAR(20)` | `NOT NULL` |
| `orcamento_mensal` | `DECIMAL(14,2)` | `NOT NULL`, `CHECK (orcamento_mensal >= 0)` |

### 6. `equipe_manutencao`
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_equipe` | `INTEGER` | **PK** |
| `id_secretaria` | `INTEGER` | **FK** → `secretaria_orgao(id_secretaria)`, `NOT NULL` |
| `nome_equipe` | `VARCHAR(100)` | `NOT NULL`, `UNIQUE` |
| `categoria_problema_atendida` | `VARCHAR(60)` | `NOT NULL` |
| `turno_trabalho` | `VARCHAR(30)` | `NOT NULL` |
| `status_disponibilidade` | `VARCHAR(30)` | `NOT NULL` |

### 7. `funcionario`
| Coluna | Tipo | Restrições |
|---|---|---|
| `matricula_funcionario` | `VARCHAR(20)` | **PK** |
| `id_equipe` | `INTEGER` | **FK** → `equipe_manutencao(id_equipe)`, `NOT NULL` |
| `cpf` | `CHAR(11)` | `NOT NULL`, `UNIQUE` |
| `nome_funcionario` | `VARCHAR(150)` | `NOT NULL` |
| `cargo_funcao` | `VARCHAR(80)` | `NOT NULL` |
| `data_admissao` | `DATE` | `NOT NULL` |
| `telefone_corporativo` | `VARCHAR(20)` | `NULL` |

### 8. `veiculo_frota`
| Coluna | Tipo | Restrições |
|---|---|---|
| `placa_veiculo` | `CHAR(7)` | **PK** |
| `id_secretaria` | `INTEGER` | **FK** → `secretaria_orgao(id_secretaria)`, `NOT NULL` |
| `tipo_veiculo` | `VARCHAR(50)` | `NOT NULL` |
| `modelo` | `VARCHAR(80)` | `NOT NULL` |
| `ano` | `INTEGER` | `NOT NULL`, `CHECK (ano >= 1950)` |
| `capacidade_carga` | `DECIMAL(10,2)` | `NOT NULL`, `CHECK (capacidade_carga >= 0)` |
| `status_manutencao` | `VARCHAR(30)` | `NOT NULL` |

### 9. `ordem_servico`
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_os` | `INTEGER` | **PK** |
| `id_ocorrencia` | `INTEGER` | **FK** → `ocorrencia(id_ocorrencia)`, `NOT NULL` |
| `id_equipe` | `INTEGER` | **FK** → `equipe_manutencao(id_equipe)`, `NOT NULL` |
| `placa_veiculo` | `CHAR(7)` | **FK** → `veiculo_frota(placa_veiculo)`, `NOT NULL` |
| `data_emissao` | `TIMESTAMP` | `NOT NULL` |
| `data_inicio_execucao` | `TIMESTAMP` | `NULL` |
| `data_conclusao` | `TIMESTAMP` | `NULL` |
| `relatorio_tecnico` | `TEXT` | `NULL` |
| `custo_mao_de_obra` | `DECIMAL(12,2)` | `NULL`, `CHECK (custo_mao_de_obra >= 0)` |
| `status_os` | `VARCHAR(30)` | `NOT NULL` |

### 10. `material_insumo`
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_material` | `INTEGER` | **PK** |
| `nome_material` | `VARCHAR(120)` | `NOT NULL`, `UNIQUE` |
| `unidade_medida` | `VARCHAR(20)` | `NOT NULL` |
| `quantidade_estoque_atual` | `DECIMAL(12,3)` | `NOT NULL`, `CHECK (quantidade_estoque_atual >= 0)` |
| `custo_unitario_medio` | `DECIMAL(12,2)` | `NOT NULL`, `CHECK (custo_unitario_medio >= 0)` |

### 11. `fornecedor`
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_fornecedor` | `INTEGER` | **PK** |
| `cnpj` | `CHAR(14)` | `NOT NULL`, `UNIQUE` |
| `razao_social` | `VARCHAR(150)` | `NOT NULL` |
| `telefone` | `VARCHAR(20)` | `NOT NULL` |
| `email_comercial` | `VARCHAR(150)` | `NOT NULL` |
| `cidade_sede` | `VARCHAR(100)` | `NOT NULL` |

### 12. `avaliacao_atendimento`
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_avaliacao` | `INTEGER` | **PK** |
| `id_os` | `INTEGER` | **FK** → `ordem_servico(id_os)`, `NOT NULL`, `UNIQUE` |
| `id_cidadao` | `INTEGER` | **FK** → `cidadao(id_cidadao)`, `NOT NULL` |
| `nota_satisfacao` | `INTEGER` | `NOT NULL`, `CHECK (nota_satisfacao BETWEEN 1 AND 5)` |
| `comentario_cidadao` | `TEXT` | `NULL` |
| `problema_resolvido_boolean` | `BOOLEAN` | `NOT NULL` |
| `data_avaliacao` | `TIMESTAMP` | `NOT NULL` |

### 13. `consumo_material_os`
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_os` | `INTEGER` | **PK**, **FK** → `ordem_servico(id_os)`, `NOT NULL`, `ON DELETE CASCADE` |
| `id_material` | `INTEGER` | **PK**, **FK** → `material_insumo(id_material)`, `NOT NULL` |
| `quantidade_utilizada` | `DECIMAL(12,3)` | `NOT NULL`, `CHECK (quantidade_utilizada > 0)` |
| `data_baixa_estoque` | `TIMESTAMP` | `NOT NULL` |

### 14. `fornecimento_insumo`
| Coluna | Tipo | Restrições |
|---|---|---|
| `id_fornecedor` | `INTEGER` | **PK**, **FK** → `fornecedor(id_fornecedor)`, `NOT NULL` |
| `id_material` | `INTEGER` | **PK**, **FK** → `material_insumo(id_material)`, `NOT NULL` |
| `numero_lote_compra` | `VARCHAR(50)` | **PK**, `NOT NULL` |
| `data_entrega` | `DATE` | `NOT NULL` |
| `quantidade_comprada` | `DECIMAL(12,3)` | `NOT NULL`, `CHECK (quantidade_comprada > 0)` |
| `preco_unitario_compra` | `DECIMAL(12,2)` | `NOT NULL`, `CHECK (preco_unitario_compra >= 0)` |
