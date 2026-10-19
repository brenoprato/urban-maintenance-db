# Relatório de Execução — Etapa 1 (Descrição do Problema, ODS e Escopo)

## 1. Resumo das Entregas Realizadas

A **Etapa 1** do projeto de Banco de Dados foi integralmente executada no diretório [`etapa1/`](file:///home/BRENO/Prog/projects/bd_final/etapa1), produzindo um relatório técnico acadêmico no padrão **ABNT (`abntex2`)**, 100% compatível com o Overleaf e alinhado com a especificação de domínio ([`.agents/spec_bd.md`](file:///home/BRENO/Prog/projects/bd_final/.agents/spec_bd.md)) e com a modelagem da Etapa 2 ([`modelagem/`](file:///home/BRENO/Prog/projects/bd_final/modelagem)):

- **Configuração e Metadados do Grupo ([`etapa1/dados-grupo.tex`](file:///home/BRENO/Prog/projects/bd_final/etapa1/dados-grupo.tex))**:
  - Placeholders simbólicos configurados para os 3 integrantes (`[NOME 1] — RA [RA 1]`, `[NOME 2] — RA [RA 2]`, `[NOME 3] — RA [RA 3]`) e para os metadados institucionais (`[NOME DA INSTITUIÇÃO]`, `[CURSO]`, `[PROFESSOR(A)]`, `[CIDADE]`).
- **Elementos Pré-Textuais**:
  - Capa e Folha de Rosto automatizadas em [`etapa1/main.tex`](file:///home/BRENO/Prog/projects/bd_final/etapa1/main.tex) com natureza do trabalho acadêmico.
  - Resumo em português ([`etapa1/pre-textuais/resumo.tex`](file:///home/BRENO/Prog/projects/bd_final/etapa1/pre-textuais/resumo.tex)) e Abstract em inglês ([`etapa1/pre-textuais/abstract.tex`](file:///home/BRENO/Prog/projects/bd_final/etapa1/pre-textuais/abstract.tex)) em parágrafo único conforme ABNT NBR 6028.
  - Lista de Ilustrações, Lista de Tabelas e Sumário gerados automaticamente.
- **Capítulos Textuais**:
  - **Capítulo 1 — Introdução ([`etapa1/capitulos/01-introducao.tex`](file:///home/BRENO/Prog/projects/bd_final/etapa1/capitulos/01-introducao.tex))**: Contextualização da zeladoria urbana no Brasil, problematização da fragmentação informacional, conexão com a Agenda 2030 (ODS 11, 9 e 16), objetivo geral, objetivos específicos e organização do documento.
  - **Capítulo 2 — Descrição do Problema ([`etapa1/capitulos/02-problema.tex`](file:///home/BRENO/Prog/projects/bd_final/etapa1/capitulos/02-problema.tex))**: Panorama estatístico fundamentado no Censo Demográfico 2022 do IBGE (divulgação 2024/2025) e nos diagnósticos do SNIS (2023), Tabela 1 de cobertura e déficit de infraestrutura urbana, fluxo operacional ponta a ponta em 7 etapas ilustrado pela Figura 1 ([`etapa1/figuras/fluxo-zeladoria.pdf`](file:///home/BRENO/Prog/projects/bd_final/etapa1/figuras/fluxo-zeladoria.pdf)), causas estruturais e impactos socioeconômicos/ambientais, Tabela 2 de atores e papéis no sistema, arcabouço legal (Estatuto da Cidade — Lei 10.257/2001 e Lei do Usuário do Serviço Público — Lei 13.460/2017), limitações das soluções existentes (portais 156 desconectados do *back-office*) e justificativa do banco de dados relacional.
  - **Capítulo 3 — Enquadramento nos Objetivos de Desenvolvimento Sustentável ([`etapa1/capitulos/03-ods.tex`](file:///home/BRENO/Prog/projects/bd_final/etapa1/capitulos/03-ods.tex))**: Enquadramento primário no **ODS 11** com detalhamento das Metas **11.3** (Indicador 11.3.2), **11.7** (Indicadores 11.7.1 e 11.7.2) e **11.6** (Indicador 11.6.1), enquadramento secundário nos **ODS 9** (Meta 9.1) e **ODS 16** (Metas 16.6 e 16.7), e Tabela 3 relacionando metas, indicadores e mecanismos concretos do sistema.
  - **Capítulo 4 — Escopo do Sistema de Banco de Dados ([`etapa1/capitulos/04-escopo-sistema.tex`](file:///home/BRENO/Prog/projects/bd_final/etapa1/capitulos/04-escopo-sistema.tex))**: Arquitetura funcional em 3 subdomínios, descrição das **12 entidades principais** na Tabela 4 (`regiao_cep`, `cidadao`, `ocorrencia`, `anexo_midia`, `avaliacao_atendimento`, `secretaria_orgao`, `equipe_manutencao`, `funcionario`, `veiculo_frota`, `ordem_servico`, `material_insumo`, `fornecedor`), regras de negócio essenciais (incluindo as 2 associações N:M de consumo de materiais por OS e fornecimento por lote) e os 5 grupos de consultas operacionais e gerenciais que o banco deve responder, estabelecendo a ponte direta com a Etapa 2.
  - **Capítulo 5 — Considerações Finais ([`etapa1/capitulos/05-consideracoes-finais.tex`](file:///home/BRENO/Prog/projects/bd_final/etapa1/capitulos/05-consideracoes-finais.tex))**: Síntese dos resultados da Etapa 1, contribuições para os ODS e encadeamento metodológico para a Etapa 2.
- **Referências Bibliográficas ([`etapa1/referencias.bib`](file:///home/BRENO/Prog/projects/bd_final/etapa1/referencias.bib))**:
  - 14 referências reais e verificadas (ONU, ONU Brasil, IPEA, IBGE, SNIS, Presidência da República, Elmasri & Navathe, Heuser), todas efetivamente citadas no texto via `\cite` e `\citeonline` no padrão autor-data ABNT (`abntex2cite`).
- **Compilação e Automação ([`etapa1/Makefile`](file:///home/BRENO/Prog/projects/bd_final/etapa1/Makefile) e [`etapa1/main.pdf`](file:///home/BRENO/Prog/projects/bd_final/etapa1/main.pdf))**:
  - Documento compilado em [`etapa1/main.pdf`](file:///home/BRENO/Prog/projects/bd_final/etapa1/main.pdf) com 24 páginas totais (15 páginas de capítulos textuais, dentro da faixa de 8 a 15 páginas de conteúdo), zero erros e zero warnings de formatação.

## 2. Pendências para Preenchimento Manual pelo Usuário

- Editar [`etapa1/dados-grupo.tex`](file:///home/BRENO/Prog/projects/bd_final/etapa1/dados-grupo.tex) substituindo os placeholders simbólicos pelos dados reais:
  - `\instituicaoNome`: `[NOME DA INSTITUIÇÃO]`
  - `\cursoNome`: `[CURSO]`
  - `\professorNome`: `[PROFESSOR(A)]`
  - `\cidadeLocal`: `[CIDADE]`
  - `\integrantes`: `[NOME 1]`, `[RA 1]`, `[NOME 2]`, `[RA 2]`, `[NOME 3]`, `[RA 3]`
