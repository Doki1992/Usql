/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ejecucion_usql;

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

/**
 *
 * @author mike
 */
public class Instruccion_crear {

    private static final String bd_maestro = Contexto.CanonicalPath+"/Bases/maestro.bd";
    private static final String BD_USUARIO = Contexto.CanonicalPath+"/Bases/usuario.bd";
    private static final String BD_BASES = Contexto.CanonicalPath+"/Bases/bases/";
    private static final String BD_OBJ = Contexto.CanonicalPath+"/Bases/objetos/";
    private static final String BD_FUNC = Contexto.CanonicalPath+"/Bases/funciones/";
    private static final String BD_TABLA =Contexto.CanonicalPath+ "/Bases/tablas/";
    private static XmlParser parser;
    private static Admon_archivo ar = new Admon_archivo();
    private static Ent levantado;

    public static void CrearBase(crear_base base) {
        try {
            String archivo_maestro = ar.LeerRegistroBd(0, bd_maestro, ar.Tamano(bd_maestro));
            parser = new XmlParser(new ByteArrayInputStream(archivo_maestro.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado;
            if (!levantado.existe(base.f1.tokenImage)) {
                ar.EscribirRegistroBd(300 * levantado.tabla.size(), GeneraTextoBd(base), bd_maestro);
                GenerarArchivoBd(base.f1.tokenImage);
                GenerarArchivoFun(base.f1.tokenImage);
                GenerarArchivoObj(base.f1.tokenImage);
            } else {
                Debuger.Debug("Error la base con nombre " + base.f1.tokenImage + " ya existe.");
            }
        } catch (IOException ex) {
            Debuger.Debug(ex.getMessage());
        } catch (ParseException ex) {
            Debuger.Debug(ex.getMessage());
        }
    }

    public static String GeneraTextoBd(crear_base base) throws IOException {
        StringBuilder texto = new StringBuilder();
        texto.append("<db>").append("<seek>").append(300 * levantado.tabla.size())
                .append("</seek>")
                .append("<nombre>").append(base.f1.tokenImage)
                .append("</nombre>")
                .append("<path>").append("\"").append(BD_BASES)
                .append(base.f1.tokenImage)
                .append(".bd").append("\"")
                .append("</path>")
                .append("</db>");
        Debuger.Debug("Escribiendo registro bd ...");
        return texto.toString();
    }

    public static void GenerarArchivoFun(String nombrebd) throws IOException {
        ar.EscribirRegistroBd(0, "", BD_FUNC + nombrebd + "_func.bd");
        Debuger.Debug("Escribiendo archivo de funciones ...");
    }

    public static void GenerarArchivoObj(String nombrebd) throws IOException {
        ar.EscribirRegistroBd(0, "", BD_OBJ + nombrebd + "_obj.bd");
        Debuger.Debug("Escribiendo archivo de objetos ...");
    }

    public static void GenerarArchivoBd(String nombrebd) throws IOException {
        StringBuilder proc = new StringBuilder();
        StringBuilder obj = new StringBuilder();
        proc.append("<procedure>")
                .append("<path>")
                .append("\"" + BD_FUNC + nombrebd + "_func.bd" + "\"")
                .append("</path>")
                .append("</procedure>");
        ar.EscribirRegistroBd(0, proc.toString(), BD_BASES + nombrebd + ".bd");
        obj.append("<object>")
                .append("<path>")
                .append("\"" + BD_OBJ + nombrebd + "_obj.bd" + "\"")
                .append("</path>")
                .append("</object>");
        ar.EscribirRegistroBd(300, obj.toString(), BD_BASES + nombrebd + ".bd");
        Debuger.Debug("Escribiendo archivo de base ...");
    }

    public static void GenerarArchivoTabla(String nombreTabla) throws IOException {
        ar.EscribirRegistroBd(0, "", nombreTabla);
        Debuger.Debug("Escribiendo archivo de Tabla ...");
    }

    public static void CrearTabla(crear_tabla tabla) {
        try {
            String archivo_bd = ar.LeerRegistroBd(0, Contexto.EnUso.path, ar.Tamano(Contexto.EnUso.path));
            parser = new XmlParser(new ByteArrayInputStream(archivo_bd.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado;
            if (!levantado.existe(tabla.f1.tokenImage)) {
                ar.EscribirRegistroBd(600 + 500 * levantado.tabla.size(), CrearTextoTabla(tabla), BD_BASES + Contexto.EnUso.nombre + ".bd");
                GenerarArchivoTabla(BD_TABLA + Contexto.EnUso.nombre +"_"+ tabla.f1.tokenImage + ".bd");
            } else {
                Debuger.Debug("Error la tabla " + tabla.f1.tokenImage + " ya exite");
            }
        } catch (IOException ex) {
            Debuger.Debug(ex.getMessage());
        } catch (ParseException ex) {
            Debuger.Debug(ex.getMessage());
        } catch (NullPointerException ex) {
            Debuger.Debug("Error no se ha seleccionado una base de datos ...");
        }
    }

    public static String CrearTextoTabla(crear_tabla tabla) {
        StringBuilder texto = new StringBuilder();
        try {
            DepthFirstRetVisitor_usql<Simbolo> here = new DepthFirstRetVisitor_usql<>();
            here.levantado = levantado;
            Simbolo t = tabla.accept(here);
            texto.append("<tabla>")
                    .append("<nombre>")
                    .append(t.nombre)
                    .append("</nombre>")
                    .append("<permiso>")
                    .append("\"").append("\"")
                    .append("</permiso>")
                    .append("<path>")
                    .append("\"")
                    .append(BD_TABLA + Contexto.EnUso.nombre + "_" + t.nombre + ".bd")
                    .append("\"")
                    .append("</path>")
                    .append("<rows>");
            Tabla t1 = (Tabla) t.v;
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
            texto.append(600 + 500 * levantado.tabla.size());
            texto.append("</seek>");
            texto.append("</tabla>");
            Debuger.Debug("Generando texto de tabla...");
        } catch (NullPointerException ex) {
            Debuger.Debug("No se creo la tabla");
            return "";
        }
        return texto.toString();
    }

    public static void CrearObjeto(crear_objeto objeto) {
        String archivo_bd;
        try {
            archivo_bd = ar.LeerRegistroBd(0, Contexto.EnUso.path_obj, ar.Tamano(Contexto.EnUso.path_obj));
            parser = new XmlParser(new ByteArrayInputStream(archivo_bd.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado;
            if (!levantado.existe(objeto.f1.tokenImage)) {
                ar.EscribirRegistroBd(500 * levantado.tabla.size(), CrearTextoObjeto(objeto), Contexto.EnUso.path_obj);
                Debuger.Debug("Escribiendo objeto " + objeto.f1.tokenImage);
            } else {
                Debuger.Debug("Error el objeto " + objeto.f1.tokenImage + " ya exite");
            }
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch (NullPointerException ex) {
            Debuger.Debug("Error no se ha seleccionado ninguna BD...");
        }

    }

    public static String CrearTextoObjeto(crear_objeto objeto) {
        StringBuilder texto = new StringBuilder();
        DepthFirstRetVisitor_usql<Simbolo> here = new DepthFirstRetVisitor_usql<>();
        Simbolo obj = objeto.accept(here);
        texto.append("<obj>")
                .append("<seek>")
                .append(500 * levantado.tabla.size())
                .append("</seek>")
                .append("<nombre>")
                .append(obj.nombre)
                .append("</nombre>")
                .append("<permiso>")
                .append("\"\"")
                .append("</permiso>")
                .append("<atr>");
        Objeto bbb = (Objeto) obj.v;
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

    public static void CrearFunProc(crear_funcion funcion) {
        String archivo_bd;
        try {
            archivo_bd = ar.LeerRegistroBd(0, Contexto.EnUso.path_proc, ar.Tamano(Contexto.EnUso.path_proc));
            parser = new XmlParser(new ByteArrayInputStream(archivo_bd.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado;
            if(!levantado.existe(funcion.f1.tokenImage)){
                ar.EscribirRegistroBd(10000 * levantado.tabla.size(), CrearTextoFuncion(funcion), Contexto.EnUso.path_proc);
            }else{
                Debuger.Debug("Error la funcion con nombre " + funcion.f1.tokenImage + " ya existe...");
            }
        } catch (IOException | ParseException ex) {
            Debuger.Debug(ex);
        } catch(NullPointerException ex){
            Debuger.Debug("Error no se ha seleccionado ninguna BD...");            
        }
    }

    public static void CrearFunProc(crear_procedimiento procedimiento){
        String archivo_bd;
        try {
            archivo_bd = ar.LeerRegistroBd(0, Contexto.EnUso.path_proc, ar.Tamano(Contexto.EnUso.path_proc));
            parser = new XmlParser(new ByteArrayInputStream(archivo_bd.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado;
            if(!levantado.existe(procedimiento.f1.tokenImage)){
                ar.EscribirRegistroBd(10000 * levantado.tabla.size(), CrearTextoProcedimiento(procedimiento), Contexto.EnUso.path_proc);
            }else{
                Debuger.Debug("Error el procedimiento con nombre " + procedimiento.f1.tokenImage + " ya existe...");
            }
        } catch (IOException | ParseException ex) {
            Debuger.Debug(ex);
        } catch(NullPointerException ex){
            Debuger.Debug("Error no se ha seleccionado ninguna BD...");            
        }
    }
    public static String CrearTextoFuncion(crear_funcion funcion){
        StringBuilder texto =  new StringBuilder();
        DepthFirstRetVisitor_usql<Simbolo> here = new DepthFirstRetVisitor_usql<>();        
        Simbolo func =  funcion.accept(here);
        FuncionProc f =  (FuncionProc) func.v;
        texto.append("<proc>")
                .append("<seek>")
                .append(10000 * levantado.tabla.size())
                .append("</seek>")
                .append("<nombre>")
                .append(func.nombre)
                .append("</nombre>")
                .append("<permiso>")
                .append(f.permisos)
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
                .append(f.src)
                .append("\"")
                .append("</src>")
                .append("</proc>");
        Debuger.Debug("Escribiendo texto de funcion " + func.nombre);
        return texto.toString();
    }
    
    public static String CrearTextoProcedimiento(crear_procedimiento procedimiento){
        StringBuilder texto =  new StringBuilder();
        DepthFirstRetVisitor_usql<Simbolo> here = new DepthFirstRetVisitor_usql<>();        
        Simbolo func =  procedimiento.accept(here);
        FuncionProc f =  (FuncionProc) func.v;
        texto.append("<proc>")
                .append("<seek>")
                .append(10000 * levantado.tabla.size())
                .append("</seek>")
                .append("<nombre>")
                .append(func.nombre)
                .append("</nombre>")
                .append("<permiso>")
                .append(f.permisos)
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
                .append(f.src)
                .append("\"")
                .append("</src>")
                .append("</proc>");
        Debuger.Debug("Escribiendo texto de funcion " + func.nombre);
        return texto.toString();
    }
    public static int levantado_tabla = 0;
    public static void CrearUsuario(crear_usuario usuario){
        String archivo_bd;
        try {
            archivo_bd = ar.LeerRegistroBd(0, BD_USUARIO, ar.Tamano(BD_USUARIO));
            parser = new XmlParser(new ByteArrayInputStream(archivo_bd.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado_user;            
            if (!levantado.existe(usuario.f1.tokenImage)) {
                ar.EscribirRegistroBd(300 * (levantado.tabla.size()), GenerarTextoUsuario(usuario), BD_USUARIO);
                Debuger.Debug("Creando usuario " +usuario.f1.tokenImage);
            }else {
                Debuger.Debug("Error el usuario " + usuario.f1.tokenImage + " ya existe...");
            }
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch(NullPointerException ex){
            Debuger.Debug("Error no se ha seleccionado ninguna BD...");            
        }                    
    }
    
    public static String GenerarTextoUsuario(crear_usuario usuario){
        StringBuilder texto =  new StringBuilder();
        DepthFirstRetVisitor_usql<Simbolo> here = new DepthFirstRetVisitor_usql<>();        
        Simbolo user =  usuario.accept(here);
        Usuario_ent u  = (Usuario_ent) user.v;
        texto.append("<usuario>")                                
                .append("<nombre>")
                .append(u.nombre)
                .append("</nombre>")
                .append("<seek>")
                .append(300*(levantado.tabla.size()+levantado_tabla))
                .append("</seek>")
                .append("<clave>")
                .append("").append(u.clave).append("")
                .append("</clave>")
                .append("</usuario>");        
        return texto.toString();
    }
    
    public static void CrearDirectorioTabla(String nombre_directorio) {
        File file = new File(BD_BASES + Contexto.EnUso.nombre + "_" + nombre_directorio);
        file.mkdir();
    }
}
