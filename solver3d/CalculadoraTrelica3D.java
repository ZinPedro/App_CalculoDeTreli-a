package solver3d;

import model3d.*;
import solver.SistemaLinearSolver;
import java.util.List;

public class CalculadoraTrelica3D {

    private Trelica3D trelica;
    private String relatorio = "";

    public CalculadoraTrelica3D(Trelica3D trelica) {
        this.trelica = trelica;
    }

    public String validar() {
        List<No3D> nos = trelica.getNos();
        List<Elemento3D> barras = trelica.getElementos();
        List<Vinculo3D> vinculos = trelica.getVinculos();

        if (nos.isEmpty()) return "A treliça 3D não possui nós.";
        if (barras.isEmpty()) return "A treliça 3D não possui barras.";
        if (vinculos.isEmpty()) return "A treliça 3D não possui vínculos.";

        int n = nos.size();
        int m = barras.size();
        int r = contarReacoes(vinculos);
        if (m + r != 3 * n) {
            return String.format(
                "Estrutura 3D não isostática.\n" +
                "Barras m=%d Reações r=%d Nós n=%d\n" +
                "Precisa: m+r = 3n -> %d != %d", m, r, n, m + r, 3 * n);
        }
        return null;
    }

    public int contarReacoes(List<Vinculo3D> vinculos) {
        int total = 0;
        for (Vinculo3D v : vinculos) total += v.contarReacoes();
        return total;
    }

    public void resolver() throws Exception {
        String erro = validar();
        if (erro != null) throw new Exception(erro);

        List<No3D> nos = trelica.getNos();
        List<Elemento3D> barras = trelica.getElementos();
        List<Vinculo3D> vinculos = trelica.getVinculos();

        int n = nos.size();
        int m = barras.size();
        int total = m + contarReacoes(vinculos);
        double[][] a = new double[3 * n][total];
        double[] b = new double[3 * n];

        // Cada barra contribui com os cossenos diretores nas equações X, Y e Z dos nós.
        for (int j = 0; j < m; j++) {
            Elemento3D barra = barras.get(j);
            if (barra.getComprimento() < 1e-9) {
                throw new Exception("Barra " + barra.getId() + " possui comprimento zero.");
            }
            double cx = barra.getCx();
            double cy = barra.getCy();
            double cz = barra.getCz();
            int ni = nos.indexOf(barra.getNoInicial());
            int nf = nos.indexOf(barra.getNoFinal());

            a[3 * ni][j] += cx;
            a[3 * ni + 1][j] += cy;
            a[3 * ni + 2][j] += cz;
            a[3 * nf][j] -= cx;
            a[3 * nf + 1][j] -= cy;
            a[3 * nf + 2][j] -= cz;
        }

        // Cada restrição ativa cria uma incógnita de reação independente.
        int col = m;
        for (Vinculo3D v : vinculos) {
            int i = nos.indexOf(v.getNo());
            if (v.isRestringeX()) a[3 * i][col++] = 1;
            if (v.isRestringeY()) a[3 * i + 1][col++] = 1;
            if (v.isRestringeZ()) a[3 * i + 2][col++] = 1;
        }

        for (int i = 0; i < n; i++) {
            No3D no = nos.get(i);
            b[3 * i] = -no.getFx();
            b[3 * i + 1] = -no.getFy();
            b[3 * i + 2] = -no.getFz();
        }

        double[] x;
        try {
            x = SistemaLinearSolver.resolver(a, b);
        } catch (ArithmeticException e) {
            throw new Exception("Sistema singular: a treliça 3D está instável ou mal vinculada.");
        }

        for (int i = 0; i < m; i++) {
            barras.get(i).setForcaInterna(x[i]);
        }

        int idx = m;
        for (Vinculo3D v : vinculos) {
            if (v.isRestringeX()) v.setReacaoX(x[idx++]); else v.setReacaoX(0);
            if (v.isRestringeY()) v.setReacaoY(x[idx++]); else v.setReacaoY(0);
            if (v.isRestringeZ()) v.setReacaoZ(x[idx++]); else v.setReacaoZ(0);
        }

        relatorio = gerarRelatorio(nos, barras, vinculos);
    }

    private String gerarRelatorio(List<No3D> nos, List<Elemento3D> barras, List<Vinculo3D> vinculos) {
        StringBuilder sb = new StringBuilder();
        sb.append("RESULTADOS DA TRELIÇA 3D\n");
        sb.append("========================\n\n");

        sb.append("FORÇAS NAS BARRAS\n");
        for (Elemento3D e : barras) {
            double f = e.getForcaInterna();
            String tipo = Math.abs(f) < 1e-6 ? "NULA" : f > 0 ? "TRAÇÃO" : "COMPRESSÃO";
            sb.append(String.format("Barra %d (nó %d -> %d): |F| = %.3f N [%s]%n",
                e.getId(), e.getNoInicial().getId(), e.getNoFinal().getId(), Math.abs(f), tipo));
        }

        sb.append("\nFORÇAS EXTERNAS\n");
        for (No3D no : nos) {
            if (Math.abs(no.getFx()) > 1e-9 || Math.abs(no.getFy()) > 1e-9 || Math.abs(no.getFz()) > 1e-9) {
                sb.append(String.format("Nó %d: Fx=%.3f N  Fy=%.3f N  Fz=%.3f N%n",
                    no.getId(), no.getFx(), no.getFy(), no.getFz()));
            }
        }

        sb.append("\nREAÇÕES DE APOIO\n");
        for (Vinculo3D v : vinculos) {
            sb.append(String.format("Nó %d: Rx=%.3f N  Ry=%.3f N  Rz=%.3f N%n",
                v.getNo().getId(), v.getReacaoX(), v.getReacaoY(), v.getReacaoZ()));
        }
        return sb.toString();
    }

    public String getRelatorio() {
        return relatorio;
    }
}
