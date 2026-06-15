package model;

import enums.TipoVinculo;
public class Vinculo {

    private No no;
    private TipoVinculo tipo;
    private double anguloGraus;
    
    private double reacaoX;
    private double reacaoY;

    public Vinculo(No no, TipoVinculo tipo) {
        this.no = no;
        this.tipo = tipo;
    }

    public Vinculo(No no, TipoVinculo tipo, double anguloGraus) {
        this(no, tipo);
        this.anguloGraus = anguloGraus;
    }

    //Getters
    public No getNo() {
        return no;
    }

    public TipoVinculo getTipo() {
        return tipo;
    }

    public double getAnguloGraus() {
        return anguloGraus;
    }

    public int contarReacoes() {
        return getDirecoesReacao().length;
    }

    public double[][] getDirecoesReacao() {
        if (tipo == TipoVinculo.PINO) {
            return new double[][] {{1, 0}, {0, 1}};
        }
        if (tipo == TipoVinculo.ROLETE_HORIZONTAL) {
            return new double[][] {{1, 0}};
        }
        if (tipo == TipoVinculo.PINO_ANGULADO) {
            return direcoesPinoAngulado();
        }
        return new double[][] {{0, 1}};
    }

    private double[][] direcoesPinoAngulado() {
        double rad = Math.toRadians(anguloGraus);
        boolean possuiX = Math.abs(Math.cos(rad)) > 1e-9;
        boolean possuiY = Math.abs(Math.sin(rad)) > 1e-9;

        if (possuiX && possuiY) {
            return new double[][] {{1, 0}, {0, 1}};
        }
        if (possuiX) {
            return new double[][] {{1, 0}};
        }
        if (possuiY) {
            return new double[][] {{0, 1}};
        }
        return new double[0][2];
    }

    public double getReacaoX() {
        return reacaoX;
    }

    public double getReacaoY() {
        return reacaoY;
    }

    //Setters
    public void setReacaoX(double reacaoX) {
        this.reacaoX = reacaoX;
    }

    public void setReacaoY(double reacaoY) {
        this.reacaoY = reacaoY;
    }

    public void limparReacoes() {
        reacaoX = 0;
        reacaoY = 0;
    }

    public void adicionarReacao(double intensidade, double[] direcao) {
        reacaoX += intensidade * direcao[0];
        reacaoY += intensidade * direcao[1];
    }

    @Override
    public String toString() {
        return "Vinculo{" +
                "tipo=" + tipo +
                ", anguloGraus=" + anguloGraus +
                ", reacaoX=" + reacaoX +
                ", reacaoY=" + reacaoY +
                '}';
    }
}
