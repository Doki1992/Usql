/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entorno;

/**
 *
 * @author mike
 */
public class AccesoObjTab extends Valor{
    public String valor ;
    public boolean esta = false;
    public AccesoObjTab(String valor, boolean esta) {
        this.valor = valor;
        this.Tipo =  "Acceso";
        this.esta =  esta;
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
