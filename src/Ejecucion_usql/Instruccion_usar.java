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

public class Instruccion_usar {

    private static final String bd_maestro = Contexto.CanonicalPath+"/Bases/maestro.bd";
    private static final String BD_BASES = Contexto.CanonicalPath+"/Bases/bases/";
    private static final String BD_OBJ = Contexto.CanonicalPath+"/Bases/objetos/";
    private static final String BD_FUNC = Contexto.CanonicalPath+"/Bases/funciones/";
    private static final String BD_TABLA =Contexto.CanonicalPath+ "/Bases/tablas/";
    private static XmlParser parser;
    private static Admon_archivo ar = new Admon_archivo();
    private static Ent levantado;

    public static void UsarBase(String NombreBase) {
        try {
            LevantarArchivoMaestro();
            if (levantado.existe(NombreBase)) {
                Simbolo EnUso = levantado.Buscar(NombreBase);
                Bd bd = (Bd) EnUso.v;
                bd.Tipo =  bd.Tipo.replace("\"", "");
                bd.path =  bd.path.replace("\"", "");
                bd.nombre =  bd.nombre.replace("\"", "");
                Simbolo pathProc =  RecuperarProc(bd);
                Simbolo pathObject = RecuperarObje(bd);
                bd.path_obj = pathObject.v.ACadena();
                bd.path_proc =  pathProc.v.ACadena();
                Contexto.EnUso = bd;
                Instruccion_crear.GenerarRespuestaCrear("se ha seleccionado la BD" + NombreBase, 3);
                Debuger.Debug("se ha seleccionado la BD" + NombreBase, false, null);
            } else {
                Debuger.Debug("Error la base de datos con nombre " + NombreBase + " no existe...",false, null);
                Instruccion_crear.GenerarRespuestaCrear("Error la base de datos con nombre " + NombreBase + " no existe...", 1);
            }
        } catch (IOException ex) {
            Debuger.Debug(ex.getMessage());
        } catch (ParseException ex) {
            Debuger.Debug(ex.getMessage(),false, null);
            Instruccion_crear.GenerarRespuestaCrear(ex.getMessage(), 1);
        }

    }

    public static void LevantarArchivoMaestro() throws ParseException, IOException {
        String archivo_maestro = ar.LeerRegistroBd(0, bd_maestro, ar.Tamano(bd_maestro));
        parser = new XmlParser(new ByteArrayInputStream(archivo_maestro.getBytes(StandardCharsets.UTF_8)));
        INode init = parser.Inicio();
        DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
        init.accept(v);
        levantado = v.levantado;
    }

    public static Simbolo RecuperarProc(Bd bd) throws IOException, ParseException {
        String ArchivoProc = ar.LeerRegistroBd(0, bd.path.replace("\"", ""), 300);
        parser = new XmlParser(new ByteArrayInputStream(ArchivoProc.getBytes(StandardCharsets.UTF_8)));
        INode init = parser.proc();
        DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
        return init.accept(v);
    }
    
     public static Simbolo RecuperarObje(Bd bd) throws IOException, ParseException {
        String ArchivoProc = ar.LeerRegistroBd(300, bd.path.replace("\"", ""), 300);
        parser = new XmlParser(new ByteArrayInputStream(ArchivoProc.getBytes(StandardCharsets.UTF_8)));
        INode init = parser.obj();
        DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
        return init.accept(v);
    }
     
     

}
