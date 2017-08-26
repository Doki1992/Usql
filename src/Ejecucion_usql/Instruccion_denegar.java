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


public class Instruccion_denegar {
    private static final String bd_maestro = Contexto.CanonicalPath + "/Bases/maestro.bd";
    private static final String BD_BASES = Contexto.CanonicalPath + "/Bases/bases/";
    private static final String BD_USUARIO = Contexto.CanonicalPath + "/Bases/usuario.bd";
    private static final String BD_OBJ = Contexto.CanonicalPath + "/Bases/objetos/";
    private static final String BD_FUNC = Contexto.CanonicalPath + "/Bases/funciones/";
    private static final String BD_TABLA = Contexto.CanonicalPath + "/Bases/tablas/";
    private static XmlParser parser;
    private static Admon_archivo ar = new Admon_archivo();
    private static Ent levantado;
    private static Bd BdDenegar;
    
    public static void DenegarPermisos(Simbolo otorgar) {
        Tabla cont =  (Tabla) otorgar.v;
        FijarBase(cont.valores.get(0).nombre);
        RecogerObjetosUsql(cont);
    }
    
     private static void RecogerObjetosUsql(Tabla cont) {
        String archivoLeer;
        try {
            archivoLeer = ar.LeerRegistroBd(600, BdDenegar.path, ar.Tamano(BdDenegar.path));
            archivoLeer += ar.LeerRegistroBd(0, BdDenegar.path_obj, ar.Tamano(BdDenegar.path_obj));
            archivoLeer += ar.LeerRegistroBd(0, BdDenegar.path_proc, ar.Tamano(BdDenegar.path_proc));
            parser = new XmlParser(new ByteArrayInputStream(archivoLeer.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado;
            agregarPermisos(cont);
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch (NullPointerException ex) {
            Debuger.Debug("Que extraño que entre a este catch...;-(");
        }
    }
    
    private static void FijarBase(String id) {
        String texto;
        try {
            texto = ar.LeerRegistroBd(0, bd_maestro, ar.Tamano(bd_maestro));
            parser = new XmlParser(new ByteArrayInputStream(texto.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado;
            if(levantado.existe(id)){
                BdDenegar =  (Bd) levantado.Buscar(id).v;
                BdDenegar.path =  BdDenegar.path.replace("\"", "");
                BdDenegar.path_obj =  BD_OBJ + id + "_obj.bd";
                BdDenegar.path_proc =  BD_FUNC + id + "_func.bd";
            }else {
                Debuger.Debug("Error no existe la bd con nombre " + id);
            }
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch(NullPointerException ex){
            Debuger.Debug(ex);
        }
    }
    
    private static void agregarPermisos(Tabla cont) throws IOException{
        Simbolo user =  cont.valores.get(1);
        Simbolo objeto =  cont.valores.get(2);
        LinkedList<Simbolo> objetosUsql =  new LinkedList<>();
        objetosUsql.addAll(levantado.tabla.values());
        for(Simbolo s : objetosUsql){
            if(objeto.nombre.equals("*")){
                EscribirTexto(s, user.nombre);
                Debuger.Debug("Quitando permisos a objeto usql " + s.nombre + "...");
            }else if(objeto.nombre.equals(s.nombre)){
                EscribirTexto(s, user.nombre);
                Debuger.Debug("Quitando permisos a objeto usql " + s.nombre + "...");
            }else{
//                Debuger.Debug("No exite ningun objeto uslq con el nombre " + objeto.nombre + "...");
//                break;
            }
        }
    }
    
    private static void EscribirTexto(Simbolo s,String quien) throws IOException{
        switch(s.v.Tipo){
            case Contexto.OBJ:
                Objeto obj =  (Objeto) s.v;
                LimpiarEspacioObjeto(obj.seek);
                obj.Permisos=obj.Permisos .replace(quien + ".","");
                GenerarTextoObjetoNuevo(s);
                ar.EscribirRegistroBd(obj.seek,GenerarTextoObjetoNuevo(s),BdDenegar.path_obj.replace("\"", ""));
                break;
            case Contexto.USUARIO:                
                break;
            case Contexto.DB:
                Bd db = (Bd) s.v;
                break;
            case Contexto.FP:
                FuncionProc fp =  (FuncionProc) s.v;
                LimpiarEspacioFp(fp.seek);
                fp.permisos=fp.permisos .replace(quien + ".","");                
                ar.EscribirRegistroBd(fp.seek, CrearTextoFuncion(s),BdDenegar.path_proc.replace("\"", ""));
                break;
            case Contexto.TB:
                Tabla t  =  (Tabla) s.v;
                LimpiarEspacioTabla(t.seek);
                t.permisos=t.permisos .replace(quien + ".","");
                ar.EscribirRegistroBd(t.seek,GenerarTextoTablaNueva(s),BdDenegar.path.replace("\"", ""));                 
                break;
        }
    }
    
    
     private static void LimpiarEspacioObjeto(long seek) throws IOException {
        for (int i = 0; i < 500; i++) {
            ar.EscribirRegistroBd(seek + i, "\0", BdDenegar.path_obj);
        }
    }
    
    
    private static void LimpiarEspacioTabla(long seek) throws IOException {
        for (int i = 0; i < 500; i++) {
            ar.EscribirRegistroBd(seek + i, "\0", BdDenegar.path);
        }
    }
    
    private static void LimpiarEspacioFp(long seek) throws IOException {
        for (int i = 0; i < 10000; i++) {
            ar.EscribirRegistroBd(seek + i, "\0", BdDenegar.path_proc);
        }
    }
    
    public static String CrearTextoFuncion(Simbolo s){
        StringBuilder texto =  new StringBuilder();        
        FuncionProc f =  (FuncionProc) s.v;
        texto.append("<proc>")
                .append("<seek>")
                .append(10000 * levantado.tabla.size())
                .append("</seek>")
                .append("<nombre>")
                .append(s.nombre)
                .append("</nombre>")
                .append("<permiso>")
                .append("\"")
                .append(f.permisos.replace("\"", ""))
                .append("\"")
                .append("</permiso>")
                .append("<atr>");
        for(int i = 0; i < f.parametros.size(); i++){
            Simbolo p =  f.parametros.get(i);
            texto.append("<")
                    .append(p.tipo)
                    .append(">")
                    .append(p.nombre)
                    .append("</")
                    .append(p.tipo)
                    .append(">");                   
        }
        texto.append("</atr>")
                .append("<src>")
                .append("\"")
                .append(f.src.replace("\"", ""))
                .append("\"")
                .append("</src>")
                .append("</proc>");
        Debuger.Debug("Escribiendo texto de funcion " + s.nombre);
        return texto.toString();
    }
    
    protected static String GenerarTextoTablaNueva(Simbolo tabla) {
        StringBuilder texto = new StringBuilder();
        Tabla t = (Tabla) tabla.v;
        texto.append("<tabla>")
                .append("<nombre>")
                .append(tabla.nombre)
                .append("</nombre>")
                .append("<permiso>")
                .append("\"")
                .append(t.permisos.replace("\"", ""))
                .append("\"")
                .append("</permiso>")
                .append("<path>")
                .append("\"")
                .append(t.path.replace("\"", ""))
                .append("\"")
                .append("</path>")
                .append("<rows>");
        Tabla t1 = (Tabla) tabla.v;
        for (int i = 0; i < t1.valores.size(); i++) {
            texto.append("<")
                    .append(t1.valores.get(i).tipo)
                    .append(">")
                    .append(t1.valores.get(i).nombre);
            Columna co = (Columna) t1.valores.get(i).v;
            for (int j = 0; j < co.atributos.size(); j++) {
                texto.append("<")
                        .append(co.atributos.get(j).nombre)
                        .append(">")
                        .append(co.atributos.get(j).v.ACadena())
                        .append("</")
                        .append(co.atributos.get(j).nombre)
                        .append(">");
            }
            texto.append("</")
                    .append(t1.valores.get(i).tipo)
                    .append(">");
        }
        texto.append("</rows>");
        texto.append("<seek>");
        texto.append(t.seek);
        texto.append("</seek>");
        texto.append("</tabla>");
        Debuger.Debug("Generando texto de tabla " + tabla.nombre + "...");
        return texto.toString();
    }
    
    protected static String GenerarTextoObjetoNuevo(Simbolo s) {
        StringBuilder texto = new StringBuilder();
        Objeto bbb = (Objeto) s.v;
        texto.append("<obj>")
                .append("<seek>")
                .append(bbb.seek)
                .append("</seek>")
                .append("<nombre>")
                .append(s.nombre)
                .append("</nombre>")
                .append("<permiso>")
                .append("\"")
                .append(bbb.Permisos.replace("\"", ""))
                .append("\"")
                .append("</permiso>")
                .append("<atr>");
        LinkedList<Simbolo> values = new LinkedList<>();
        values.addAll(bbb.valor.tabla.values());
        for (int i = 0; i < values.size(); i++) {
            texto.append("<")
                    .append(values.get(i).tipo)
                    .append(">")
                    .append(values.get(i).nombre)
                    .append("</")
                    .append(values.get(i).tipo)
                    .append(">");
        }
        texto.append("</atr>");
        texto.append("</obj>");
        return texto.toString();

    }
}
