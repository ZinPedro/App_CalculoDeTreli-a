package UserInterface;

import javax.swing.JFrame;

public class MainFrame extends JFrame {

    public MainFrame() {

        setTitle("Analisador de Treliças");

        setSize(1000, 700);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        add(new PainelDesenho());

    }
}