package UserInterface;

import model.*;
import enums.TipoVinculo;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * Painel lateral direito.
 * Exibe informações e controles para o nó ou barra selecionada.
 */
public class PainelLateral extends JPanel {

    private JLabel lblTitulo;
    private JPanel painelNo;
    private JPanel painelBarra;
    private JPanel painelVazio;

    // Campos do painel de nó
    private JLabel  lblNoId;
    private JLabel  lblVinculo;
    private JLabel  lblReacoes;
    private JTextField campoFx;
    private JTextField campoFy;
    private JButton btnFxPos, btnFxNeg, btnFyPos, btnFyNeg;

    // Campos do painel de barra
    private JLabel lblBarraId;
    private JLabel lblBarraForca;
    private JLabel lblBarraTipo;

    private No       noAtual     = null;
    private Elemento barraAtual  = null;

    private Consumer<Void> onForcaAlterada;

    public PainelLateral() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(220, 0));
        setBackground(new Color(248, 248, 252));
        setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, new Color(200, 200, 210)));

        // Título
        lblTitulo = new JLabel("Nenhum item selecionado", SwingConstants.CENTER);
        lblTitulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        lblTitulo.setOpaque(true);
        lblTitulo.setBackground(new Color(60, 80, 140));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        add(lblTitulo, BorderLayout.NORTH);

        // Painel central (troca de acordo com seleção)
        painelVazio = criarPainelVazio();
        painelNo    = criarPainelNo();
        painelBarra = criarPainelBarra();

        mostrarVazio();
    }

    public void setOnForcaAlterada(Consumer<Void> cb) { this.onForcaAlterada = cb; }

    // -----------------------------------------------------------------------
    // Seleção
    // -----------------------------------------------------------------------

    public void selecionarNo(No no) {
        noAtual    = no;
        barraAtual = null;
        atualizarPainelNo();
        mostrar(painelNo);
        lblTitulo.setText("Nó " + no.getId());
    }

    public void selecionarBarra(Elemento e) {
        barraAtual = e;
        noAtual    = null;
        atualizarPainelBarra();
        mostrar(painelBarra);
        lblTitulo.setText("Barra " + e.getId());
    }

    public void limparSelecao() {
        noAtual = null; barraAtual = null;
        lblTitulo.setText("Nenhum item selecionado");
        mostrarVazio();
    }

    public No       getNoAtual()    { return noAtual;    }
    public Elemento getBarraAtual() { return barraAtual; }

    // -----------------------------------------------------------------------
    // Atualização dos painéis
    // -----------------------------------------------------------------------

    public void atualizarPainelNo() {
        if (noAtual == null) return;

        lblNoId.setText("ID: " + noAtual.getId() +
            "   (" + (int)noAtual.getX() + ", " + (int)noAtual.getY() + ")");

        // Vínculo
        Vinculo v = buscarVinculo(noAtual);
        if (v != null) {
            lblVinculo.setText("Vínculo: " + v.getTipo());
            if (v.getReacaoX() != 0 || v.getReacaoY() != 0) {
                lblReacoes.setText(String.format("<html>Rx = %.2f N<br>Ry = %.2f N</html>",
                    v.getReacaoX(), v.getReacaoY()));
            } else {
                lblReacoes.setText("(calcule para ver reações)");
            }
        } else {
            lblVinculo.setText("Sem vínculo");
            lblReacoes.setText("");
        }

        // Forças
        campoFx.setText(String.valueOf(noAtual.getFx()));
        campoFy.setText(String.valueOf(noAtual.getFy()));
    }

    public void atualizarPainelBarra() {
        if (barraAtual == null) return;
        lblBarraId.setText("Barra " + barraAtual.getId() +
            "  (nó " + barraAtual.getNoInicial().getId() +
            " → " + barraAtual.getNoFinal().getId() + ")");
        double f = barraAtual.getForcaInterna();
        if (Math.abs(f) < 1e-9) {
            lblBarraForca.setText("Força interna: —");
            lblBarraTipo.setText("(calcule para ver resultado)");
            lblBarraTipo.setForeground(Color.GRAY);
        } else {
            lblBarraForca.setText(String.format("Força: %.4f N", Math.abs(f)));
            boolean tracao = f > 0;
            lblBarraTipo.setText(tracao ? "TRAÇÃO" : "COMPRESSÃO");
            lblBarraTipo.setForeground(tracao ? new Color(0, 100, 180) : new Color(180, 0, 0));
        }
    }

    // -----------------------------------------------------------------------
    // Construção dos sub-painéis
    // -----------------------------------------------------------------------

    private JPanel criarPainelVazio() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(248, 248, 252));
        JLabel msg = new JLabel(
            "<html><center>Clique em um nó<br>ou barra para ver<br>informações</center></html>",
            SwingConstants.CENTER);
        msg.setForeground(new Color(150, 150, 160));
        msg.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        p.add(msg, BorderLayout.CENTER);
        return p;
    }

    private JPanel criarPainelNo() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(248, 248, 252));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Identificação
        lblNoId = rotulo("");
        p.add(lblNoId);
        p.add(Box.createVerticalStrut(4));

        lblVinculo = rotulo("");
        lblVinculo.setForeground(new Color(80, 80, 160));
        p.add(lblVinculo);
        p.add(Box.createVerticalStrut(2));

        lblReacoes = rotulo("");
        lblReacoes.setForeground(new Color(0, 120, 60));
        p.add(lblReacoes);

        p.add(new JSeparator());
        p.add(Box.createVerticalStrut(8));

        // Forças externas
        JLabel secForca = new JLabel("Forças externas aplicadas");
        secForca.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        secForca.setAlignmentX(LEFT_ALIGNMENT);
        p.add(secForca);
        p.add(Box.createVerticalStrut(8));

        // Fx
        p.add(criarLinhaForca("Fx (→+  ←−)", true));
        p.add(Box.createVerticalStrut(6));
        // Fy
        p.add(criarLinhaForca("Fy (↑+  ↓−)", false));
        p.add(Box.createVerticalStrut(4));

        // Dica de sinal
        JLabel dica = new JLabel("<html><font color='#888888' size='2'>" +
            "↑ Fy positivo = para cima<br>" +
            "↓ Fy negativo = para baixo<br>" +
            "→ Fx positivo = para direita<br>" +
            "← Fx negativo = para esquerda</font></html>");
        dica.setAlignmentX(LEFT_ALIGNMENT);
        p.add(dica);

        return p;
    }

    private JPanel criarLinhaForca(String label, boolean isFx) {
        JPanel linha = new JPanel();
        linha.setLayout(new BoxLayout(linha, BoxLayout.Y_AXIS));
        linha.setBackground(new Color(248, 248, 252));
        linha.setAlignmentX(LEFT_ALIGNMENT);
        linha.setMaximumSize(new Dimension(200, 80));

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        linha.add(lbl);
        linha.add(Box.createVerticalStrut(3));

        JPanel controles = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        controles.setBackground(new Color(248, 248, 252));
        controles.setAlignmentX(LEFT_ALIGNMENT);

        JTextField campo = new JTextField("0", 6);
        campo.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        campo.setMaximumSize(new Dimension(70, 28));

        JButton btnPos = new JButton(isFx ? "→" : "↑");
        JButton btnNeg = new JButton(isFx ? "←" : "↓");
        estilizarBotaoSeta(btnPos, true);
        estilizarBotaoSeta(btnNeg, false);

        if (isFx) {
            campoFx = campo;
            btnFxPos = btnPos; btnFxNeg = btnNeg;
        } else {
            campoFy = campo;
            btnFyPos = btnPos; btnFyNeg = btnNeg;
        }

        // Botão positivo: incrementa 100
        btnPos.addActionListener(e -> incrementarForca(campo, isFx, +10));
        // Botão negativo: decrementa 10
        btnNeg.addActionListener(e -> incrementarForca(campo, isFx, -10));

        // Edição direta: ao sair do campo, aplica
        campo.addActionListener(e -> aplicarForca(campo, isFx));
        campo.addFocusListener(new FocusAdapter() {
            public void focusLost(FocusEvent e) { aplicarForca(campo, isFx); }
        });

        controles.add(btnNeg);
        controles.add(campo);
        controles.add(btnPos);
        linha.add(controles);
        return linha;
    }

    private void incrementarForca(JTextField campo, boolean isFx, double delta) {
        if (noAtual == null) return;
        try {
            double val = Double.parseDouble(campo.getText().replace(",", ".")) + delta;
            campo.setText(String.valueOf(val));
            aplicarForca(campo, isFx);
        } catch (NumberFormatException ignored) {}
    }

    private void aplicarForca(JTextField campo, boolean isFx) {
        if (noAtual == null) return;
        try {
            double val = Double.parseDouble(campo.getText().replace(",", "."));
            if (isFx) noAtual.setFx(val); else noAtual.setFy(val);
            campo.setText(String.valueOf(val));
            if (onForcaAlterada != null) onForcaAlterada.accept(null);
        } catch (NumberFormatException ignored) {
            campo.setBackground(new Color(255, 220, 220));
        }
    }

    private JPanel criarPainelBarra() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(248, 248, 252));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblBarraId    = rotulo("");
        lblBarraForca = rotulo("Força interna: —");
        lblBarraTipo  = new JLabel("(calcule para ver resultado)");
        lblBarraTipo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        lblBarraTipo.setAlignmentX(LEFT_ALIGNMENT);
        lblBarraTipo.setForeground(Color.GRAY);

        p.add(lblBarraId);
        p.add(Box.createVerticalStrut(8));
        p.add(lblBarraForca);
        p.add(Box.createVerticalStrut(4));
        p.add(lblBarraTipo);

        return p;
    }

    // -----------------------------------------------------------------------
    // Utilitários
    // -----------------------------------------------------------------------

    private JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private void estilizarBotaoSeta(JButton btn, boolean positivo) {
        btn.setPreferredSize(new Dimension(32, 26));
        btn.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBackground(positivo ? new Color(220, 240, 220) : new Color(240, 220, 220));
        btn.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void mostrar(JPanel painel) {
        remove(painelVazio); remove(painelNo); remove(painelBarra);
        add(painel, BorderLayout.CENTER);
        revalidate(); repaint();
    }

    private void mostrarVazio() {
        remove(painelNo); remove(painelBarra);
        add(painelVazio, BorderLayout.CENTER);
        revalidate(); repaint();
    }

    private Vinculo buscarVinculo(No no) {
        // PainelLateral não tem referência à trelica; MainFrame injeta via callback
        return _vinculoCallback != null ? _vinculoCallback.apply(no) : null;
    }

    // Callback para buscar vínculo de um nó
    private java.util.function.Function<No, Vinculo> _vinculoCallback;
    public void setVinculoCallback(java.util.function.Function<No, Vinculo> cb) {
        _vinculoCallback = cb;
    }
}
