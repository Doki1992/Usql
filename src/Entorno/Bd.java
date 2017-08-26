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
public class Bd extends Valor{
    public int seek;
    public String nombre;
    public String path;
    public String path_proc;
    public String path_obj;
    
    public Bd(int seek, String nombre, String path, String tipo){
        this.seek =  seek;
        this.nombre =  nombre;
        this.path =  path;
        this.Tipo =  "BD";
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
