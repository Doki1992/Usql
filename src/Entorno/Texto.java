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
public class Texto extends  Valor{
    public String valor;
    public Texto(String valor, String tipo){
        this.Tipo =  Contexto.TEX;
        this.valor = valor.replace("\"", "");
    }
    @Override
    public String ACadena() {
        return valor;
    }

    @Override
    public int AEntero() {
        try {
            return Integer.parseInt(valor);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public double ADoble() {
        try {
            return Double.parseDouble(valor);
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public boolean ABool() {
        if(valor.equals("1")){
            return true;
        }else {
            return false;
        }
    }
    
}
