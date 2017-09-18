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
import static Ejecucion_usql.Instruccion_insetar.ComprobarTipoObjeto;
import static Ejecucion_usql.Instruccion_insetar.VerificarIntegridadReferencial;
import static Ejecucion_usql.Instruccion_insetar.contieneForanea;
import static Ejecucion_usql.Instruccion_insetar.contieneUnico;
import static Ejecucion_usql.Instruccion_insetar.obtenerForanea;
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
    private static syntaxtree.exp_logica expresion =  null;
    private static DepthFirstRetVisitor_usql este =  null;
    private static Tabla tabla =  null;
    private static Ent valoresTabla  =  null;
    private static Integer posActualizar = -1;
    private static LinkedList<Simbolo> valoresInsertar =  new LinkedList<>();
    private static LinkedList<Integer> posiciones =  new LinkedList<>();
    private static String LRE =  iniciarLRE();
    private static Admon_archivo ar = new Admon_archivo();
    
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
        LRE = iniciarLRE();
    }
    
    private static void init(actualizar n, DepthFirstRetVisitor_usql este){
        Instruccion_actualizar.este = este;
        tabla =  Instruccion_insetar.LeerTabla(n.f2.tokenImage);
        Instruccion_insetar.levantarRegistros(tabla.path.replace("\"",""), tabla.cuerpo);
        iniciarComponentes(n);                
    }
    
    
    
    public static void ActualizarRegistro(actualizar n, DepthFirstRetVisitor_usql este) throws IOException{
        init(n, este);
        if(comprobarColumnas(listaColumnas)){
            valoresTabla =  new Ent(este.obtenerGlobal());
            este.vaciarGlobal();
            este.fijarGlobal(valoresTabla);
            obtenerPosiciones();
            if(expresion != null){
                for(LinkedList<Simbolo>reg : tabla.cuerpo.registros){
                    posActualizar++;
                    AsignaValores(tabla, reg);
                    if(((Simbolo)expresion.accept(este)).v.ABool()){                        
                        actualizar(reg, posActualizar);
                    }
                }
            }else{
                //actualiza todos los registros
                for(LinkedList<Simbolo>reg : tabla.cuerpo.registros){
                    posActualizar++;
                    actualizar(reg, posActualizar);
                }
            }
            ReIniciarValores();
        }else{            
            ReIniciarValores();
        }        
    }
    
    protected  static void obtenerPosiciones(){
        int pos = 0;
        for(Simbolo s : tabla.valores){
            for(String nombre : listaColumnas){
                if(nombre.equals(s.nombre)){
                    posiciones.add(pos);
                    break;
                }
            }
            pos++;
        }
    }
    
    private static Boolean VerificarUnico(String valor, int posColum){
        boolean unica = true;
        for(LinkedList<Simbolo> fila :  tabla.cuerpo.registros){
            Simbolo data =  fila.get(posColum);
            if(data.v.ACadena().equals(valor)){
                unica =  false;
                break;
            }
        }
        return unica;
    }
    
    private static boolean verificarUnicos(){
        Boolean continuar =  true;        
        for (int i = 0; i < posiciones.size(); i++) {
            Columna c =  (Columna) tabla.valores.get(posiciones.get(i)).v;
            String valor =  listaValores.get(i).v.ACadena();
            boolean esUnica =  contieneUnico(c.atributos);
            if(esUnica){
                continuar =  VerificarUnico(valor, posiciones.get(i));
            }
         }
        return continuar;
    }
    
    protected  static void actualizar(LinkedList<Simbolo>reg, int pos) throws IOException{
        boolean continua = verificarUnicos();
        if(continua){
            continua = EsAutoIncrementable();
        }
        if(continua){
            continua = VerificarForaneaActualizar();
        }
        if(continua){
          continua =  VerificarTiposActualizar(listaValores);
        }                                
        if(continua){
            ActualizarReg(reg);
            limpiarRegistro(pos);            
            String texto = generarTextoRegistro(reg);
            ar.EscribirRegistroBd(500*pos,texto, tabla.path.replace("\"", ""));
            Debuger.Debug("registro actualizado con exito", false, null);
        }else{
            Debuger.Debug("Error al actualizar registro...", false, null);
        }            
    }
    
    protected static String generarTextoRegistro(LinkedList<Simbolo> ValoresInsertar){
        StringBuilder texto =  new StringBuilder();
        texto.append("<rows>");
        for(Simbolo s :  ValoresInsertar){
            if(Contexto.EsObjeto(s.tipo)){
                 texto.append("<")
                        .append(s.nombre)
                        .append(">");
                Objeto o =  (Objeto) s.v;
                for(Simbolo ob :  o.valor.tabla.values()){
                    texto.append("<")
                        .append(ob.nombre)
                        .append(">")
                        .append("\"")
                        .append(ob.v.ACadena())
                        .append("\"")
                        .append("</")
                        .append(ob.nombre)
                        .append(">");
                }
                texto.append("</")
                        .append(s.nombre)
                        .append(">");
            } else {
                texto.append("<")
                        .append(s.nombre)
                        .append(">")
                        .append("\"")
                        .append(s.v.ACadena())
                        .append("\"")
                        .append("</")
                        .append(s.nombre)
                        .append(">");
            }
        }        
        texto.append("</rows>");
        return texto.toString();
    }
    
    protected  static void ActualizarReg(LinkedList<Simbolo> registro){
        for(int i = 0; i < posiciones.size(); i++){
            registro.get(posiciones.get(i)).v = listaValores.get(i).v;
        }        
    }
    
    public static void limpiarRegistro(int pos) throws IOException{
        ar.EscribirRegistroBd(500*pos, LRE, tabla.path.replace("\"", ""));
    }
    
    private static String iniciarLRE(){
        StringBuilder texto =  new StringBuilder();
        for(int i = 0; i < 500; i++){
           texto.append("\0");
        }
        return texto.toString();
    }
    
    protected static Boolean VerificarTiposActualizar(LinkedList<Simbolo> valores){        
        Instruccion_declarar dec  = new Instruccion_declarar(null);
        boolean CoincidenTipos =  false;
        Simbolo insertar;
        for(int i =  0; i < posiciones.size(); i++){
            Simbolo val =  tabla.valores.get(posiciones.get(i));
            Simbolo aux =  new Simbolo(val.nombre, val.tipo, null);           
            Simbolo vald =  valores.get(i);
            if(dec.comprobarTipos(val, vald)){
                CoincidenTipos = true; 
                insertar  =  new Simbolo (val.nombre,val.tipo,null);
                insertar.v =  dec.v;
                valoresInsertar.add(insertar);
            }else if (ComprobarTipoObjeto(aux, vald)){
                CoincidenTipos = true;
                insertar =  new Simbolo(val.nombre, val.tipo, null);
                insertar.v =  vald.v;
                valoresInsertar.add(insertar);
            } else {
                Debuger.Debug("Error de tipos al actualizar...", false, null);
               return false;
            }
        }        
        return CoincidenTipos;
    }
    
    /**
     *
     * @param valores
     * @return
     */
    protected static boolean VerificarForaneaActualizar(){
        Boolean continuar = true;        
        for (int i = 0; i < posiciones.size(); i++) {
            Columna c =  (Columna) tabla.valores.get(posiciones.get(i)).v;
            String valor =  listaValores.get(i).v.ACadena();
            boolean esForanea =  contieneForanea(c.atributos);
            if(esForanea){
                String ref =  obtenerForanea(c.atributos);
                continuar = VerificarIntegridadReferencial(valor, ref);
                if(!continuar){
                    Debuger.Debug("Violacion de integridad referencial...", false, null);
                    break;                    
                }                
            }
        }
        return continuar;
    }
    
    
    private static void generarValoresInsertar(){
        for(String nombre :  listaColumnas){
            posiciones.add(Contexto.ObtenerPosicion(tabla.valores, nombre));            
        }
    }
    
    protected static boolean EsAutoIncrementable(){
        Boolean continuar =  true;        
        for (int i = 0; i < posiciones.size(); i++) {
            Columna c =  (Columna) tabla.valores.get(posiciones.get(i)).v;            
            boolean esAuto =  contieneAuto(c.atributos);
            if(esAuto){
                return false;
            }
         }
        return continuar;
    }
    
    protected static boolean contieneAuto(LinkedList<Simbolo> atributos){
        for(Simbolo s: atributos){
            if(s.nombre.equals("auto")){
                return true;
            }
        }
        return false;
    }
    
    protected static void limpiarRegistro(){        
        
    }
    
    private static void AsignaValores(Tabla t, LinkedList<Simbolo> registro) {
        for (int i = 0; i < t.valores.size(); i++) {
            Simbolo s = new Simbolo(t.valores.get(i).nombre, t.valores.get(i).tipo, null);
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
                return false;
            }            
        }
        return existe;
    }
    
    private static void iniciarComponentes(actualizar n){
        nombreTabla =  n.f2.tokenImage;
        iniciarListaColumnas(n);
        iniciarValores(n);       
        if(n.f12.present()){
            syntaxtree.NodeSequence ns = (syntaxtree.NodeSequence) n.f12.node; 
            expresion = (exp_logica) ns.nodes.get(1); 
        }
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
        Simbolo primerValor = (Simbolo)((lista_expresion)n.f9).f0.accept(este);
        listaValores.add(primerValor);
        syntaxtree.NodeListOptional nlo = n.f10;
        for(INode_usql node : nlo.nodes){
            syntaxtree.NodeSequence ns =  (syntaxtree.NodeSequence) node;
            primerValor = (Simbolo) ((lista_expresion)ns.nodes.get(1)).f0.accept(este);
            listaValores.add(primerValor);
        }
    }
    
}
