# Notas Técnicas, Decisões de Modelagem e Ambiguidades (`NOTAS.md`)

**Sistema:** Monitoramento e Manutenção Colaborativa de Infraestrutura Urbana (ODS 11 e ODS 9)  
**Fonte Primária:** `.agents/spec_bd.md`  
**Diretrizes da Etapa:** `.agents/etapa2.md`

---

## 1. Contagem e Verificação de Elementos do Modelo

| Métrica | Quantidade | Detalhamento |
|---|---|---|
| **Entidades no Diagrama ER** | **12** (requisito mínimo: $\ge 10$) | `regiao_cep` (5 atr.), `cidadao` (6 atr.), `ocorrencia` (9 atr.), `anexo_midia` (4 atr.), `secretaria_orgao` (6 atr.), `equipe_manutencao` (5 atr.), `funcionario` (6 atr.), `veiculo_frota` (6 atr.), `ordem_servico` (7 atr.), `material_insumo` (5 atr.), `fornecedor` (6 atr.), `avaliacao_atendimento` (5 atr.) |
| **Atributos de Entidades** | **70** | Todos simples/atômicos conforme especificação, sendo **12 atributos identificadores** (`●` no brModelo) e **58 atributos descritivos** (`○` no brModelo) |
| **Relacionamentos no Diagrama ER** | **14** | 1 relacionamento `1:1` (`recebe`), 11 relacionamentos `1:N` (`endereco_moradia`, `local_do_problema`, `reporta`, `possui`, `coordena`, `mantem_frota`, `e_composta_por`, `gera`, `executa`, `desloca_para`, `registra_avaliacao`) e 2 relacionamentos `N:M` (`consome`, `fornece`) |
| **Atributos de Relacionamentos** | **6** | 2 em `consome` (`quantidade_utilizada`, `data_baixa_estoque`) e 4 em `fornece` (`numero_lote_compra` [identificador parcial `●`], `data_entrega`, `quantidade_comprada`, `preco_unitario_compra`) |
| **Tabelas no Esquema Relacional** | **14** | 12 tabelas de entidades fortes (com absorção de FKs `1:1` e `1:N`) + 2 tabelas associativas (`consumo_material_os`, `fornecimento_insumo`) |
| **Chaves Estrangeiras (FKs)** | **16** | 12 FKs em tabelas de entidades (`1:1` e `1:N`) + 4 FKs nas 2 tabelas associativas (`N:M`) |

---

## 2. Ambiguidades Identificadas na Especificação e Decisões Adotadas

Seguindo a diretriz da missão (*"Você não inventa o domínio. Você formaliza o que a spec e o modelo já definem. Se algo estiver ambíguo ou inconsistente, registre em `NOTAS.md` e avise o usuário"*), listam-se abaixo os pontos analisados na `spec_bd.md`:

### 2.1. Classificação de `anexo_midia` ("Entidade Forte / Dependente")
- **O que diz a `spec_bd.md`:** Na Seção 2.1, `Anexo_Midia` é rotulada como `*(Entidade Forte / Dependente)*` e possui chave primária substituta própria `id_anexo [PK]`. Na Seção 3 (Etapa 3 da spec), ela é mapeada na seção *A. Tabelas Originadas de Entidades* com `PK: id_anexo` e `FK: #id_ocorrencia referencia Ocorrencia(id_ocorrencia)` (não como chave primária composta `(id_ocorrencia, id_anexo)` de entidade fraca clássica).
- **Decisão formalizada:** Modelada como **entidade forte** dotada de identificador próprio (`id_anexo`), mas com **dependência existencial (cardinalidade mínima `1` / `(1,1)`)** no relacionamento `possui` com `ocorrencia`. No mapeamento relacional (`schema.sql`), aplicaram-se a **Regra 1** e a **Regra 4** com restrição `NOT NULL` e política `ON DELETE CASCADE` na FK `id_ocorrencia`, preservando integralmente tanto a semântica de dependência existencial quanto a estrutura de chaves definida na `spec_bd.md`.

### 2.2. Discriminador `numero_lote_compra [PK parcial]` no relacionamento N:M `fornece`
- **O que diz a `spec_bd.md`:** Na Seção 2.2, o relacionamento N:M `fornece` (entre `Fornecedor` e `Material_Insumo`) possui o atributo `numero_lote_compra [PK parcial]`, além de `data_entrega`, `quantidade_comprada` e `preco_unitario_compra`. Na Seção 3, gera a tabela associativa `fornecimento_insumo` com PK composta `(id_fornecedor, id_material, numero_lote_compra)`.
- **Decisão formalizada:** Conforme a **Regra 5** de Elmasri & Navathe, quando um relacionamento N:M representa eventos recorrentes entre o mesmo par de instâncias (múltiplos lotes de fornecimento do mesmo material pelo mesmo fornecedor), a chave primária da relação associativa inclui o atributo discriminador do relacionamento. Nos diagramas do brModelo, `numero_lote_compra` foi demarcado como atributo identificador (`●`) ligado ao losango `fornece`, refletindo fielmente a indicação `[PK parcial]` da especificação.

### 2.3. Dupla Notação de Cardinalidade na Tabela 2.2 da `spec_bd.md` (`1 : N` vs `(min, max)`)
- **O que diz a `spec_bd.md`:** A coluna *"Cardinalidade (A : B)"* da Tabela 2.2 apresenta simultaneamente duas convenções:
  1. No topo da célula: a **razão de cardinalidade máxima** (`1 : N`, `1 : 1`, `N : N`), onde uma instância de `A` se associa a `N` instâncias de `B`.
  2. Na base da célula: o par de **cardinalidades `(min, max)`** no padrão brModelo/Heuser (`(0,N) : (1,1)`), indicando quantas ocorrências do relacionamento cada entidade participa no mínimo e no máximo.
- **Decisão formalizada:** Todos os diagramas foram construídos utilizando a ferramenta **brModelo 3.2 (`tools/brModelo.jar`)** com a notação `(min,max)` (`(0,1)`, `(1,1)`, `(0,n)`, `(1,n)`) posicionada junto a cada entidade, exatamente como adotado no ensino brasileiro de Bancos de Dados (Heuser / brModelo) e recomendado pela Seção 4.1 de `etapa2.md`.

### 2.4. Padronização de Nomenclatura (`snake_case` minúsculo)
- **O que diz a `spec_bd.md` vs `etapa2.md`:** Na `spec_bd.md`, os nomes das entidades estão grafados em `Pascal_Snake_Case` (`Regiao_CEP`, `Ordem_Servico`, etc.), enquanto todos os atributos e relacionamentos estão em `snake_case` minúsculo. Já a `etapa2.md` exige nomes de tabelas e colunas em `snake_case` minúsculo, sem acentos, no singular, e **nomes idênticos entre diagrama, `tabelas.md` e `schema.sql`** (DoD #8).
- **Decisão formalizada:** Todos os nomes de entidades/tabelas, relacionamentos e colunas foram padronizados em `snake_case` minúsculo e no singular (`regiao_cep`, `cidadao`, `ocorrencia`, `anexo_midia`, `secretaria_orgao`, `equipe_manutencao`, `funcionario`, `veiculo_frota`, `ordem_servico`, `material_insumo`, `fornecedor`, `avaliacao_atendimento`, `consumo_material_os`, `fornecimento_insumo`) de maneira 100% uniforme nos diagramas `.brM3`/`.png`/`.svg`/`.pdf`, em `tabelas.md` e em `schema.sql`.

### 2.5. Preservação dos Nomes de PKs e FKs da `spec_bd.md`
- **O que diz a `spec_bd.md`:** Nove entidades utilizam chaves primárias no padrão `id_<entidade>` (`id_cidadao`, `id_ocorrencia`, `id_anexo`, `id_secretaria`, `id_equipe`, `id_os`, `id_material`, `id_fornecedor`, `id_avaliacao`), enquanto três entidades utilizam chaves naturais consagradas no domínio público/urbano: `cep` (em `regiao_cep`), `matricula_funcionario` (em `funcionario`) e `placa_veiculo` (em `veiculo_frota`). Além disso, as FKs que apontam para `regiao_cep(cep)` recebem sufixo de papel (`cep_moradia` em `cidadao` e `cep_ocorrencia` em `ocorrencia`) para explicitar sua semântica.
- **Decisão formalizada:** Foram mantidos **exatamente** os nomes de PKs e FKs definidos na `spec_bd.md`, garantindo total rastreabilidade com o documento de especificação.

### 2.6. Restrições de Integridade Semântica (Caminhos Cíclicos no Esquema)
Observam-se dois ciclos relacionais legítimos na especificação cujo controle de consistência semântica vai além de chaves estrangeiras declarativas simples (devendo ser garantido por *triggers* ou pela camada de aplicação):
1. **Ciclo `cidadao` — `ocorrencia` — `ordem_servico` — `avaliacao_atendimento`:** A tabela `avaliacao_atendimento` referencia `ordem_servico(id_os)` e `cidadao(id_cidadao)`. Pela regra de negócio (*"Um cidadão pode avaliar várias OS que abriu"*), o `id_cidadao` registrado em `avaliacao_atendimento` deve ser o mesmo `id_cidadao` autor da `ocorrencia` que originou aquela `ordem_servico`.
2. **Ciclo `secretaria_orgao` — `equipe_manutencao` / `veiculo_frota` — `ordem_servico`:** Uma `ordem_servico` aloca uma `equipe_manutencao` e um `veiculo_frota`, ambos subordinados a uma `secretaria_orgao`. Caso a prefeitura exija que a equipe e o veículo alocados em uma OS pertençam à mesma secretaria, tal validação é verificada via consulta/trigger no momento da emissão da OS.

### 2.7. Justificativa sobre Regras de Mapeamento Não Acionadas (Regras 2, 6, 7, 8 e 9)
Para fins de completude acadêmica frente ao procedimento de 9 itens da Seção 5 de `etapa2.md`:
- **Regra 2 (Entidade fraca):** Não há entidade fraca sem PK própria na `spec_bd.md` (ver item 2.1 sobre `anexo_midia`).
- **Regra 6 (Atributo multivalorado):** Na `spec_bd.md`, atributos como `telefone` (em `cidadao`, `fornecedor`) e `telefone_contato`/`telefone_corporativo` foram definidos como atributos simples monovalorados (telefone principal de contato). Já os múltiplos arquivos de mídia de uma ocorrência foram modelados na própria spec como uma entidade dedicada (`anexo_midia`, relacionamento 1:N `possui`).
- **Regra 7 (Relacionamento n-ário, $n > 2$):** Todos os 14 relacionamentos da `spec_bd.md` são binários ($n = 2$). A mobilização de equipe, veículo e ocorrência converge na entidade forte central `ordem_servico`.
- **Regra 8 (Especialização/Generalização):** A especificação não define subtipos/supertipos (ex.: categorias de funcionários são representadas pelo atributo `cargo_funcao` em `funcionario`).
- **Regra 9 (Atributos derivados):** A especificação define `quantidade_estoque_atual` e `custo_unitario_medio` como atributos persistidos na entidade `material_insumo` (atualizados a cada entrada em `fornecimento_insumo` e baixa em `consumo_material_os`), razão pela qual são mantidos como colunas em `material_insumo`.

---

## 3. Geração Nativa com brModelo (`tools/brModelo.jar`) e Organização dos Diagramas (`diagramas/`)

Os diagramas conceituais ER foram construídos utilizando diretamente a engine oficial do **brModelo 3.2 (`tools/brModelo.jar`)** por meio do gerador `tools/GeradorBrModelo.java`, que instancia as classes nativas `diagramas.conceitual.DiagramaConceitual`, `Entidade`, `Relacionamento`, `Atributo`, `Ligacao` e `Cardinalidade`, organiza os atributos no padrão clássico do brModelo (`DoAnyThing(123)` / `CONST_DO_ORGATTR` com `───●` para atributos identificadores e `───○` para atributos simples), serializa os arquivos editáveis `.brM3` via `GuardaPadraoBrM` e exporta `.png` (via `DiagramaConceitual.ExternalPaint(Graphics2D)` em alta resolução), `.svg` e `.pdf`:

1. **`04-modelo-completo` (`.brM3`, `.png`, `.svg`, `.pdf`):** Diagrama ER Conceitual **Completo Unificado** em grade planar $4 \times 3$ (zero cruzamentos de linhas), reunindo todas as **12 entidades**, **70 atributos de entidades** (12 identificadores `●` + 58 descritivos `○`), **14 relacionamentos**, **6 atributos de relacionamentos** e **28 cardinalidades `(min,max)`**.
2. **`00-visao-geral` (`.brM3`, `.png`, `.svg`, `.pdf`):** Visão geral macro **sem atributos** (apenas as **12 entidades**, os **14 relacionamentos** e as **28 cardinalidades `(min,max)`**), para leitura limpa e imediata da topologia do domínio.
3. **`01-demandas-cidadao` (`.brM3`, `.png`, `.svg`, `.pdf`):** Subdomínio 1 (Atendimento ao Cidadão, Geolocalização e Avaliação), detalhando `regiao_cep`, `cidadao`, `ocorrencia`, `anexo_midia`, `avaliacao_atendimento` e `ordem_servico`, e os relacionamentos `endereco_moradia`, `local_do_problema`, `reporta`, `possui`, `gera`, `recebe` e `registra_avaliacao`.
4. **`02-estrutura-operacional` (`.brM3`, `.png`, `.svg`, `.pdf`):** Subdomínio 2 (Gestão Interna, Equipes, RH e Frota), detalhando `secretaria_orgao`, `equipe_manutencao`, `funcionario`, `veiculo_frota` e `ordem_servico`, e os relacionamentos `coordena`, `mantem_frota`, `e_composta_por`, `executa` e `desloca_para`.
5. **`03-execucao-suprimentos` (`.brM3`, `.png`, `.svg`, `.pdf`):** Subdomínio 3 (Ordens de Serviço, Almoxarifado e Fornecedores), detalhando `ordem_servico`, `material_insumo`, `fornecedor` e entidades de fronteira (`ocorrencia`, `equipe_manutencao`, `veiculo_frota`, `avaliacao_atendimento`), cobrindo `gera`, `executa`, `desloca_para`, `recebe`, `consome` e `fornece`.
