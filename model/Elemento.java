package model;

public class Elemento {

    private static int contador = 0;

    private int id;

    private No noInicial;
    private No noFinal;

    private double forcaInterna;

    public Elemento(No noInicial, No noFinal) {
        this.id = contador++;
        this.noInicial = noInicial;
        this.noFinal = noFinal;
    }

    public int getId() {
        return id;
    }

    public No getNoInicial() {
        return noInicial;
    }

    public No getNoFinal() {
        return noFinal;
    }

    public double getComprimento() {

        double dx = noFinal.getX() - noInicial.getX();
        double dy = noFinal.getY() - noInicial.getY();

        return Math.sqrt(dx * dx + dy * dy);
    }

    public double getCos() {
        return (noFinal.getX() - noInicial.getX()) / getComprimento();
    }

    public double getSen() {
        return -(noFinal.getY() - noInicial.getY()) / getComprimento(); //Invertido porque Y cresce para cima
    }

    public double getForcaInterna() {
        return forcaInterna;
    }

    public void setForcaInterna(double forcaInterna) {
        this.forcaInterna = forcaInterna;
    }

    @Override
    public String toString() {
        return "Elemento{" +
                "id=" + id +
                ", noInicial=" + noInicial.getId() +
                ", noFinal=" + noFinal.getId() +
                ", forcaInterna=" + forcaInterna +
                '}';
    }
}