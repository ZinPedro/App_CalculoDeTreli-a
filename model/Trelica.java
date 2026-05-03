package model;

import java.util.*;

public class Trelica {

    private List<No> nos = new ArrayList<>();
    private List<Elemento> elementos = new ArrayList<>();
    private List<Vinculo> vinculos = new ArrayList<>();

    //Adcionar
    public void adicionarNo(No no) {
        nos.add(no);
    }

    public void adicionarElemento(Elemento elemento) {
        elementos.add(elemento);
    }

    public void adicionarVinculo(Vinculo vinculo) {
        vinculos.add(vinculo);
    }


    //Remover
    public void removerNo(No no) {
        // remover barras conectadas ao nó
        elementos.removeIf(e -> e.getNoInicial() == no || e.getNoFinal() == no);
        // remover vínculo associado ao nó
        vinculos.removeIf(v -> v.getNo() == no);
        // remover o nó
        nos.remove(no);
    }

    public void removerElemento(Elemento e) {
        elementos.remove(e);
    }

    public void removerVinculo(Vinculo vinculo) {
        vinculos.remove(vinculo);
    }

    //Getters
    public List<No> getNos() {
        return nos;
    }
    public List<Elemento> getElementos() {
        return elementos;
    }
    public List<Vinculo> getVinculos() {
        return vinculos;
    }

    //Retorna Nós conectados ao nó selecionadado (conectados pela barra)
    public Set<No> nosConectados(No origem) { 
        Set<No> visitados = new LinkedHashSet<>();
        Queue<No> fila = new LinkedList<>();

        fila.add(origem);
        visitados.add(origem);

        while(!fila.isEmpty()){
            No atual = fila.poll();
            for(Elemento e : elementos){
                No outro = null;
                if(e.getNoInicial() == atual){
                    outro = e.getNoFinal();
                }else if (e.getNoFinal() == atual){
                    outro = e.getNoInicial();
                }
                if(outro != null && !visitados.contains(outro)){
                    visitados.add(outro);
                    fila.add(outro);
                }
            }
        }
        return visitados;
    }

    //construção da treliça a partir do nó selecionado
    public Trelica subTrelicaConectada(No noRaiz){
        Set<No> nosComp = nosConectados(noRaiz);
        Trelica sub = new Trelica();

        for(No n : nos){
            if(nosComp.contains(n)){
                sub.adicionarNo(n);
            }
        }
        for(Elemento e : elementos){
            if(nosComp.contains(e.getNoInicial())){
                sub.adicionarElemento(e);
            }
        }
        for(Vinculo v : vinculos){
            if(nosComp.contains(v.getNo())){
                sub.adicionarVinculo(v);
            }
        }
        return sub;
    }


}