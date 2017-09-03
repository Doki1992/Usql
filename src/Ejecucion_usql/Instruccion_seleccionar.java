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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

public class Instruccion_seleccionar {
    
    private static String ordeneraPor = "";
    private static boolean esDecendente = false;
    private static boolean esTodo =  false;
    private static LinkedList<String> listaColumnas =  new LinkedList<>();
    private static LinkedList<String> listaNombreTablas =  new LinkedList<>();
    private static LinkedList<Simbolo> listaTablas =  new LinkedList<>();
    private static exp_logica exp = null;
    private static DepthFirstRetVisitor_usql este =  null;
    private static Cuerpo_tabla producto_cartesiano =  new Cuerpo_tabla();
    private static Ent valoresTabla;
    private static Cuerpo_tabla resultado;
    
    public static void SeleccionarRegistro(seleccionar n, DepthFirstRetVisitor_usql este){
        IniciarComponentesConsulta(n);
        if(!listaTablas.isEmpty()){
            Simbolo vista = new Simbolo("temp", Contexto.TB, null);
            CrearVista(vista);  
            valoresTabla =  new Ent(este.obtenerGlobal());
            este.vaciarGlobal();
            este.fijarGlobal(valoresTabla);
            Cuerpo_tabla resultado_parcial  = new Cuerpo_tabla();
            for(LinkedList<Simbolo> reg : ((Tabla)vista.v).cuerpo.registros ){
                AsignaValores((Tabla)vista.v, reg);
                if(((Simbolo)exp.accept(este)).v.ABool()){
                    resultado_parcial.registros.add(reg);
                }
            }
        } else {
            Debuger.Debug("Enserio Mike enserio !", false, null);
        }
    }
    
    private static void CrearVista(Simbolo vista) {        
        Tabla temp =  new Tabla("");
        temp.valores = GenerarEncabezadoConsulta();        
        CrearSuperTabla();
        temp.cuerpo = producto_cartesiano; 
        vista.v =  temp;
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
                    s.v =  new Objeto(Contexto.OBJ);
                    s.v = val.v;
            }
            valoresTabla.tabla.put(s.nombre, s);
        }
    }
    protected static void IniciarComponentesConsulta(seleccionar n){
        syntaxtree.NodeChoice primeraColumna = n.f1;
        syntaxtree.NodeListOptional otrasColumnas = n.f2;
        syntaxtree.NodeToken primerTabla =  n.f4;
        syntaxtree.NodeListOptional otrasTablas =  n.f5;
        syntaxtree.NodeOptional choice2=  n.f6;
        llenarColumnas(primeraColumna, otrasColumnas);
        llenarTablas(primerTabla, otrasTablas);
        asignarOtrosValores(choice2);
    }
    
    private static void llenarColumnas(syntaxtree.NodeChoice primerColumna, syntaxtree.NodeListOptional otrasColumnas){
        String primerc =  "";
        switch(primerColumna.which){
            case 0:
                primerc =  ((syntaxtree.NodeToken)primerColumna.choice).tokenImage;
                listaColumnas.add(primerc);
                break;
            case 1:
                esTodo =  true;
                break;                
        }
        for(syntaxtree.INode_usql node : otrasColumnas.nodes){
            syntaxtree.NodeSequence ns =  (syntaxtree.NodeSequence) node;
            primerc =  ((syntaxtree.NodeToken)ns.nodes.get(1)).tokenImage;
            listaColumnas.add(primerc);
        }
    }
    
    private static void llenarTablas(syntaxtree.NodeToken primerTabla, syntaxtree.NodeListOptional otrasTablas){
        String primertabla =  primerTabla.tokenImage;
        listaNombreTablas.add(primertabla);
        for(syntaxtree.INode_usql node : otrasTablas.nodes){
            syntaxtree.NodeSequence ns =  (syntaxtree.NodeSequence) node;
            primertabla = ((syntaxtree.NodeToken)ns.nodes.get(1)).tokenImage;
            listaNombreTablas.add(primertabla);
        }
        
        for(String nombre : listaNombreTablas){
            Simbolo tabla =  new Simbolo(nombre, Contexto.TB, null);            
            tabla.v = Instruccion_insetar.LeerTabla(nombre);
            if(tabla.v != null){
                listaTablas.add(tabla);
                Tabla tt =(Tabla) tabla.v;
                Instruccion_insetar.levantarRegistros(tt.path.replace("\"", ""), tt.cuerpo);
            }            
        }
    }
    
    private static void asignarOtrosValores(syntaxtree.NodeOptional opcional){
        syntaxtree.NodeSequence ns = (syntaxtree.NodeSequence) opcional.node;        
        if(opcional.present()){
            exp =  (exp_logica) ns.nodes.get(1);
            syntaxtree.NodeOptional ns1 =  (syntaxtree.NodeOptional) ns.nodes.get(2);
            if(ns1.present()){
                syntaxtree.NodeSequence ns2 =  (syntaxtree.NodeSequence) ns1.node;
                syntaxtree.NodeOptional ns3 = (syntaxtree.NodeOptional) ns2.nodes.get(3);
                ordeneraPor =  ((syntaxtree.NodeToken)ns2.nodes.get(2)).tokenImage;
                if(ns3.present()){
                    syntaxtree.NodeChoice nc =  (syntaxtree.NodeChoice) ns3.node;
                    switch(nc.which){
                        case 0:
                            esDecendente =  false;
                            break;
                        case 1:
                            esDecendente =  true;
                            break;
                    }
                }
            }
        }
        
    }
    
    private static LinkedList<Simbolo> GenerarEncabezadoConsulta() {
        LinkedList<Simbolo> encabezado = new LinkedList<>();
        for (Simbolo s : listaTablas) {
            Tabla t = (Tabla) s.v;
            for (Simbolo ss : t.valores) {
                ss.nombre = s.nombre + "." + ss.nombre;
                encabezado.add(ss);
            }
        }
        return encabezado;
    }
    
    private static boolean contieneEncabezado(String nombre, LinkedList<Simbolo> valores){
        return valores.stream().anyMatch((Simbolo a) -> a.nombre.equals(nombre));
    }

    private static void RealizarProdcutoCartesiano(Cuerpo_tabla cp1, Cuerpo_tabla cp2) {
        LinkedList<LinkedList<Simbolo>> temp =  new LinkedList<>();
        if (cp1.registros.isEmpty()) {
            cp1.registros.addAll(cp2.registros);
        } else {
            for (LinkedList<Simbolo> registro : cp1.registros) {
                for (LinkedList<Simbolo> registro1 : cp2.registros) {
                    LinkedList<Simbolo>regTemp =  new LinkedList<>();
                    regTemp.addAll(registro);
                    regTemp.addAll(registro1);
                    temp.add(regTemp);
                }
            }
            cp1.registros.clear();
            cp1.registros.addAll(temp);
        }
    }
    
    private static void CrearSuperTabla(){
        for(Simbolo s :  listaTablas){
            Tabla t = (Tabla) s.v;
            RealizarProdcutoCartesiano(producto_cartesiano, t.cuerpo);
        }
    }
}
