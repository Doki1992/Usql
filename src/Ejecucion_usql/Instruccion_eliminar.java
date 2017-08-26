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
public class Instruccion_eliminar {
    private static final String bd_maestro = Contexto.CanonicalPath + "/Bases/maestro.bd";
    private static final String BD_BASES = Contexto.CanonicalPath + "/Bases/bases/";
    private static final String BD_USUARIO = Contexto.CanonicalPath+"/Bases/usuario.bd";
    private static final String BD_OBJ = Contexto.CanonicalPath + "/Bases/objetos/";
    private static final String BD_FUNC = Contexto.CanonicalPath + "/Bases/funciones/";
    private static final String BD_TABLA = Contexto.CanonicalPath + "/Bases/tablas/";
    private static XmlParser parser;
    private static Admon_archivo ar = new Admon_archivo();
    private static Ent levantado;
    
    public static void EliminarTabla(Simbolo tablaEliminar){
        String contenidoTexto;
        try {
            contenidoTexto = ar.LeerRegistroBd(0, Contexto.EnUso.path, ar.Tamano(Contexto.EnUso.path));
            parser = new XmlParser(new ByteArrayInputStream(contenidoTexto.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado;
            if (levantado.existe(tablaEliminar.nombre)) {
                Simbolo s  = levantado.Buscar(tablaEliminar.nombre);
                Tabla val = (Tabla) s.v;                 
                levantado.tabla.remove(tablaEliminar.nombre);
                CorrerRegistroTabla();
                File file =  new File(val.path.replace("\"", ""));
                file.delete();
                Debuger.Debug("Eliminando la tabla " + s.nombre + "...");
            } else {
                Debuger.Debug("Error la tabla " + tablaEliminar.nombre + " no existe...");
            }
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch (NullPointerException ex) {
            Debuger.Debug("Error no se ha seleccionado una base de datos ...");
        }
    }
    private static void LimpiarEspacioTabla(int size) throws IOException {
        for (int i = 0; i < size; i++) {
            ar.EscribirRegistroBd(600 + i, "\0", Contexto.EnUso.path);
        }
        Debuger.Debug("Liberando espacio de tabla  en archivo BD...");
    }
    
    private static void LimpiarEspacioObjeto(int size) throws IOException {
        for (int i = 0; i < size; i++) {
            ar.EscribirRegistroBd(i, "\0", Contexto.EnUso.path_obj);
        }
        Debuger.Debug("Liberando espacio de objeto  en archivo obj...");
    }

    private static void LimpiarEspacioUsuario(int  size) throws IOException{
         for (int i = 0; i < size; i++) {
            ar.EscribirRegistroBd(i, "\0", BD_USUARIO);
        }
         Debuger.Debug("Liberando espacio de usuario  en archivo de usuarios...");
    }
    
    private static void CorrerRegistroTabla() throws IOException{
        LinkedList<Simbolo> tablas =  new LinkedList<>(levantado.tabla.values());
        LinkedList<Integer> seeks  =  GenerarSeekTablas(tablas);
        LimpiarEspacioTabla((seeks.size()+1)*500);
        int cont = 0;
        for(Simbolo s : tablas){
            Tabla actual =  (Tabla) s.v;            
            actual.seek =  seeks.get(cont);
            ar.EscribirRegistroBd(actual.seek, Instruccion_alterar.GenerarTextoTablaNueva(s),Contexto.EnUso.path );
            cont++;
        }
        Debuger.Debug("Corriendo registros de tablas en la BD...");
    }
   
    private static LinkedList<Integer> GenerarSeekTablas(LinkedList<Simbolo> tablas){
        LinkedList<Integer> seeks = new LinkedList<>();
        int cont = 0;
        for(Simbolo s: tablas){           
            seeks.add(600+500*cont);
            cont++;
        }
        return seeks;
    }
    
    public static void EliminarUsuario(Simbolo s){
        String contenidoTexto;
        try {
            contenidoTexto = ar.LeerRegistroBd(0, BD_USUARIO, ar.Tamano(BD_USUARIO));
            parser = new XmlParser(new ByteArrayInputStream(contenidoTexto.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado_user;
            if (levantado.existe(s.nombre)) {
                Simbolo s1  = levantado.Buscar(s.nombre);
                Usuario_ent val = (Usuario_ent) s1.v;                 
                levantado.tabla.remove(s1.nombre);
                CorrerRegistroUsuario();
                Debuger.Debug("Eliminando usuario " + s.nombre);
            } else {
                Debuger.Debug("Error el usuario " + s.nombre + " no existe...");
            }
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch (NullPointerException ex) {
            Debuger.Debug("Error no se ha seleccionado una base de datos ...");
        }
    }
    
    public static void EliminarBd(Simbolo bdEliminar){
        String contenidoTexto;
        try {
            contenidoTexto = ar.LeerRegistroBd(0, bd_maestro, ar.Tamano(bd_maestro));
            parser = new XmlParser(new ByteArrayInputStream(contenidoTexto.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado;
            if (levantado.existe(bdEliminar.nombre)) {
                Simbolo s  = levantado.Buscar(bdEliminar.nombre);
                Bd val = (Bd) s.v;                 
                levantado.tabla.remove(bdEliminar.nombre);
                CorrerRegistroBd();
                File file =  new File(val.path.replace("\"", ""));
                file.delete();
                Debuger.Debug("Eliminando la bd " + s.nombre + "...");
                String path_func = BD_FUNC + s.nombre+"_"+"func.bd";
                file = new File(path_func);
                file.delete();
                Debuger.Debug("Eliminando archivo de funciones " +  "...");
                String path_obj = BD_OBJ + s.nombre+"_"+"obj.bd";
                file = new File(path_obj);
                file.delete();
                Debuger.Debug("Eliminando archivo de objetos " +  "...");
            } else {
                Debuger.Debug("Error la bd " + bdEliminar.nombre + " no existe...");
            }
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch (NullPointerException ex) {
            Debuger.Debug("Error no se ha seleccionado una base de datos ...");
        }
    }
    private static void CorrerRegistroUsuario() throws IOException{
        LinkedList<Simbolo> usuarios =  new LinkedList<>(levantado.tabla.values());
        LinkedList<Integer> seeks  =  GenerarSeekUsuario(usuarios);
        LimpiarEspacioUsuario((seeks.size()+1)*300);
        Debuger.Debug("Corriendo registros de usuarios en archivo de usuarios...");
        int cont = 0;
        for(Simbolo s : usuarios){
            Usuario_ent actual =  (Usuario_ent) s.v;            
            actual.seek =  seeks.get(cont);
            ar.EscribirRegistroBd(actual.seek, Instruccion_alterar.GenerarTextoUsuarioNuevo(s),BD_USUARIO);
            cont++;
        }
    }
    
    private static LinkedList<Integer> GenerarSeekUsuario(LinkedList<Simbolo> usuarios){
         LinkedList<Integer> seeks = new LinkedList<>();
        int cont = 0;
        for(Simbolo s: usuarios){            
            seeks.add(300*cont);
            cont++;
        }
        return seeks;
    }
    private static void CorrerRegistroBd() throws IOException{
        LinkedList<Simbolo> bds =  new LinkedList<>(levantado.tabla.values());
        LinkedList<Integer> seeks  =  GenerarSeekBd(bds);
        LimpiarEspacioBd((seeks.size()+1)*300);
        Debuger.Debug("Corriendo registros de tablas en la BD...");
        int cont = 0;
        for(Simbolo s : bds){
            Bd actual =  (Bd) s.v;            
            actual.seek =  seeks.get(cont);
            ar.EscribirRegistroBd(actual.seek, GeneraTextoBd(s),bd_maestro );
            cont++;
        }        
    }
    protected static String GeneraTextoBd(Simbolo base) throws IOException {
        StringBuilder texto = new StringBuilder();
        Bd val =  (Bd) base.v;
        texto.append("<db>").append("<seek>").append(val.seek)
                .append("</seek>")
                .append("<nombre>").append(base.nombre)
                .append("</nombre>")
                .append("<path>").append("\"").append(BD_BASES)
                .append(base.nombre)
                .append(".bd").append("\"")
                .append("</path>")
                .append("</db>");
        Debuger.Debug("Escribiendo registro bd ...");
        return texto.toString();
    }
    
    private static void LimpiarEspacioBd(int size) throws IOException{
        for (int i = 0; i < size; i++) {
            ar.EscribirRegistroBd(i, "\0", bd_maestro);
        }
        Debuger.Debug("Liberando espacio de bd  en archivo maestro...");
    }
    
    private static LinkedList<Integer> GenerarSeekBd(LinkedList<Simbolo> bds){
        LinkedList<Integer> seeks = new LinkedList<>();
        int cont = 0;
        for(Simbolo s: bds){            
            seeks.add(300*cont);
            cont++;
        }
        return seeks;
    }
    
    
    public static void EliminarObjeto(Simbolo objeto){
         String contenidoTexto;
        try {
            contenidoTexto = ar.LeerRegistroBd(0, Contexto.EnUso.path_obj, ar.Tamano(Contexto.EnUso.path_obj));
            parser = new XmlParser(new ByteArrayInputStream(contenidoTexto.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado;
            if (levantado.existe(objeto.nombre)) {
                Simbolo s  = levantado.Buscar(objeto.nombre);
                Objeto val = (Objeto) s.v;                 
                levantado.tabla.remove(objeto.nombre);
                CorrerRegistroObjeto();                
                Debuger.Debug("Eliminando objeto " + s.nombre + "...");
            } else {
                Debuger.Debug("Error objeto " + objeto.nombre + " no existe...");
            }
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch (NullPointerException ex) {
            Debuger.Debug("Error no se ha seleccionado una base de datos ...");
        }
    }
    
    private static void CorrerRegistroObjeto() throws IOException{
        LinkedList<Simbolo> objetos =  new LinkedList<>(levantado.tabla.values());
        LinkedList<Integer> seeks  =  GenerarSeekObjetos(objetos);
        LimpiarEspacioObjeto((seeks.size()+1)*500);
        int cont = 0;
        for(Simbolo s : objetos){
            Objeto actual =  (Objeto) s.v;            
            actual.seek =  seeks.get(cont);
            ar.EscribirRegistroBd(actual.seek, Instruccion_alterar.GenerarTextoObjetoNuevo(s),Contexto.EnUso.path_obj );
            cont++;
        }
        Debuger.Debug("Corriendo registros de objetos en el archivo de objetos...");
    }
    
    private static LinkedList<Integer> GenerarSeekObjetos(LinkedList<Simbolo> objetos){
         LinkedList<Integer> seeks = new LinkedList<>();
        int cont = 0;
        for(Simbolo s: objetos){           
            seeks.add(500*cont);
            cont++;
        }
        return seeks;
    }
}
