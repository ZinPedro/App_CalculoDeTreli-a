package UserInterface;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("Editor de Treliças");
        setSize(1200, 750);
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

        add(barra,   BorderLayout.NORTH);
        add(painel,  BorderLayout.CENTER);
        add(lateral, BorderLayout.EAST);
    }
}
