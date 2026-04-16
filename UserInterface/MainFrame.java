package UserInterface;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {

        setTitle("Editor de Treliças");

        setSize(1000, 700);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        PainelDesenho painel = new PainelDesenho();

        BarraFerramentas barra = new BarraFerramentas(painel);

        add(barra, BorderLayout.NORTH);
        add(painel, BorderLayout.CENTER);

    }

}