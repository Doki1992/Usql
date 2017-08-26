/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entorno;

import java.util.HashMap;
import java.util.Hashtable;

/**
 *
 * @author mike
 */
public class Ent {
    public Ent ant;
    public HashMap<String, Simbolo> tabla;
    public Ent(Ent ant){
        this.ant =  ant;
        tabla =  new HashMap<>();
    }
    
    public Simbolo Buscar(String id){
        for(Ent a = this; a!= null; a = a.ant ){
            Simbolo s = a.tabla.get(id);
            if(s!=null){
                return s;
            }
        }
        return null;
    }
    
    public void insertar(String id, Simbolo s){
        this.tabla.put(id, s);
    }
    
    public boolean existe(String id){
        return (this.tabla.get(id)!=null);
    }
}
