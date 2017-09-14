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
import static Ejecucion_usql.Instruccion_crear.CrearTextoObjeto;
import static Ejecucion_usql.Instruccion_crear.CrearTextoTabla;
import static Ejecucion_usql.Instruccion_crear.GeneraTextoBd;
import static Ejecucion_usql.Instruccion_crear.GenerarArchivoBd;
import static Ejecucion_usql.Instruccion_crear.GenerarArchivoFun;
import static Ejecucion_usql.Instruccion_crear.GenerarArchivoObj;
import static Ejecucion_usql.Instruccion_crear.GenerarArchivoTabla;
import static Ejecucion_usql.Instruccion_crear.GenerarTextoUsuario;
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

public class Instruccion_login {
    private static final String bd_maestro = Contexto.CanonicalPath+"/Bases/maestro.bd";
    private static final String BD_USUARIO = Contexto.CanonicalPath+"/Bases/usuario.bd";
    private static final String BD_BASES = Contexto.CanonicalPath+"/Bases/bases/";
    private static final String BD_OBJ = Contexto.CanonicalPath+"/Bases/objetos/";
    private static final String BD_FUNC = Contexto.CanonicalPath+"/Bases/funciones/";
    private static final String BD_TABLA =Contexto.CanonicalPath+ "/Bases/tablas/";
    private static XmlParser parser;
    private static Admon_archivo ar = new Admon_archivo();
    private static Ent levantado;
    private static String nombreActual = "";
    
    
    public static void IniciarSesion(String usuario, String clave){
        String archivo_bd;
        try {
            archivo_bd = ar.LeerRegistroBd(0, BD_USUARIO, ar.Tamano(BD_USUARIO));
            parser = new XmlParser(new ByteArrayInputStream(archivo_bd.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado_user;            
            if (levantado.existe(usuario)) {
                Contexto.UsuarioEnUso = (Usuario_ent) levantado.Buscar(usuario).v;
                if(!Contexto.UsuarioEnUso.clave.replace("\"", "").equals(clave)){
                    Contexto.UsuarioEnUso =  null;                   
                    Acciones("Clave Erronea...", 1);
                }else {                   
                    Acciones("Sesion iniciada con exito...", 2);
                }
            }else if(usuario.equals("admin")){
                Contexto.UsuarioEnUso =  new Usuario_ent(usuario, clave);            
            }else {                
                Acciones("Error el usuario con nombre " + usuario +  " no existe", 0);
            }
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch(NullPointerException ex){
            Debuger.Debug("Error no se ha seleccionado ninguna BD...");            
        }
    }
    
    private static void generaPaqueteRespuesta(String respuesta, int status){
        StringBuilder texto =  new StringBuilder();
        switch(status){
            case 0:
                texto.append("[")
                        .append("\"")
                        .append("validar")
                        .append("\"")
                        .append(":")
                        .append(1600)
                        .append(",")
                        .append("\"")
                        .append("error")
                        .append("\"")
                        .append(":")
                        .append("\"")
                        .append(respuesta)
                        .append("\"")
                        .append("]");
                Contexto.PaqueteRespuesta =  texto.toString();
                break;
            case 1:
                texto.append("[")
                        .append("\"")
                        .append("validar")
                        .append("\"")
                        .append(":")
                        .append(1600)
                        .append(",")
                        .append("\"")
                        .append("error")
                        .append("\"")
                        .append(":")
                        .append("\"")
                        .append(respuesta)
                        .append("\"")
                        .append("]");
                Contexto.PaqueteRespuesta =  texto.toString();
                break;
            case 2:
                texto.append("[")
                        .append("\"")
                        .append("validar")
                        .append("\"")
                        .append(":")
                        .append(1700)
                        .append(",")
                        .append("\"")
                        .append("ok")
                        .append("\"")
                        .append(":")
                        .append("\"")
                        .append(respuesta)
                        .append("\"")
                        .append(",");
                texto.append("\"").append("arbol").append("\"");
                generarArbol(texto);  
                texto.append("]");
                Debuger.Debug(texto.toString());
                Contexto.PaqueteRespuesta =  texto.toString();
                break;
        }
    }
    
    protected  static void generarArbol(StringBuilder texto){
        texto.append(":\"{");
        texto.append("|Bases| : [");        
        Ent bases =  levantarBases();
        int size =  bases.tabla.values().size();
        int penultimo = 1;
        for(Simbolo s :  bases.tabla.values()){
            Bd actual = (Bd) s.v; 
            nombreActual = actual.nombre;
            texto.append("{")
                    .append("|")
                    .append(s.nombre.replace("\"", ""))
                    .append("|")
                    .append(":")
                    .append("{")
                    .append("|name| :")
                    .append("|")
                    .append(s.nombre.replace("\"", ""))
                    .append("|,")
                    .append("|objetos|:")
                    .append("[");
            generarEstructuraObjetos(texto,BD_OBJ.replace("\"", "") + actual.nombre + "_obj.bd");
            texto.append("]");
            texto.append(",");
            texto.append("|tablas| :")
                    .append("[");
            generarEstructuraTablas(texto, actual.path.replace("\"", ""));
            texto.append("]");
            texto.append(",");
            texto.append("|funciones| :")
                    .append("[");
            generarEstructuraFunciones(texto, BD_FUNC.replace("\"", "") + actual.nombre + "_func.bd");
            texto.append("]");
            texto.append("}");
            texto.append("}");
            if(penultimo<size){
                texto.append(",");
            }
            penultimo++;
        }
        texto.append("]");
        generarEstructuraUsuaruio(texto);
        texto.append("}\"");
    }
    
    protected static void generarEstructuraTablas(StringBuilder texto,String path_bases){
        Ent tablas =  levantarTablas(path_bases);
        int size =  tablas.tabla.values().size();
        int penultimo = 1;       
        for(Simbolo s : tablas.tabla.values()){
            texto.append("{")
                    .append("|")
                    .append("name")
                    .append("|")
                    .append(":|")
                    .append(s.nombre.replace("\"", ""))
                    .append("|")
                    .append(",|type| :")
                    .append("|table|,")
                    .append("|padre| :")
                    .append("|")
                    .append(nombreActual)
                    .append("|}");
            if(penultimo<size){
                texto.append(",");
            }   
            penultimo++;
        }        
    }
    
    protected static void generarEstructuraObjetos(StringBuilder texto,String path_objetos){
        Ent objetos =  levantarObjetos(path_objetos);
        int size =  objetos.tabla.values().size();
        int penultimo = 1;      
        for(Simbolo s : objetos.tabla.values()){
            texto.append("{")
                    .append("|")
                    .append("name")
                    .append("|")
                    .append(":|")
                    .append(s.nombre.replace("\"", ""))
                    .append("|")
                    .append(",|type| :")
                    .append("|object|,")
                    .append("|padre| :")
                    .append("|")
                    .append(nombreActual)
                    .append("|}");
            if(penultimo<size){
                texto.append(",");
            } 
            penultimo++;
        }
    }
    
    protected static void generarEstructuraUsuaruio(StringBuilder texto){
        Ent usuarios =  levantarUsuarios(); 
        int size =  usuarios.tabla.values().size();
        int penultimo = 1;
        texto.append(", |usuarios| :")
                .append("[");
        for(Simbolo s :  usuarios.tabla.values()){
            texto.append("{")
                    .append("|name| : |")
                    .append(s.nombre)
                    .append("|")
                    .append(",|type| :")
                    .append("|user|,")
                    .append("|padre| :")
                    .append("|")
                    .append(nombreActual)
                    .append("|")
                    .append("}");
            if(penultimo<size){
                texto.append(",");
            }
            penultimo++;
        }
        texto.append("]");
    }
    
    protected static void generarEstructuraFunciones(StringBuilder texto,String path_funciones){
        Ent funciones =  levantarTablas(path_funciones);
        int size =  funciones.tabla.values().size();
        int penultimo = 1;         
        for(Simbolo s : funciones.tabla.values()){
             texto.append("{")
                    .append("|")
                    .append("name")
                    .append("|")
                    .append(":|")
                    .append(s.nombre.replace("\"", ""))
                    .append("|")
                    .append(",|type| :")
                    .append("|fuction|,")
                    .append("|padre| :")
                    .append("|")
                    .append(nombreActual)
                    .append("|}");
            if(penultimo<size){
                texto.append(",");
            } 
            penultimo++;
        }
    }
    
    protected static Ent levantarBases(){
         Ent local =  new Ent(null);
         try {
            String archivo_maestro = ar.LeerRegistroBd(0, bd_maestro, ar.Tamano(bd_maestro));
            parser = new XmlParser(new ByteArrayInputStream(archivo_maestro.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            local = v.levantado;
           
        } catch (IOException ex) {
            Debuger.Debug(ex.getMessage());
        } catch (ParseException ex) {
            Debuger.Debug(ex.getMessage());
        }
        return local;
    }
    
    protected static Ent levantarTablas(String path){
        Ent local =  new Ent(null);
        try {
            String archivo_bd = ar.LeerRegistroBd(0, path, ar.Tamano(path));
            parser = new XmlParser(new ByteArrayInputStream(archivo_bd.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            local = v.levantado;            
        } catch (IOException ex) {
            Debuger.Debug(ex.getMessage());
        } catch (ParseException ex) {
            Debuger.Debug(ex.getMessage());
        } catch (NullPointerException ex) {
            Debuger.Debug("Error no se ha seleccionado una base de datos ...");
        }
        return local;
    }
    
    protected static Ent levantarObjetos(String path){
        Ent local =  new Ent(null);
        String archivo_bd;
        try {
            archivo_bd = ar.LeerRegistroBd(0, path, ar.Tamano(path));
            parser = new XmlParser(new ByteArrayInputStream(archivo_bd.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado;            
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch (NullPointerException ex) {
            Debuger.Debug("Error no se ha seleccionado ninguna BD...");
        }
        return local;
    }
    
    protected static Ent levantarFunciones(String path){
        Ent local =  new Ent(null);
        String archivo_bd;
        try {
            archivo_bd = ar.LeerRegistroBd(0, path, ar.Tamano(path));
            parser = new XmlParser(new ByteArrayInputStream(archivo_bd.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            local = v.levantado;            
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch (NullPointerException ex) {
            Debuger.Debug("Error no se ha seleccionado ninguna BD...");
        }
        return local;
    }
    
    protected static Ent levantarUsuarios(){
        Ent local =  new Ent(null);
        String archivo_bd;
        try {
            archivo_bd = ar.LeerRegistroBd(0, BD_USUARIO, ar.Tamano(BD_USUARIO));
            parser = new XmlParser(new ByteArrayInputStream(archivo_bd.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            local = v.levantado_user;            
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch (NullPointerException ex) {
            Debuger.Debug("Error no se ha seleccionado ninguna BD...");
        }
        return local;
    }
    
    private static void Acciones(String respuesta, int status){
        Debuger.Debug(respuesta,false,null);
        generaPaqueteRespuesta(respuesta, status);
    }
}
