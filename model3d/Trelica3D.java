package model3d;

import java.util.ArrayList;
import java.util.List;

public class Trelica3D {

    private List<No3D> nos = new ArrayList<>();
    private List<Elemento3D> elementos = new ArrayList<>();
    private List<Vinculo3D> vinculos = new ArrayList<>();

    public void adicionarNo(No3D no) { nos.add(no); }
    public void adicionarElemento(Elemento3D elemento) { elementos.add(elemento); }
    public void adicionarVinculo(Vinculo3D vinculo) { vinculos.add(vinculo); }

    public List<No3D> getNos() { return nos; }
    public List<Elemento3D> getElementos() { return elementos; }
    public List<Vinculo3D> getVinculos() { return vinculos; }
}
