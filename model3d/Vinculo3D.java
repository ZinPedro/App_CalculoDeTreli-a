package model3d;

public class Vinculo3D {

    private No3D no;
    private boolean restringeX;
    private boolean restringeY;
    private boolean restringeZ;
    private double reacaoX;
    private double reacaoY;
    private double reacaoZ;

    public Vinculo3D(No3D no, boolean restringeX, boolean restringeY, boolean restringeZ) {
        this.no = no;
        this.restringeX = restringeX;
        this.restringeY = restringeY;
        this.restringeZ = restringeZ;
    }

    public No3D getNo() { return no; }
    public boolean isRestringeX() { return restringeX; }
    public boolean isRestringeY() { return restringeY; }
    public boolean isRestringeZ() { return restringeZ; }
    public double getReacaoX() { return reacaoX; }
    public double getReacaoY() { return reacaoY; }
    public double getReacaoZ() { return reacaoZ; }
    public void setReacaoX(double reacaoX) { this.reacaoX = reacaoX; }
    public void setReacaoY(double reacaoY) { this.reacaoY = reacaoY; }
    public void setReacaoZ(double reacaoZ) { this.reacaoZ = reacaoZ; }

    public int contarReacoes() {
        int total = 0;
        if (restringeX) total++;
        if (restringeY) total++;
        if (restringeZ) total++;
        return total;
    }
}
