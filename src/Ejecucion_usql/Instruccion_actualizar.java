/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ejecucion_usql;

/**
 *
 * @author mike
 */

import Analizador_xml.*;
import Analizador_usql.*;
import Entorno.*;
import arbolxml.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import proyecto.Admon_archivo;
import syntaxtree.*;
import visitor.*;
import visitorxml.*;
import proyecto.*;
import arbolxml.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Instruccion_actualizar {
    private static String nombreTabla = "";
    private static LinkedList<String> listaColumnas =  new LinkedList<>();
    private static LinkedList<Simbolo> listaValores =  new LinkedList<>();
    private static syntaxtree.lista_expresion expresion =  null;
    private static DepthFirstRetVisitor_usql este =  null;
    private static Tabla tabla =  null;
    
    private static void ReIniciarValores(){
        nombreTabla = "";
        listaColumnas =  new LinkedList<>();
        listaValores =  new LinkedList<>();
        expresion =  null;
        este  =  null;
        tabla =  null;        
    }
    
    protected static void ActualizarRegistro(actualizar n, DepthFirstRetVisitor_usql este){
        tabla =  Instruccion_insetar.LeerTabla(n.f2.tokenImage);
        Instruccion_insetar.levantarRegistros(tabla.path.replace("\"",""), tabla.cuerpo);
        iniciarComponentes(n);
    }
    
    private static void iniciarComponentes(actualizar n){
        nombreTabla =  n.f2.tokenImage;
        iniciarListaColumnas(n);
    }
    
    private static void iniciarListaColumnas(actualizar n){
        String primerColumna =  n.f4.tokenImage;
        listaColumnas.add(primerColumna);
        syntaxtree.NodeListOptional nlo = n.f5;
        for(INode_usql node :  nlo.nodes){
            syntaxtree.NodeSequence ns =  (syntaxtree.NodeSequence) node;
            primerColumna = ((syntaxtree.NodeToken) ns.nodes.get(1)).tokenImage;
            listaColumnas.add(primerColumna);
        }
        
    }
}
