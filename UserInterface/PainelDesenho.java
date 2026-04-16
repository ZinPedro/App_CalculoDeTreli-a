package UserInterface;

import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

import java.awt.Graphics;
import java.awt.Color;

import model.Trelica;
import model.Elemento;
import model.No;

public class PainelDesenho extends JPanel {

    private int tamanhoGrid = 40;

    private Trelica trelica = new Trelica();

    private No noSelecionado = null;

    private int mouseX = -1;
    private int mouseY = -1;

    public PainelDesenho() {

        addMouseListener(new java.awt.event.MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int x = e.getX();
                int y = e.getY();

                No noClicado = encontrarNoProximo(x, y);

                if (noClicado != null) {

                    if (noSelecionado == null) {

                        noSelecionado = noClicado;

                    } else {

                        criarElemento(noSelecionado, noClicado);

                        noSelecionado = null;

                    }

                } else {

                    criarNo(x, y);

                }

                repaint();

            }

        });

        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {

            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {

                mouseX = e.getX();
                mouseY = e.getY();

                repaint();

            }

        });

    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        desenharGrid(g);

        desenharNos(g);

        desenharElementos(g);

        desenharMira(g);

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

        int gridX = Math.round((float) xMouse / tamanhoGrid) * tamanhoGrid;
        int gridY = Math.round((float) yMouse / tamanhoGrid) * tamanhoGrid;

        int id = trelica.getNos().size() + 1;

        No novoNo = new No(id, gridX, gridY);

        trelica.adicionarNo(novoNo);

        if (noSelecionado != null) {

            criarElemento(noSelecionado, novoNo);
            noSelecionado = null;

        } else {

            noSelecionado = novoNo;

        }

        repaint();
    }

    private void desenharNos(Graphics g) {

        g.setColor(Color.BLUE);

        for (No no : trelica.getNos()) {

            int r = 8;

            int x = (int) no.getX() - r / 2;
            int y = (int) no.getY() - r / 2;

            g.fillOval(x, y, r, r);

        }

    }

    private No encontrarNoProximo(int x, int y) {

        int raioSelecao = 10;

        for (No no : trelica.getNos()) {

            double dx = no.getX() - x;
            double dy = no.getY() - y;

            double distancia = Math.sqrt(dx * dx + dy * dy);

            if (distancia < raioSelecao) {
                return no;
            }
        }

        return null;
    }

    private void criarElemento(No n1, No n2) {

        if (n1 == n2) {
            return;
        }

        int id = trelica.getElementos().size() + 1;

        Elemento elemento = new Elemento(id, n1, n2);

        trelica.adicionarElemento(elemento);

    }

    private void desenharElementos(Graphics g) {

        g.setColor(Color.BLACK);

        for (Elemento e : trelica.getElementos()) {

            int x1 = (int) e.getNoInicial().getX();
            int y1 = (int) e.getNoInicial().getY();

            int x2 = (int) e.getNoFinal().getX();
            int y2 = (int) e.getNoFinal().getY();

            g.drawLine(x1, y1, x2, y2);

        }

    }

    private void desenharMira(Graphics g) {

        if (mouseX < 0 || mouseY < 0)
            return;

        int gridX = Math.round((float) mouseX / tamanhoGrid) * tamanhoGrid;
        int gridY = Math.round((float) mouseY / tamanhoGrid) * tamanhoGrid;

        g.setColor(Color.RED);

        int r = 6;

        g.drawOval(gridX - r / 2, gridY - r / 2, r, r);

    }
}