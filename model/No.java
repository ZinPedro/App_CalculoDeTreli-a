package model;

import java.util.ArrayList;
import java.util.List;

public class No {

    private static int contador = 0;

    private int id;
    private double x;
    private double y;

    //Forças
    private double fx = 0;
    private double fy = 0;

    public No(double x, double y) {
        this.id = contador++;
        this.x = x;
        this.y = y;
    }

    //Getters
    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getFx(){
        return fx;
    }

    public double getFy(){
        return fy;
    }

    //Setters
    public void setFx(double fx){
        this.fx = fx;
    }

    public void setFy(double fy){
        this.fy = fy;
    }    

    public List<Forca> getForcas(){
        List<Forca> lista = new ArrayList<>();
        if(fx != 0 || fy != 0){
            lista.add(new Forca(fx,fy));
        }
        return lista;
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