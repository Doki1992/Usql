/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto;
import Analizador_xml.*;
import Analizador_usql.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import syntaxtree.*;
import arbolxml.*;
import visitor.*;
import visitorxml.*;
import Entorno.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *
 * @author mike
 */
public class proyecto {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException_usql, java.text.ParseException {
        // TODO code application logic here   
//        
        SimpleDateFormat dd =  new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        Date d = dd.parse("20-02-1889 18:00:00"); 
        Contexto.ObtenerPathCononico();
        UsqlParser a = new UsqlParser(new java.io.FileInputStream("sample.dat"));
        INode_usql init  = a.Inicio();
        DepthFirstRetVisitor_usql <Simbolo> v = new DepthFirstRetVisitor_usql<>();
        v.BackUpDump = init;
        init.accept(v);
//         
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                try {
//                    new Uiservidor().setVisible(true);
//                } catch (IOException ex) {
//                    Logger.getLogger(proyecto.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        });
//        new Uiservidor().setVisible(true);
//        Contexto.ObtenerPathCononico();
//        Servidor server =  new Servidor();  
    }
    
}
