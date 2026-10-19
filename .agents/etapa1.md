## 1. Missão

Produzir, em **LaTeX** e seguindo as **normas da ABNT**, um relatório acadêmico que descreve **em detalhes** o problema escolhido pelo grupo para o trabalho de Banco de Dados e indica **a qual ODS (Objetivo de Desenvolvimento Sustentável) da ONU ele pertence**.

O trabalho é **em grupo**. Os nomes e RAs dos integrantes **ainda não foram informados**: use placeholders (ver seção 5) e nunca invente nomes.

O texto da Etapa 1 serve de base para a Etapa 2 (modelo ER, feita por outro agente). Portanto, **não pode contradizer** a spec e o modelo do projeto.

## 2. Entradas (leia antes de escrever)

1. A **spec do problema** e o **modelo** já existentes no repositório. Procure em `../docs/`, `../spec/`, `../README.md` ou arquivos `*.md` na raiz. Se não achar, **pergunte ao usuário o caminho**.
2. Quaisquer instruções da disciplina (nome da disciplina, professor, instituição, curso, semestre, template exigido). Se faltar algo, use placeholder e liste as pendências no final da resposta.

Se a spec não deixar claro o problema, o público afetado ou o escopo do sistema, faça **no máximo uma pergunta objetiva** ao usuário e então prossiga com a melhor suposição declarada.

## 3. Requisitos do enunciado (checklist obrigatório)

- [ ] Texto descrevendo **em detalhes** o problema escolhido.
- [ ] Indicação **explícita** do ODS em que o problema se encaixa, com justificativa.
- [ ] Formato ABNT, em LaTeX, compilando sem erros para PDF.

## 4. Stack e ferramentas

| Item | Escolha | Observação |
|---|---|---|
| Classe | `abntex2` | Padrão de fato para ABNT em LaTeX (NBR 14724 e correlatas). Já vem com `memoir` por baixo. |
| Citações | `abntex2cite` com `[alf]` | Autor-data, padrão ABNT (NBR 10520). Estilo bibliográfico `abntex2-alf` (NBR 6023). |
| Compilação | `latexmk -pdf` | Resolve BibTeX e referências cruzadas automaticamente. |
| Codificação | UTF-8, `\usepackage[T1]{fontenc}`, `\usepackage[utf8]{inputenc}` | Idioma `brazil`. |
| Fonte | `\usepackage{times}` ou `lmodern` | ABNT pede fonte 12 pt legível (Times/Arial são as mais usadas). |
| Extras | `hyperref`, `graphicx`, `booktabs`, `csquotes`, `lipsum` (só para teste) | |

**Verifique antes de começar:** `kpsewhich abntex2.cls`. Se não retornar caminho, o pacote não está instalado. Instale com `sudo apt install texlive-publishers texlive-lang-portuguese texlive-latex-extra` (ou `tlmgr install abntex2`). Se não for possível instalar, avise o usuário e sugira o **Overleaf**, que já traz `abntex2`.

**Se a instituição fornecer um template próprio**, use-o no lugar do modelo genérico e mantenha apenas o conteúdo desta spec. Pergunte ao usuário uma única vez se existe template.

## 5. Estrutura de arquivos

```
etapa1/
├── AGENTS.md
├── main.tex                 # preâmbulo + \input dos capítulos
├── referencias.bib          # BibTeX (só fontes reais e verificadas)
├── dados-grupo.tex          # nomes, RAs, instituição, curso, disciplina
├── pre-textuais/
│   ├── resumo.tex
│   └── abstract.tex         # opcional, só se a disciplina pedir
├── capitulos/
│   ├── 01-introducao.tex
│   ├── 02-problema.tex
│   ├── 03-ods.tex
│   ├── 04-escopo-sistema.tex
│   └── 05-consideracoes-finais.tex
├── figuras/
└── Makefile                 # alvo `pdf` e `clean`
```

### `dados-grupo.tex` (placeholders até o usuário informar)

```latex
\newcommand{\instituicaoNome}{[NOME DA INSTITUIÇÃO]}
\newcommand{\cursoNome}{[CURSO]}
\newcommand{\disciplinaNome}{Banco de Dados}
\newcommand{\professorNome}{[PROFESSOR(A)]}
\newcommand{\cidadeLocal}{[CIDADE]}
\newcommand{\anoTrabalho}{2026}
\newcommand{\tituloTrabalho}{[TÍTULO DO TRABALHO]}
% Integrantes: um \item por pessoa, formato "Nome Completo — RA 000000"
\newcommand{\integrantes}{%
  [NOME 1] --- RA [RA 1]\\
  [NOME 2] --- RA [RA 2]\\
  [NOME 3] --- RA [RA 3]%
}
```

Quando o usuário passar nomes e RAs, **edite apenas este arquivo**. Para a capa, use `\imprimirautor`/`\author` com `\integrantes`, conforme o padrão da classe.

## 6. Elementos ABNT exigidos

**Pré-textuais:** capa, folha de rosto (com natureza do trabalho: "Trabalho apresentado à disciplina de Banco de Dados do curso de …"), resumo em português com palavras-chave, sumário. Lista de figuras e de tabelas só se houver figuras/tabelas.

**Textuais:** introdução, desenvolvimento (capítulos 2 a 4), considerações finais.

**Pós-textuais:** referências (obrigatório). Apêndices/anexos só se necessários.

**Formatação (a classe `abntex2` já aplica a maior parte; confirme no PDF):**
- A4, margens: superior e esquerda 3 cm; inferior e direita 2 cm.
- Fonte 12 pt, espaçamento 1,5 entre linhas no corpo do texto.
- Citações diretas longas (mais de 3 linhas): recuo de 4 cm, fonte 10 pt, sem aspas.
- Legendas, notas de rodapé e citações longas em fonte menor (10 pt).
- Numeração progressiva das seções; paginação a partir da introdução.
- Figuras e tabelas com título acima, **fonte abaixo** (mesmo quando for "Elaborado pelos autores").
- Referências alinhadas à esquerda, espaçamento simples, ordem alfabética.

## 7. Conteúdo: o que cada capítulo deve conter

### 1. Introdução
Contexto geral, apresentação do problema em poucas linhas, objetivo do trabalho (descrever o problema e modelar o banco de dados), organização do texto.

### 2. Descrição do problema (capítulo central, o mais longo)
Descreva **em detalhes**, com base em dados reais:
- **Contexto:** onde e para quem o problema acontece (Brasil e, quando útil, global).
- **Problema:** o que acontece exatamente; fluxo atual do processo, de ponta a ponta.
- **Causas** e **consequências/impactos** (sociais, econômicos, ambientais, de saúde, conforme o tema).
- **Atores envolvidos** (quem produz, usa, gerencia e é afetado pelos dados).
- **Dados e estatísticas** de fontes oficiais, citados com ABNT.
- **Soluções existentes e lacunas** (o que já se faz e onde falta organização/dados).
- **Por que um banco de dados ajuda:** que informações precisam ser registradas, consultadas e cruzadas.

### 3. Enquadramento nos ODS
- Indique o **ODS principal** (número e nome) e a **justificativa**.
- Cite a(s) **meta(s)** específica(s) da Agenda 2030 relacionada(s) (ex.: "meta X.Y") e, se houver, o **indicador** oficial associado.
- Se fizer sentido, liste **ODS secundários** com uma frase cada. Não infle a lista: no máximo 2 ou 3, todos justificados.
- **Verifique número, nome e texto das metas em fonte oficial** (ver seção 8). Não use memória.

### 4. Escopo do sistema de banco de dados
Resumo de alto nível do que o banco vai cobrir: principais entidades de negócio, regras de negócio importantes, consultas que o sistema precisa responder. **Deve ser coerente com o modelo ER da Etapa 2.** Não detalhe atributos nem cardinalidades aqui, apenas aponte que o modelo será apresentado na Etapa 2.

### 5. Considerações finais
Síntese, contribuição do trabalho para o ODS e próximos passos (modelo ER e implementação).

## 8. Fontes e citações (regra anti-alucinação)

- **Nunca invente referências, dados, estatísticas, autores, URLs ou números de meta.** Se não verificou, não escreva.
- Se houver ferramenta de busca/web, **pesquise e leia a fonte** antes de citar. Se não houver, escreva o dado como `\textbf{[VERIFICAR]}` ou deixe `% TODO: fonte` e liste a pendência para o usuário.
- Fontes preferenciais para o ODS e dados:
  - ONU Brasil (brasil.un.org) e a Agenda 2030 oficial para texto de ODS e metas.
  - IPEA, IBGE (ODS Brasil / Agenda 2030) para indicadores nacionais.
  - Ministérios, agências reguladoras, OMS/OPAS, FAO, Banco Mundial e artigos científicos conforme o tema.
- Cada entrada do `referencias.bib` deve ter autor/instituição, título, ano e, para sites, `url` e `urlaccess` (data de acesso). Use o **dia de hoje** como data de acesso.
- Use `\cite{}` (parêntese) e `\citeonline{}` (autor no texto) do `abntex2cite`.
- Parafraseie. Citação direta curta entre aspas com página; citação longa em bloco `\begin{citacao}`.

## 9. Estilo de escrita

- Português formal, impessoal ou 1ª pessoa do plural ("neste trabalho, descrevemos…"), sendo **consistente** no documento inteiro.
- Parágrafos de 4 a 8 linhas, sem listas longas no corpo; ABNT favorece texto corrido.
- Evite jargão técnico de BD fora do capítulo 4 (a Etapa 1 é sobre o problema, não sobre o esquema).
- Sem "ChatGPT/IA" como autor ou fonte. Revisar ortografia e concordância antes de entregar.
- Extensão alvo: **8 a 15 páginas** de corpo, salvo orientação diferente da disciplina.

## 10. Build e verificação

```bash
cd etapa1
latexmk -pdf -interaction=nonstopmode -halt-on-error main.tex
```

Antes de dar a tarefa como concluída:
1. Compila **sem erros** e sem `Undefined citation` / `Undefined reference` / `Overfull \hbox` graves.
2. Abra o PDF (ou converta páginas em imagem) e confira capa, folha de rosto, sumário, margens e legendas.
3. Todas as citações no texto existem no `.bib` e todas as entradas do `.bib` são citadas.
4. Nenhum placeholder `[NOME …]`/`[VERIFICAR]` ficou sem estar listado nas pendências.
5. Entregue `main.pdf` junto com o código-fonte.

## 11. Regras de trabalho

- Faça alterações pequenas e revisáveis (um capítulo por commit/passo).
- Não altere arquivos fora de `etapa1/` (a spec e a Etapa 2 pertencem a outros).
- Não mude o problema ou o escopo definido na spec. Se achar uma inconsistência, **avise o usuário** em vez de decidir sozinho.
- Ao final, responda com: (a) o que foi feito, (b) lista de **pendências** (nomes/RAs, template, fontes a verificar), (c) o caminho do PDF.
