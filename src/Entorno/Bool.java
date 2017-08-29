/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entorno;

import proyecto.Contexto;
import proyecto.Debuger;

/**
 *
 * @author mike
 */
public class Bool extends Valor{
    public String valor;
    public Bool(String valor, String tipo){
        this.valor =  valor;
        this.Tipo =  Contexto.BOl;
    }
    
    @Override
    public String ACadena() {
        Debuger.Debug("ESTE ES EL VALOR DE VAL EN BOOL " + valor + ".....");
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
