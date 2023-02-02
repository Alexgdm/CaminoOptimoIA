package algoritmos;

import entidades.nodo;
import entidades.vecino;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

/**
 * Clase en donde se van a ejecutar algoritmos de busqueda para encontrar rutas 
 * optimas para recorrer la ciudad de Encarnación.
 * @author alexd
 */


public class busqueda {      
    
    public static ArrayList<nodo> Nodos;
    /**
     * Metodo que se encarga de hacer una busqueda por el menor costo local. 
     * @param n hace referencia al identificador del nodo actual.
     * @param nodosRecorridos lista de objetos del tipo nodo que contiene los nodos que se recorren en cada iteracion
     * @param Grafo el grafo obtenido de la lectura del CSV
     * @return ArrayList una lista de nodos que dependiendo de las iteraciones se devuelve vacía o con el recorrido hecho
     * @author alexd
    **/
    public static ArrayList<nodo> busquedaPorMenorCosto(String n, ArrayList<nodo> nodosRecorridos, ArrayList<nodo> Grafo) {
        //Creacion del grafo
        Nodos = Grafo;
        //Se crea un nuevo objeto del tipo nodo para guardar la informacion del nodo actual
        nodo nodoActual = null;
        //Creacion de objeto del tipo vecino para guardar la informacion del vecino optimo
        vecino vecinoOptimo = null;        
        //Se recorre el grafo hasta encontrar el nodo actual
        for (nodo N : Nodos) {
            //Se obtiene la informacion del nodo actual y se rompe el bucle
            if (N.getNodo().equals(n)) {
                nodoActual = N;
                break;
            }
        }
        
        //Se verifica que se haya seteado un nodo desde el string
        if (nodoActual == null) {
            System.out.println("No se pudo obtener la informacion del nodo: " + n + " como origen");
            return null;
        }
        //Bloque try-catch para evitar el desbordamiento de la pila
        try {
            if (!verificaNodo(nodoActual.getNodo(), nodosRecorridos)) {
                //Se verifica que el nodo actual tenga vecinos
                if (!nodoActual.getVecinos().isEmpty()) {
                    //Se recorre la lista de vecinos para ver a cual trasladarnos
                    for (vecino v : nodoActual.getVecinos()) {
                        //Se verifica si el vecino ya fue recorrido
                        if (!verificaNodo(v.getNodo(), nodosRecorridos)) {
                            //Si entra aca el vecino no fue recorrido
                            if (vecinoOptimo == null) {
                                //Si entra aca es porque aun no se seteo ningun valor para el vecino optimo
                                vecinoOptimo = v;
                            } else {
                                //Si entra aca es porque ya tenemos un nodo dentro del vecino optimo y debemos verificar los costos
                                if (Integer.valueOf(vecinoOptimo.getCosto()) > Integer.valueOf(v.getCosto())) {
                                    vecinoOptimo = v;
                                } else if (Objects.equals(Integer.valueOf(vecinoOptimo.getCosto()), Integer.valueOf(v.getCosto()))) {
                                    //Si los costos son iguales el nodo optimo se elige al azar
                                    if (new Random().nextBoolean()) {
                                        //Se tira una moneda booleana si es verdadero se cambian los nodos
                                        vecinoOptimo = v;
                                    }                            
                                }
                            }
                        }
                    }
                } else {
                    //Si entra aca es porque el nodo no tiene vecinos.
                    System.out.println("El nodo: " + nodoActual.getNodo() + " no tiene vecinos");
                    return null;
                }
                //A estas alturas del código si el vecinoOptimo se mantuvo en null, significa que todos los vecinos fueron recorridos.
                if (vecinoOptimo == null) {           
                    //Se agrega a la lista el ultimo nodo que se pudo recorrer
                    nodosRecorridos.add(nodoActual);
                    if (!verificaGrafo(nodosRecorridos)) {
                        //Se hace un llamado a la funcion que busca nodos por recorrer sin importar si debe pasar por nodos no recorridos
                        nodoActual = busquedaDeNodosPorRecorrer(nodosRecorridos);
                        //Se agrega el nuevo nodo a la lista 
                        nodosRecorridos.add(nodoActual);
                        busquedaPorMenorCosto(nodoActual.getNodo(), nodosRecorridos, Nodos);                 
                    } else {
                        return nodosRecorridos;
                    }
                } else {
                    //Significa que todavia tenemos trayecto para recorrer
                    //Se añade el nodo actual a la lista de nodos recorridos
                    nodosRecorridos.add(nodoActual);
                    //Se recurre a la recursividad de funciones para volver a hacer el mismo proceso con los valores del vecino optimo
                    busquedaPorMenorCosto(vecinoOptimo.getNodo(), nodosRecorridos, Nodos);
                }            
            } else {
                //Es un nodo ya recorrido, debe tener otro tratamiento la informacion
                if (!verificaGrafo(nodosRecorridos)) {
                    //Se hace un llamado a la funcion que busca nodos por recorrer sin importar si debe pasar por nodos no recorridos
                    nodoActual = busquedaDeNodosPorRecorrer(nodosRecorridos);
                    //Se agrega el nuevo nodo a la lista 
                    nodosRecorridos.add(nodoActual);
                    busquedaPorMenorCosto(nodoActual.getNodo(), nodosRecorridos, Nodos);                 
                } else {
                    return nodosRecorridos;
                }            
            }        
        } catch(StackOverflowError ex) {
            //En caso de que la memoria de la pila se llene, devolvemos una lista de nodosRecorridos vacia
            nodosRecorridos.clear();
            return nodosRecorridos;
        }

        /*
         * A este punto el programa no podría estar llegando puesto que en caso de que el vecino optimo sea nulo
         * el mismo ya retorna una lista de nodos que fueron recorridos. Caso contrario la funcion se llama a si
         * misma lo que hace que no pueda pasar de la condición de arriba.
        */
        return nodosRecorridos;
    }


    /**
     * Función que trata de buscar nodos por recorrer moviendose entre nodos recorridos y expandiendo sus vecinos
     * @param nodosRecorridos lista de nodos recorridos
     * @return nodo 
     * @author alexd
     */
    private static nodo busquedaDeNodosPorRecorrer(ArrayList<nodo> nodosRecorridos){
        //Se obtiene el ultimo nodo que se pudo recorrer hasta quedar sin salida
        nodo nodoActual = nodosRecorridos.get(nodosRecorridos.size() - 1);
        nodo nodoAnterior = nodosRecorridos.get(nodosRecorridos.size() - 2);
        //Funcionamiento diferente para nodos con un solo vecino
        if (nodoActual.getVecinos().size() == 1) {
            //Se obtiene el objeto nodo del vecino y se devuelve ese nodo para ser agregado en la funcion principal
            nodoActual = obtenerNodo(nodoActual.getVecinos().get(0).getNodo());
            return nodoActual;
        }
        
        //Creamos un objeto nodo y seteamos en null para realizar verificaciones
        nodo nodoOptimo = null;        
        //Funcionamiento para nodos con mas de un vecino
        for (vecino v : nodoActual.getVecinos()) {
            //Primero verificamos que el nodo vecino este sin recorrer
            if (!verificaNodo(v.getNodo(), nodosRecorridos)) {
                //Si el vecino aun no se recorrió entonces obtenemos el objeto nodo y devolvemos ese nodo.
                return obtenerNodo(v.getNodo());
            }

            //Se verifica si el vecino que estamos recorriendo es nodo que se recorrió anteriormente
            if (v.getNodo().equals(nodoAnterior.getNodo())) {
                //Si la condicion se cumple entonces este vecino se descarta
                continue;
            }
            if (nodoOptimo == null) {
                //Se asigna al primer vecino que este disponible
                nodoOptimo = obtenerNodo(v.getNodo());
            } else {
                //Se controlan las cantidades de vecinos del nodo vecino que ya seteamos con el vecino que se recorre
                if (obtenerNodo(v.getNodo()).getVecinos().size() > nodoOptimo.getVecinos().size()) {
                    //Si la cantidad de vecinos del nodo que recorremos es mayor seteamos este como optimo por las posibilidades de que tenga
                    //mas probabilidades de nodos por recorrer
                    nodoOptimo = obtenerNodo(v.getNodo()); 
                }
            }
        }
        return nodoOptimo;
    }

    /** 
     * Función que sirve para verificar sin un nodo ya fue recorrido
     * @param nodo se recibe el nodo vecino a ser analizado
     * @param nodos_recorridos se recibe la lista de nodos recorridos hasta el momento
     * @return TRUE si es que el nodo ya se recorrió y FALSE si no se recorrió ninguna vez
     * @author alexd
     */
    private static boolean verificaNodo (String nodo, ArrayList<nodo> nodos_recorridos) {        
        for (nodo n : nodos_recorridos) {
            if (n.getNodo().equals(nodo)) {
                return true;
            }
        }
        return false;
    }    
    
    
    /**
     * Función que verifica que todos los nodos del grafo hayan sido recorridos
     * @param nodosRecorridos
     * @return boolean 
     * @author alexd
     */
    private static boolean verificaGrafo(ArrayList<nodo> nodosRecorridos) {
        int bandera = 0;
        for (nodo N : Nodos) {
            bandera = 0;
            for (nodo N2 : nodosRecorridos) {
                if (N.getNodo().equals(N2.getNodo())) {
                    bandera ++ ;
                }
            }
            if (bandera == 0) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Función para obtener el objeto nodo desde su identificador
     * @param nodo que es un identificador del nodo
     * @return objeto del tipo nodo
     * @author alexd
     */
    private static nodo obtenerNodo(String nodo) {
        for (nodo n : Nodos) {
            if (n.getNodo().equals(nodo)) {
                return n;
            }
        }
        return null;
    }    
}