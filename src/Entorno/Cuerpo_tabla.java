/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entorno;

import java.util.LinkedList;

/**
 *
 * @author mike
 */
public class Cuerpo_tabla extends Valor{
    public LinkedList<LinkedList<Simbolo>> registros;
    String valor = "";
    
    
    public Cuerpo_tabla(){
        registros = new LinkedList<>();                
        this.Tipo = "CUE";
    }
    
    public LinkedList<Simbolo> CrearRegistro(){
        return null;
    }
    
    
    
     @Override
    public String ACadena() {        
        return valor;    
    }

    @Override
    public int AEntero() {
        return Integer.parseInt(valor);
    }

    @Override
    public double ADoble() {
        return Double.parseDouble(valor);
    }

    @Override
    public boolean ABool() {
        if(valor.equals("1") || valor.equals("true")){
            return true;
        }
        return false;
    }
    
}
