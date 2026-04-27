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

        criarBotao(Ferramenta.DESENHAR_BARRA, "Desenhar barras");
        criarBotao(Ferramenta.DESENHAR_NO, "Criar nós");
        criarBotao(Ferramenta.APAGAR_NO, "Apagar nó");
        criarBotao(Ferramenta.APAGAR_BARRA, "Apagar barra");

        criarBotaoVinculo();
        criarMenuVinculos();

        criarBotao(Ferramenta.APAGAR_VINCULO, "Apagar vinculo");

        selecionarFerramenta(Ferramenta.DESENHAR_BARRA);
    }

    private void criarBotao(Ferramenta ferramenta, String tooltip) {

        JButton botao = new JButton();

        botao.setPreferredSize(new Dimension(40, 40));

        botao.setToolTipText(tooltip);

        botao.setBorder(new LineBorder(Color.GRAY));

        botao.addActionListener(e -> selecionarFerramenta(ferramenta));

        botoes.put(ferramenta, botao);

        add(botao);
    }

    private void selecionarFerramenta(Ferramenta ferramenta) {

        painel.setFerramentaAtual(ferramenta);

        for (JButton b : botoes.values()) {
            b.setBorder(new LineBorder(Color.GRAY));
        }

        botoes.get(ferramenta).setBorder(new LineBorder(Color.BLUE, 3));
    }

    private void criarMenuVinculos() {

        JMenuItem pino = new JMenuItem("PINO");
        JMenuItem rolete = new JMenuItem("ROLETE");

        pino.addActionListener(e -> {
            painel.setTipoVinculoAtual(enums.TipoVinculo.PINO);
            selecionarFerramenta(Ferramenta.CRIAR_VINCULO);
        });

        rolete.addActionListener(e -> {
            painel.setTipoVinculoAtual(enums.TipoVinculo.ROLETE);
            selecionarFerramenta(Ferramenta.CRIAR_VINCULO);
        });

        menuVinculo.add(pino);
        menuVinculo.add(rolete);
    }

    private void criarBotaoVinculo() {

        JButton botao = new JButton();

        botao.setPreferredSize(new Dimension(40, 40));

        botao.setToolTipText("Criar vínculo");

        botao.setBorder(new LineBorder(Color.GRAY));

        botao.addActionListener(e -> {

            selecionarFerramenta(Ferramenta.CRIAR_VINCULO);

            menuVinculo.show(botao, 0, botao.getHeight());

        });

        botoes.put(Ferramenta.CRIAR_VINCULO, botao);

        add(botao);
    }
}