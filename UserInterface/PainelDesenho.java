package UserInterface;

import model.*;
import enums.Ferramenta;
import enums.TipoVinculo;
import solver.CalculadoraTrelica;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class PainelDesenho extends JPanel {

    // Estrutura de dados principal
    private List<No>       todosNos       = new ArrayList<>();
    private List<Elemento> todosElementos = new ArrayList<>();
    private List<Vinculo>  todosVinculos  = new ArrayList<>();

    // Estado da UI
    private final int GRID = 50;
    private int mouseX = -1, mouseY = -1;
    private No       noSelecionado   = null;   // primeiro clique na criação de barra
    private No       noHover         = null;
    private Elemento elementoHover   = null;
    private No       noAtivo         = null;   // selecionado no painel lateral
    private Elemento barraAtiva      = null;

    private Ferramenta  ferramenta      = Ferramenta.DESENHAR_BARRA;
    private TipoVinculo tipoVinculo     = TipoVinculo.PINO;
    private boolean     mostrarCores    = false;  // cores tração/compressão sem valores

    // Referência ao painel lateral
    private PainelLateral painelLateral;

    // -----------------------------------------------------------------------
    public PainelDesenho() {
        setBackground(Color.WHITE);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { aoClicar(e); }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseMoved(MouseEvent e) {
                mouseX = e.getX(); mouseY = e.getY();
                noHover      = encontrarNo(mouseX, mouseY);
                elementoHover = encontrarBarra(mouseX, mouseY);
                repaint();
            }
        });
    }

    public void setPainelLateral(PainelLateral pl) { this.painelLateral = pl; }

    // -----------------------------------------------------------------------
    // Setters de estado
    // -----------------------------------------------------------------------
    public void setFerramenta(Ferramenta f) {
        this.ferramenta = f;
        noSelecionado = null;
        repaint();
    }
    public void setTipoVinculo(TipoVinculo t) { this.tipoVinculo = t; }
    public void setMostrarCores(boolean b) { mostrarCores = b; repaint(); }

    // -----------------------------------------------------------------------
    // Acesso aos dados para cálculo por componente
    // -----------------------------------------------------------------------
    public Trelica trelicaDoElementoAtivo() {
        // Determina o nó de referência para a componente
        No ref = null;
        if (noAtivo != null)   ref = noAtivo;
        else if (barraAtiva != null) ref = barraAtiva.getNoInicial();

        if (ref == null && !todosNos.isEmpty()) ref = todosNos.get(0);
        if (ref == null) return null;

        return construirTrelicaCompleta().subTrelicaConectada(ref);
    }

    private Trelica construirTrelicaCompleta() {
        Trelica t = new Trelica();
        for (No n : todosNos)      t.adicionarNo(n);
        for (Elemento e : todosElementos) t.adicionarElemento(e);
        for (Vinculo v : todosVinculos)  t.adicionarVinculo(v);
        return t;
    }

    public Vinculo vinculoDoNo(No no) {
        for (Vinculo v : todosVinculos) if (v.getNo() == no) return v;
        return null;
    }

    // -----------------------------------------------------------------------
    // Clique
    // -----------------------------------------------------------------------
    private void aoClicar(MouseEvent e) {
        int x = e.getX(), y = e.getY();

        switch (ferramenta) {

            case DESENHAR_BARRA:
                desenharBarra(x, y);
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

            case APAGAR_VINCULO:
                apagarVinculo(x, y);
                break;

            case SELECIONAR:
                selecionar(x, y);
                break;
        }
        repaint();
    }

    // -----------------------------------------------------------------------
    // Seleção (painel lateral)
    // -----------------------------------------------------------------------
    private void selecionar(int x, int y) {
        No  no  = encontrarNo(x, y);
        if (no != null) {
            noAtivo    = no;
            barraAtiva = null;
            if (painelLateral != null) painelLateral.selecionarNo(no);
            return;
        }
        Elemento barra = encontrarBarra(x, y);
        if (barra != null) {
            barraAtiva = barra;
            noAtivo    = null;
            if (painelLateral != null) painelLateral.selecionarBarra(barra);
            return;
        }
        // Clique no vazio: limpa seleção
        noAtivo = null; barraAtiva = null;
        if (painelLateral != null) painelLateral.limparSelecao();
    }

    // -----------------------------------------------------------------------
    // Criação de barra (e nós implícitos)
    // -----------------------------------------------------------------------
    private void desenharBarra(int x, int y) {
        int gx = snap(x), gy = snap(y);

        // Primeiro verifica se existe nó exatamente na posição de grade
        No no = encontrarNoNaGrade(gx, gy);
        if (no == null) {
            no = new No(gx, gy);
            todosNos.add(no);
            verificarDivisaoBarra(no);
        }

        if (noSelecionado == null) {
            noSelecionado = no;
            return;
        }

        if (noSelecionado == no) { noSelecionado = null; return; }

        // Verificar barra duplicada
        if (barraExiste(noSelecionado, no)) { noSelecionado = null; return; }

        // Verificar cruzamento
        Elemento novaBarra = new Elemento(noSelecionado, no);
        List<Elemento> copia = new ArrayList<>(todosElementos);
        boolean cancelar = false;
        for (Elemento e : copia) {
            if (compartilhaNo(e, noSelecionado, no)) continue;
            Point p = intersecao(novaBarra, e);
            if (p == null) continue;
            // Cruzamento: criar nó e dividir
            No noInt = encontrarNoNaGrade(p.x, p.y);
            if (noInt == null) {
                noInt = new No(p.x, p.y);
                todosNos.add(noInt);
            }
            dividirBarra(e, noInt);
            todosElementos.add(new Elemento(noSelecionado, noInt));
            todosElementos.add(new Elemento(noInt, no));
            noSelecionado = null;
            return;
        }

        todosElementos.add(novaBarra);
        noSelecionado = null;
    }

    // -----------------------------------------------------------------------
    // Apagar
    // -----------------------------------------------------------------------
    private void apagarNo(int x, int y) {
        No no = encontrarNo(x, y);
        if (no == null) return;
        todosElementos.removeIf(e -> e.getNoInicial()==no || e.getNoFinal()==no);
        todosVinculos.removeIf(v -> v.getNo()==no);
        todosNos.remove(no);
        if (noAtivo == no) { noAtivo = null; if (painelLateral!=null) painelLateral.limparSelecao(); }
    }

    private void apagarBarra(int x, int y) {
        Elemento alvo = encontrarBarra(x, y);
        if (alvo != null) {
            todosElementos.remove(alvo);
            if (barraAtiva == alvo) { barraAtiva = null; if (painelLateral!=null) painelLateral.limparSelecao(); }
        }
    }

    // -----------------------------------------------------------------------
    // Vínculos
    // -----------------------------------------------------------------------
    private void criarVinculo(int x, int y) {
        int gx = snap(x), gy = snap(y);
        No no = encontrarNoNaGrade(gx, gy);
        if (no == null) {
            no = new No(gx, gy);
            todosNos.add(no);
        }
        for (Vinculo v : todosVinculos) if (v.getNo()==no) return; // já tem
        todosVinculos.add(new Vinculo(no, tipoVinculo));
    }

    private void apagarVinculo(int x, int y) {
        No no = encontrarNo(x, y);
        if (no == null) return;
        todosVinculos.removeIf(v -> v.getNo() == no);
    }

    // -----------------------------------------------------------------------
    // Calcular (chamado por BarraFerramentas)
    // -----------------------------------------------------------------------
    public void calcular() {
        Trelica sub = trelicaDoElementoAtivo();
        if (sub == null || sub.getNos().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Selecione um nó ou barra da treliça a calcular\n" +
                "(use a ferramenta Selecionar e clique no elemento).",
                "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CalculadoraTrelica calc = new CalculadoraTrelica(sub);
        try {
            calc.resolver();

            // Atualizar painel lateral com resultado
            if (painelLateral != null) {
                if (noAtivo != null)   painelLateral.atualizarPainelNo();
                if (barraAtiva != null) painelLateral.atualizarPainelBarra();
            }

            repaint();

            // Diálogo de resultado
            JTextArea area = new JTextArea(calc.getRelatorio());
            area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
            area.setEditable(false);
            area.setBackground(new Color(24, 26, 32));
            area.setForeground(new Color(220, 230, 255));
            area.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

            JScrollPane scroll = new JScrollPane(area);
            scroll.setPreferredSize(new Dimension(520, 380));

            JOptionPane.showMessageDialog(this, scroll,
                "Resultados da Treliça", JOptionPane.PLAIN_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                ex.getMessage(), "Erro no Cálculo", JOptionPane.ERROR_MESSAGE);
        }
    }

    // -----------------------------------------------------------------------
    // Pintura
    // -----------------------------------------------------------------------
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        desenharGrid(g2);
        desenharBarras(g2);
        desenharVinculos(g2);
        desenharForcas(g2);
        desenharNos(g2);
        desenharPreviewBarra(g2);
        desenharMira(g2);
    }

    private void desenharGrid(Graphics2D g) {
        g.setColor(new Color(235, 235, 245));
        for (int x = 0; x < getWidth();  x += GRID) g.drawLine(x, 0, x, getHeight());
        for (int y = 0; y < getHeight(); y += GRID) g.drawLine(0, y, getWidth(), y);
        // Eixo X e Y em destaque
        g.setColor(new Color(200, 200, 215));
        g.setStroke(new BasicStroke(1));
    }

    private void desenharBarras(Graphics2D g) {
        for (Elemento e : todosElementos) {
            int x1 = (int)e.getNoInicial().getX(), y1 = (int)e.getNoInicial().getY();
            int x2 = (int)e.getNoFinal().getX(),   y2 = (int)e.getNoFinal().getY();

            boolean ehAtiva = (e == barraAtiva);
            double f = e.getForcaInterna();
            boolean calculada = Math.abs(f) > 1e-9 || mostrarCores;

            Color cor;
            float espessura;

            if (mostrarCores && Math.abs(f) > 1e-9) {
                cor       = f > 0 ? new Color(0, 110, 200) : new Color(200, 30, 30);
                espessura = 4f;
            } else if (mostrarCores && Math.abs(f) <= 1e-9 && e.getForcaInterna() != 0) {
                cor = Color.GRAY; espessura = 3f;
            } else if (ehAtiva) {
                cor = new Color(255, 160, 0); espessura = 3.5f;
            } else if (e == elementoHover && ferramenta == Ferramenta.APAGAR_BARRA) {
                cor = new Color(220, 50, 50); espessura = 2.5f;
            } else if (e == elementoHover) {
                cor = new Color(80, 120, 220); espessura = 2.5f;
            } else {
                cor = new Color(50, 50, 55); espessura = 2f;
            }

            g.setColor(cor);
            g.setStroke(new BasicStroke(espessura, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g.drawLine(x1, y1, x2, y2);
            g.setStroke(new BasicStroke(1));

            // Rótulo de resultado se hover e calculado
            if (ehAtiva && Math.abs(f) > 1e-9) {
                int mx = (x1+x2)/2, my = (y1+y2)/2;
                String txt = String.format("%.1f N %s", Math.abs(f), f>0?"(T)":"(C)");
                g.setColor(Color.DARK_GRAY);
                g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 11));
                g.drawString(txt, mx+5, my-6);
            }
        }
    }

    private void desenharNos(Graphics2D g) {
        for (No no : todosNos) {
            int x = (int)no.getX(), y = (int)no.getY();
            int r;
            Color cor;

            if      (no == noSelecionado) { cor = new Color(255, 140, 0); r = 10; }
            else if (no == noAtivo)       { cor = new Color(255, 180, 0); r = 11; }
            else if (no == noHover)       { cor = new Color(80, 200, 80); r = 10; }
            else                          { cor = new Color(30, 80, 200); r = 8;  }

            // Sombra
            g.setColor(new Color(0,0,0,40));
            g.fillOval(x-r/2+1, y-r/2+1, r, r);

            g.setColor(cor);
            g.fillOval(x-r/2, y-r/2, r, r);
            g.setColor(cor.darker());
            g.setStroke(new BasicStroke(1.5f));
            g.drawOval(x-r/2, y-r/2, r, r);
            g.setStroke(new BasicStroke(1));

            // ID
            g.setColor(new Color(60, 60, 80));
            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
            g.drawString(String.valueOf(no.getId()), x+7, y-5);
        }
    }

    private void desenharVinculos(Graphics2D g) {
        g.setStroke(new BasicStroke(2));
        for (Vinculo v : todosVinculos) {
            int x = (int)v.getNo().getX(), y = (int)v.getNo().getY();
            g.setColor(new Color(50, 50, 60));

            if (v.getTipo() == TipoVinculo.PINO) {
                int t = 14;
                int[] xs = {x-t, x+t, x}, ys = {y+t, y+t, y};
                g.fillPolygon(xs, ys, 3);
                g.setColor(Color.WHITE);
                g.drawPolygon(xs, ys, 3);
                g.setColor(new Color(50,50,60));
                g.setStroke(new BasicStroke(2));
                g.drawLine(x-t-4, y+t, x+t+4, y+t);
                // Pino central
                g.setColor(new Color(200, 200, 200));
                g.fillOval(x-3, y-3, 6, 6);
            } else {
                int t = 11;
                int[] xs = {x-t, x+t, x}, ys = {y+t, y+t, y};
                g.fillPolygon(xs, ys, 3);
                g.setColor(Color.WHITE);
                g.drawPolygon(xs, ys, 3);
                g.setColor(new Color(50,50,60));
                // Rodinhas
                g.setStroke(new BasicStroke(1.5f));
                g.drawOval(x-t+2, y+t, 8, 8);
                g.drawOval(x+t-9, y+t, 8, 8);
                g.drawLine(x-t-4, y+t+9, x+t+4, y+t+9);
            }
            g.setStroke(new BasicStroke(1));

            // Reações calculadas
            double rx = v.getReacaoX(), ry = v.getReacaoY();
            if (Math.abs(rx) > 1e-9 || Math.abs(ry) > 1e-9) {
                g.setColor(new Color(0, 140, 70));
                g.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
                g.drawString(String.format("Rx=%.1f", rx), x-30, y+30);
                g.drawString(String.format("Ry=%.1f", ry), x-30, y+42);
            }
        }
    }

    private void desenharForcas(Graphics2D g) {
        g.setColor(new Color(180, 0, 180));
        g.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        for (No no : todosNos) {
            double fx = no.getFx(), fy = no.getFy();
            if (Math.abs(fx) < 1e-9 && Math.abs(fy) < 1e-9) continue;

            int x = (int)no.getX(), y = (int)no.getY();
            double mag = Math.sqrt(fx*fx + fy*fy);
            double escala = Math.min(60.0/mag, 0.8);
            int dx = (int)(fx*escala), dy = (int)(-fy*escala); // Fy positivo = cima (Y invertido no canvas)

            g.drawLine(x, y, x+dx, y+dy);
            desenharSeta(g, x+dx, y+dy, dx, dy);

            g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 10));
            g.drawString(String.format("%.0fN", mag), x+dx+4, y+dy-2);
        }
        g.setStroke(new BasicStroke(1));
    }

    private void desenharSeta(Graphics2D g, int ax, int ay, int dx, int dy) {
        double len = Math.sqrt(dx*dx+dy*dy); if (len<1) return;
        double ux=dx/len, uy=dy/len; int s=9;
        g.drawLine(ax, ay, (int)(ax-s*ux+s/2.0*uy), (int)(ay-s*uy-s/2.0*ux));
        g.drawLine(ax, ay, (int)(ax-s*ux-s/2.0*uy), (int)(ay-s*uy+s/2.0*ux));
    }

    private void desenharPreviewBarra(Graphics2D g) {
        if (noSelecionado == null || mouseX < 0) return;
        g.setColor(new Color(100, 150, 255, 160));
        g.setStroke(new BasicStroke(2, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
            1, new float[]{8, 5}, 0));
        g.drawLine((int)noSelecionado.getX(), (int)noSelecionado.getY(), snap(mouseX), snap(mouseY));
        g.setStroke(new BasicStroke(1));
    }

    private void desenharMira(Graphics2D g) {
        if (mouseX < 0) return;
        int gx = snap(mouseX), gy = snap(mouseY);
        g.setColor(new Color(220, 50, 50, 180));
        g.setStroke(new BasicStroke(1));
        g.drawLine(gx-6, gy, gx+6, gy);
        g.drawLine(gx, gy-6, gx, gy+6);
        g.drawOval(gx-4, gy-4, 8, 8);
    }

    // -----------------------------------------------------------------------
    // Geométricos
    // -----------------------------------------------------------------------
    private int snap(int v) { return Math.round((float)v/GRID)*GRID; }

    /** Busca nó EXATAMENTE na posição de grade (sem tolerância de arredondamento). */
    private No encontrarNoNaGrade(int gx, int gy) {
        for (No no : todosNos)
            if ((int)no.getX()==gx && (int)no.getY()==gy) return no;
        return null;
    }

    /** Busca nó por proximidade ao clique (tolerância = metade do grid). */
    private No encontrarNo(int x, int y) {
        int tol = GRID/2 - 2;
        No melhor = null; double melhorDist = tol;
        for (No no : todosNos) {
            double d = dist(x, y, no.getX(), no.getY());
            if (d < melhorDist) { melhorDist = d; melhor = no; }
        }
        return melhor;
    }

    private Elemento encontrarBarra(int x, int y) {
        for (Elemento e : todosElementos) {
            if (distPontoSeg(x, y,
                e.getNoInicial().getX(), e.getNoInicial().getY(),
                e.getNoFinal().getX(),   e.getNoFinal().getY()) < 7) return e;
        }
        return null;
    }

    private double dist(double ax, double ay, double bx, double by) {
        double dx=ax-bx, dy=ay-by; return Math.sqrt(dx*dx+dy*dy);
    }

    private double distPontoSeg(double px, double py,
            double x1, double y1, double x2, double y2) {
        double dx=x2-x1, dy=y2-y1;
        if (dx==0 && dy==0) return dist(px,py,x1,y1);
        double t = Math.max(0, Math.min(1, ((px-x1)*dx+(py-y1)*dy)/(dx*dx+dy*dy)));
        return dist(px, py, x1+t*dx, y1+t*dy);
    }

    private boolean barraExiste(No a, No b) {
        for (Elemento e : todosElementos)
            if ((e.getNoInicial()==a && e.getNoFinal()==b) ||
                (e.getNoInicial()==b && e.getNoFinal()==a)) return true;
        return false;
    }

    private boolean compartilhaNo(Elemento e, No a, No b) {
        return e.getNoInicial()==a || e.getNoFinal()==a ||
               e.getNoInicial()==b || e.getNoFinal()==b;
    }

    private void dividirBarra(Elemento e, No meio) {
        No n1=e.getNoInicial(), n2=e.getNoFinal();
        todosElementos.remove(e);
        todosElementos.add(new Elemento(n1, meio));
        todosElementos.add(new Elemento(meio, n2));
    }

    private void verificarDivisaoBarra(No novoNo) {
        List<Elemento> copia = new ArrayList<>(todosElementos);
        for (Elemento e : copia) {
            if (e.getNoInicial()==novoNo || e.getNoFinal()==novoNo) continue;
            if (distPontoSeg(novoNo.getX(), novoNo.getY(),
                e.getNoInicial().getX(), e.getNoInicial().getY(),
                e.getNoFinal().getX(),   e.getNoFinal().getY()) < 3) {
                dividirBarra(e, novoNo);
            }
        }
    }

    private Point intersecao(Elemento e1, Elemento e2) {
        double x1=e1.getNoInicial().getX(), y1=e1.getNoInicial().getY();
        double x2=e1.getNoFinal().getX(),   y2=e1.getNoFinal().getY();
        double x3=e2.getNoInicial().getX(), y3=e2.getNoInicial().getY();
        double x4=e2.getNoFinal().getX(),   y4=e2.getNoFinal().getY();
        double den=(x1-x2)*(y3-y4)-(y1-y2)*(x3-x4);
        if (Math.abs(den)<0.001) return null;
        double px=((x1*y2-y1*x2)*(x3-x4)-(x1-x2)*(x3*y4-y3*x4))/den;
        double py=((x1*y2-y1*x2)*(y3-y4)-(y1-y2)*(x3*y4-y3*x4))/den;
        if (!noSeg(px,py,x1,y1,x2,y2)) return null;
        if (!noSeg(px,py,x3,y3,x4,y4)) return null;
        int gx=snap((int)Math.round(px)), gy=snap((int)Math.round(py));
        return new Point(gx, gy);
    }

    private boolean noSeg(double px, double py, double x1, double y1, double x2, double y2) {
        return px>=Math.min(x1,x2)-1 && px<=Math.max(x1,x2)+1 &&
               py>=Math.min(y1,y2)-1 && py<=Math.max(y1,y2)+1;
    }
}
