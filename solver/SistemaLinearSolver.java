package solver;

public class SistemaLinearSolver {

    public static double[] resolver(double[][] A, double[] b) {

        int n = b.length;

        double[][] m = new double[n][n + 1];

        // montar matriz aumentada
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) 
                m[i][j] = A[i][j];
            m[i][n] = b[i];
        }

        // eliminação de Gauss-Jordan com pivotamento parcial
        for (int i = 0; i < n; i++) {

            //pivô máximo coluna 1
            int maxRow = i;
            for(int k = i; k < n; k++){
                if(Math.abs(m[k][i]) > Math.abs(m[maxRow][i])){
                    maxRow = k;
                }
            }

            //trocar linha i com linha MaxRow
            double [] tmp = m[i];
            m[i] = m[maxRow];
            m[maxRow] = tmp;

            double pivo = m[i][i];
            if(Math.abs(pivo) < 1e-12){
                throw new ArithmeticException("Matriz singular — pivô nulo na linha " + i);
            }

            //normalizar linha 1
            for(int j = i; j <= n; j++ ){
                m[i][j] /= pivo;
            }

            //Eliminar coluna 1em todas as outras linhas
            for(int k = 0; k < n; k++){
                if(k != i ){
                    double fator = m[k][i];
                    for(int j = i; j <= n; j++){
                        m[k][j] -= fator * m[i][j];
                    }
                }
            }


        }

        // extrair solução
        double[] x = new double[n];

        for (int i = 0; i < n; i++) {
            x[i] = m[i][n];
        }

        return x;
    }
}