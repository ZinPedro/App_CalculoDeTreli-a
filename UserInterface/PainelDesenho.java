package UserInterface;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;

import model.Trelica;
import model.No;

public class PainelDesenho extends JPanel {

    private int tamanhoGrid = 40;

    private Trelica trelica = new Trelica();

    public PainelDesenho() {

        addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {

                criarNo(e.getX(), e.getY());

            }

        });

    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        desenharGrid(g);

        desenharNos(g);

    }

    private void desenharGrid(Graphics g) {

        g.setColor(Color.LIGHT_GRAY);

        for (int x = 0; x < getWidth(); x += tamanhoGrid) {
            g.drawLine(x, 0, x, getHeight());
        }

        for (int y = 0; y < getHeight(); y += tamanhoGrid) {
            g.drawLine(0, y, getWidth(), y);
        }
    }

    private void criarNo(int xMouse, int yMouse) {

        int gridX = Math.round((float)xMouse / tamanhoGrid) * tamanhoGrid;
        int gridY = Math.round((float)yMouse / tamanhoGrid) * tamanhoGrid;

        int id = trelica.getNos().size() + 1;

        No novoNo = new No(id, gridX, gridY);

        trelica.adicionarNo(novoNo);

        repaint();
    }

    private void desenharNos(Graphics g) {

        g.setColor(Color.BLUE);

        for (No no : trelica.getNos()) {

            int r = 8;

            int x = (int) no.getX() - r/2;
            int y = (int) no.getY() - r/2;

            g.fillOval(x, y, r, r);

        }

    }
}