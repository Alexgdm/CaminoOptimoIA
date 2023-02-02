package entidades;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author alexd
 */
public class nodo {

    String nodo;
    ArrayList<vecino> vecinos = new ArrayList();
    
    public String getNodo() {
        return nodo;
    }

    public void setNodo(String nodo) {
        this.nodo = nodo;
    }

    public List<vecino> getVecinos() {
        return vecinos;
    }

    public void setVecinos(ArrayList<vecino> vecinos) {
        this.vecinos = vecinos;
    }
    
    public void addVecinos(vecino v) {
        this.vecinos.add(v);
    }

}
