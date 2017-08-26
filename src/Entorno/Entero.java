/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entorno;

import proyecto.Contexto;

/**
 *
 * @author mike
 */
public class Entero extends Valor{
    
    public String valor;
    public Entero(String valor, String tipo){
        this.Tipo = Contexto.ENT;
        this.valor = valor;
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
        if(valor.equals("1")){
            return true;
        }else if(valor.equals("0")){
            return false;
        }
        return false;
    }
}
