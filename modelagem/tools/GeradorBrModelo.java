import controlador.Controler;
import controlador.Diagrama;
import controlador.Editor;
import controlador.apoios.GuardaPadraoBrM;
import desenho.FormaElementar;
import desenho.linhas.PontoDeLinha;
import desenho.preAnyDiagrama.PreCardinalidade;
import diagramas.conceitual.Atributo;
import diagramas.conceitual.Cardinalidade;
import diagramas.conceitual.DiagramaConceitual;
import diagramas.conceitual.Entidade;
import diagramas.conceitual.Ligacao;
import diagramas.conceitual.Relacionamento;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import javax.imageio.ImageIO;

/**
 * Gerador nativo de diagramas conceituais ER utilizando a engine oficial do brModelo (brModelo.jar).
 * Produz arquivos fontes editaveis (.brM3) em modelagem/diagramas/ e renderizacoes (.png, .svg, .pdf)
 * em modelagem/diagramas/out/.
 */
public class GeradorBrModelo {

    private static final int ENT_W = 170;
    private static final int ENT_H = 40;
    private static final int REL_W = 170;
    private static final int REL_H = 48;

    // Coordenadas de colunas da grade 4x3
    private static final int X_C1 = 60;
    private static final int X_R12 = 330;
    private static final int X_C2 = 600;
    private static final int X_R23 = 870;
    private static final int X_C3 = 1140;
    private static final int X_R34 = 1410;
    private static final int X_C4 = 1680;

    private static Editor criarEditorHeadless() throws Exception {
        Editor ed = new Editor() {
            @Override
            public void NoAction() {
                // No-op em modo batch sem frame Swing principal
            }

            @Override
            public void PerformInspectorFor(FormaElementar f) {
                // No-op em modo batch
            }
        };
        ed.setShowDiagramas(new controlador.Mostrador());
        return ed;
    }

    private static DiagramaConceitual criarDiagrama(Editor ed) {
        return new DiagramaConceitual(ed);
    }

    private static Entidade criarEntidade(DiagramaConceitual diag, String nome, int x, int y, String[] attrs) {
        Entidade e = new Entidade(diag, nome);
        e.setTexto(nome);
        e.SetBounds(x, y, ENT_W, ENT_H);
        e.ReSized();
        e.Reenquadre();
        if (attrs != null && attrs.length > 0) {
            for (int i = 0; i < attrs.length; i++) {
                String raw = attrs[i];
                boolean pk = raw.startsWith("*");
                String label = pk ? raw.substring(1) : raw;
                Point ptAttach = new Point(e.getLeft() + 14, e.getTopHeight() - 2);
                Atributo a = (Atributo) diag.ExternalRealiseComando(Controler.Comandos.cmdAtributo, ptAttach);
                a.setTexto(label);
                a.setIdentificador(pk);
                a.Reenquadre();
            }
            diag.getItensSelecionados().clear();
            e.DoAnyThing(123); // CONST_DO_ORGATTR: alinha atributos no padrao brModelo
        }
        return e;
    }

    private static Relacionamento criarRelacionamento(DiagramaConceitual diag, String nome, int x, int y, String[] attrs) {
        Relacionamento r = new Relacionamento(diag, nome);
        r.setTexto(nome);
        r.SetBounds(x, y, REL_W, REL_H);
        r.ReSized();
        r.Reenquadre();
        if (attrs != null && attrs.length > 0) {
            for (int i = 0; i < attrs.length; i++) {
                String raw = attrs[i];
                boolean pk = raw.startsWith("*");
                String label = pk ? raw.substring(1) : raw;
                Point ptAttach = new Point(r.getLeft() + REL_W / 2, r.getTopHeight() - 2);
                Atributo a = (Atributo) diag.ExternalRealiseComando(Controler.Comandos.cmdAtributo, ptAttach);
                a.setTexto(label);
                a.setIdentificador(pk);
                a.Reenquadre();
            }
            diag.getItensSelecionados().clear();
            r.DoAnyThing(123);
        }
        return r;
    }

    // Pontos de ancoragem nas bordas da Entidade
    private static Point entLeft(Entidade e, int dy) {
        return new Point(e.getLeft() + 2, e.getTop() + ENT_H / 2 + dy);
    }

    private static Point entRight(Entidade e, int dy) {
        return new Point(e.getLeftWidth() - 2, e.getTop() + ENT_H / 2 + dy);
    }

    private static Point entTop(Entidade e, int dx) {
        return new Point(e.getLeft() + ENT_W / 2 + dx, e.getTop() + 2);
    }

    private static Point entBottom(Entidade e, int dx) {
        return new Point(e.getLeft() + ENT_W / 2 + dx, e.getTopHeight() - 2);
    }

    // Pontos de ancoragem nos vertices do Relacionamento (internos ao losango para captura exata)
    private static Point relLeft(Relacionamento r) {
        return new Point(r.getLeft() + 12, r.getTop() + REL_H / 2);
    }

    private static Point relRight(Relacionamento r) {
        return new Point(r.getLeftWidth() - 12, r.getTop() + REL_H / 2);
    }

    private static Point relTop(Relacionamento r) {
        return new Point(r.getLeft() + REL_W / 2, r.getTop() + 10);
    }

    private static Point relBottom(Relacionamento r) {
        return new Point(r.getLeft() + REL_W / 2, r.getTopHeight() - 10);
    }

    private static desenho.formas.Forma encontrarFormaEm(DiagramaConceitual diag, Point pt) {
        for (FormaElementar item : diag.getListaDeItens()) {
            if (item instanceof Entidade || item instanceof Relacionamento) {
                java.awt.Rectangle rect = new java.awt.Rectangle(
                        item.getLeft(), item.getTop(), item.getWidth(), item.getHeight());
                if (rect.contains(pt)) {
                    return (desenho.formas.Forma) item;
                }
            }
        }
        return null;
    }

    private static void vincularFormas(DiagramaConceitual diag, Ligacao lig, Point ptEnt, Point ptRel) {
        desenho.formas.Forma fRel = encontrarFormaEm(diag, ptRel);
        desenho.formas.Forma fEnt = encontrarFormaEm(diag, ptEnt);
        if (fRel != null && fEnt != null) {
            lig.FormasALigar = new desenho.formas.Forma[]{fRel, fEnt};
            lig.Ligar();
        }
    }

    private static void configurarCardinalidade(DiagramaConceitual diag, Ligacao lig, PreCardinalidade.TiposCard card, int dx, int dy) {
        diag.getItensSelecionados().clear();
        lig.PrepareCardinalidade();
        if (lig.getFormaPontaA() == null || lig.getFormaPontaB() == null) {
            throw new RuntimeException("Ligacao desconectada: A=" + lig.getFormaPontaA() + " B=" + lig.getFormaPontaB());
        }
        if (lig.getCard() != null) {
            lig.getCard().setSelecionado(false);
            lig.getCard().setCard(card);
            lig.getCard().setStopRaize(true);
            lig.getCard().setWidth(29);
            lig.getCard().setHeight(15);
            lig.getCard().setStopRaize(false);
            lig.getCard().Posicione();
            if (dx != 0 || dy != 0) {
                lig.getCard().setMovimentacaoManual(true);
                lig.getCard().DoMove(dx, dy);
                lig.getCard().Reenquadre();
            }
        }
    }

    /**
     * Conexao reta direta (horizontal ou vertical alinhada).
     */
    private static Ligacao ligarReto(
            DiagramaConceitual diag,
            Point ptEnt,
            Point ptRel,
            PreCardinalidade.TiposCard card,
            int cardDx,
            int cardDy) {
        Ligacao lig = new Ligacao(diag);
        lig.setInteligente(false);
        lig.SuperInicie(0, ptEnt, ptRel);
        vincularFormas(diag, lig, ptEnt, ptRel);
        lig.reSetBounds();
        configurarCardinalidade(diag, lig, card, cardDx, cardDy);
        return lig;
    }

    /**
     * Conexao ortogonal em L (sai verticalmente da Entidade e entra horizontalmente no Relacionamento).
     * Nota: em SuperLinha.Inicie(n, ptEnt, ptRel), PontaA (idx 0) = ptRel e PontaB (idx n+1) = ptEnt.
     */
    private static Ligacao ligarL_VertHoriz(
            DiagramaConceitual diag,
            Point ptEnt,
            Point ptRel,
            PreCardinalidade.TiposCard card,
            int cardDx,
            int cardDy) {
        Ligacao lig = new Ligacao(diag);
        lig.setInteligente(false);
        lig.SuperInicie(1, ptEnt, ptRel);
        vincularFormas(diag, lig, ptEnt, ptRel);
        Point pRel = lig.getPontaA().getCentro();
        Point pEnt = lig.getPontaB().getCentro();
        lig.getPontos().get(1).setCentro(new Point(pEnt.x, pRel.y));
        lig.reSetBounds();
        configurarCardinalidade(diag, lig, card, cardDx, cardDy);
        return lig;
    }

    /**
     * Conexao ortogonal em Z (sai horizontalmente da Entidade ate canalX, desce/sobe verticalmente e entra horizontalmente no Relacionamento).
     * Nota: em SuperLinha.Inicie(2, ptEnt, ptRel), idx 0 = pRel, idx 1 = adj a pRel, idx 2 = adj a pEnt, idx 3 = pEnt.
     */
    private static Ligacao ligarZ_HorizVertHoriz(
            DiagramaConceitual diag,
            Point ptEnt,
            Point ptRel,
            int canalX,
            PreCardinalidade.TiposCard card,
            int cardDx,
            int cardDy) {
        Ligacao lig = new Ligacao(diag);
        lig.setInteligente(false);
        lig.SuperInicie(2, ptEnt, ptRel);
        vincularFormas(diag, lig, ptEnt, ptRel);
        Point pRel = lig.getPontaA().getCentro();
        Point pEnt = lig.getPontaB().getCentro();
        lig.getPontos().get(1).setCentro(new Point(canalX, pRel.y));
        lig.getPontos().get(2).setCentro(new Point(canalX, pEnt.y));
        lig.reSetBounds();
        configurarCardinalidade(diag, lig, card, cardDx, cardDy);
        return lig;
    }

    // Atributos de cada entidade conforme spec_bd.md / tabelas.md / schema.sql
    private static final String[] ATTRS_REGIAO_CEP = {
        "*cep", "cidade", "bairro", "logradouro", "ponto_referencia_regiao"
    };
    private static final String[] ATTRS_CIDADAO = {
        "*id_cidadao", "cpf", "nome_completo", "email", "telefone", "numero_residencia"
    };
    private static final String[] ATTRS_OCORRENCIA = {
        "*id_ocorrencia", "protocolo", "titulo", "descricao_detalhada",
        "categoria_problema", "nivel_gravidade", "data_abertura", "status_atual", "numero_aproximado"
    };
    private static final String[] ATTRS_ANEXO_MIDIA = {
        "*id_anexo", "url_arquivo", "tipo_midia", "data_upload"
    };
    private static final String[] ATTRS_SECRETARIA_ORGAO = {
        "*id_secretaria", "nome_secretaria", "sigla", "nome_secretario_responsavel",
        "telefone_contato", "orcamento_mensal"
    };
    private static final String[] ATTRS_EQUIPE_MANUTENCAO = {
        "*id_equipe", "nome_equipe", "categoria_problema_atendida", "turno_trabalho", "status_disponibilidade"
    };
    private static final String[] ATTRS_FUNCIONARIO = {
        "*matricula_funcionario", "cpf", "nome_funcionario", "cargo_funcao", "data_admissao", "telefone_corporativo"
    };
    private static final String[] ATTRS_VEICULO_FROTA = {
        "*placa_veiculo", "tipo_veiculo", "modelo", "ano", "capacidade_carga", "status_manutencao"
    };
    private static final String[] ATTRS_ORDEM_SERVICO = {
        "*id_os", "data_emissao", "data_inicio_execucao", "data_conclusao",
        "relatorio_tecnico", "custo_mao_de_obra", "status_os"
    };
    private static final String[] ATTRS_MATERIAL_INSUMO = {
        "*id_material", "nome_material", "unidade_medida", "quantidade_estoque_atual", "custo_unitario_medio"
    };
    private static final String[] ATTRS_FORNECEDOR = {
        "*id_fornecedor", "cnpj", "razao_social", "telefone", "email_comercial", "cidade_sede"
    };
    private static final String[] ATTRS_AVALIACAO_ATENDIMENTO = {
        "*id_avaliacao", "nota_satisfacao", "comentario_cidadao", "problema_resolvido_boolean", "data_avaliacao"
    };

    // Atributos de relacionamentos N:M
    private static final String[] ATTRS_REL_CONSOME = {
        "quantidade_utilizada", "data_baixa_estoque"
    };
    private static final String[] ATTRS_REL_FORNECE = {
        "*numero_lote_compra", "data_entrega", "quantidade_comprada", "preco_unitario_compra"
    };

    /**
     * Constroi o modelo completo (com ou sem atributos de entidades).
     * Usado para 00-visao-geral (comAtributosEntidade = false) e 04-modelo-completo (comAtributosEntidade = true).
     */
    private static DiagramaConceitual construirModeloGlobal(Editor ed, boolean comAtributosEntidade) {
        DiagramaConceitual diag = criarDiagrama(ed);

        int yRow1 = comAtributosEntidade ? 100 : 90;
        int yCorridor12 = comAtributosEntidade ? 380 : 195;
        int yRow2 = comAtributosEntidade ? 470 : 290;
        int yCorridor23 = comAtributosEntidade ? 730 : 410;
        int yRow3 = comAtributosEntidade ? 820 : 500;

        // --- ROW 1: Demandas do Cidadao ---
        Entidade eRegiao = criarEntidade(diag, "regiao_cep", X_C1, yRow1, comAtributosEntidade ? ATTRS_REGIAO_CEP : null);
        Entidade eCidadao = criarEntidade(diag, "cidadao", X_C2, yRow1, comAtributosEntidade ? ATTRS_CIDADAO : null);
        Entidade eOcorrencia = criarEntidade(diag, "ocorrencia", X_C3, yRow1, comAtributosEntidade ? ATTRS_OCORRENCIA : null);
        Entidade eAnexo = criarEntidade(diag, "anexo_midia", X_C4, yRow1, comAtributosEntidade ? ATTRS_ANEXO_MIDIA : null);

        Relacionamento rEndMoradia = criarRelacionamento(diag, "endereco_moradia", X_R12, yRow1 - 4, null);
        Relacionamento rReporta = criarRelacionamento(diag, "reporta", X_R23, yRow1 - 4, null);
        Relacionamento rPossui = criarRelacionamento(diag, "possui", X_R34, yRow1 - 4, null);
        Relacionamento rLocalProb = criarRelacionamento(diag, "local_do_problema", (X_C1 + X_C3) / 2, 18, null);

        // Conexoes Horizontais Row 1
        ligarReto(diag, entRight(eRegiao, 0), relLeft(rEndMoradia), PreCardinalidade.TiposCard.C0N, 0, 0);
        ligarReto(diag, entLeft(eCidadao, 0), relRight(rEndMoradia), PreCardinalidade.TiposCard.C11, 0, 0);

        ligarReto(diag, entRight(eCidadao, 0), relLeft(rReporta), PreCardinalidade.TiposCard.C0N, 0, 0);
        ligarReto(diag, entLeft(eOcorrencia, 0), relRight(rReporta), PreCardinalidade.TiposCard.C11, 0, 0);

        ligarReto(diag, entRight(eOcorrencia, 0), relLeft(rPossui), PreCardinalidade.TiposCard.C0N, 0, 0);
        ligarReto(diag, entLeft(eAnexo, 0), relRight(rPossui), PreCardinalidade.TiposCard.C11, 0, 0);

        // Arco Superior: local_do_problema (regiao_cep <-> ocorrencia)
        ligarL_VertHoriz(diag, entTop(eRegiao, 0), relLeft(rLocalProb), PreCardinalidade.TiposCard.C0N, 0, 0);
        ligarL_VertHoriz(diag, entTop(eOcorrencia, 0), relRight(rLocalProb), PreCardinalidade.TiposCard.C11, 0, 0);

        // --- ROW 2: Avaliacao, Ordem de Servico e Suprimentos ---
        Entidade eAvaliacao = criarEntidade(diag, "avaliacao_atendimento", X_C1, yRow2, comAtributosEntidade ? ATTRS_AVALIACAO_ATENDIMENTO : null);
        Entidade eOs = criarEntidade(diag, "ordem_servico", X_C2, yRow2, comAtributosEntidade ? ATTRS_ORDEM_SERVICO : null);
        Entidade eMaterial = criarEntidade(diag, "material_insumo", X_C3, yRow2, comAtributosEntidade ? ATTRS_MATERIAL_INSUMO : null);
        Entidade eFornecedor = criarEntidade(diag, "fornecedor", X_C4, yRow2, comAtributosEntidade ? ATTRS_FORNECEDOR : null);

        Relacionamento rRecebe = criarRelacionamento(diag, "recebe", X_R12, yRow2 - 4, null);
        Relacionamento rConsome = criarRelacionamento(diag, "consome", X_R23, yRow2 - 4, comAtributosEntidade ? ATTRS_REL_CONSOME : null);
        Relacionamento rFornece = criarRelacionamento(diag, "fornece", X_R34, yRow2 - 4, comAtributosEntidade ? ATTRS_REL_FORNECE : null);

        // Conexoes Horizontais Row 2
        ligarReto(diag, entRight(eAvaliacao, 0), relLeft(rRecebe), PreCardinalidade.TiposCard.C11, 0, 0);
        ligarReto(diag, entLeft(eOs, 0), relRight(rRecebe), PreCardinalidade.TiposCard.C01, 0, 0);

        ligarReto(diag, entRight(eOs, 0), relLeft(rConsome), PreCardinalidade.TiposCard.C0N, 0, 0);
        ligarReto(diag, entLeft(eMaterial, 0), relRight(rConsome), PreCardinalidade.TiposCard.C0N, 0, 0);

        ligarReto(diag, entRight(eMaterial, 0), relLeft(rFornece), PreCardinalidade.TiposCard.C1N, 0, 0);
        ligarReto(diag, entLeft(eFornecedor, 0), relRight(rFornece), PreCardinalidade.TiposCard.C1N, 0, 0);

        // Corredor 1-2: registra_avaliacao (cidadao <-> avaliacao_atendimento) e gera (ocorrencia <-> ordem_servico)
        Relacionamento rRegAval = criarRelacionamento(diag, "registra_avaliacao", X_R12, yCorridor12, null);
        ligarZ_HorizVertHoriz(diag, entLeft(eCidadao, 12), relRight(rRegAval), X_C2 - 55, PreCardinalidade.TiposCard.C0N, 0, 18);
        ligarL_VertHoriz(diag, entTop(eAvaliacao, 0), relLeft(rRegAval), PreCardinalidade.TiposCard.C11, 0, 0);

        Relacionamento rGera = criarRelacionamento(diag, "gera", X_R23, yCorridor12, null);
        ligarZ_HorizVertHoriz(diag, entLeft(eOcorrencia, 12), relRight(rGera), X_C3 - 55, PreCardinalidade.TiposCard.C0N, 0, 18);
        ligarL_VertHoriz(diag, entTop(eOs, 0), relLeft(rGera), PreCardinalidade.TiposCard.C11, 0, 0);

        // --- ROW 3: Estrutura Operacional, Frota e Equipes ---
        Entidade eVeiculo = criarEntidade(diag, "veiculo_frota", X_C1, yRow3, comAtributosEntidade ? ATTRS_VEICULO_FROTA : null);
        Entidade eSecretaria = criarEntidade(diag, "secretaria_orgao", X_C2, yRow3, comAtributosEntidade ? ATTRS_SECRETARIA_ORGAO : null);
        Entidade eEquipe = criarEntidade(diag, "equipe_manutencao", X_C3, yRow3, comAtributosEntidade ? ATTRS_EQUIPE_MANUTENCAO : null);
        Entidade eFuncionario = criarEntidade(diag, "funcionario", X_C4, yRow3, comAtributosEntidade ? ATTRS_FUNCIONARIO : null);

        Relacionamento rMantemFrota = criarRelacionamento(diag, "mantem_frota", X_R12, yRow3 - 4, null);
        Relacionamento rCoordena = criarRelacionamento(diag, "coordena", X_R23, yRow3 - 4, null);
        Relacionamento rComposta = criarRelacionamento(diag, "e_composta_por", X_R34, yRow3 - 4, null);

        // Conexoes Horizontais Row 3
        ligarReto(diag, entRight(eVeiculo, 0), relLeft(rMantemFrota), PreCardinalidade.TiposCard.C11, 0, 0);
        ligarReto(diag, entLeft(eSecretaria, 0), relRight(rMantemFrota), PreCardinalidade.TiposCard.C0N, 0, 0);

        ligarReto(diag, entRight(eSecretaria, 0), relLeft(rCoordena), PreCardinalidade.TiposCard.C1N, 0, 0);
        ligarReto(diag, entLeft(eEquipe, 0), relRight(rCoordena), PreCardinalidade.TiposCard.C11, 0, 0);

        ligarReto(diag, entRight(eEquipe, 0), relLeft(rComposta), PreCardinalidade.TiposCard.C1N, 0, 0);
        ligarReto(diag, entLeft(eFuncionario, 0), relRight(rComposta), PreCardinalidade.TiposCard.C11, 0, 0);

        // Corredor 2-3: desloca_para (veiculo_frota <-> ordem_servico) e executa (equipe_manutencao <-> ordem_servico)
        Relacionamento rDesloca = criarRelacionamento(diag, "desloca_para", X_R12, yCorridor23, null);
        ligarL_VertHoriz(diag, entTop(eVeiculo, 0), relLeft(rDesloca), PreCardinalidade.TiposCard.C0N, 0, 0);
        ligarZ_HorizVertHoriz(diag, entLeft(eOs, 12), relRight(rDesloca), X_C2 - 55, PreCardinalidade.TiposCard.C11, 0, 18);

        Relacionamento rExecuta = criarRelacionamento(diag, "executa", X_R23, yCorridor23, null);
        ligarL_VertHoriz(diag, entTop(eEquipe, 0), relRight(rExecuta), PreCardinalidade.TiposCard.C0N, 0, 0);
        ligarZ_HorizVertHoriz(diag, entRight(eOs, 12), relLeft(rExecuta), X_C2 + ENT_W + 55, PreCardinalidade.TiposCard.C11, 0, 18);

        diag.getItensSelecionados().clear();
        return diag;
    }

    /**
     * Subdominio 1: Demandas do Cidadao, Geolocalizacao e Avaliacao (01-demandas-cidadao)
     */
    private static DiagramaConceitual construirModulo1(Editor ed) {
        DiagramaConceitual diag = criarDiagrama(ed);
        int yRow1 = 100;
        int yCorridor = 380;
        int yRow2 = 470;

        Entidade eRegiao = criarEntidade(diag, "regiao_cep", X_C1, yRow1, ATTRS_REGIAO_CEP);
        Entidade eCidadao = criarEntidade(diag, "cidadao", X_C2, yRow1, ATTRS_CIDADAO);
        Entidade eOcorrencia = criarEntidade(diag, "ocorrencia", X_C3, yRow1, ATTRS_OCORRENCIA);
        Entidade eAnexo = criarEntidade(diag, "anexo_midia", X_C4, yRow1, ATTRS_ANEXO_MIDIA);

        Relacionamento rEndMoradia = criarRelacionamento(diag, "endereco_moradia", X_R12, yRow1 - 4, null);
        Relacionamento rReporta = criarRelacionamento(diag, "reporta", X_R23, yRow1 - 4, null);
        Relacionamento rPossui = criarRelacionamento(diag, "possui", X_R34, yRow1 - 4, null);
        Relacionamento rLocalProb = criarRelacionamento(diag, "local_do_problema", (X_C1 + X_C3) / 2, 18, null);

        ligarReto(diag, entRight(eRegiao, 0), relLeft(rEndMoradia), PreCardinalidade.TiposCard.C0N, 0, 0);
        ligarReto(diag, entLeft(eCidadao, 0), relRight(rEndMoradia), PreCardinalidade.TiposCard.C11, 0, 0);

        ligarReto(diag, entRight(eCidadao, 0), relLeft(rReporta), PreCardinalidade.TiposCard.C0N, 0, 0);
        ligarReto(diag, entLeft(eOcorrencia, 0), relRight(rReporta), PreCardinalidade.TiposCard.C11, 0, 0);

        ligarReto(diag, entRight(eOcorrencia, 0), relLeft(rPossui), PreCardinalidade.TiposCard.C0N, 0, 0);
        ligarReto(diag, entLeft(eAnexo, 0), relRight(rPossui), PreCardinalidade.TiposCard.C11, 0, 0);

        ligarL_VertHoriz(diag, entTop(eRegiao, 0), relLeft(rLocalProb), PreCardinalidade.TiposCard.C0N, 0, 0);
        ligarL_VertHoriz(diag, entTop(eOcorrencia, 0), relRight(rLocalProb), PreCardinalidade.TiposCard.C11, 0, 0);

        Entidade eAvaliacao = criarEntidade(diag, "avaliacao_atendimento", X_C1, yRow2, ATTRS_AVALIACAO_ATENDIMENTO);
        Entidade eOs = criarEntidade(diag, "ordem_servico", X_C2, yRow2, ATTRS_ORDEM_SERVICO);
        Relacionamento rRecebe = criarRelacionamento(diag, "recebe", X_R12, yRow2 - 4, null);

        ligarReto(diag, entRight(eAvaliacao, 0), relLeft(rRecebe), PreCardinalidade.TiposCard.C11, 0, 0);
        ligarReto(diag, entLeft(eOs, 0), relRight(rRecebe), PreCardinalidade.TiposCard.C01, 0, 0);

        Relacionamento rRegAval = criarRelacionamento(diag, "registra_avaliacao", X_R12, yCorridor, null);
        ligarZ_HorizVertHoriz(diag, entLeft(eCidadao, 12), relRight(rRegAval), X_C2 - 55, PreCardinalidade.TiposCard.C0N, 0, 18);
        ligarL_VertHoriz(diag, entTop(eAvaliacao, 0), relLeft(rRegAval), PreCardinalidade.TiposCard.C11, 0, 0);

        Relacionamento rGera = criarRelacionamento(diag, "gera", X_R23, yCorridor, null);
        ligarZ_HorizVertHoriz(diag, entLeft(eOcorrencia, 12), relRight(rGera), X_C3 - 55, PreCardinalidade.TiposCard.C0N, 0, 18);
        ligarL_VertHoriz(diag, entTop(eOs, 0), relLeft(rGera), PreCardinalidade.TiposCard.C11, 0, 0);

        diag.getItensSelecionados().clear();
        return diag;
    }

    /**
     * Subdominio 2: Estrutura Operacional, Equipes, RH e Frota (02-estrutura-operacional)
     */
    private static DiagramaConceitual construirModulo2(Editor ed) {
        DiagramaConceitual diag = criarDiagrama(ed);
        int yRow1 = 50;
        int yCorridor = 300;
        int yRow2 = 390;

        Entidade eOs = criarEntidade(diag, "ordem_servico", X_C2, yRow1, ATTRS_ORDEM_SERVICO);

        Entidade eVeiculo = criarEntidade(diag, "veiculo_frota", X_C1, yRow2, ATTRS_VEICULO_FROTA);
        Entidade eSecretaria = criarEntidade(diag, "secretaria_orgao", X_C2, yRow2, ATTRS_SECRETARIA_ORGAO);
        Entidade eEquipe = criarEntidade(diag, "equipe_manutencao", X_C3, yRow2, ATTRS_EQUIPE_MANUTENCAO);
        Entidade eFuncionario = criarEntidade(diag, "funcionario", X_C4, yRow2, ATTRS_FUNCIONARIO);

        Relacionamento rMantemFrota = criarRelacionamento(diag, "mantem_frota", X_R12, yRow2 - 4, null);
        Relacionamento rCoordena = criarRelacionamento(diag, "coordena", X_R23, yRow2 - 4, null);
        Relacionamento rComposta = criarRelacionamento(diag, "e_composta_por", X_R34, yRow2 - 4, null);

        ligarReto(diag, entRight(eVeiculo, 0), relLeft(rMantemFrota), PreCardinalidade.TiposCard.C11, 0, 0);
        ligarReto(diag, entLeft(eSecretaria, 0), relRight(rMantemFrota), PreCardinalidade.TiposCard.C0N, 0, 0);

        ligarReto(diag, entRight(eSecretaria, 0), relLeft(rCoordena), PreCardinalidade.TiposCard.C1N, 0, 0);
        ligarReto(diag, entLeft(eEquipe, 0), relRight(rCoordena), PreCardinalidade.TiposCard.C11, 0, 0);

        ligarReto(diag, entRight(eEquipe, 0), relLeft(rComposta), PreCardinalidade.TiposCard.C1N, 0, 0);
        ligarReto(diag, entLeft(eFuncionario, 0), relRight(rComposta), PreCardinalidade.TiposCard.C11, 0, 0);

        Relacionamento rDesloca = criarRelacionamento(diag, "desloca_para", X_R12, yCorridor, null);
        ligarL_VertHoriz(diag, entTop(eVeiculo, 0), relLeft(rDesloca), PreCardinalidade.TiposCard.C0N, 0, 0);
        ligarZ_HorizVertHoriz(diag, entLeft(eOs, 0), relRight(rDesloca), X_C2 - 55, PreCardinalidade.TiposCard.C11, 0, 0);

        Relacionamento rExecuta = criarRelacionamento(diag, "executa", X_R23, yCorridor, null);
        ligarL_VertHoriz(diag, entTop(eEquipe, 0), relRight(rExecuta), PreCardinalidade.TiposCard.C0N, 0, 0);
        ligarZ_HorizVertHoriz(diag, entRight(eOs, 0), relLeft(rExecuta), X_C2 + ENT_W + 55, PreCardinalidade.TiposCard.C11, 0, 0);

        diag.getItensSelecionados().clear();
        return diag;
    }

    /**
     * Subdominio 3: Execucao de Ordens de Servico, Almoxarifado e Fornecedores (03-execucao-suprimentos)
     */
    private static DiagramaConceitual construirModulo3(Editor ed) {
        DiagramaConceitual diag = criarDiagrama(ed);
        int yRow1 = 50;
        int yCorridor12 = 330;
        int yRow2 = 420;
        int yCorridor23 = 680;
        int yRow3 = 770;

        Entidade eOcorrencia = criarEntidade(diag, "ocorrencia", X_C3, yRow1, ATTRS_OCORRENCIA);

        Entidade eAvaliacao = criarEntidade(diag, "avaliacao_atendimento", X_C1, yRow2, ATTRS_AVALIACAO_ATENDIMENTO);
        Entidade eOs = criarEntidade(diag, "ordem_servico", X_C2, yRow2, ATTRS_ORDEM_SERVICO);
        Entidade eMaterial = criarEntidade(diag, "material_insumo", X_C3, yRow2, ATTRS_MATERIAL_INSUMO);
        Entidade eFornecedor = criarEntidade(diag, "fornecedor", X_C4, yRow2, ATTRS_FORNECEDOR);

        Relacionamento rRecebe = criarRelacionamento(diag, "recebe", X_R12, yRow2 - 4, null);
        Relacionamento rConsome = criarRelacionamento(diag, "consome", X_R23, yRow2 - 4, ATTRS_REL_CONSOME);
        Relacionamento rFornece = criarRelacionamento(diag, "fornece", X_R34, yRow2 - 4, ATTRS_REL_FORNECE);

        ligarReto(diag, entRight(eAvaliacao, 0), relLeft(rRecebe), PreCardinalidade.TiposCard.C11, 0, 0);
        ligarReto(diag, entLeft(eOs, 0), relRight(rRecebe), PreCardinalidade.TiposCard.C01, 0, 0);

        ligarReto(diag, entRight(eOs, 0), relLeft(rConsome), PreCardinalidade.TiposCard.C0N, 0, 0);
        ligarReto(diag, entLeft(eMaterial, 0), relRight(rConsome), PreCardinalidade.TiposCard.C0N, 0, 0);

        ligarReto(diag, entRight(eMaterial, 0), relLeft(rFornece), PreCardinalidade.TiposCard.C1N, 0, 0);
        ligarReto(diag, entLeft(eFornecedor, 0), relRight(rFornece), PreCardinalidade.TiposCard.C1N, 0, 0);

        Relacionamento rGera = criarRelacionamento(diag, "gera", X_R23, yCorridor12, null);
        ligarZ_HorizVertHoriz(diag, entLeft(eOcorrencia, 0), relRight(rGera), X_C3 - 55, PreCardinalidade.TiposCard.C0N, 0, 0);
        ligarL_VertHoriz(diag, entTop(eOs, 0), relLeft(rGera), PreCardinalidade.TiposCard.C11, 0, 0);

        Entidade eVeiculo = criarEntidade(diag, "veiculo_frota", X_C1, yRow3, ATTRS_VEICULO_FROTA);
        Entidade eEquipe = criarEntidade(diag, "equipe_manutencao", X_C3, yRow3, ATTRS_EQUIPE_MANUTENCAO);

        Relacionamento rDesloca = criarRelacionamento(diag, "desloca_para", X_R12, yCorridor23, null);
        ligarL_VertHoriz(diag, entTop(eVeiculo, 0), relLeft(rDesloca), PreCardinalidade.TiposCard.C0N, 0, 0);
        ligarZ_HorizVertHoriz(diag, entLeft(eOs, 12), relRight(rDesloca), X_C2 - 55, PreCardinalidade.TiposCard.C11, 0, 18);

        Relacionamento rExecuta = criarRelacionamento(diag, "executa", X_R23, yCorridor23, null);
        ligarL_VertHoriz(diag, entTop(eEquipe, 0), relRight(rExecuta), PreCardinalidade.TiposCard.C0N, 0, 0);
        ligarZ_HorizVertHoriz(diag, entRight(eOs, 12), relLeft(rExecuta), X_C2 + ENT_W + 55, PreCardinalidade.TiposCard.C11, 0, 18);

        diag.getItensSelecionados().clear();
        return diag;
    }

    private static String escapeXml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private static void exportarDiagrama(
            DiagramaConceitual diag,
            String baseName,
            File dirDiagramas,
            File dirOut) throws Exception {

        diag.getItensSelecionados().clear();
        Point extremo = diag.getPontoExtremo();
        int width = Math.max(extremo.x + 60, 800);
        int height = Math.max(extremo.y + 50, 400);

        // 1. Renderizar PNG em alta definicao (2x scale) usando ExternalPaint nativo do brModelo.jar
        File pngFile = new File(dirOut, baseName + ".png");
        double scale = 2.0;
        BufferedImage img = new BufferedImage((int) (width * scale), (int) (height * scale), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        g2.scale(scale, scale);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, width, height);
        diag.ExternalPaint(g2);
        g2.dispose();
        ImageIO.write(img, "png", pngFile);

        // 2. Salvar arquivo nativo .brM3 (serializacao GuardaPadraoBrM do brModelo 3.2)
        File brm3File = new File(dirDiagramas, baseName + ".brM3");
        diag.setNome(baseName);
        diag.setArquivo("");
        GuardaPadraoBrM gp = new GuardaPadraoBrM(diag);
        gp.versaoDiagrama = "3.2.0";
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(brm3File))) {
            oos.writeObject(gp);
        }
        diag.setArquivo(brm3File.getAbsolutePath());

        // 3. Exportar SVG vetorial fiel as coordenadas exatas calculadas pelo brModelo.jar
        File svgFile = new File(dirOut, baseName + ".svg");
        StringBuilder svg = new StringBuilder();
        svg.append(String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<svg xmlns=\"http://www.w3.org/2000/svg\" width=\"%d\" height=\"%d\" viewBox=\"0 0 %d %d\">\n" +
                "  <rect width=\"100%%\" height=\"100%%\" fill=\"#ffffff\"/>\n",
                width, height, width, height));

        List<FormaElementar> itens = diag.getListaDeItens();

        // Camada 1: Linhas (Ligacao)
        for (FormaElementar item : itens) {
            if (item instanceof Ligacao) {
                Ligacao lig = (Ligacao) item;
                List<PontoDeLinha> pts = lig.getPontos();
                if (pts != null && pts.size() >= 2) {
                    StringBuilder sbPts = new StringBuilder();
                    for (int i = 0; i < pts.size(); i++) {
                        Point c = pts.get(i).getCentro();
                        if (i > 0) sbPts.append(" ");
                        sbPts.append(c.x).append(",").append(c.y);
                    }
                    svg.append(String.format(
                            "  <polyline points=\"%s\" fill=\"none\" stroke=\"#000000\" stroke-width=\"1.2\"/>\n",
                            sbPts.toString()));
                }
            }
        }

        // Camada 2: Entidades, Relacionamentos, Atributos e Cardinalidades
        for (FormaElementar item : itens) {
            if (item instanceof Entidade) {
                Entidade e = (Entidade) item;
                int x = e.getLeft(), y = e.getTop(), w = e.getWidth(), h = e.getHeight();
                svg.append(String.format(
                        "  <rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" fill=\"#808080\" stroke=\"none\"/>\n" +
                        "  <rect x=\"%d\" y=\"%d\" width=\"%d\" height=\"%d\" fill=\"#ffffff\" stroke=\"#000000\" stroke-width=\"1.2\"/>\n" +
                        "  <text x=\"%d\" y=\"%d\" text-anchor=\"middle\" font-family=\"DejaVu Sans, Arial, sans-serif\" font-size=\"12\" fill=\"#000000\">%s</text>\n",
                        x + 2, y + 2, w, h,
                        x, y, w, h,
                        x + w / 2, y + h / 2 + 4, escapeXml(e.getTexto())));
            } else if (item instanceof Relacionamento) {
                Relacionamento r = (Relacionamento) item;
                int x = r.getLeft(), y = r.getTop(), w = r.getWidth(), h = r.getHeight();
                String poly = String.format("%d,%d %d,%d %d,%d %d,%d",
                        x, y + h / 2,
                        x + w / 2, y,
                        x + w, y + h / 2,
                        x + w / 2, y + h);
                svg.append(String.format(
                        "  <polygon points=\"%s\" fill=\"#ffffff\" stroke=\"#000000\" stroke-width=\"1.2\"/>\n" +
                        "  <text x=\"%d\" y=\"%d\" text-anchor=\"middle\" font-family=\"DejaVu Sans, Arial, sans-serif\" font-size=\"12\" fill=\"#000000\">%s</text>\n",
                        poly,
                        x + w / 2, y + h / 2 + 4, escapeXml(r.getTexto())));
            } else if (item instanceof Atributo) {
                Atributo a = (Atributo) item;
                int x = a.getLeft(), y = a.getTop(), h = a.getHeight();
                int cx = x + 7;
                int cy = y + h / 2;
                String fill = a.isIdentificador() ? "#000000" : "#ffffff";
                svg.append(String.format(
                        "  <circle cx=\"%d\" cy=\"%d\" r=\"5.5\" fill=\"%s\" stroke=\"#000000\" stroke-width=\"1.2\"/>\n" +
                        "  <text x=\"%d\" y=\"%d\" text-anchor=\"start\" font-family=\"DejaVu Sans, Arial, sans-serif\" font-size=\"12\" fill=\"#000000\">%s</text>\n",
                        cx, cy, fill,
                        x + 16, cy + 4, escapeXml(a.getTexto())));
            } else if (item instanceof Cardinalidade) {
                Cardinalidade c = (Cardinalidade) item;
                int x = c.getLeft(), y = c.getTop(), w = c.getWidth(), h = c.getHeight();
                svg.append(String.format(
                        "  <text x=\"%d\" y=\"%d\" text-anchor=\"middle\" font-family=\"DejaVu Sans, Arial, sans-serif\" font-size=\"12\" fill=\"#000000\">%s</text>\n",
                        x + w / 2, y + h / 2 + 4, escapeXml(c.FullCard())));
            }
        }

        svg.append("</svg>\n");
        Files.write(svgFile.toPath(), svg.toString().getBytes(StandardCharsets.UTF_8));

        // 4. Converter SVG vetorial para PDF via rsvg-convert
        File pdfFile = new File(dirOut, baseName + ".pdf");
        ProcessBuilder pb = new ProcessBuilder(
                "rsvg-convert", "-f", "pdf", "-o", pdfFile.getAbsolutePath(), svgFile.getAbsolutePath());
        int exitCode = pb.start().waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Falha ao converter SVG para PDF com rsvg-convert: codigo " + exitCode);
        }

        // 5. Validar recarregamento do arquivo .brM3 via GuardaPadraoBrM
        try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(new java.io.FileInputStream(brm3File))) {
            GuardaPadraoBrM gpLido = (GuardaPadraoBrM) ois.readObject();
            Diagrama carregado = gpLido.getDiagrama();
            if (carregado == null || carregado.getListaDeItens().isEmpty()) {
                throw new RuntimeException("Falha na validacao de leitura do arquivo .brM3: " + brm3File);
            }
        }

        System.out.printf("Gerado com sucesso: %s (.brM3, .png, .svg, .pdf) [%dx%d px, %d itens]%n",
                baseName, width, height, itens.size());
    }

    public static void main(String[] args) {
        try {
            File baseDir = args.length > 0 ? new File(args[0]) : new File("modelagem/diagramas");
            File outDir = new File(baseDir, "out");
            baseDir.mkdirs();
            outDir.mkdirs();

            Editor ed = criarEditorHeadless();

            exportarDiagrama(construirModeloGlobal(ed, false), "00-visao-geral", baseDir, outDir);
            exportarDiagrama(construirModulo1(ed), "01-demandas-cidadao", baseDir, outDir);
            exportarDiagrama(construirModulo2(ed), "02-estrutura-operacional", baseDir, outDir);
            exportarDiagrama(construirModulo3(ed), "03-execucao-suprimentos", baseDir, outDir);
            exportarDiagrama(construirModeloGlobal(ed, true), "04-modelo-completo", baseDir, outDir);

            new File("config.chc").delete();
            System.exit(0);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
    }
}
