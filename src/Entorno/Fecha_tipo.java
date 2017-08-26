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
public class Fecha_tipo extends Valor{
    
    public String valor;
    public Fecha_tipo(String valor, String tipo){
        this.valor =  valor;
        this.Tipo =  Contexto.DAT;
    }
    @Override
    public String ACadena() {
        return valor.replace("\"", "");
    }

    @Override
    public int AEntero() {
        return 0;
    }

    @Override
    public double ADoble() {
        return 0.0;
    }

    @Override
    public boolean ABool() {
        return  false;
    }
    
}
