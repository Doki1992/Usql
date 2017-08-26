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
public class Usuario_ent extends Valor{
    public String nombre;
    public String clave;
    public int seek;
    
    public Usuario_ent(String nombre, String clave){
        this.nombre = nombre;
        this.clave =  clave;
        this.Tipo  = "Usuario";
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
