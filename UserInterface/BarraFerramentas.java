package UserInterface;

import enums.Ferramenta;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BarraFerramentas extends JPanel {

    private PainelDesenho painel;

    private Map<Ferramenta, JButton> botoes = new HashMap<>();

    private JPopupMenu menuVinculo = new JPopupMenu();

    public BarraFerramentas(PainelDesenho painel) {

        this.painel = painel;

        setLayout(new FlowLayout(FlowLayout.RIGHT));
        setBackground(new Color(245, 245, 250));
        setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 210)));

        //Ferramentas
        criarBotao(Ferramenta.SELECIONAR,     "↖ Selecionar",   "Selecionar nó ou barra para ver detalhes no painel lateral");
        criarBotao(Ferramenta.DESENHAR_BARRA, "╱ Barra",         "Desenhar barra (clique no 1º nó, depois no 2º)");
        criarBotao(Ferramenta.APAGAR_NO,      "✕ Nó",            "Apagar nó e suas barras conectadas");
        criarBotao(Ferramenta.APAGAR_BARRA,   "✕ Barra",         "Apagar barra");

        criarSep();

        criarBotaoVinculo();
        criarMenuVinculos();

        criarBotao(Ferramenta.APAGAR_VINCULO, "✕ Vínculo",       "Apagar vínculo do nó");

        criarSep();

        //Tração/Compressão
        JToggleButton btnCores = new JToggleButton("🎨 Cores");
        btnCores.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        btnCores.setPreferredSize(new Dimension(90, 36));
        btnCores.setToolTipText("Mostrar cores de tração (azul) e compressão (vermelho)");
        btnCores.setFocusPainted(false);
        btnCores.addActionListener(e -> painel.setMostrarCores(btnCores.isSelected()));
        add(btnCores);

        criarSep();

        // Zoom do plano cartesiano 2D
        JButton btnZoomMenos = new JButton("-");
        estilizarBotaoAcao(btnZoomMenos, "Diminuir zoom do plano");
        btnZoomMenos.addActionListener(e -> painel.diminuirZoom());
        add(btnZoomMenos);

        JButton btnZoomReset = new JButton("100%");
        estilizarBotaoAcao(btnZoomReset, "Restaurar zoom do plano");
        btnZoomReset.addActionListener(e -> painel.resetarZoom());
        add(btnZoomReset);

        JButton btnZoomMais = new JButton("+");
        estilizarBotaoAcao(btnZoomMais, "Aumentar zoom do plano");
        btnZoomMais.addActionListener(e -> painel.aumentarZoom());
        add(btnZoomMais);

        criarSep();

        JButton btnLimpar = new JButton("Limpar");
        estilizarBotaoAcao(btnLimpar, "Limpar toda a treliça 2D");
        btnLimpar.setPreferredSize(new Dimension(78, 36));
        btnLimpar.addActionListener(e -> confirmarLimpeza());
        add(btnLimpar);

        criarSep();

        //Calcular
        JButton btnCalc = new JButton("⚙ Calcular");
        btnCalc.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        btnCalc.setPreferredSize(new Dimension(100, 36));
        btnCalc.setBackground(new Color(30, 120, 60));
        btnCalc.setForeground(Color.WHITE);
        btnCalc.setFocusPainted(false);
        btnCalc.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnCalc.setToolTipText("Calcular treliça do elemento selecionado");
        btnCalc.addActionListener(e -> painel.calcular());
        add(btnCalc);

        criarSep();
        
        //Ajuda
        JButton btnAjuda = new JButton("? Ajuda");
        btnAjuda.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        btnAjuda.setPreferredSize(new Dimension(80, 36));
        btnAjuda.setFocusPainted(false);
        btnAjuda.addActionListener(e -> mostrarAjuda());
        add(btnAjuda);

        selecionarFerramenta(Ferramenta.DESENHAR_BARRA);
    }

    private void criarBotao(Ferramenta f, String label, String tooltip) {
        JButton b = new JButton(label);
        b.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        b.setPreferredSize(new Dimension(label.length() * 8 + 20, 36));
        b.setToolTipText(tooltip);
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(new Color(190, 190, 200)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addActionListener(e -> selecionarFerramenta(f));
        botoes.put(f, b);
        add(b);
    }

    private void criarSep() {
        JSeparator s = new JSeparator(SwingConstants.VERTICAL);
        s.setPreferredSize(new Dimension(2, 32));
        add(s);
    }

    private void estilizarBotaoAcao(JButton b, String tooltip) {
        b.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        b.setPreferredSize(new Dimension(58, 36));
        b.setToolTipText(tooltip);
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(new Color(190, 190, 200)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void selecionarFerramenta(Ferramenta f) {

        painel.setFerramenta(f);

        for (JButton b : botoes.values()) {
            b.setBorder(new LineBorder(new Color(190,190,200)));
        }

        JButton sel = botoes.get(f);
        if (sel != null) sel.setBorder(new LineBorder(new Color(30, 100, 220), 2));
    }

    private void criarMenuVinculos() {

        JMenuItem pino = new JMenuItem("PINO(Rx + Ry)");
        JMenuItem rolete = new JMenuItem("ROLETE vertical (Ry)");
        JMenuItem roleteHorizontal = new JMenuItem("ROLETE horizontal (Rx)");
        JMenuItem pinoAngulado = new JMenuItem("PINO angulado...");

        pino.addActionListener(e -> {
            painel.setTipoVinculo(enums.TipoVinculo.PINO);
            painel.setAnguloVinculo(0);
            selecionarFerramenta(Ferramenta.CRIAR_VINCULO);
        });

        rolete.addActionListener(e -> {
            painel.setTipoVinculo(enums.TipoVinculo.ROLETE);
            painel.setAnguloVinculo(0);
            selecionarFerramenta(Ferramenta.CRIAR_VINCULO);
        });

        roleteHorizontal.addActionListener(e -> {
            painel.setTipoVinculo(enums.TipoVinculo.ROLETE_HORIZONTAL);
            painel.setAnguloVinculo(0);
            selecionarFerramenta(Ferramenta.CRIAR_VINCULO);
        });

        pinoAngulado.addActionListener(e -> selecionarPinoAngulado());

        menuVinculo.add(pino);
        menuVinculo.add(rolete);
        menuVinculo.add(roleteHorizontal);
        menuVinculo.add(pinoAngulado);
    }

    private void selecionarPinoAngulado() {
        String resposta = JOptionPane.showInputDialog(
            this,
            "Ângulo de sustentação em graus (0° = direita, 90° = cima):",
            "45");
        if (resposta == null || resposta.trim().isEmpty()) return;
        try {
            double angulo = Double.parseDouble(resposta.trim().replace(",", "."));
            painel.setTipoVinculo(enums.TipoVinculo.PINO_ANGULADO);
            painel.setAnguloVinculo(angulo);
            selecionarFerramenta(Ferramenta.CRIAR_VINCULO);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Ângulo inválido.", "Vínculo angulado", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void criarBotaoVinculo() {

        JButton b = new JButton("▲ Vínculo");
        b.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        b.setPreferredSize(new Dimension(95, 36));
        b.setToolTipText("Adicionar vínculo: Pino, rolete vertical/horizontal ou vínculo angulado");
        b.setFocusPainted(false);
        b.setBorder(new LineBorder(new Color(190, 190, 200)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        b.addActionListener(e -> {
            selecionarFerramenta(Ferramenta.CRIAR_VINCULO);
            menuVinculo.show(b, 0, b.getHeight());
        });

        botoes.put(Ferramenta.CRIAR_VINCULO, b);
        add(b);
    }

    private void mostrarAjuda() {
        String html = "<html><body style='font-family:sans-serif; width:460px; padding:8px'>" +
            "<h2 style='color:#1a3a6e'>📐 Como usar o Editor de Treliças</h2>" +

            "<h3 style='color:#2a5a2a'>🔧 Ferramentas</h3>" +
            "<ul>" +
            "<li><b>↖ Selecionar</b> — clique em um nó ou barra para ver detalhes no painel lateral</li>" +
            "<li><b>╱ Barra</b> — clique no 1º nó, depois no 2º. Se não houver nó na grade, ele é criado automaticamente</li>" +
            "<li><b>✕ Nó / ✕ Barra / ✕ Vínculo</b> — apaga o elemento clicado</li>" +
            "<li><b>▲ Vínculo</b> — escolha Pino, Rolete vertical, Rolete horizontal ou Pino angulado e clique em um nó</li>" +
            "<li><b>🎨 Cores</b> — após calcular, exibe barras em azul (tração) ou vermelho (compressão)</li>" +
            "<li><b>⚙ Calcular</b> — selecione um nó ou barra da treliça desejada, depois clique</li>" +
            "</ul>" +

            "<h3 style='color:#2a5a2a'>📋 Forças externas</h3>" +
            "<ul>" +
            "<li>Selecione um nó com a ferramenta <b>Selecionar</b></li>" +
            "<li>No painel direito, digite os valores de <b>Fx</b> e <b>Fy</b> ou use as setas</li>" +
            "<li><b>Fx positivo</b> → direita &nbsp; <b>Fx negativo</b> → esquerda</li>" +
            "<li><b>Fy positivo</b> → cima &nbsp; <b>Fy negativo</b> → baixo</li>" +
            "<li>A seta aparece no desenho em tempo real</li>" +
            "</ul>" +

            "<h3 style='color:#8a2a2a'>⚠️ Condição de isostasia — obrigatória para calcular</h3>" +
            "<p>A treliça precisa satisfazer: <b>m + r = 2n</b></p>" +
            "<ul>" +
            "<li><b>m</b> = número de barras</li>" +
            "<li><b>r</b> = número de reações (Pino = 2; Rolete vertical/horizontal e Pino angulado = 1)</li>" +
            "<li><b>n</b> = número de nós</li>" +
            "</ul>" +
            "<p><b>Exemplo:</b> treliça simples com 3 nós (n=3), 3 barras (m=3), 1 pino + 1 rolete (r=3):<br>" +
            "→ 3 + 3 = 6 = 2×3 ✔</p>" +

            "<h3 style='color:#8a2a2a'>🚫 Problemas comuns</h3>" +
            "<ul>" +
            "<li><b>Nós sobrepostos:</b> use sempre a ferramenta Barra — ela cria o nó automaticamente na grade. " +
            "Evite criar nós manualmente perto de posições já ocupadas</li>" +
            "<li><b>Barras duplicadas:</b> o sistema impede automaticamente</li>" +
            "<li><b>Cruzamento de barras:</b> quando duas barras se cruzam na grade, um nó de interseção é criado automaticamente</li>" +
            "<li><b>Estrutura instável:</b> se m+r &lt; 2n, adicione barras ou vínculos</li>" +
            "<li><b>Hiperestática:</b> se m+r &gt; 2n, remova barras ou vínculos</li>" +
            "<li><b>Múltiplas treliças:</b> use a ferramenta Selecionar para indicar qual treliça calcular antes de pressionar Calcular</li>" +
            "</ul>" +

            "<h3 style='color:#2a5a2a'>✅ Montagem recomendada</h3>" +
            "<ol>" +
            "<li>Desenhe as barras (os nós são criados automaticamente)</li>" +
            "<li>Adicione os vínculos nos nós de apoio. No Pino angulado, 0° aponta para direita e 90° para cima</li>" +
            "<li>Selecione os nós de carga e defina Fx/Fy no painel lateral</li>" +
            "<li>Selecione qualquer nó/barra da treliça com Selecionar</li>" +
            "<li>Clique em ⚙ Calcular</li>" +
            "</ol>" +
            "</body></html>";

        JEditorPane editor = new JEditorPane("text/html", html);
        editor.setEditable(false);
        editor.setBackground(new Color(252, 252, 255));

        JScrollPane scroll = new JScrollPane(editor);
        scroll.setPreferredSize(new Dimension(520, 480));
        scroll.getVerticalScrollBar().setValue(0);

        JOptionPane.showMessageDialog(painel, scroll,
            "Ajuda — Editor de Treliças", JOptionPane.PLAIN_MESSAGE);
    }

    private void confirmarLimpeza() {
        int opcao = JOptionPane.showConfirmDialog(
            painel,
            "Limpar toda a treliça 2D?",
            "Limpar",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (opcao == JOptionPane.YES_OPTION) {
            painel.limparTudo();
        }
    }
}
