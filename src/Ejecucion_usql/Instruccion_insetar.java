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

public class Instruccion_insetar {

    private static final String bd_maestro = Contexto.CanonicalPath + "/Bases/maestro.bd";
    private static final String BD_BASES = Contexto.CanonicalPath + "/Bases/bases/";
    private static final String BD_USUARIO = Contexto.CanonicalPath + "/Bases/usuario.bd";
    private static final String BD_OBJ = Contexto.CanonicalPath + "/Bases/objetos/";
    private static final String BD_FUNC = Contexto.CanonicalPath + "/Bases/funciones/";
    private static final String BD_TABLA = Contexto.CanonicalPath + "/Bases/tablas/";
    private static XmlParser parser;
    private static Admon_archivo ar = new Admon_archivo();
    private static Ent levantado;

    private static Tabla LeerTabla(String identificador) {
        String contenidoTexto;
        try {
            contenidoTexto = ar.LeerRegistroBd(0, Contexto.EnUso.path, ar.Tamano(Contexto.EnUso.path));
            parser = new XmlParser(new ByteArrayInputStream(contenidoTexto.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado;
            if (levantado.existe(identificador)) {
                return ((Tabla) levantado.Buscar(identificador).v);
            } else {
                Debuger.Debug("Error el objeto " + identificador + " no existe...");
            }
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch (NullPointerException ex) {
            Debuger.Debug("Error no se ha seleccionado una base de datos ...");
        }
        return null;
    }
    
    private static void levantarRegistros (String path, Cuerpo_tabla cp){
        String contenidoTexto;
        try {
            contenidoTexto = ar.LeerRegistroBd(0, path, ar.Tamano(path));
            parser = new XmlParser(new ByteArrayInputStream(contenidoTexto.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            v.SetCuerpoTabla(cp);
            init.accept(v);            
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch (NullPointerException ex) {
            Debuger.Debug("Error no se ha seleccionado una base de datos ...");
        }
        
        
    }

    public static void InsertarRegistro(insertar n, DepthFirstRetVisitor_usql este) {
        Tabla t = LeerTabla(n.f3.tokenImage);
        Cuerpo_tabla cp =  new Cuerpo_tabla(); 
        levantarRegistros(t.path.replace("\"", ""), cp);
        if (t != null) {
            t.cuerpo =  cp;
        } else {
            Debuger.Debug("Error al insertar, la tabla con nombre " + n.f3.tokenImage + " no existe...", false, null);
        }
    }
}
