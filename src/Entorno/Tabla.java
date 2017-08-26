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
public class Tabla extends Valor{
    
     public Ent columnas_actuales;
     public LinkedList<Simbolo> valores;
     public Cuerpo_tabla cuerpo;
     public String path;
     public String permisos;
     public String referencias;
     public int seek=0;
     public Tabla(String tipo){
        this.Tipo = tipo;
        columnas_actuales =  new Ent(null);
        cuerpo =  new Cuerpo_tabla();
        valores =  new LinkedList<>();
        this.Tipo = "TB";
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
