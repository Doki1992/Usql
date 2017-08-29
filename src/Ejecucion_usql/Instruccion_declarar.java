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
    
    public Instruccion_declarar(Ent entorno){
        this.entorno =  entorno;
    }
    
    private Simbolo buscarPadre(String id){
        return null;
    }
    
    private boolean comprobarTipos(Simbolo tipo, Simbolo exp){
        if(tipo.tipo.equals(Contexto.TEX)){
            return true;
        }else if(tipo.tipo.equals(Contexto.DOB) && (exp.v.Tipo.equals(Contexto.DOB) || exp.v.Tipo.equals(Contexto.ENT) || exp.v.Tipo.equals(Contexto.BOl))){
            return true;
        }else if(tipo.tipo.equals(Contexto.ENT) && (exp.v.Tipo.equals(Contexto.ENT) || exp.v.Tipo.equals(Contexto.BOl))){
            return true;
        }else if(tipo.tipo.equals(Contexto.BOl) && (exp.v.Tipo.equals(Contexto.BOl))){
            return true;
        }else if(tipo.tipo.equals(Contexto.DAT) && (exp.v.Tipo.equals(Contexto.DAT))){
            return true;
        }else if(tipo.tipo.equals(Contexto.DATH) && (exp.v.Tipo.equals(Contexto.DATH))){
            return true;
        }else{
            Debuger.Debug("Error de tipos en declaracion " + "...",false,null); 
            return false;
        }                
    }
    public void declararVar(Simbolo tipo, Simbolo exp, Simbolo primerId, NodeListOptional listaId){

        if(!comprobarTipos(tipo, exp))
            return;
        
        Simbolo s =  new Simbolo(primerId.nombre, exp.v.Tipo, exp.v);
        this.entorno.insertar(s.nombre, s);
        for(INode_usql node : listaId.nodes){
            NodeSequence ns = (NodeSequence) node;
            NodeToken tok =  (NodeToken) ns.nodes.get(1);
            s =  new Simbolo (tok.tokenImage,exp.v.Tipo,exp.v);
            this.entorno.insertar(s.nombre, s);
        }
    }
    
    public void declararObjeto(){
        
    }
    
}
