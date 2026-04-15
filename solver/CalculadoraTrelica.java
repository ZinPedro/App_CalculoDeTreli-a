package solver;

import model.Trelica;
import model.No;
import model.Elemento;

public class CalculadoraTrelica {

    private Trelica trelica;

    public CalculadoraTrelica(Trelica trelica) {
        this.trelica = trelica;
    }

    public void resolver() {

        System.out.println("Iniciando cálculo da treliça...");

        for (No no : trelica.getNos()) {
            analisarNo(no);
        }

        System.out.println("Cálculo finalizado.");
    }

    private void analisarNo(No no) {

        System.out.println("Analisando nó: " + no.getId());

        double somaFx = 0;
        double somaFy = 0;

        if (no.getForcas() != null) {
            no.getForcas().forEach(forca -> {
                // apenas exibição por enquanto
                System.out.println("Força aplicada: Fx=" + forca.getFx() + " Fy=" + forca.getFy());
            });
        }

        for (Elemento elemento : trelica.getElementos()) {

            if (elemento.getNoInicial() == no || elemento.getNoFinal() == no) {

                System.out.println("Elemento conectado: " + elemento.getId());

            }

        }

    }
}