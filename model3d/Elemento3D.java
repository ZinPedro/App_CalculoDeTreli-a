package model3d;

public class Elemento3D {

    private int id;
    private No3D noInicial;
    private No3D noFinal;
    private double forcaInterna;

    public Elemento3D(int id, No3D noInicial, No3D noFinal) {
        this.id = id;
        this.noInicial = noInicial;
        this.noFinal = noFinal;
    }

    public int getId() { return id; }
    public No3D getNoInicial() { return noInicial; }
    public No3D getNoFinal() { return noFinal; }
    public double getForcaInterna() { return forcaInterna; }
    public void setForcaInterna(double forcaInterna) { this.forcaInterna = forcaInterna; }

    public double getComprimento() {
        double dx = noFinal.getX() - noInicial.getX();
        double dy = noFinal.getY() - noInicial.getY();
        double dz = noFinal.getZ() - noInicial.getZ();
        return Math.sqrt(dx * dx + dy * dy + dz * dz);
    }

    public double getCx() { return (noFinal.getX() - noInicial.getX()) / getComprimento(); }
    public double getCy() { return (noFinal.getY() - noInicial.getY()) / getComprimento(); }
    public double getCz() { return (noFinal.getZ() - noInicial.getZ()) / getComprimento(); }
}
