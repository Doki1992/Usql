/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entorno;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map.Entry;
import proyecto.Debuger;

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
        if(this.tabla.containsKey(id)){
            Debuger.Debug("Se ha detectado un estado de ambiguedad entre un objeto usql y el nombre de una variable...", false, null);
            Debuger.Debug("Esposible que exista un objeto usql con el mismo nombre que la variable " + id + "...",false, null);
            Debuger.Debug("Por favor verifique el nombre de las variables declaradas y asegurese de que no tengan el nombre de ningun objero usql...",false,null);
            ListarObjetosUsql();
            
        }else{
            this.tabla.put(id, s);
        }               
    }
    
    public boolean existe(String id){
        return (this.tabla.get(id)!=null);
    }
    
    private void ListarObjetosUsql(){
        Debuger.Debug("Listado de objetos usql...");
        for(Entry<String, Simbolo> s: this.tabla.entrySet() ){            
            Debuger.Debug(s.getKey() + "...");
        }
    }
}
