/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entorno;

import java.util.LinkedList;
import syntaxtree.*;
/**
 *
 * @author mike
 */
public class FuncionProc extends Valor{
    public String permisos;
    public LinkedList<Simbolo> parametros;
    public NodeListOptional sentencias;
    public Simbolo ValorRetorono;
    public String src ;
    public int seek;
    public FuncionProc(NodeListOptional sentencias){
        parametros =  new LinkedList<>();
        this.sentencias =  sentencias;
        this.Tipo = "FP";
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
