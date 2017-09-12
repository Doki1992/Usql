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
import com.sun.org.apache.bcel.internal.generic.AALOAD;
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
    private static Ent valoresTabla  =  null;
    private static Integer posActualizar = -1;
    private static LinkedList<Simbolo> valoresInsertar =  new LinkedList<>();
    private static LinkedList<Integer> posiciones =  new LinkedList<>();
    
    private static void ReIniciarValores(){
        nombreTabla = "";
        listaColumnas =  new LinkedList<>();
        listaValores =  new LinkedList<>();
        expresion =  null;
        este  =  null;
        tabla =  null;        
        valoresTabla =  null;
        posActualizar = -1;
        valoresInsertar =  new LinkedList<>();
        posiciones =  new LinkedList<>();
    }
    
    private static void init(actualizar n, DepthFirstRetVisitor_usql este){
        Instruccion_actualizar.este = este;
        tabla =  Instruccion_insetar.LeerTabla(n.f2.tokenImage);
        Instruccion_insetar.levantarRegistros(tabla.path.replace("\"",""), tabla.cuerpo);
        iniciarComponentes(n);
        
    }
    
    protected static void ActualizarRegistro(actualizar n, DepthFirstRetVisitor_usql este){
        init(n, este);
        if(comprobarColumnas(listaColumnas)){
            valoresTabla =  new Ent(este.obtenerGlobal());
            este.vaciarGlobal();
            este.fijarGlobal(valoresTabla);
            if(expresion != null){
                for(LinkedList<Simbolo>reg : tabla.cuerpo.registros){
                    posActualizar++;
                    AsignaValores(tabla, reg);
                    if(((Simbolo)expresion.accept(este)).v.ABool()){
                        generarValoresInsertar();
                        //verificarTipos();
                        //actualizar(reg,posActualizar);
                    }
                }
            }else{
                //actualiza todos los registros
                for(LinkedList<Simbolo>reg : tabla.cuerpo.registros){
                    
                }
            }
            ReIniciarValores();
        }        
    }
    
    private static void generarValoresInsertar(){
        for(String nombre :  listaColumnas){
            posiciones.add(Contexto.ObtenerPosicion(tabla.valores, nombre));
            
        }
    }
    
    private static void AsignaValores(Tabla t, LinkedList<Simbolo> registro) {
        for (int i = 0; i < t.valores.size(); i++) {
            Simbolo s = t.valores.get(i);
            Simbolo val = registro.get(i);
            switch (s.tipo) {
                case Contexto.TEX:
                    s.v = new Texto(val.v.ACadena(), s.tipo);
                    break;
                case Contexto.BOl:
                    s.v = new Bool(val.v.ACadena(), s.tipo);
                    break;
                case Contexto.ENT:
                    s.v = new Entero(val.v.ACadena(), s.tipo);
                    break;
                case Contexto.DOB:
                    s.v = new Doble(val.v.ACadena(), s.tipo);
                    break;
                case Contexto.DAT:
                    s.v = new Fecha_tipo(val.v.ACadena(), s.tipo);
                    break;
                case Contexto.DATH:
                    s.v = new Fecha_hora_tipo(val.v.ACadena(), s.tipo);
                    break;
                default:
                    s.v = new Objeto(Contexto.OBJ);
                    s.v = val.v;
            }
            valoresTabla.tabla.put(s.nombre, s);
        }
    }

    
    
    private static boolean comprobarColumnas(LinkedList<String>columnas){
        boolean existe =  true;
        for(String nombre : columnas){
            existe = Instruccion_seleccionar.contieneEncabezado(nombre, tabla.valores);
            if(!existe){
                Debuger.Debug("Error la tabla " + nombreTabla + " no tiene ningun atributo con nombre " + nombre + "...", false, null);
                return existe;
            }
        }
        return existe;
    }
    
    private static void iniciarComponentes(actualizar n){
        nombreTabla =  n.f2.tokenImage;
        iniciarListaColumnas(n);
        iniciarValores(n);       
        expresion =  (lista_expresion)n.f12.node; 
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
    
    private static void iniciarValores(actualizar n){
        Simbolo primerValor = (Simbolo) n.f9.accept(este);
        listaValores.add(primerValor);
        syntaxtree.NodeListOptional nlo = n.f10;
        for(INode_usql node : nlo.nodes){
            syntaxtree.NodeSequence ns =  (syntaxtree.NodeSequence) node;
            primerValor = (Simbolo) ns.nodes.get(1).accept(este);
            listaValores.add(primerValor);
        }
    }
    
}
