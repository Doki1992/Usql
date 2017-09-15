/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto;

import Entorno.Simbolo;
import java.util.LinkedList;

/**
 *
 * @author mike
 */
public class Debuger {
    public static void Debug(Object x){
        System.out.println(x);
    }
    
    public static LinkedList<Object> Impresiones =  new LinkedList<>();
    
    public static void Debug(Object x, boolean debug, Simbolo s){
        if(debug){
            Debug(x);
        }else{
            Debug((x==null?s.v.ACadena():x));
            Impresiones.add((x==null?s.v.ACadena():x));
           // imprimir();
        }
    }
    
    public static void imprimir(){
        Uiservidor.consolaServidor.setText("");
        String contenidoPasado = "";
        for(Object o : Impresiones){            
             Uiservidor.consolaServidor.setText(contenidoPasado + o.toString());
             contenidoPasado = Uiservidor.consolaServidor.getText() + "\n";
        }
    }
}
