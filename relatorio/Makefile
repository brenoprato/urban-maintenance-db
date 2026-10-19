# =============================================================================
# Makefile — Etapa 1 (Relatório ABNT em LaTeX)
# Alvos: pdf (default), figura, clean, distclean
# =============================================================================

MAIN = main
LATEXMK = latexmk
LATEXMK_FLAGS = -pdf -interaction=nonstopmode -halt-on-error

.PHONY: all pdf figura clean distclean

all: pdf

figura: figuras/fluxo-zeladoria.pdf

figuras/fluxo-zeladoria.pdf: figuras/fluxo-zeladoria.tex
	cd figuras && pdflatex -interaction=nonstopmode -halt-on-error fluxo-zeladoria.tex && rm -f fluxo-zeladoria.aux fluxo-zeladoria.log

pdf: figura
	$(LATEXMK) $(LATEXMK_FLAGS) $(MAIN).tex

clean:
	$(LATEXMK) -c $(MAIN).tex
	rm -f $(MAIN).bbl $(MAIN).run.xml $(MAIN).synctex.gz $(MAIN).lo*

distclean: clean
	rm -f $(MAIN).pdf
