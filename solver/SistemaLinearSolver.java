package solver;

public class SistemaLinearSolver {

    public static double[] resolver(double[][] A, double[] b) {

        int n = b.length;

        double[][] matriz = new double[n][n + 1];

        // montar matriz aumentada
        for (int i = 0; i < n; i++) {

            for (int j = 0; j < n; j++) {
                matriz[i][j] = A[i][j];
            }

            matriz[i][n] = b[i];
        }

        // eliminação de Gauss
        for (int i = 0; i < n; i++) {

            double pivô = matriz[i][i];

            for (int j = i; j < n + 1; j++) {
                matriz[i][j] /= pivô;
            }

            for (int k = 0; k < n; k++) {

                if (k != i) {

                    double fator = matriz[k][i];

                    for (int j = i; j < n + 1; j++) {
                        matriz[k][j] -= fator * matriz[i][j];
                    }

                }

            }

        }

        // extrair solução
        double[] x = new double[n];

        for (int i = 0; i < n; i++) {
            x[i] = matriz[i][n];
        }

        return x;
    }
}