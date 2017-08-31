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
public class Objeto extends Valor{

    public Ent valor;
    public String Permisos;
    public int seek;
    
    public Objeto(String tipo){
        this.Tipo = tipo;
        valor =  new Ent(null);
        this.Tipo = "OBJ";
    }
    
    @Override
    public String ACadena() {
        return "";
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
        return false;
    }
    
}
