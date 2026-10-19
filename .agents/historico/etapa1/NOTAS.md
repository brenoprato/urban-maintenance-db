# Notas de Execução — Etapa 1 (Relatório de Descrição do Problema, ODS e Escopo)

## Decisões Arquiteturais e Editoriais

1. **Alinhamento Estrito com a Especificação e com a Etapa 2 (`spec_bd.md` e `modelagem/`)**:
   - O relatório da Etapa 1 foi redigido como o documento fundacional do **Sistema de Monitoramento e Manutenção Colaborativa de Infraestrutura Urbana** (zeladoria urbana colaborativa e gestão de Ordens de Serviço), mantendo 100% de consistência semântica, terminológica e quantitativa com `.agents/spec_bd.md` e com o trabalho já consolidado da Etapa 2 (`modelagem/`).
   - No Capítulo 4 (*Escopo do Sistema de Banco de Dados*), foram apresentadas as **12 entidades principais de negócio** organizadas em três subdomínios (*Demanda Cidadã e Território*, *Estrutura Administrativa e Operacional* e *Execução de Serviços e Suprimentos*), além das **2 relações associativas N:M** (consumo de materiais por Ordem de Serviço e fornecimento de lotes de materiais por fornecedor), totalizando exatamente as **14 tabelas relacionais em 3FN** implementadas na Etapa 2.

2. **Padrão ABNT (`abntex2`) e Compatibilidade com Overleaf**:
   - Estrutura modular em `/home/BRENO/Prog/projects/bd_final/etapa1/` com separação de metadados (`dados-grupo.tex`), elementos pré-textuais (`pre-textuais/resumo.tex` e `abstract.tex`), cinco capítulos (`capitulos/01-introducao.tex` a `05-consideracoes-finais.tex`), figura vetorial TikZ (`figuras/fluxo-zeladoria.tex` + `.pdf` + `.png`) e base bibliográfica verificada (`referencias.bib`).
   - Placeholders simbólicos em `dados-grupo.tex` (`{[NOME 1]} --- RA {[RA 1]}`, etc.) protegidos por chaves `{...}` para evitar que o comando `\\` interprete colchetes na linha seguinte como argumento opcional de espaçamento vertical no LaTeX.
   - Uso de `organization` em caixa alta UTF-8 combinado com `org-short` em `referencias.bib` para que o estilo `abntex2-alf` exiba siglas curtas nas citações no texto (`IBGE (2025)`, `SNIS (2023)`, `ONU Brasil (2015a)`) e nomes institucionais completos em maiúsculas na lista de Referências.
   - Inclusão simultânea dos campos `urlaccessdate` (utilizado pelo `abntex2-alf.bst` para imprimir `Acesso em: ...`) e `urlaccess` (mencionado no guia da Etapa 1) em todas as referências com URL.

3. **Verificação Factual de Dados e Fontes Oficiais**:
   - **IBGE — Censo Demográfico 2022 (Malha de Setores Censitários / População Urbana, 2024)**: 87,4% da população brasileira (177,5 milhões de pessoas) reside em áreas urbanas.
   - **IBGE — Censo Demográfico 2022: Características Urbanísticas do Entorno dos Domicílios (divulgado em 17/04/2025)**: Universo de ~174,2 milhões de moradores em áreas com características urbanas; iluminação pública (97,5% atendidos / 4,5 milhões sem cobertura), pavimentação (88,5% atendidos / 20,1 milhões sem cobertura), bueiro/boca de lobo (53,7% atendidos / 80,6 milhões sem cobertura), calçada/passeio (83,9% atendidos / 28,0 milhões sem cobertura), calçada livre de obstáculos (67,9% atendidos / 55,9 milhões expostos a obstáculos), rampa para cadeirantes (14,9% atendidos / 148,3 milhões sem cobertura) e arborização (66,0% atendidos / 59,2 milhões sem cobertura).
   - **SNIS 2023 (ano-base 2022)**: Diagnóstico Temático de Drenagem e Manejo das Águas Pluviais Urbanas (45,1% das vias urbanas com redes de drenagem subterrânea; 18,4% dos municípios com cadastro técnico completo) e Diagnóstico Temático de Manejo de Resíduos Sólidos Urbanos (coleta seletiva e pontos viciados de descarte).
   - **ODS da ONU (Agenda 2030)**: ODS 11 como principal (Metas 11.3 [Indicador 11.3.2], 11.7 [Indicadores 11.7.1 e 11.7.2] e 11.6 [Indicador 11.6.1]) e ODS 9 (Meta 9.1) e ODS 16 (Metas 16.6 e 16.7) como secundários.

4. **Compilação e Qualidade Tipográfica**:
   - Compilação limpa via `latexmk -pdf` sem nenhum erro e com zero warnings de documento (`0 Overfull \hbox`, `0 Underfull \hbox`, `0 Undefined citations/references`).
   - Volume de texto nos capítulos numerados (Capítulos 1 a 5): **15 páginas de corpo textual** (páginas 7 a 21 do PDF, numeração impressa 6 a 20), dentro da meta de 8 a 15 páginas de conteúdo definida no guia da Etapa 1.
