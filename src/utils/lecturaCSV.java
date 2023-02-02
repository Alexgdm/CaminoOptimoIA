package utils;

import com.opencsv.CSVReader;
import entidades.nodo;
import entidades.vecino;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author alexd
 */
public class lecturaCSV {

    public static ArrayList<nodo> crearGrafo(String nombreArchivo) {
        //Creacion de lista de objetos del tipo nodo
        ArrayList<nodo> nodos = new ArrayList();
        //Creacion de nueva instancia para lectura de .csv
        CSVReader reader;
        try {
            //Abrir archivo csv
            reader = new CSVReader(new FileReader("/home/alexd/Documentos/IA_2023/" + nombreArchivo), ',');
            //Variable que va a guardar dentro de un arreglo de string los valores de las celdas de cada fila
            String[] record;

            try {
                //Lectura linea por linea del archivo
                while ((record = reader.readNext()) != null) {
                    //Creacion de instancias de objetos
                    nodo n = new nodo();
                    vecino v = new vecino();
                    //Fin de creacion de instancias

                    if (nodos.isEmpty()) {
                        //Primera lectura del archivo
                        n.setNodo(record[0]);
                        v.setNodo(record[1]);
                        v.setCosto(record[2]);
                        n.addVecinos(v);
                        nodos.add(n);
                    } else {
                        //Existe ya un nodo en la lista
                        
                        //Bandera que indica que el nodo fue encontrado
                        boolean existe = false;
                        
                        /*
                         * Recorrido a la lista de nodos registrados para verificar si ya se leyó
                         * el mismo nodo con otro destino.
                        */
                        for (nodo N : nodos) {
                            if (record[0].equals(N.getNodo())) {
                                v.setNodo(record[1]);
                                v.setCosto(record[2]);
                                N.addVecinos(v);
                                existe = true;
                            }
                        }
                        
                        //Si no existe el nodo de origen actual dentro de la lista de nodos
                        if(existe != true) {
                            //Se añade un nodo mas a la lista de nodos
                            n.setNodo(record[0]);
                            v.setNodo(record[1]);
                            v.setCosto(record[2]);
                            n.addVecinos(v);
                            nodos.add(n);
                        }
                    }
                }

            } catch (IOException ex) {
                System.out.println("Ocurrió un error al leer el archivo. ---> " + ex);
            }

        } catch (FileNotFoundException ex) {
            System.out.println("Ocurrió un error al buscar el archivo. ---> " + ex);
        }
        //Se devuelve la lista de nodos
        return nodos;
    }
    
    /**
     * Función que devuelve las calles en donde se puede iniciar el recorrido
     * @return ArrayList con los nombres de las calles
     * @author alexd
     */
    public static ArrayList<String> obtienePuntosSalida() {
        //Creacion de lista
        ArrayList<String> listaCalles = new ArrayList();
        //Creacion de nueva instancia para lectura de .csv
        CSVReader reader;
        try {
            //Abrir archivo csv
            reader = new CSVReader(new FileReader("/home/alexd/Documentos/IA_2023/puntos_de_salida.csv"), ',');   
            //Variable que va a guardar dentro de un arreglo de string los valores de las celdas de cada fila
            String[] record;
            
            try {
                //Lectura linea por linea del archivo
                while ((record = reader.readNext()) != null) {
                    listaCalles.add(record[0]);
                }
                //Devolvemos la lista de calles
                return listaCalles;
            } catch (IOException ex) {
                System.out.println("Ocurrió un error al leer el archivo. ---> " + ex);
            }

        } catch(FileNotFoundException ex) {
            System.out.println("Ocurrió un error al buscar el archivo. ---> " + ex);
        }
        return listaCalles;
    }
    
    
    /**
     * Función que devuelve las calles en donde se puede iniciar el recorrido
     * @return ArrayList con los nombres de las calles
     * @author alexd
     */
    public static String obtieneNodoSalida(String calle) {
        //Creacion de nueva instancia para lectura de .csv
        CSVReader reader;
        try {
            //Abrir archivo csv
            reader = new CSVReader(new FileReader("/home/alexd/Documentos/IA_2023/puntos_de_salida.csv"), ',');   
            //Variable que va a guardar dentro de un arreglo de string los valores de las celdas de cada fila
            String[] record;
            
            try {
                //Lectura linea por linea del archivo
                while ((record = reader.readNext()) != null) {
                    if (record[0].equals(calle)) {
                        return record[1];
                    }
                }
            } catch (IOException ex) {
                System.out.println("Ocurrió un error al leer el archivo. ---> " + ex);
            }

        } catch(FileNotFoundException ex) {
            System.out.println("Ocurrió un error al buscar el archivo. ---> " + ex);
        }
        return null;
    }
    
    
    /**
     * Función que devuelve las calles en donde se puede iniciar el recorrido
     * @return ArrayList con los nombres de las calles
     * @author alexd
     */
    public static String obtieneCoordenada(nodo nodo) {
        //Creacion de nueva instancia para lectura de .csv
        CSVReader reader;
        try {
            //Abrir archivo csv
            reader = new CSVReader(new FileReader("/home/alexd/Documentos/IA_2023/coordenadas_nodos.csv"), ',');   
            //Variable que va a guardar dentro de un arreglo de string los valores de las celdas de cada fila
            String[] record;
            
            try {
                //Lectura linea por linea del archivo
                while ((record = reader.readNext()) != null) {
                    if (record[0].equals(nodo.getNodo())) {
                        return "[" + record[1] + "," + record[2] + "],";
                    }
                }
            } catch (IOException ex) {
                System.out.println("Ocurrió un error al leer el archivo. ---> " + ex);
            }

        } catch(FileNotFoundException ex) {
            System.out.println("Ocurrió un error al buscar el archivo. ---> " + ex);
        }
        return null;
    }
    
    
    /**
     * Función utilizada para obtener el costo de un nodo a otro
     * @param nodoOrigen
     * @param nodoDestino
     * @param archivo
     * @return int
     * @author alexd
     */
    public static int buscaCosto(String nodoOrigen, String nodoDestino, String archivo) {
        //Creacion de nueva instancia para lectura de .csv
        CSVReader reader;
        try {
            //Abrir archivo csv
            reader = new CSVReader(new FileReader("/home/alexd/Documentos/IA_2023/" + archivo), ',');   
            //Variable que va a guardar dentro de un arreglo de string los valores de las celdas de cada fila
            String[] record;
            
            try {
                //Lectura linea por linea del archivo
                while ((record = reader.readNext()) != null) {
                    if(record[0].equals(nodoOrigen) && record[1].equals(nodoDestino)) {
                        return Integer.valueOf(record[2]);
                    }
                }
            } catch (IOException ex) {
                System.out.println("Ocurrió un error al leer el archivo. ---> " + ex);
            }

        } catch(FileNotFoundException ex) {
            System.out.println("Ocurrió un error al buscar el archivo. ---> " + ex);
        }
        return 0;
    }
    
}
