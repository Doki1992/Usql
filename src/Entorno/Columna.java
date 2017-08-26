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
public class Columna extends Valor{
    public LinkedList<Simbolo> atributos;
    public String valor;
    public Columna(String valor, String tipo){
        this.atributos =  new LinkedList<>();
        this.Tipo =  (tipo.equals(""))?"COL":tipo;
        this.valor = valor;              
    }
     @Override
    public String ACadena() {   
        return valor;
    }

    @Override
    public int AEntero(){
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
