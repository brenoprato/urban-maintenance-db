# Notas de Execução — Etapa 1 (Versão Simplificada — UNIFESP / ICT)

## Decisões Arquiteturais e Editoriais

1. **Adequação ao Enunciado Oficial da Disciplina (`Trabalho Final de Banco de Dados.pdf`)**:
   - A partir do documento oficial da disciplina (*Universidade Federal de São Paulo — Instituto de Ciência e Tecnologia — Disciplina: Banco de Dados — Professora: Daniela Leal Musa*), o relatório da **Etapa 1** em [`relatorio/`](file:///home/BRENO/Prog/projects/bd_final/relatorio) foi reformulado para um formato enxuto, direto e autocontido usando a classe `article` do LaTeX (5 páginas).
   - O cabeçalho reproduz exatamente a identidade visual do enunciado da professora (UNIFESP, ICT, Disciplina: Banco de Dados, Professora: Daniela Leal Musa, Data de Entrega: 19/10/2026), mantendo os nomes e RAs dos 3 integrantes como placeholders simbólicos em [`relatorio/dados-grupo.tex`](file:///home/BRENO/Prog/projects/bd_final/relatorio/dados-grupo.tex).

2. **Estrutura Direta em 3 Seções**:
   - **Seção 1 — Descrição do Problema**: Contextualiza o desafio real da zeladoria urbana nos municípios brasileiros com dados objetivos do Censo Demográfico 2022 do IBGE, aponta as 5 falhas estruturais dos canais atuais (desconexão entre o registro 156 e a execução interna das secretarias) e destaca a aplicabilidade em projetos de extensão do ICT/UNIFESP.
   - **Seção 2 — Enquadramento nos Objetivos de Desenvolvimento Sustentável (ODS)**: Fundamenta o encaixe direto no **ODS 11 (Cidades e Comunidades Sustentáveis)** — **Metas 11.3, 11.6 e 11.7** — e o apoio complementar aos **ODS 9 (Meta 9.1)** e **ODS 16 (Metas 16.6 e 16.7)**.
   - **Seção 3 — Funcionamento do Sistema e Regras de Negócio**: Descreve em texto corrido claro as **12 entidades principais** (`regiao_cep`, `cidadao`, `ocorrencia`, `anexo_midia`, `avaliacao_atendimento`, `secretaria_orgao`, `equipe_manutencao`, `funcionario`, `veiculo_frota`, `ordem_servico`, `material_insumo` e `fornecedor`), seus 70 atributos próprios, seus 14 relacionamentos binários, suas cardinalidades (1:N, 1:1 e N:M) e os 6 atributos próprios de relacionamento (`consome` / `consumo_material_os` e `fornece` / `fornecimento_insumo`), atendendo diretamente aos critérios de avaliação da Etapa 1/Etapa 2 da professora.

3. **Simplicidade de Arquivos e Compilação**:
   - O diretório [`relatorio/`](file:///home/BRENO/Prog/projects/bd_final/relatorio) contém apenas 4 arquivos essenciais: `main.tex`, `dados-grupo.tex`, `Makefile` e `main.pdf`.
   - Compilação limpa sem erros e com zero warnings (`Overfull`/`Underfull \hbox` = 0).
