package UserInterface;

import UserInterface3D.PainelTrelica3D;
import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout cards = new CardLayout();
    private JPanel painelCentral = new JPanel(cards);
    private boolean telaCheia = false;
    private Rectangle janelaAnterior;

    public MainFrame() {
        setTitle("Editor de Treliças");
        setSize(1200, 750);
        setMinimumSize(new Dimension(980, 620));
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        PainelDesenho painel = new PainelDesenho();
        PainelLateral lateral = new PainelLateral();
        BarraFerramentas barra = new BarraFerramentas(painel);

        // Conectar painel lateral ao painel de desenho
        painel.setPainelLateral(lateral);
        lateral.setOnForcaAlterada(v -> painel.repaint());
        lateral.setVinculoCallback(no -> painel.vinculoDoNo(no));

        JPanel modo2D = new JPanel(new BorderLayout());
        modo2D.add(barra, BorderLayout.NORTH);
        modo2D.add(painel, BorderLayout.CENTER);
        modo2D.add(lateral, BorderLayout.EAST);

        painelCentral.add(modo2D, "2D");
        painelCentral.add(new PainelTrelica3D(), "3D");

        add(criarBarraModo(), BorderLayout.NORTH);
        add(painelCentral, BorderLayout.CENTER);
    }

    private JComponent criarBarraModo() {
        JPanel topo = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topo.setBackground(new Color(38, 52, 78));
        topo.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));

        JLabel titulo = new JLabel("Calculadora de Treliças");
        titulo.setForeground(Color.WHITE);
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));

        JButton btn2D = criarBotaoTopo("Aplicativo 2D");
        JButton btn3D = criarBotaoTopo("Aplicativo 3D");
        JButton btnTela = criarBotaoTopo("Tela cheia");

        btn2D.addActionListener(e -> cards.show(painelCentral, "2D"));
        btn3D.addActionListener(e -> cards.show(painelCentral, "3D"));
        btnTela.addActionListener(e -> {
            alternarTelaCheia();
            btnTela.setText(telaCheia ? "Janela" : "Tela cheia");
        });

        topo.add(titulo);
        topo.add(Box.createHorizontalStrut(18));
        topo.add(btn2D);
        topo.add(btn3D);
        topo.add(Box.createHorizontalStrut(18));
        topo.add(btnTela);
        return topo;
    }

    private JButton criarBotaoTopo(String texto) {
        JButton botao = new JButton(texto);
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return botao;
    }

    private void alternarTelaCheia() {
        GraphicsDevice tela = GraphicsEnvironment
            .getLocalGraphicsEnvironment()
            .getDefaultScreenDevice();

        if (!telaCheia) {
            janelaAnterior = getBounds();
            dispose();
            setUndecorated(true);
            tela.setFullScreenWindow(this);
            setVisible(true);
            telaCheia = true;
        } else {
            tela.setFullScreenWindow(null);
            dispose();
            setUndecorated(false);
            if (janelaAnterior != null) setBounds(janelaAnterior);
            setVisible(true);
            telaCheia = false;
        }
    }
}
