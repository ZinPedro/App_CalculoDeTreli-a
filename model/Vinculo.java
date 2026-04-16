package model;

import enums.TipoVinculo;


public class Vinculo {

    private No no;
    private TipoVinculo tipo;
    
    private double reacaoX;
    private double reacaoY;

    public Vinculo(No no, TipoVinculo tipo) {
        this.no = no;
        this.tipo = tipo;
    }

    public No getNo() {
        return no;
    }

    public TipoVinculo getTipo() {
        return tipo;
    }

    public double getReacaoX() {
        return reacaoX;
    }

    public double getReacaoY() {
        return reacaoY;
    }

    public void setReacaoX(double reacaoX) {
        this.reacaoX = reacaoX;
    }

    public void setReacaoY(double reacaoY) {
        this.reacaoY = reacaoY;
    }

    @Override
    public String toString() {
        return "Vinculo{" +
                "tipo=" + tipo +
                ", reacaoX=" + reacaoX +
                ", reacaoY=" + reacaoY +
                '}';
    }
}