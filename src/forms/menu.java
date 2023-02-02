package forms;

import algoritmos.busqueda;
import com.formdev.flatlaf.FlatDarkLaf;
import entidades.nodo;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import utils.exportaRecorrido;
import utils.lecturaCSV;

/**
 * Menu principal del programa
 * @author alexd
 */
public class menu extends javax.swing.JFrame {

    /**
     * Creates new form menu
     */
    
    //Cantidad maxima de nodos recorridos que se pueden tener, para asegurar un recorrido optimo
    int cantMaxNodos = 500;
    
    
    public menu() {
        initComponents();
        inicializarOrigen();
    }

    private void inicializarOrigen() {
        ArrayList<String> listaCalles = lecturaCSV.obtienePuntosSalida();
        for (String calle : listaCalles) {
            CB_Origen.addItem(calle);
        }
    }
    
    private String obtenerArchivoHorasPico() {
        String nombreArchivo = null;
        
        /*
         * Definicion de horas pico
        */
        //De 6 a 7:20
        if (Integer.valueOf(String.valueOf(SP_Hora.getValue())) == 6) {
            nombreArchivo = "grafo_encar_hora_pico.csv";
        } else if (Integer.valueOf(String.valueOf(SP_Hora.getValue())) == 7 && Integer.valueOf(String.valueOf(SP_Minuto.getValue())) <= 20) {
            nombreArchivo = "grafo_encar_hora_pico.csv";
        }
        //De 11 a 12:10
        if (Integer.valueOf(String.valueOf(SP_Hora.getValue())) == 11) {
            nombreArchivo = "grafo_encar_hora_pico.csv";
        } else if (Integer.valueOf(String.valueOf(SP_Hora.getValue())) == 12 && Integer.valueOf(String.valueOf(SP_Minuto.getValue())) <= 10) {
            nombreArchivo = "grafo_encar_hora_pico.csv";
        }
        //De 13 a 13:40
        if (Integer.valueOf(String.valueOf(SP_Hora.getValue())) == 13 && Integer.valueOf(String.valueOf(SP_Minuto.getValue())) <= 40) {
            nombreArchivo = "grafo_encar_hora_pico.csv";
        }
        //De 17 a 18:30
        if (Integer.valueOf(String.valueOf(SP_Hora.getValue())) == 17) {
            nombreArchivo = "grafo_encar_hora_pico.csv";
        } else if (Integer.valueOf(String.valueOf(SP_Hora.getValue())) == 18 && Integer.valueOf(String.valueOf(SP_Minuto.getValue())) <= 30) {
            nombreArchivo = "grafo_encar_hora_pico.csv";
        }
        /*
         * Fin de definicion de horas pico
        */
        return nombreArchivo;
    }
    
    
    /**
     * Funcion utilizada para buscar un recorrido optimo apartir de un punto de salida establecido por el usuario
     * @return void
     * @author alexd
     */
    private void buscarCaminoManual() {
        //String horaSalida = String.valueOf(SP_Hora.getValue()) + ":" + String.valueOf(SP_Minuto.getValue());    
        String nombreArchivo = obtenerArchivoHorasPico();
        //Si el nombre se mantuvo en nulo significa que el horario no es horario pico
        if (nombreArchivo == null) {
            nombreArchivo = "grafo_encar.csv";
        }
        //Se establece el nodo de origen del recorrido
        String nodoOrigen = lecturaCSV.obtieneNodoSalida(CB_Origen.getSelectedItem().toString());
        //Se crea el grafo
        ArrayList<nodo> Grafo = lecturaCSV.crearGrafo(nombreArchivo);
        //Creacion y limpieza de la variable que va a registrar el resultado optimo
        ArrayList<nodo> resultadoBusqueda = new ArrayList<>();
        ArrayList<nodo> resultadoOptimo = new ArrayList<>();
        resultadoBusqueda.clear();
        for (int x = 0; x < 10; x++) {
            //Se limpia los resultados anteriores
            resultadoBusqueda.clear();            
            Progreso.setValue(x);
            Progreso.setStringPainted(true);
            System.out.println("Busqueda Nro.: " + x);
            //Bucle que va a recurrir a la misma funcion hasta que esta obtenga como valor una lista con registros dentro
            while(resultadoBusqueda.isEmpty() || resultadoBusqueda.size() > cantMaxNodos) {
                //Se limpia los resultados anteriores
                resultadoBusqueda.clear();
                //Se vuelve a llamar a la funcion
                resultadoBusqueda = busqueda.busquedaPorMenorCosto(nodoOrigen, resultadoBusqueda, Grafo);
                //Si la funcion retorna null significa que en algun punto el código paso todos los returns y llamados a la funcion entonces detiene la ejecución
                if (resultadoBusqueda == null) {
                    System.exit(0);
                }
            }        
            
            if (x == 0) {
                resultadoOptimo = resultadoBusqueda;
            } else {
                if (calculaCosto(resultadoBusqueda, nombreArchivo) < calculaCosto(resultadoOptimo, nombreArchivo)) {
                    resultadoOptimo = resultadoBusqueda;
                }
            }
        }
        //Fragmento que muestra en pantalla el recorrido optimo obtenido siempre y cuando este no sea nulo
        if (resultadoOptimo != null) {
            JOptionPane.showMessageDialog(this, "Camino optimo obtenido !!");
            TA_Recorrido.setText(null);
            for (nodo n : resultadoOptimo) {
                TA_Recorrido.setText(TA_Recorrido.getText() + n.getNodo() + "\n");
            }                  
        }
        TA_Recorrido.setText(TA_Recorrido.getText() + "------------------- RESUMEN DEL RECORRIDO -------------------\n");
        TA_Recorrido.setText(TA_Recorrido.getText() + "............::::::::::: Costo total          : " + calculaCosto(resultadoOptimo, nombreArchivo) + " :::::::::::............\n");
        TA_Recorrido.setText(TA_Recorrido.getText() + "............::::::::::: Nodos visitados: " + resultadoOptimo.size() + " :::::::::::............");
        exportaRecorrido.exportaMapa(resultadoOptimo);
    }
    
    /**
     * Funcion utilizada para buscar un recorrido optimo desde todos los puntos de salida disponibles
     * @return void
     * @author alexd
     */    
    private void buscarCaminoAutomatico() {
        //String horaSalida = String.valueOf(SP_Hora.getValue()) + ":" + String.valueOf(SP_Minuto.getValue());    
        String nombreArchivo = obtenerArchivoHorasPico();
        //Si el nombre se mantuvo en nulo significa que el horario no es horario pico
        if (nombreArchivo == null) {
            nombreArchivo = "grafo_encar.csv";
        }
        //Se crea el grafo
        ArrayList<nodo> Grafo = lecturaCSV.crearGrafo(nombreArchivo);
        //Creacion y limpieza de la variable que va a registrar el resultado optimo
        ArrayList<nodo> resultadoBusqueda = new ArrayList<>();
        ArrayList<nodo> resultadoOptimo = new ArrayList<>();
        //Nos aseguramos que la lista de busqueda esté vacía
        resultadoBusqueda.clear();
        //Se recorren las calles que pueden ser puntos de salida
        for (String calle : lecturaCSV.obtienePuntosSalida()) {
            //Se obtiene el nodo inicial de cada calle
            String nodoOrigen = lecturaCSV.obtieneNodoSalida(calle);
            //Se hacen 10 lecturas validas por cada calle esperando obtener un recorrido mas optimo entre todos los recorridos
            for (int x = 0; x < 10; x++) {
                //Se limpia los resultados anteriores
                resultadoBusqueda.clear();            
                System.out.println(".....::::: Calle: " + calle + "     Busqueda Nro.: " + x + " :::::.....");
                //Bucle que va a recurrir a la misma funcion hasta que esta obtenga como valor una lista con registros dentro
                while(resultadoBusqueda.isEmpty() || resultadoBusqueda.size() > cantMaxNodos) {
                    //Se limpia los resultados anteriores
                    resultadoBusqueda.clear();
                    //Se vuelve a llamar a la funcion
                    resultadoBusqueda = busqueda.busquedaPorMenorCosto(nodoOrigen, resultadoBusqueda, Grafo);
                    //Si la funcion retorna null significa que en algun punto el código paso todos los returns y llamados a la funcion entonces detiene la ejecución
                    if (resultadoBusqueda == null) {
                        System.exit(0);
                    }
                }        

                if (x == 0) {
                    resultadoOptimo = resultadoBusqueda;
                } else {
                    if (calculaCosto(resultadoBusqueda, nombreArchivo) < calculaCosto(resultadoOptimo, nombreArchivo)) {
                        resultadoOptimo = resultadoBusqueda;
                    }
                }
            }
        }
        //Fragmento que muestra en pantalla el recorrido optimo obtenido siempre y cuando este no sea nulo
        if (resultadoOptimo != null) {
            JOptionPane.showMessageDialog(this, "Camino optimo obtenido !!");
            TA_Recorrido.setText(null);
            
            for (nodo n : resultadoOptimo) {
                TA_Recorrido.setText(TA_Recorrido.getText() + n.getNodo() + "\n");
            }                  
        }
        TA_Recorrido.setText(TA_Recorrido.getText() + "------------------- RESUMEN DEL RECORRIDO -------------------\n");
        TA_Recorrido.setText(TA_Recorrido.getText() + "............::::::::::: Costo total          : " + calculaCosto(resultadoOptimo, nombreArchivo) + " :::::::::::............\n");
        TA_Recorrido.setText(TA_Recorrido.getText() + "............::::::::::: Nodos visitados: " + resultadoOptimo.size() + " :::::::::::............");
        exportaRecorrido.exportaMapa(resultadoOptimo);
    }
    
    /**
     * Funcion que calcula el costo total desde una lista de nodos
     * @param nodosRecorridos
     * @param archivo
     * @return int
     * @author alexd
     */
    private int calculaCosto(ArrayList<nodo> nodosRecorridos, String archivo) {
        int costoTotal = 0;
        int index = 0;
        for (nodo N : nodosRecorridos) {
            //Si es el primer nodo del recorrido salta y suma uno a la variable indice
            if (index == 0) {
                index++;
                continue;
            }
            //Se hace llamada a la funcion buscaCosto y se suma al costoTotal
            costoTotal += lecturaCSV.buscaCosto(nodosRecorridos.get(index - 1).getNodo(), N.getNodo(), archivo);
            //Sumamos uno al indice
            index++;
        }
        return costoTotal;
    }
    
    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BG_Opciones = new javax.swing.ButtonGroup();
        acerca_de = new javax.swing.JDialog();
        jLabel5 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TA_Recorrido = new javax.swing.JTextArea();
        jPanel2 = new javax.swing.JPanel();
        RB_Automatico = new javax.swing.JRadioButton();
        RB_Manual = new javax.swing.JRadioButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        SP_Hora = new javax.swing.JSpinner();
        jLabel3 = new javax.swing.JLabel();
        SP_Minuto = new javax.swing.JSpinner();
        jLabel4 = new javax.swing.JLabel();
        CB_Origen = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        Progreso = new javax.swing.JProgressBar();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem3 = new javax.swing.JMenuItem();

        acerca_de.setMaximumSize(new java.awt.Dimension(295, 340));
        acerca_de.setMinimumSize(new java.awt.Dimension(295, 340));
        acerca_de.setPreferredSize(new java.awt.Dimension(295, 340));
        acerca_de.setResizable(false);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imagenes/UC_Logo_80x80.png"))); // NOI18N

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Acerca de", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("Programa para la busqueda del");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel7.setText("camino óptimo para los recogedores");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel8.setText("de basura de la ciudad de Encarnación");

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel9.setText("Para su funcionamiento requirió la");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel10.setText("confección y diseño de un grafo con");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel11.setText("un total de 362 nodos.");

        jLabel12.setFont(new java.awt.Font("Noto Sans", 1, 13)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Hecho por: alumnos del 4° curso - 2022");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel12)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout acerca_deLayout = new javax.swing.GroupLayout(acerca_de.getContentPane());
        acerca_de.getContentPane().setLayout(acerca_deLayout);
        acerca_deLayout.setHorizontalGroup(
            acerca_deLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(acerca_deLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        acerca_deLayout.setVerticalGroup(
            acerca_deLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(acerca_deLayout.createSequentialGroup()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("Noto Sans", 1, 18)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Búsqueda del recorrido óptimo");

        TA_Recorrido.setColumns(20);
        TA_Recorrido.setRows(5);
        jScrollPane1.setViewportView(TA_Recorrido);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Seleccione una opción"));

        BG_Opciones.add(RB_Automatico);
        RB_Automatico.setText("El programa seleccionará el mejor punto de salida");
        RB_Automatico.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RB_AutomaticoActionPerformed(evt);
            }
        });

        BG_Opciones.add(RB_Manual);
        RB_Manual.setSelected(true);
        RB_Manual.setText("Elegir el punto de salida");
        RB_Manual.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RB_ManualActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(RB_Manual)
                    .addComponent(RB_Automatico))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(RB_Manual)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RB_Automatico)
                .addContainerGap())
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Datos Iniciales"));

        jLabel2.setText("Calle inicial del recorrido");

        SpinnerNumberModel modeloSpinnerHoras = new SpinnerNumberModel();
        modeloSpinnerHoras.setMaximum(23);
        modeloSpinnerHoras.setMinimum(0);
        SP_Hora.setModel(modeloSpinnerHoras);

        jLabel3.setText(":");

        SpinnerNumberModel modeloSpinnerMinutos = new SpinnerNumberModel();
        modeloSpinnerMinutos.setMaximum(59);
        modeloSpinnerMinutos.setMinimum(0);
        SP_Minuto.setModel(modeloSpinnerMinutos);

        jLabel4.setText("Hora de inicio del recorrido");

        jButton1.setText("Buscar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CB_Origen, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(SP_Hora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SP_Minuto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CB_Origen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SP_Hora, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(SP_Minuto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton1))
                .addGap(23, 23, 23))
        );

        Progreso.setMaximum(1000000);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(Progreso, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Progreso, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jMenu1.setText("Información");

        jMenuItem1.setText("Acerca de");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("Instrucciones");
        jMenu1.add(jMenuItem2);

        jMenuItem3.setText("Cerrar");
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void RB_ManualActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RB_ManualActionPerformed
        CB_Origen.setEnabled(true);
    }//GEN-LAST:event_RB_ManualActionPerformed

    private void RB_AutomaticoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RB_AutomaticoActionPerformed
        CB_Origen.setEnabled(false);
    }//GEN-LAST:event_RB_AutomaticoActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (RB_Manual.isSelected()) {
            buscarCaminoManual();
        } else {
            buscarCaminoAutomatico();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        acerca_de.setVisible(true);
        acerca_de.setLocationRelativeTo(this);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        /*try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(menu.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }*/
        //</editor-fold>
        
        try {
            UIManager.setLookAndFeel( new FlatDarkLaf() );
        } catch( UnsupportedLookAndFeelException ex ) {
            System.err.println( "Error al inicializar flatlaf ---> " + ex );
        }
        
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new menu().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup BG_Opciones;
    private javax.swing.JComboBox<String> CB_Origen;
    private javax.swing.JProgressBar Progreso;
    private javax.swing.JRadioButton RB_Automatico;
    private javax.swing.JRadioButton RB_Manual;
    private javax.swing.JSpinner SP_Hora;
    private javax.swing.JSpinner SP_Minuto;
    private javax.swing.JTextArea TA_Recorrido;
    private javax.swing.JDialog acerca_de;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}
