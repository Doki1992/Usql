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
/**
 *
 * @author mike
 */
public class proyecto {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException, ParseException_usql {
        // TODO code application logic here
        Contexto.ObtenerPathCononico();
        UsqlParser a = new UsqlParser(new java.io.FileInputStream("sample.dat"));
        INode_usql init  = a.Inicio();
        DepthFirstRetVisitor_usql <Simbolo> v = new DepthFirstRetVisitor_usql<>();
        init.accept(v);
                         
    }
    
}