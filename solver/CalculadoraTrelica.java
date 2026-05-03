package solver;

import model.*;
import enums.TipoVinculo;
import java.util.List;

/*
    Calcula treliça plana pelo método dos Nós (matriz)
    Tração (Positiva), Compressão (Negativa)
    Pré-Condição: m + r = 2n (m = numero de barras, r = numero de reações dos vinculos (pino = 2, rolete = 1), n = numero de nós )
*/

public class CalculadoraTrelica {

    private Trelica trelica;
    private String relatorio = "";
    private boolean calculoValido = false;

    public CalculadoraTrelica(Trelica trelica) {
        this.trelica = trelica;
    }

    public String validar() {
        List<No> nos = trelica.getNos();
        List<Elemento> barras = trelica.getElementos();
        List<Vinculo> vinculos = trelica.getVinculos();

        if(nos.isEmpty()){
            return "A treliça não possui nós!";
        }
        if(barras.isEmpty()){
            return "A treliça não possui barras!";
        }
        if(vinculos.isEmpty()){
            return "A treliça não possui vinculos!";
        }

        //m + r = 2n
        int n = nos.size(), m = barras.size(), r = contarReacoes(vinculos);
        if(m + r != 2 * n){
            return String.format(
                "Estrutura não isostática.\n" +
                "Barras m=%d Reações r=%d Nós n=%d\n" +
                "Precisa: m+r = 2n → %d ≠ %d", m, r, n, m+r, 2*n); 
        }
        return null;
    }

    public int contarReacoes(List<Vinculo> vinculos){
        int r = 0;
        for (Vinculo v : vinculos){
            r += (v.getTipo() == TipoVinculo.PINO) ? 2 : 1;
        }
        return r;
    }


    public void resolver() throws Exception {

        calculoValido = false;
        relatorio = "";

        String erro = validar();
        if(erro!= null){
            throw new Exception(erro);
        }

        List<No> nos = trelica.getNos();
        List<Elemento> barras = trelica.getElementos();
        List<Vinculo> vinculos = trelica.getVinculos();

        int n = nos.size(), m = barras.size();
        int total = m + contarReacoes(vinculos);
        
        double[][] A = new double[2 * n][total];
        double[] b = new double[2 * n];

        for(int j = 0; j < m; j++){
            Elemento bar = barras.get(j);
            double cos = bar.getCos(), sen = bar.getSen();
            int ni = nos.indexOf(bar.getNoInicial());
            int nf = nos.indexOf(bar.getNoFinal());
            A[2*ni][j] += cos;
            A[2*ni+1][j] += sen;
            A[2*nf][j] -= cos;
            A[2*nf+1][j] -= sen; 
        }

        int col = m;
        for(Vinculo v : vinculos){
            int i = nos.indexOf(v.getNo());
            if(v.getTipo() == TipoVinculo.PINO){
                A[2*i][col++] = 1;
                A[2*i+1][col++] = 1;
            }else{
                A[2*i+1][col++] = 1;
            }
        }

        for(int i = 0; i < n; i++){
            No no = nos.get(i);
            b[2*i] = -no.getFx();
            b[2*i+1] = no.getFy();
        }

        double[] x;
        try{
            x = SistemaLinearSolver.resolver(A, b);
        }catch (ArithmeticException e){
            throw new Exception("Sistema singular — estrutura instável ou inválida.\n" +
                "Verifique os vínculos e a geometria.");
        }

        for(int i = 0; i < m; i++){
            barras.get(i).setForcaInterna(x[i]);
        }

        int idx = m;
        for(Vinculo v : vinculos){
            if(v.getTipo() == TipoVinculo.PINO){
                v.setReacaoX(x[idx++]);
                v.setReacaoY(x[idx++]);
            }else{
                v.setReacaoX(0);
                v.setReacaoY(x[idx++]);
            }
        }

        relatorio = gerarRelatorio(nos,barras,vinculos);
        calculoValido = true;
        
    }

    private String gerarRelatorio(List<No> nos, List<Elemento> barras, List<Vinculo> vinculos){
        StringBuilder sb = new StringBuilder();
        sb.append("╔══════════════════════════════════════╗\n");
        sb.append("║      RESULTADOS DA TRELIÇA           ║\n");
        sb.append("╚══════════════════════════════════════╝\n\n");

        sb.append("▶ FORÇAS NAS BARRAS\n");
        for (Elemento e : barras){
            double f = e.getForcaInterna();
            String est = Math.abs(f) < 1e-6 ? "NULA" : f > 0 ? "TRAÇÃO" : "COMPRESSÃO";
            sb.append(String.format("  Barra %d (nó %d→%d)   |F| = %.3f N  [%s]%n",
                e.getId(), e.getNoInicial().getId(), e.getNoFinal().getId(), Math.abs(f), est));
        }

        sb.append("\n▶ FORÇAS EXTERNAS\n");
        for (No no : nos) {
            if (no.getFx() != 0 || no.getFy() != 0)
                sb.append(String.format("  Nó %d   Fx=%.3f N   Fy=%.3f N%n",
                    no.getId(), no.getFx(), no.getFy()));
        }

        sb.append("\n▶ REAÇÕES DE APOIO\n");
        for (Vinculo v : vinculos) {
            sb.append(String.format("  %s  Nó %d   Rx=%.3f N   Ry=%.3f N%n",
                v.getTipo(), v.getNo().getId(), v.getReacaoX(), v.getReacaoY()));
        }
        sb.append("\n══════════════════════════════════════\n");
        return sb.toString();
    } 

    public String getRelatorio(){
        return relatorio;
    }

    public boolean isCalculoValido(){
        return calculoValido;
    }




}