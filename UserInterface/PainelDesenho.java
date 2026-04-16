package UserInterface;

import model.No;
import model.Elemento;
import model.Trelica;
import model.Vinculo;
import enums.Ferramenta;
import enums.TipoVinculo;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Point;

import java.awt.Graphics;
import java.awt.Color;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;

public class PainelDesenho extends JPanel {

    private Trelica trelica = new Trelica();

    private int tamanhoGrid = 40;

    private int mouseX = -1;
    private int mouseY = -1;

    private No noSelecionado = null;
    private No noHover = null;
    private Elemento elementoHover = null;

    private Ferramenta ferramentaAtual = Ferramenta.DESENHAR_BARRA;
    private TipoVinculo tipoVinculoAtual = TipoVinculo.PINO;

    public PainelDesenho() {

        addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                int x = e.getX();
                int y = e.getY();

                switch (ferramentaAtual) {

                    case DESENHAR_NO:
                        criarNo(x, y);
                        break;

                    case DESENHAR_BARRA:
                        processarCriacaoBarra(x, y);
                        break;

                    case APAGAR_NO:
                        apagarNo(x, y);
                        break;

                    case APAGAR_BARRA:
                        apagarBarra(x, y);
                        break;

                    case CRIAR_VINCULO:
                        criarVinculo(x, y);
                        break;
                }

                repaint();
            }

        });

        addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseMoved(MouseEvent e) {

                mouseX = e.getX();
                mouseY = e.getY();

                noHover = encontrarNoProximo(mouseX, mouseY);
                elementoHover = encontrarElementoProximo(mouseX, mouseY);

                repaint();
            }

        });

    }

    public void setFerramentaAtual(Ferramenta ferramenta) {
        this.ferramentaAtual = ferramenta;
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        desenharGrid(g);

        desenharElementos(g);

        desenharNos(g);

        desenharMira(g);

        desenharVinculo(g);

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

    private void desenharNos(Graphics g) {

        for (No no : trelica.getNos()) {

            int r;

            if (no == noHover) {
                g.setColor(Color.GREEN);
                r = 16;
            } else {
                g.setColor(Color.BLUE);
                r = 10;
            }

            int x = (int) no.getX() - r / 2;
            int y = (int) no.getY() - r / 2;

            g.fillOval(x, y, r, r);

        }

    }

    private void desenharElementos(Graphics g) {

        for (Elemento e : trelica.getElementos()) {

            int x1 = (int) e.getNoInicial().getX();
            int y1 = (int) e.getNoInicial().getY();

            int x2 = (int) e.getNoFinal().getX();
            int y2 = (int) e.getNoFinal().getY();

            if (ferramentaAtual == Ferramenta.APAGAR_BARRA && e == elementoHover) {

                g.setColor(Color.RED);

            } else if (e == elementoHover) {

                g.setColor(Color.BLUE);

            } else {

                g.setColor(Color.BLACK);

            }

            g.drawLine(x1, y1, x2, y2);

        }
    }

    private void desenharMira(Graphics g) {

        if (mouseX < 0)
            return;

        int gridX = Math.round((float) mouseX / tamanhoGrid) * tamanhoGrid;
        int gridY = Math.round((float) mouseY / tamanhoGrid) * tamanhoGrid;

        g.setColor(Color.RED);

        int r = 6;

        g.drawOval(gridX - r / 2, gridY - r / 2, r, r);

    }

    private void desenharVinculo(Graphics g) {

        for (Vinculo v : trelica.getVinculos()) {

            No no = v.getNo();

            int x = (int) no.getX();
            int y = (int) no.getY();

            g.setColor(Color.BLACK);

            if (v.getTipo() == TipoVinculo.PINO) {

                int tamanho = 12;

                int[] xs = { x - tamanho, x + tamanho, x };
                int[] ys = { y + tamanho, y + tamanho, y };

                g.drawPolygon(xs, ys, 3);
            }

            if (v.getTipo() == TipoVinculo.ROLETE) {

                int tamanho = 9;

                int[] xs = { x - tamanho, x + tamanho, x };
                int[] ys = { y + tamanho, y + tamanho, y };

                g.drawPolygon(xs, ys, 3);

                int r = 6;

                g.drawOval(x - r / 2, y + tamanho + 2, r, r);
            }
        }
    }

    private void criarNo(int x, int y) {

        int gridX = Math.round((float) x / tamanhoGrid) * tamanhoGrid;
        int gridY = Math.round((float) y / tamanhoGrid) * tamanhoGrid;

        No existente = encontrarNoProximo(gridX, gridY);

        if (existente != null)
            return;

        No novoNo = new No(gridX, gridY);

        trelica.adicionarNo(novoNo);

        verificarDivisaoBarra(novoNo);

    }

    private void criarVinculo(int x, int y) {

        No no = encontrarNoProximo(x, y);

        if (no == null) {

            int gridX = Math.round((float) x / tamanhoGrid) * tamanhoGrid;
            int gridY = Math.round((float) y / tamanhoGrid) * tamanhoGrid;

            no = new No(gridX, gridY);

            trelica.adicionarNo(no);

            verificarDivisaoBarra(no);
        }

        boolean jaExiste = false;

        for (Vinculo v : trelica.getVinculos()) {
            if (v.getNo() == no) {
                jaExiste = true;
                break;
            }
        }

        if (!jaExiste) {

            trelica.adicionarVinculo(
                    new Vinculo(no, tipoVinculoAtual));
        }
    }

    public void setTipoVinculoAtual(TipoVinculo tipo) {
        this.tipoVinculoAtual = tipo;
    }

    private void processarCriacaoBarra(int x, int y) {

        No no = encontrarNoProximo(x, y);

        if (no == null) {

            int gridX = Math.round((float) x / tamanhoGrid) * tamanhoGrid;
            int gridY = Math.round((float) y / tamanhoGrid) * tamanhoGrid;

            no = new No(gridX, gridY);

            trelica.adicionarNo(no);

            verificarDivisaoBarra(no);
        }

        if (noSelecionado == null) {

            noSelecionado = no;
            return;

        }

        if (noSelecionado == no)
            return;

        Elemento novaBarra = new Elemento(noSelecionado, no);

        for (Elemento e : trelica.getElementos()) {

            // ignorar barras que compartilham nós
            if (e.getNoInicial() == noSelecionado ||
                    e.getNoFinal() == noSelecionado ||
                    e.getNoInicial() == no ||
                    e.getNoFinal() == no) {
                continue;
            }

            Point p = intersecao(novaBarra, e);

            if (p != null) {

                if (!pontoNaGrade(p.x, p.y)) {

                    JOptionPane.showMessageDialog(
                            this,
                            "Não é permitido cruzamento de barras fora da grade.");

                    noSelecionado = null;
                    return;
                }

                No novoNo = encontrarNoProximo(p.x, p.y);

                if (novoNo == null) {

                    novoNo = new No(p.x, p.y);
                    trelica.adicionarNo(novoNo);
                }

                dividirBarra(e, novoNo);

                trelica.adicionarElemento(new Elemento(noSelecionado, novoNo));
                trelica.adicionarElemento(new Elemento(novoNo, no));

                noSelecionado = null;

                repaint();

                return;
            }
        }

        trelica.adicionarElemento(novaBarra);

        noSelecionado = null;

    }

    private void apagarNo(int x, int y) {

        No no = encontrarNoProximo(x, y);

        if (no != null) {

            trelica.removerNo(no);

        }

    }

    private void apagarBarra(int x, int y) {

        Elemento alvo = null;

        for (Elemento e : trelica.getElementos()) {

            double dist = distanciaPontoSegmento(
                    x, y,
                    e.getNoInicial().getX(), e.getNoInicial().getY(),
                    e.getNoFinal().getX(), e.getNoFinal().getY());

            if (dist < 5) {
                alvo = e;
                break;
            }

        }

        if (alvo != null) {

            trelica.removerElemento(alvo);

        }

    }

    private No encontrarNoProximo(double x, double y) {

        for (No no : trelica.getNos()) {

            double dx = no.getX() - x;
            double dy = no.getY() - y;

            if (Math.sqrt(dx * dx + dy * dy) < 10) {

                return no;

            }

        }

        return null;

    }

    private Elemento encontrarElementoProximo(int x, int y) {

        for (Elemento e : trelica.getElementos()) {

            double dist = distanciaPontoSegmento(
                    x, y,
                    e.getNoInicial().getX(), e.getNoInicial().getY(),
                    e.getNoFinal().getX(), e.getNoFinal().getY());

            if (dist < 6) {
                return e;
            }
        }

        return null;
    }

    private double distanciaPontoSegmento(
            double px, double py,
            double x1, double y1,
            double x2, double y2) {

        double dx = x2 - x1;
        double dy = y2 - y1;

        if (dx == 0 && dy == 0) {

            dx = px - x1;
            dy = py - y1;

            return Math.sqrt(dx * dx + dy * dy);

        }

        double t = ((px - x1) * dx + (py - y1) * dy) /
                (dx * dx + dy * dy);

        t = Math.max(0, Math.min(1, t));

        double projX = x1 + t * dx;
        double projY = y1 + t * dy;

        dx = px - projX;
        dy = py - projY;

        return Math.sqrt(dx * dx + dy * dy);

    }

    private boolean pontoSobreBarra(No no, Elemento e) {

        double dist = distanciaPontoSegmento(
                no.getX(),
                no.getY(),
                e.getNoInicial().getX(),
                e.getNoInicial().getY(),
                e.getNoFinal().getX(),
                e.getNoFinal().getY());

        return dist < 5;
    }

    private void dividirBarra(Elemento e, No novoNo) {

        No n1 = e.getNoInicial();
        No n2 = e.getNoFinal();

        trelica.removerElemento(e);

        trelica.adicionarElemento(new Elemento(n1, novoNo));
        trelica.adicionarElemento(new Elemento(novoNo, n2));

    }

    private void verificarDivisaoBarra(No novoNo) {

        List<Elemento> copia = new ArrayList<>(trelica.getElementos());

        for (Elemento e : copia) {

            if (pontoSobreBarra(novoNo, e)) {

                if (novoNo != e.getNoInicial() && novoNo != e.getNoFinal()) {

                    dividirBarra(e, novoNo);

                }

            }

        }

    }

    private Point intersecao(Elemento e1, Elemento e2) {

        double x1 = e1.getNoInicial().getX();
        double y1 = e1.getNoInicial().getY();

        double x2 = e1.getNoFinal().getX();
        double y2 = e1.getNoFinal().getY();

        double x3 = e2.getNoInicial().getX();
        double y3 = e2.getNoInicial().getY();

        double x4 = e2.getNoFinal().getX();
        double y4 = e2.getNoFinal().getY();

        double den = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);

        if (Math.abs(den) < 0.0001)
            return null;

        double px = ((x1 * y2 - y1 * x2) * (x3 - x4) - (x1 - x2) * (x3 * y4 - y3 * x4)) / den;
        double py = ((x1 * y2 - y1 * x2) * (y3 - y4) - (y1 - y2) * (x3 * y4 - y3 * x4)) / den;

        if (!pontoNoSegmento(px, py, x1, y1, x2, y2))
            return null;
        if (!pontoNoSegmento(px, py, x3, y3, x4, y4))
            return null;

        return new Point((int) Math.round(px), (int) Math.round(py));
    }

    private boolean pontoNaGrade(double x, double y) {

        double gx = Math.round(x / tamanhoGrid) * tamanhoGrid;
        double gy = Math.round(y / tamanhoGrid) * tamanhoGrid;

        return Math.abs(x - gx) < 3 && Math.abs(y - gy) < 3;
    }

    private boolean pontoNoSegmento(double px, double py,
            double x1, double y1,
            double x2, double y2) {

        double minX = Math.min(x1, x2) - 1;
        double maxX = Math.max(x1, x2) + 1;

        double minY = Math.min(y1, y2) - 1;
        double maxY = Math.max(y1, y2) + 1;

        return px >= minX && px <= maxX && py >= minY && py <= maxY;
    }

}