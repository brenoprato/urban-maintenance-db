## 1. Missão

A partir da **spec e do modelo que já existem no repositório**, produzir na pasta **`modelagem/`**:

1. **Diagrama ER detalhado** (notação conceitual brModelo / Chen), com todas as entidades, atributos, chaves identificadoras, relacionamentos nomeados e cardinalidades `(min,max)`.
2. **Lista das tabelas** geradas pelo **mapeamento ER → relacional**, em **formato texto**, com atributos, **chaves primárias (PK)** e **chaves estrangeiras (FK)**.

Você **não inventa o domínio**. Você **formaliza** o que a spec e o modelo já definem. Se algo estiver ambíguo ou inconsistente, registre em `.agents/historico/etapa2/NOTAS.md` e avise o usuário, em vez de decidir sozinho.

## 2. Entradas

Localize a spec e o modelo em [`.agents/spec_bd.md`](file:///home/BRENO/Prog/projects/bd_final/.agents/spec_bd.md). Leia tudo antes de desenhar.

## 3. Requisitos obrigatórios do enunciado (checklist)

- [x] **Pelo menos 10 entidades**, cada uma com nome e atributos (entregues: **12 entidades** e **70 atributos de entidades**).
- [x] **Relacionamentos com nome e cardinalidades** e atributos do relacionamento quando existirem (entregues: **14 relacionamentos** e **6 atributos de relacionamentos**).
- [x] Aplicar as **regras de mapeamento ER → relacional** e **apresentar as tabelas geradas** (entregues: **14 tabelas** e **16 FKs** em `modelagem/tabelas.md` e `modelagem/schema.sql`).
- [x] Entregar: (1) diagrama ER detalhado no padrão **brModelo** (`.brM3`, `.png`, `.svg`, `.pdf` em `modelagem/diagramas/`); (2) lista de tabelas com atributos, PKs e FKs em texto (`modelagem/tabelas.md`).

## 4. Ferramentas (pesquisadas e testadas)

### 4.1 Diagrama ER: **brModelo 3.2 (`modelagem/tools/brModelo.jar` + `modelagem/tools/GeradorBrModelo.java`)** ✅ ferramenta principal

Por quê:
- É o **padrão visual do ensino brasileiro de Banco de Dados** (Heuser / brModelo): retângulos com sombra projetada para entidades, losangos para relacionamentos, árvores verticais compactas de atributos com círculos preenchidos (`───●`) para atributos identificadores (PK) e círculos vazados (`───○`) para atributos simples, e cardinalidades `(min,max)` posicionadas junto às bordas das entidades.
- A engine interna do `brModelo.jar` (`diagramas.conceitual.DiagramaConceitual`, `Entidade`, `Relacionamento`, `Atributo`, `Ligacao`, `Cardinalidade` e `controlador.apoios.GuardaPadraoBrM`) **pode ser operada 100% via linha de comando em modo headless (`-Djava.awt.headless=true`)** por meio do gerador Java [`modelagem/tools/GeradorBrModelo.java`](file:///home/BRENO/Prog/projects/bd_final/modelagem/tools/GeradorBrModelo.java).
- Gera simultaneamente:
  1. **Arquivos nativos `.brM3`** (`GuardaPadraoBrM`) que abrem diretamente na interface gráfica do `brModelo.jar` (**Sistema → Abrir**) para inspeção ou edição manual;
  2. **PNG em alta resolução (2x)** renderizado pelo próprio método nativo `DiagramaConceitual.ExternalPaint(Graphics2D)` do `brModelo.jar`;
  3. **SVG vetorial e PDF** (via `rsvg-convert`) nas coordenadas exatas calculadas pelo `brModelo.jar`.

Pré-requisitos e execução (a partir de `modelagem/`):
```bash
make diagramas
make validar
```

Diretrizes técnicas para manutenção do `GeradorBrModelo.java`:
- **Instanciação Headless do `Editor`:** Sobrescreva `NoAction()` e `PerformInspectorFor(FormaElementar)` como *no-op* em `controlador.Editor` e chame `ed.setShowDiagramas(new controlador.Mostrador())`. Instancie `new DiagramaConceitual(ed)` diretamente (sem subclasse anônima) para manter compatibilidade binária na serialização Java de `GuardaPadraoBrM` (`.brM3`). Remova `config.chc` temporário e invoque `System.exit(0)` no fim do `main`.
- **Atributos (`Atributo`) e Alinhamento (`DoAnyThing(123)`):** Adicione atributos com `diag.ExternalRealiseComando(Controler.Comandos.cmdAtributo, pt)`, marque chaves com `a.setIdentificador(true)`, limpe a seleção (`diag.getItensSelecionados().clear()`) e chame `forma.DoAnyThing(123)` (`CONST_DO_ORGATTR`).
- **Ligações (`Ligacao`) e Cardinalidades (`Cardinalidade`):** Em `SuperLinha.Inicie(n, ptEnt, ptRel)`, `PontaA` (índice `0`) fica em `ptRel` e `PontaB` (índice `n+1`) fica em `ptEnt`. Vincule com `lig.FormasALigar = new Forma[]{fRel, fEnt}; lig.Ligar();`. Pré-inicialize `card.setWidth(29)` e `card.setHeight(15)` antes de chamar `card.Posicione()`, e execute `diag.ExternalPaint(g2)` antes de serializar o `.brM3`.
- **Organização dos diagramas:** Gere `04-modelo-completo` cobrindo **todo o modelo** (todas as entidades, todos os atributos, atributos identificadores `●`, relacionamentos e cardinalidades), `00-visao-geral` **sem atributos** (apenas entidades, relacionamentos e cardinalidades para visão macro da topologia) e os diagramas modulares por subdomínio (`01-demandas-cidadao`, `02-estrutura-operacional`, `03-execucao-suprimentos`).

### 4.2 Alternativas e quando usar

| Ferramenta | Uso | Limitação |
|---|---|---|
| **brModelo (`brModelo.jar` + `GeradorBrModelo.java`)** | **Ferramenta principal adotada.** Gera `.brM3` nativo editável + `.png`, `.svg` e `.pdf` no padrão clássico Heuser/brModelo. | Requer manter as coordenadas da grade ortogonal em `GeradorBrModelo.java`. |
| **PlantUML (`@startchen`)** | Alternativa rápida em texto puro para notação de Chen com elipses. | O layout automático do Graphviz espalha elipses de atributos ao redor das entidades e não segue a estética de árvore compacta (`───●` / `───○`) do brModelo. |
| **Graphviz (DOT) direto** | Plano B para grafos customizados. | Verboso; não gera arquivos `.brM3` compatíveis com o brModelo. |
| **Mermaid `erDiagram`** | Prévia rápida de referência em Markdown (usado na seção 2.3 da `spec_bd.md`). | É notação **pé de galinha** (crow's foot), **não é Chen/brModelo**. Use apenas como resumo visual complementar em Markdown, **nunca como entrega principal do diagrama ER conceitual**. |

### 4.3 Validação do mapeamento: SQL DDL em SQLite (ou PostgreSQL)

Escreva `modelagem/schema.sql` com `CREATE TABLE` refletindo **exatamente** a lista de tabelas e execute para provar que PKs/FKs são consistentes:

```bash
sqlite3 :memory: ".read schema.sql" ".tables"
sqlite3 :memory: "PRAGMA foreign_keys=ON;" ".read schema.sql" "PRAGMA foreign_key_check;"
```

## 5. Procedimento de mapeamento ER → relacional (aplicar nesta ordem)

Siga as regras clássicas de Elmasri & Navathe (7 passos + especialização). **Documente qual regra foi aplicada em cada tabela** (coluna "Regra" na lista final).

1. **Entidade forte** → uma tabela; atributos simples e os componentes dos compostos viram colunas; a PK é o atributo-chave.
2. **Entidade fraca** → tabela com os atributos próprios + **FK da entidade dona**; **PK = (PK da dona, chave parcial)**.
3. **Relacionamento 1:1** → colocar a FK no lado de **participação total** (ou fundir tabelas, se ambos totais); atributos do relacionamento vão junto. Garanta `UNIQUE` na FK.
4. **Relacionamento 1:N** → FK no lado **N** apontando para o lado 1; atributos do relacionamento vão para o lado N.
5. **Relacionamento N:M** → **nova tabela associativa**; PK composta pelas FKs das duas entidades (mais um atributo discriminador se o mesmo par puder repetir, como `numero_lote_compra`); atributos do relacionamento viram colunas.
6. **Atributo multivalorado** → **nova tabela** com FK da entidade + o valor; PK = (FK, valor).
7. **Relacionamento n-ário (n > 2)** → **nova tabela** com FK de cada entidade participante; defina a PK pelas cardinalidades.
8. **Especialização/generalização** → escolha **uma** estratégia por hierarquia e **justifique em `.agents/historico/etapa2/NOTAS.md`**.
9. **Atributos derivados** → **não** viram coluna (registre que são calculados). Exceção justificada em `.agents/historico/etapa2/NOTAS.md`.

Convenções: nomes de tabelas, entidades, relacionamentos e colunas em `snake_case` minúsculo, sem acentos, no singular, **idênticos em todo o projeto** (`.agents/spec_bd.md`, diagramas `.brM3`/`.png`, `modelagem/tabelas.md` e `modelagem/schema.sql`).

## 6. Estrutura de arquivos e separação entre Projeto (`modelagem/`) e IA/Histórico (`.agents/`)

Todo artefato relativo a agentes de IA, relatórios de execução e notas de decisão deve ficar **exclusivamente dentro de `.agents/`**, mantendo a pasta do projeto (`modelagem/`) limpa apenas com os entregáveis técnicos:

```text
.agents/
├── etapa1.md                           # Instruções do agente da Etapa 1
├── etapa2.md                           # Instruções do agente da Etapa 2 (este arquivo)
├── spec_bd.md                          # Especificação técnica do banco de dados
└── historico/
    └── etapa2/
        ├── NOTAS.md                    # Decisões de modelagem, contagem de elementos e ambiguidades
        └── RELATORIO.md                # Relatório técnico completo do que foi executado na Etapa 2

modelagem/
├── Makefile                            # Alvos: diagramas, validar, abrir-brmodelo, clean
├── tabelas.md                          # Entregável 2 (mapeamento relacional em texto + rastreabilidade)
├── schema.sql                          # DDL SQL que valida o mapeamento
├── tools/
│   ├── brModelo.jar                    # Engine oficial do brModelo 3.2
│   ├── GeradorBrModelo.java            # Gerador programático nativo (.brM3, .png, .svg, .pdf)
│   └── sqlite3                         # Wrapper CLI em Python para sqlite3
└── diagramas/
    ├── 00-visao-geral.brM3             # Visão geral macro sem atributos (entidades + relacionamentos + cardinalidades)
    ├── 01-demandas-cidadao.brM3        # Subdomínio 1 detalhado
    ├── 02-estrutura-operacional.brM3   # Subdomínio 2 detalhado
    ├── 03-execucao-suprimentos.brM3    # Subdomínio 3 detalhado
    ├── 04-modelo-completo.brM3         # Modelo completo unificado com todos os atributos e chaves
    └── out/                            # .png, .svg, .pdf gerados
```

## 7. Qualidade: definição de pronto (DoD)

1. Há **≥ 10 entidades** com nome e atributos (12 entidades e 70 atributos confirmados em `.agents/historico/etapa2/NOTAS.md`).
2. **Todo relacionamento** tem nome (verbo) e cardinalidades `(min,max)` em **ambos** os lados; relacionamentos N:M com atributos próprios aparecem com eles.
3. **Todo elemento da spec/modelo** aparece no diagrama e **nenhum elemento novo** foi inventado sem registrar em `.agents/historico/etapa2/NOTAS.md`.
4. O diagrama segue o padrão **brModelo**, é **legível** (PNG/SVG revisados visualmente, sem cruzamento de linhas), tem versão visão geral sem atributos (`00-visao-geral`), versões detalhadas por subdomínio (`01` a `03`) e modelo completo unificado (`04-modelo-completo`).
5. Toda entidade e todo relacionamento do diagrama tem **tabela correspondente** em `modelagem/tabelas.md`.
6. Cada tabela tem **PK definida**; cada FK aponta para PK real de outra tabela.
7. `modelagem/schema.sql` **executa sem erros** e `PRAGMA foreign_key_check` volta vazio.
8. Nomes **idênticos** (`snake_case` minúsculo no singular) entre `.agents/spec_bd.md`, diagramas, `modelagem/tabelas.md` e `modelagem/schema.sql`.
9. Todos os documentos de histórico/IA (`NOTAS.md`, `RELATORIO.md`) permanecem isolados em `.agents/historico/etapa2/`, sem arquivos de agente dentro de `modelagem/`.

## 8. Regras de trabalho

- Mantenha **fonte única da verdade**: [`.agents/spec_bd.md`](file:///home/BRENO/Prog/projects/bd_final/.agents/spec_bd.md) e [`modelagem/tools/GeradorBrModelo.java`](file:///home/BRENO/Prog/projects/bd_final/modelagem/tools/GeradorBrModelo.java) (gerando `modelagem/diagramas/*.brM3`) definem o modelo; [`modelagem/tabelas.md`](file:///home/BRENO/Prog/projects/bd_final/modelagem/tabelas.md) e [`modelagem/schema.sql`](file:///home/BRENO/Prog/projects/bd_final/modelagem/schema.sql) derivam do mesmo modelo.
- Nunca crie novos arquivos `AGENTS.md` fora de `.agents/` (apenas `.agents/etapa1.md` e `.agents/etapa2.md` são os arquivos de instrução de agentes).
- Registre notas e relatórios exclusivamente em `.agents/historico/`.
