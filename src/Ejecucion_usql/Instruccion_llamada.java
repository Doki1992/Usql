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
import static Ejecucion_usql.Instruccion_login.generarArbol;
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
import java.util.LinkedList;
import java.util.List;


public class Instruccion_llamada {
    private  final String bd_maestro = Contexto.CanonicalPath+"/Bases/maestro.bd";
    private  final String BD_USUARIO = Contexto.CanonicalPath+"/Bases/usuario.bd";
    private  final String BD_BASES = Contexto.CanonicalPath+"/Bases/bases/";
    private  final String BD_OBJ = Contexto.CanonicalPath+"/Bases/objetos/";
    private  final String BD_FUNC = Contexto.CanonicalPath+"/Bases/funciones/";
    private  final String BD_TABLA =Contexto.CanonicalPath+ "/Bases/tablas/";
    private  XmlParser parser;
    private  Admon_archivo ar = new Admon_archivo();
    private  Ent levantado;
    private  Ent valoresTablaFp;
    private  LinkedList<Simbolo> parametros = null;
    private  LinkedList<Simbolo> parametrosVal = null;
    
    private void reinit(){
        parametros = null;
        parametrosVal = null;
        levantado = null;
        valoresTablaFp = null;
    }
    
    public  Simbolo ejecutarMetodo(String nombre, syntaxtree.NodeOptional node, syntaxtree.NodeListOptional otros, DepthFirstRetVisitor_usql este) throws IOException, ParseException_usql{
        levantado =  Instruccion_login.levantarTablas(BD_FUNC.replace("\"", "") + Contexto.EnUso.nombre + "_func.bd");        
        Simbolo retorno =  null;
        Simbolo func = null;
        if((func = levantado.Buscar(nombre)) != null){
            FuncionProc fp =  (FuncionProc) func.v;
            valoresTablaFp =  new Ent(este.obtenerGlobal());
            este.vaciarGlobal();
            este.fijarGlobal(valoresTablaFp);
            fijarParametros(node, otros,este);
            if(parametros.size() == fp.parametros.size()){
              boolean t =   verificarTiposLlamada(fp.parametros);
                if(t){
                   Instruccion_principal.instruccionPrincipal(fp.src.replace("\"", ""));
                   retorno =  Contexto.retorno;
                   if(retorno != null){
                       reinit();
                       return retorno;
                   }
                }
            } else {
                Debuger.Debug("Error cantidad de parametros incorrecta", false, null);
            }
        }else{
            Debuger.Debug("La funcion con nombre " + nombre, false, null);
        }
        reinit();
        return null;
    }
    
    protected  boolean verificarTiposLlamada(LinkedList<Simbolo> params){
        
        parametrosVal =  new LinkedList<>();
        for(int i = 0; i < params.size(); i++){
            Instruccion_declarar dec =  new Instruccion_declarar(null);
            Simbolo iz =  params.get(i); 
            Simbolo der = parametros.get(i);
            if(dec.comprobarTipos(iz, der)){
                parametrosVal.add(new Simbolo(iz.nombre, iz.nombre, dec.v));                
            }else {
                Debuger.Debug("Error de tipos en parametros", false, null);
                return false;
            }
        }
        return true;
    }
    
    protected  void fijarParametros (syntaxtree.NodeOptional node, syntaxtree.NodeListOptional otros, DepthFirstRetVisitor_usql este){
        Simbolo primero;
        parametros =  new LinkedList<>();
        if(node.present()){
            primero =  (Simbolo) node.node.accept(este);
            parametros.add(primero);
        }
        if(otros.present()){
            for(INode_usql no : otros.nodes){
                syntaxtree.NodeSequence ns = (syntaxtree.NodeSequence) no;
                primero = (Simbolo) ns.nodes.get(1).accept(este);
                parametros.add(primero);
            }                        
        }
    }
}
