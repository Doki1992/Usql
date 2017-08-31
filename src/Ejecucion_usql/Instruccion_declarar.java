/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ejecucion_usql;

import Analizador_xml.ParseException;
import Analizador_xml.XmlParser;
import static Ejecucion_usql.Instruccion_alterar.GenerarTextoObjetoNuevo;
import Entorno.*;
import arbolxml.INode;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import proyecto.Admon_archivo;
import proyecto.Contexto;
import proyecto.Debuger;
import syntaxtree.INode_usql;
import syntaxtree.NodeListOptional;
import syntaxtree.*;
import visitorxml.DepthFirstRetVisitor;

/**
 *
 * @author mike
 */
public class Instruccion_declarar {
    
    Ent entorno;
    String tipo;
    Valor v;
    
    private static final String BD_OBJ = Contexto.CanonicalPath + "/Bases/objetos/";
    private static XmlParser parser;
    private static Admon_archivo ar = new Admon_archivo();
    private static Ent levantado;
    
    public Instruccion_declarar(Ent entorno){
        this.entorno =  entorno;
    }
    
    private Simbolo buscarPadre(String id){
        return null;
    }
    
    private boolean comprobarTipos(Simbolo tipo, Simbolo exp){
        if(exp == null){
            return true;
        }else if(tipo.tipo.equals(Contexto.TEX)){
            this.tipo = Contexto.TEX;
            this.v = new Texto(exp.v.ACadena(), "");
            return true;
        }else if(tipo.tipo.equals(Contexto.DOB) && (exp.v.Tipo.equals(Contexto.DOB) || exp.v.Tipo.equals(Contexto.ENT) || exp.v.Tipo.equals(Contexto.BOl))){
            this.tipo = Contexto.DOB;
            this.v = new Doble(exp.v.ACadena(), "");
            return true;
        }else if(tipo.tipo.equals(Contexto.ENT) && (exp.v.Tipo.equals(Contexto.ENT) || exp.v.Tipo.equals(Contexto.BOl))){
            this.tipo = Contexto.ENT;
            this.v = new Entero(exp.v.ACadena(), "");
            return true;
        }else if(tipo.tipo.equals(Contexto.BOl) && (exp.v.Tipo.equals(Contexto.BOl))){
            this.tipo = Contexto.BOl;
            this.v = new Bool(exp.v.ACadena(), "");
            return true;
        }else if(tipo.tipo.equals(Contexto.DAT) && (exp.v.Tipo.equals(Contexto.DAT))){
            this.tipo = Contexto.DAT;
            this.v = new Fecha_tipo(exp.v.ACadena(), "");
            return true;
        }else if(tipo.tipo.equals(Contexto.DATH) && (exp.v.Tipo.equals(Contexto.DATH))){
            this.tipo = Contexto.DATH;
            this.v = new Fecha_hora_tipo(exp.v.ACadena(), "");
            return true;
        }else{
            Debuger.Debug("Error de tipos en declaracion " + "...",false,null); 
            return false;
        }                
    }
    public void declararVar(Simbolo tipo, Simbolo exp, Simbolo primerId, NodeListOptional listaId){

        if(!comprobarTipos(tipo, exp))
            return;
        
        Simbolo s =  new Simbolo(primerId.nombre, this.tipo, this.v);
        this.entorno.insertar(s.nombre, s);
        for(INode_usql node : listaId.nodes) {
            NodeSequence ns = (NodeSequence) node;
            NodeToken tok =  (NodeToken) ns.nodes.get(1);
            s =  new Simbolo (tok.tokenImage,this.tipo,this.v);
            this.entorno.insertar(s.nombre, s);
        }
    }
    
    public void AsignarVar(Simbolo iz, Simbolo exp){
        if(!comprobarTipos(iz, exp))
            return;
        iz.v = this.v;
    }
    
    public void AsignarObjeto(Simbolo iz, String hijo, Simbolo exp){
        if(iz.v.Tipo.equals(Contexto.OBJ)){
            Objeto obj =  (Objeto)iz.v;
            Simbolo son =  obj.valor.Buscar(hijo);
            if(son != null){
                AsignarVar(son, exp);
            }else{
                Debuger.Debug("El objeto " + iz.nombre + " no tiene ningun atributo con nombre " + hijo + "...", false, null);
            }
        }else{
            Debuger.Debug("Error la variable " + iz.nombre + " no es un objeto..." , false, null);
        }
    }
    
    public void declararObjeto(String nombreOjeto, String nombreTipoObjeto){
        Objeto obj =  this.LeerObjetos(nombreTipoObjeto);
        if(obj !=  null){
            Objeto  ob =  new Objeto("");
            ob.valor.tabla =  new HashMap<>(obj.valor.tabla);
            Simbolo nuevoObjeto =  new Simbolo(nombreOjeto, nombreTipoObjeto, ob);
            this.entorno.insertar(nuevoObjeto.nombre, nuevoObjeto);
            Debuger.Debug("Objeto creado exitosamente... " + nombreOjeto + " ...!");
        }
    }
    
    private Objeto LeerObjetos(String identificador) {
        String contenidoTexto;
        try {
            contenidoTexto = ar.LeerRegistroBd(0, Contexto.EnUso.path_obj, ar.Tamano(Contexto.EnUso.path_obj));
            parser = new XmlParser(new ByteArrayInputStream(contenidoTexto.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado;
            if (levantado.existe(identificador)) {
               return ((Objeto)levantado.Buscar(identificador).v); 
            } else {
                Debuger.Debug("Error el objeto " + identificador + " no existe...");
            }
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch (NullPointerException ex) {
            Debuger.Debug("Error no se ha seleccionado una base de datos ...");
        }
        return null;
    }
    
}
