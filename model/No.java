package model;

import java.util.ArrayList;
import java.util.List;

public class No {

    private int id;
    private double x;
    private double y;

    private List<Forca> forcas;
    private Vinculo vinculo;

    public No(int id, double x, double y) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.forcas = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public List<Forca> getForcas() {
        return forcas;
    }

    public void adicionarForca(Forca forca) {
        forcas.add(forca);
    }

    public Vinculo getVinculo() {
        return vinculo;
    }

    public void setVinculo(Vinculo vinculo) {
        this.vinculo = vinculo;
    }

    @Override
    public String toString() {
        return "No{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}