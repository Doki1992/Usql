/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ejecucion_usql;

import Entorno.*;
import proyecto.Contexto;
import proyecto.Debuger;
import syntaxtree.INode_usql;
import syntaxtree.NodeListOptional;
import syntaxtree.*;

/**
 *
 * @author mike
 */
public class Instruccion_declarar {
    
    Ent entorno;
    String tipo;
    Valor v;
    public Instruccion_declarar(Ent entorno){
        this.entorno =  entorno;
    }
    
    private Simbolo buscarPadre(String id){
        return null;
    }
    
    private boolean comprobarTipos(Simbolo tipo, Simbolo exp){
        if(tipo.tipo.equals(Contexto.TEX)){
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
    
    public void declararObjeto(){
        
    }
    
}
