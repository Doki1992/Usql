/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ejecucion_usql;

/**
 *
 * @author mike
 */
import Analizador_xml.*;
import Analizador_usql.*;
import Entorno.*;
import arbolxml.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import proyecto.Admon_archivo;
import syntaxtree.*;
import visitor.*;
import visitorxml.*;
import proyecto.*;
import arbolxml.*;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Operaciones_aritmeticas {

    public static Valor OperacionSuma(Simbolo iz, Simbolo der) {
        if ((iz.v.Tipo.equals(Contexto.TEX) && der.v.Tipo.equals(Contexto.ENT)) || (iz.v.Tipo.equals(Contexto.ENT) && der.v.Tipo.equals(Contexto.TEX))) {
            String valor = iz.v.ACadena() + der.v.ACadena();
            iz.v = new Texto(valor, "");
        } else if ((iz.v.Tipo.equals(Contexto.TEX) && der.v.Tipo.equals(Contexto.DOB)) || (iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.TEX))) {
            String valor = iz.v.ACadena() + der.v.ACadena();
            iz.v = new Texto(valor, "");
        } else if ((iz.v.Tipo.equals(Contexto.TEX) && der.v.Tipo.equals(Contexto.BOl)) || (iz.v.Tipo.equals(Contexto.BOl) && der.v.Tipo.equals(Contexto.TEX))) {
            String valor = iz.v.ACadena() + der.v.ACadena();
            iz.v = new Texto(valor, "");
        } else if ((iz.v.Tipo.equals(Contexto.TEX) && der.v.Tipo.equals(Contexto.DATH)) || (iz.v.Tipo.equals(Contexto.DATH) && der.v.Tipo.equals(Contexto.TEX))) {
            String valor = iz.v.ACadena() + der.v.ACadena();
            iz.v = new Texto(valor, "");
        } else if ((iz.v.Tipo.equals(Contexto.TEX) && der.v.Tipo.equals(Contexto.DAT)) || (iz.v.Tipo.equals(Contexto.DAT) && der.v.Tipo.equals(Contexto.TEX))) {
            String valor = iz.v.ACadena() + der.v.ACadena();
            iz.v = new Texto(valor, "");
        } else if ((iz.v.Tipo.equals(Contexto.TEX) && der.v.Tipo.equals(Contexto.TEX))){
            String valor = iz.v.ACadena() + der.v.ACadena();
            iz.v = new Texto(valor, "");
        } else if ((iz.v.Tipo.equals(Contexto.ENT) && der.v.Tipo.equals(Contexto.DOB)) || (iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.ENT))) {
            Double valNumerico =  iz.v.ADoble()+der.v.ADoble();
            String valor = Double.toString(valNumerico);            
            iz.v = new Doble(valor, "");
        } else if ((iz.v.Tipo.equals(Contexto.ENT) && der.v.Tipo.equals(Contexto.BOl)) || (iz.v.Tipo.equals(Contexto.BOl) && der.v.Tipo.equals(Contexto.ENT))) {
            Integer valNumerico =  iz.v.AEntero()+der.v.AEntero();
            String valor = Integer.toString(valNumerico);            
            iz.v = new Entero(valor, "");
        }else if ((iz.v.Tipo.equals(Contexto.ENT) && der.v.Tipo.equals(Contexto.ENT)) ){
            Integer valNumerico =  iz.v.AEntero()+der.v.AEntero();
            String valor = Integer.toString(valNumerico);            
            iz.v = new Entero(valor, "");
        }else if ((iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.BOl)) || (iz.v.Tipo.equals(Contexto.BOl) && der.v.Tipo.equals(Contexto.DOB))) {
            Double valNumerico =  iz.v.ADoble()+der.v.ADoble();
            String valor = Double.toString(valNumerico);            
            iz.v = new Doble(valor, "");
        }else if ((iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.DOB)) ){
            Double valNumerico =  iz.v.ADoble()+der.v.ADoble();
            String valor = Double.toString(valNumerico);            
            iz.v = new Doble(valor, "");
        }else if(iz.v.Tipo.equals(Contexto.BOl) && der.v.Tipo.equals(Contexto.BOl)){
            Boolean  valBooleano = iz.v.ABool() | der.v.ABool();
            String valor =  Boolean.toString(valBooleano);
            iz.v =  new Bool(valor, "");        
        }else{
            Debuger.Debug("Error de tipos: " + iz.v.Tipo + " y " + der.v.Tipo + " no se pueden Sumar");
            iz.v = null;
        }
        return iz.v;
    }
    
    public static Valor OperacionResta(Simbolo iz, Simbolo der){
       if ((iz.v.Tipo.equals(Contexto.ENT) && der.v.Tipo.equals(Contexto.DOB)) || (iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.ENT))) {
            Double valNumerico =  iz.v.ADoble()-der.v.ADoble();
            String valor = Double.toString(valNumerico);            
            iz.v = new Doble(valor, "");
        } else if ((iz.v.Tipo.equals(Contexto.ENT) && der.v.Tipo.equals(Contexto.BOl)) || (iz.v.Tipo.equals(Contexto.BOl) && der.v.Tipo.equals(Contexto.ENT))) {
            Integer valNumerico =  iz.v.AEntero()-der.v.AEntero();
            String valor = Integer.toString(valNumerico);            
            iz.v = new Entero(valor, "");
        }else if ((iz.v.Tipo.equals(Contexto.ENT) && der.v.Tipo.equals(Contexto.ENT)) ){
            Integer valNumerico =  iz.v.AEntero()-der.v.AEntero();
            String valor = Integer.toString(valNumerico);            
            iz.v = new Entero(valor, "");
        }else if ((iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.DOB)) || (iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.DOB))) {
            Double valNumerico =  iz.v.ADoble()-der.v.ADoble();
            String valor = Double.toString(valNumerico);            
            iz.v = new Doble(valor, "");
        }else if ((iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.DOB)) ){
            Double valNumerico =  iz.v.ADoble()-der.v.ADoble();
            String valor = Double.toString(valNumerico);            
            iz.v = new Doble(valor, "");
        }else{
            Debuger.Debug("Error de tipos: " + iz.v.Tipo + " y " + der.v.Tipo + " no se pueden Restar");
            iz.v = null;
        }
        return iz.v;
    }
    
    public static Valor OperacionMultiplicacion(Simbolo iz, Simbolo der){
        if ((iz.v.Tipo.equals(Contexto.ENT) && der.v.Tipo.equals(Contexto.DOB)) || (iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.ENT))) {
            Double valNumerico =  iz.v.ADoble()*der.v.ADoble();
            String valor = Double.toString(valNumerico);            
            iz.v = new Doble(valor, "");
        } else if ((iz.v.Tipo.equals(Contexto.ENT) && der.v.Tipo.equals(Contexto.BOl)) || (iz.v.Tipo.equals(Contexto.BOl) && der.v.Tipo.equals(Contexto.ENT))) {
            Integer valNumerico =  iz.v.AEntero()*der.v.AEntero();
            String valor = Integer.toString(valNumerico);            
            iz.v = new Entero(valor, "");
        }else if ((iz.v.Tipo.equals(Contexto.ENT) && der.v.Tipo.equals(Contexto.ENT)) ){
            Integer valNumerico =  iz.v.AEntero()*der.v.AEntero();
            String valor = Integer.toString(valNumerico);            
            iz.v = new Entero(valor, "");
        }else if ((iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.BOl)) || (iz.v.Tipo.equals(Contexto.BOl) && der.v.Tipo.equals(Contexto.DOB))) {
            Double valNumerico =  iz.v.ADoble()*der.v.ADoble();
            String valor = Double.toString(valNumerico);            
            iz.v = new Doble(valor, "");
        }else if ((iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.DOB)) ){
            Double valNumerico =  iz.v.ADoble()*der.v.ADoble();
            String valor = Double.toString(valNumerico);            
            iz.v = new Doble(valor, "");
        }else if(iz.v.Tipo.equals(Contexto.BOl) && der.v.Tipo.equals(Contexto.BOl)){
            Boolean  valBooleano = iz.v.ABool() & der.v.ABool();
            String valor =  Boolean.toString(valBooleano);
            iz.v =  new Bool(valor, "");        
        }else{
            Debuger.Debug("Error de tipos: " + iz.v.Tipo + " y " + der.v.Tipo + " no se pueden Multiplicar");
            iz.v = null;
        }
        return iz.v;
    }
    
    public static Valor OperacionDivision(Simbolo iz, Simbolo der){
        if ((iz.v.Tipo.equals(Contexto.ENT) && der.v.Tipo.equals(Contexto.DOB)) || (iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.ENT))) {
            Double valNumerico =  iz.v.ADoble()/der.v.ADoble();
            String valor = Double.toString(valNumerico);            
            iz.v = new Doble(valor, "");
        } else if ((iz.v.Tipo.equals(Contexto.ENT) && der.v.Tipo.equals(Contexto.BOl)) || (iz.v.Tipo.equals(Contexto.BOl) && der.v.Tipo.equals(Contexto.ENT))) {
            Integer valNumerico =  iz.v.AEntero()/der.v.AEntero();
            String valor = Integer.toString(valNumerico);            
            iz.v = new Entero(valor, "");
        }else if ((iz.v.Tipo.equals(Contexto.ENT) && der.v.Tipo.equals(Contexto.ENT)) ){
            Integer valNumerico =  iz.v.AEntero()/der.v.AEntero();
            String valor = Integer.toString(valNumerico);            
            iz.v = new Entero(valor, "");
        }else if ((iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.DOB)) || (iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.DOB))) {
            Double valNumerico =  iz.v.ADoble()/der.v.ADoble();
            String valor = Double.toString(valNumerico);            
            iz.v = new Doble(valor, "");
        }else if ((iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.DOB)) ){
            Double valNumerico =  iz.v.ADoble()/der.v.ADoble();
            String valor = Double.toString(valNumerico);            
            iz.v = new Doble(valor, "");
        }else{
            Debuger.Debug("Error de tipos: " + iz.v.Tipo + " y " + der.v.Tipo + " no se pueden Dividir");
            iz.v = null;
        }
        return iz.v;
    }
    
    public static Valor OperacionPotencia(Simbolo iz, Simbolo der){
        if ((iz.v.Tipo.equals(Contexto.ENT) && der.v.Tipo.equals(Contexto.DOB)) || (iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.ENT))) {
            Double valNumerico =  iz.v.ADoble()/der.v.ADoble();
            String valor = Double.toString(valNumerico); 
            iz.v = new Doble(valor, "");
        } else if ((iz.v.Tipo.equals(Contexto.ENT) && der.v.Tipo.equals(Contexto.BOl)) || (iz.v.Tipo.equals(Contexto.BOl) && der.v.Tipo.equals(Contexto.ENT))) {
            Integer valNumerico =  iz.v.AEntero()/der.v.AEntero();
            String valor = Double.toString(valNumerico); 
            iz.v = new Doble(valor, "");
        }else if ((iz.v.Tipo.equals(Contexto.ENT) && der.v.Tipo.equals(Contexto.ENT)) ){
            Double valNumerico =  Math.pow(iz.v.ADoble(), der.v.ADoble());
            String valor = Double.toString(valNumerico);            
            iz.v = new Doble(valor, "");
        }else if ((iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.DOB)) || (iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.DOB))) {
            Double valNumerico =  Math.pow(iz.v.ADoble(), der.v.ADoble());
            String valor = Double.toString(valNumerico);            
            iz.v = new Doble(valor, "");
        }else if ((iz.v.Tipo.equals(Contexto.DOB) && der.v.Tipo.equals(Contexto.DOB)) ){
            Double valNumerico =  Math.pow(iz.v.ADoble(), der.v.ADoble());
            String valor = Double.toString(valNumerico);            
            iz.v = new Doble(valor, "");
        }else{
            Debuger.Debug("Error de tipos: al" + iz.v.Tipo + " y " + der.v.Tipo + " no se puede aplicar el operador de pontencia");
            iz.v = null;
        }
        return iz.v;
    }
    
    public static Valor OperacionOr(Simbolo iz, Simbolo der){
        if(iz.v.Tipo.equals(Contexto.BOl) && der.v.Tipo.equals(Contexto.BOl)){
            if(iz.v.ABool() || der.v.ABool()){
                iz.v =  new Bool("1", "");
            }else{
                iz.v =  new Bool("0", "");
            }
        }else{
            Debuger.Debug("Ambos operandos en la operacion Or deben de ser de tipo booleano...");
            iz.v = null;
        }
        return iz.v;
    }
    
    public static Valor OperacionAnd(Simbolo iz, Simbolo der){
        if(iz.v.Tipo.equals(Contexto.BOl) && der.v.Tipo.equals(Contexto.BOl)){
            if(iz.v.ABool() && der.v.ABool()){
                iz.v =  new Bool("1", "");
            }else{
                iz.v =  new Bool("0", "");
            }
        }else{
            Debuger.Debug("Ambos operandos en la operacion And deben de ser de tipo booleano...");
            iz.v = null;
        }
        return iz.v;
    }
    
    public static Valor OperacionNot(Simbolo iz){
        if(iz.v.Tipo.equals(Contexto.BOl)){
            if(iz.v.ABool()){
                iz.v =  new Bool("0", "");
            }else{
                iz.v =  new Bool("1", "");
            }
        }else{
            Debuger.Debug("La expresion debe de ser de tipo booleano para el operador not...");
            iz.v = null;
        }
        return iz.v;
    }
}
