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
public class Doble extends Valor{
    public String valor;
    public Doble(String valor, String tipo){
        this.Tipo =  Contexto.DOB;
        this.valor =  valor;
    }
    @Override
    public String ACadena() {
    
        return valor;
    }

    @Override
    public int AEntero() {
        return 0;
    }

    @Override
    public double ADoble() {
        return Double.parseDouble(valor);
    }

    @Override
    public boolean ABool() {
        if(valor.equals("1.0")){
            return true;
        }
        return false;
    }
    
}
