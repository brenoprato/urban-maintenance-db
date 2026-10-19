JAVAC = javac
JAVA = java -Djava.awt.headless=true
BRMODELO_JAR = tools/brModelo.jar
SQLITE = $(shell command -v sqlite3 2>/dev/null || echo ./tools/sqlite3)

.PHONY: all diagramas validar clean abrir-brmodelo

all: diagramas validar

diagramas: tools/GeradorBrModelo.class
	mkdir -p diagramas diagramas/out
	$(JAVA) -cp $(BRMODELO_JAR):tools GeradorBrModelo diagramas diagramas/out
	@if command -v rsvg-convert >/dev/null 2>&1; then \
		for f in diagramas/out/*.svg; do \
			rsvg-convert -f pdf -o "$${f%.svg}.pdf" "$$f"; \
		done; \
	fi

tools/GeradorBrModelo.class: tools/GeradorBrModelo.java $(BRMODELO_JAR)
	$(JAVAC) -cp $(BRMODELO_JAR) tools/GeradorBrModelo.java

validar:
	$(SQLITE) :memory: ".read schema.sql" ".tables"
	$(SQLITE) :memory: "PRAGMA foreign_keys=ON;" ".read schema.sql" "PRAGMA foreign_key_check;"
	@test -f diagramas/04-modelo-completo.brM3
	@test -f diagramas/out/04-modelo-completo.png
	@test -f diagramas/out/04-modelo-completo.svg
	@test -f diagramas/out/04-modelo-completo.pdf

abrir-brmodelo:
	java -jar $(BRMODELO_JAR) diagramas/04-modelo-completo.brM3 &

clean:
	rm -rf diagramas/out tools/*.class
