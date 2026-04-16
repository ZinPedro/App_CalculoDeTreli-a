package model;

import java.util.ArrayList;
import java.util.List;

public class Trelica {

    private List<No> nos;
    private List<Elemento> elementos;
    private List<Vinculo> vinculos = new ArrayList<>();

    public Trelica() {
        this.nos = new ArrayList<>();
        this.elementos = new ArrayList<>();
    }

    public void adicionarNo(No no) {
        nos.add(no);
    }

    public void removerNo(No no) {

        nos.remove(no);

        elementos.removeIf(e -> e.getNoInicial() == no || e.getNoFinal() == no);

    }

    public void adicionarElemento(Elemento elemento) {
        elementos.add(elemento);
    }

    public void removerElemento(Elemento e) {
        elementos.remove(e);
    }

    public List<No> getNos() {
        return nos;
    }

    public List<Elemento> getElementos() {
        return elementos;
    }

    public No buscarNoPorId(int id) {
        for (No no : nos) {
            if (no.getId() == id) {
                return no;
            }
        }
        return null;
    }

    public Elemento buscarElementoPorId(int id) {
        for (Elemento elemento : elementos) {
            if (elemento.getId() == id) {
                return elemento;
            }
        }
        return null;
    }

    public void adicionarVinculo(Vinculo vinculo) {
        vinculos.add(vinculo);
    }

    public void removerVinculo(Vinculo vinculo) {
        vinculos.remove(vinculo);
    }

    public List<Vinculo> getVinculos() {
        return vinculos;
    }

    @Override
    public String toString() {
        return "Trelica{" +
                "nos=" + nos.size() +
                ", elementos=" + elementos.size() +
                '}';
    }

}