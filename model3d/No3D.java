package model3d;

public class No3D {

    private int id;
    private double x;
    private double y;
    private double z;
    private double fx;
    private double fy;
    private double fz;

    public No3D(int id, double x, double y, double z, double fx, double fy, double fz) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.z = z;
        this.fx = fx;
        this.fy = fy;
        this.fz = fz;
    }

    public int getId() { return id; }
    public double getX() { return x; }
    public double getY() { return y; }
    public double getZ() { return z; }
    public double getFx() { return fx; }
    public double getFy() { return fy; }
    public double getFz() { return fz; }
}
