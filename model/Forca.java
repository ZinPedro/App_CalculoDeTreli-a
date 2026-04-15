package model;

public class Forca {

    private double fx;
    private double fy;

    public Forca(double fx, double fy) {
        this.fx = fx;
        this.fy = fy;
    }

    public double getFx() {
        return fx;
    }

    public double getFy() {
        return fy;
    }

    public void setFx(double fx) {
        this.fx = fx;
    }

    public void setFy(double fy) {
        this.fy = fy;
    }

    public double getMagnitude() {
        return Math.sqrt(fx * fx + fy * fy);
    }

    @Override
    public String toString() {
        return "Forca{" +
                "fx=" + fx +
                ", fy=" + fy +
                '}';
    }
}