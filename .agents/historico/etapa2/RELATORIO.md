# Relatório de Execução e Documentação Técnica — Etapa 2 (`RELATORIO.md`)

**Projeto:** Sistema de Monitoramento e Manutenção Colaborativa de Infraestrutura Urbana (ODS 11 e ODS 9)  
**Especificação Base:** [`.agents/spec_bd.md`](file:///home/BRENO/Prog/projects/bd_final/.agents/spec_bd.md)  
**Diretrizes da Etapa:** [`.agents/etapa2.md`](file:///home/BRENO/Prog/projects/bd_final/.agents/etapa2.md) · **Notas Técnicas:** [`.agents/historico/etapa2/NOTAS.md`](file:///home/BRENO/Prog/projects/bd_final/.agents/historico/etapa2/NOTAS.md)

---

## 1. Resumo Executivo do Que Foi Realizado

A Etapa 2 teve como objetivo formalizar o **Modelo Entidade-Relacionamento (MER)** conceitual e realizar o **mapeamento ER → Relacional** completo e validado do sistema especificado em [`.agents/spec_bd.md`](file:///home/BRENO/Prog/projects/bd_final/.agents/spec_bd.md), organizando os entregáveis técnicos na pasta [`modelagem/`](file:///home/BRENO/Prog/projects/bd_final/modelagem) e o histórico/documentação de IA em [`.agents/historico/etapa2/`](file:///home/BRENO/Prog/projects/bd_final/.agents/historico/etapa2).

Durante a execução, foram cumpridas as seguintes frentes de trabalho:

1. **Automação Nativa do brModelo 3.2 (`brModelo.jar`):**
   - Inicialmente testou-se a geração via PlantUML (`@startchen`), cujo layout automático baseado em Graphviz (elipses espalhadas ao redor das entidades) não correspondia ao padrão visual acadêmico brasileiro do **brModelo**.
   - Para atender exatamente ao padrão do **brModelo**, incorporou-se o binário oficial [`modelagem/tools/brModelo.jar`](file:///home/BRENO/Prog/projects/bd_final/modelagem/tools/brModelo.jar) (v3.2) e desenvolveu-se o gerador programático em Java [`modelagem/tools/GeradorBrModelo.java`](file:///home/BRENO/Prog/projects/bd_final/modelagem/tools/GeradorBrModelo.java), que opera a engine interna do brModelo em modo *headless* (`-Djava.awt.headless=true`), sem necessidade de interação manual com GUI.
   - O gerador produz tanto os arquivos nativos **`.brM3`** (100% compatíveis e editáveis no aplicativo desktop do brModelo) quanto as exportações diretas em **`.png`** (alta definição 2x via `DiagramaConceitual.ExternalPaint`), **`.svg`** (vetorial) e **`.pdf`** (via `rsvg-convert`).

2. **Diagramas Conceituais ER Completos e Modulares (`modelagem/diagramas/`):**
   - Construção de **5 diagramas** em grade ortogonal planar $4 \times 3$ com **zero cruzamento de linhas**:
     - [`04-modelo-completo`](file:///home/BRENO/Prog/projects/bd_final/modelagem/diagramas/out/04-modelo-completo.png): **Modelo Completo Unificado**, contendo todas as **12 entidades**, **70 atributos de entidades** (12 atributos identificadores `●` + 58 atributos descritivos `○`), **14 relacionamentos**, **6 atributos próprios de relacionamentos** e **28 cardinalidades `(min,max)`**.
     - [`00-visao-geral`](file:///home/BRENO/Prog/projects/bd_final/modelagem/diagramas/out/00-visao-geral.png): **Visão Geral Macro (sem atributos)**, exibindo de forma limpa apenas as **12 entidades**, os **14 relacionamentos** e as **28 cardinalidades `(min,max)`**.
     - [`01-demandas-cidadao`](file:///home/BRENO/Prog/projects/bd_final/modelagem/diagramas/out/01-demandas-cidadao.png): Recorte do Subdomínio 1 (Atendimento ao Cidadão, Geolocalização por CEP, Ocorrências, Anexos de Mídia e Avaliação).
     - [`02-estrutura-operacional`](file:///home/BRENO/Prog/projects/bd_final/modelagem/diagramas/out/02-estrutura-operacional.png): Recorte do Subdomínio 2 (Secretarias/Órgãos, Equipes de Manutenção, Funcionários e Frota de Veículos).
     - [`03-execucao-suprimentos`](file:///home/BRENO/Prog/projects/bd_final/modelagem/diagramas/out/03-execucao-suprimentos.png): Recorte do Subdomínio 3 (Ordens de Serviço, Almoxarifado de Materiais/Insumos e Fornecedores).

3. **Mapeamento ER → Relacional Documentado (`modelagem/tabelas.md`):**
   - Aplicação sistemática das regras clássicas de Elmasri & Navathe (Regras 1, 3, 4 e 5) gerando **14 tabelas relacionais** (12 tabelas de entidades + 2 tabelas associativas `consumo_material_os` e `fornecimento_insumo`).
   - Documentação detalhada em [`modelagem/tabelas.md`](file:///home/BRENO/Prog/projects/bd_final/modelagem/tabelas.md) com tabelas individuais de colunas/tipos/restrições, resumo em notação linear e matriz completa de rastreabilidade (`E01–E12` e `R01–R14`).

4. **Validação Executável em SQL DDL (`modelagem/schema.sql`):**
   - Implementação do script [`modelagem/schema.sql`](file:///home/BRENO/Prog/projects/bd_final/modelagem/schema.sql) com todas as 14 tabelas, chaves primárias simples e compostas, 16 chaves estrangeiras com políticas de integridade referencial (`ON DELETE` / `ON UPDATE`), restrições `UNIQUE` e `CHECK`.
   - Validação automatizada via SQLite (`PRAGMA foreign_keys = ON;` e `PRAGMA foreign_key_check;`) retornando zero erros.

---

## 2. Métricas Consolidadas do Modelo

| Categoria | Total | Elementos Contabilizados |
|---|---|---|
| **Entidades Fortes** | **12** | `regiao_cep` (5 atr.), `cidadao` (6 atr.), `ocorrencia` (9 atr.), `anexo_midia` (4 atr.), `secretaria_orgao` (6 atr.), `equipe_manutencao` (5 atr.), `funcionario` (6 atr.), `veiculo_frota` (6 atr.), `ordem_servico` (7 atr.), `material_insumo` (5 atr.), `fornecedor` (6 atr.), `avaliacao_atendimento` (5 atr.) |
| **Atributos de Entidades** | **70** | **12** chaves primárias (`●`) + **58** atributos descritivos (`○`) |
| **Relacionamentos Binários** | **14** | **1** relacionamento `1:1` (`recebe`), **11** relacionamentos `1:N` (`endereco_moradia`, `local_do_problema`, `reporta`, `possui`, `coordena`, `mantem_frota`, `e_composta_por`, `gera`, `executa`, `desloca_para`, `registra_avaliacao`) e **2** relacionamentos `N:M` (`consome`, `fornece`) |
| **Atributos de Relacionamentos** | **6** | **2** em `consome` (`quantidade_utilizada`, `data_baixa_estoque`) + **4** em `fornece` (`● numero_lote_compra` [PK parcial], `data_entrega`, `quantidade_comprada`, `preco_unitario_compra`) |
| **Tabelas Relacionais** | **14** | **12** tabelas de entidades + **2** tabelas associativas (`consumo_material_os`, `fornecimento_insumo`) |
| **Chaves Estrangeiras (FKs)** | **16** | **12** FKs absorvidas nas tabelas de entidades (`1:1` e `1:N`) + **4** FKs compondo as PKs das tabelas associativas (`N:M`) |

---

## 3. Detalhamento Técnico: Como o `brModelo.jar` Foi Automatizado via CLI

Originalmente, o guia [`.agents/etapa2.md`](file:///home/BRENO/Prog/projects/bd_final/.agents/etapa2.md) considerava o brModelo uma ferramenta exclusivamente gráfica (*"sem automação por CLI; um agente não consegue operá-lo"*). Para superar essa limitação e entregar diagramas genuínos do brModelo 3.2:

1. **Inspeção de Bytecode e API Interna (`brModelo.jar`):**
   - Identificou-se que o pacote `diagramas.conceitual` (`DiagramaConceitual`, `Entidade`, `Relacionamento`, `Atributo`, `Ligacao`, `Cardinalidade`) e o controlador `controlador.Editor` podem ser instanciados sem janela Swing ativa caso os métodos `Editor.NoAction()` e `Editor.PerformInspectorFor(FormaElementar)` sejam sobrescritos como *no-op* e `ed.setShowDiagramas(new controlador.Mostrador())` seja inicializado.
   - Evitou-se criar subclasses anônimas de `DiagramaConceitual` para garantir que a serialização padrão do Java usada por `controlador.apoios.GuardaPadraoBrM` grave arquivos `.brM3` com a assinatura de classe exata (`diagramas.conceitual.DiagramaConceitual`), permitindo abri-los em qualquer instalação do `brModelo.jar`.

2. **Organização Automática de Atributos (`CONST_DO_ORGATTR = 123`):**
   - Cada atributo é anexado à sua entidade ou relacionamento via `diag.ExternalRealiseComando(Controler.Comandos.cmdAtributo, pt)` e organizado com a chamada nativa `DoAnyThing(123)`, que posiciona todos os atributos em árvore vertical compacta abaixo da forma com marcadores `●` (identificador) e `○` (simples).

3. **Roteamento Ortogonal Planar (L e Z) e Posicionamento de Cardinalidades:**
   - Em `SuperLinha.Inicie(n, ptEnt, ptRel)`, o ponto `PontaA` (índice `0`) ancora no `Relacionamento` e `PontaB` (índice `n+1`) ancora na `Entidade`. O vínculo lógico é firmado via `lig.FormasALigar = new Forma[]{fRel, fEnt}; lig.Ligar();`.
   - Como `PreCardinalidade.DoPaint(Graphics2D)` recalcula largura/altura do texto (`29x15` px) na primeira pintura e reposiciona a cardinalidade se `MovimentacaoManual` for `false`, o gerador pré-inicializa `setWidth(29)` e `setHeight(15)` antes de invocar `Posicione()` e executa `diag.ExternalPaint(g2)` antes de serializar o `.brM3`, garantindo posicionamento exato das 28 cardinalidades tanto no `.png`/`.svg`/`.pdf` quanto ao abrir o `.brM3` no aplicativo.

---

## 4. Alinhamento e Separação entre Projeto (`modelagem/`) e IA/Histórico (`.agents/`)

Conforme definido na arquitetura do repositório:

1. **Pasta [`modelagem/`](file:///home/BRENO/Prog/projects/bd_final/modelagem):**
   - Contém **exclusivamente** os artefatos técnicos de modelagem do banco de dados (`diagramas/`, `tabelas.md`, `schema.sql`, `Makefile` e `tools/`), pronta para conviver com a futura implementação do banco de dados e da aplicação.
2. **Pasta [`.agents/`](file:///home/BRENO/Prog/projects/bd_final/.agents):**
   - Centraliza toda a documentação de agentes e histórico de IA: apenas os dois arquivos de instrução de agentes ([`.agents/etapa1.md`](file:///home/BRENO/Prog/projects/bd_final/.agents/etapa1.md) e [`.agents/etapa2.md`](file:///home/BRENO/Prog/projects/bd_final/.agents/etapa2.md)), a especificação ([`.agents/spec_bd.md`](file:///home/BRENO/Prog/projects/bd_final/.agents/spec_bd.md)) e a subpasta de histórico [`.agents/historico/etapa2/`](file:///home/BRENO/Prog/projects/bd_final/.agents/historico/etapa2) contendo [`NOTAS.md`](file:///home/BRENO/Prog/projects/bd_final/.agents/historico/etapa2/NOTAS.md) e [`RELATORIO.md`](file:///home/BRENO/Prog/projects/bd_final/.agents/historico/etapa2/RELATORIO.md).
