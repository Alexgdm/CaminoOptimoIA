package main;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import entidades.nodo;
import forms.menu;
import java.util.ArrayList;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import utils.lecturaCSV;

/**
 * Archivo principal del proyecto
 * @author alexd
 */
public class caminoOptimoIA {    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel( new FlatDarkLaf() );
        } catch( UnsupportedLookAndFeelException ex ) {
            System.err.println( "Error al inicializar flatlaf ---> " + ex );
        }   

        menu m = new menu();
        m.setLocationRelativeTo(null);
        m.setVisible(true);
        
        //System.out.println(lecturaCSV.crearGrafo("grafo_encar.csv").size());
        
    }
}
