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
public class Instruccion_alterar {

    private static final String bd_maestro = Contexto.CanonicalPath + "/Bases/maestro.bd";
    private static final String BD_BASES = Contexto.CanonicalPath + "/Bases/bases/";
    private static final String BD_USUARIO = Contexto.CanonicalPath+"/Bases/usuario.bd";
    private static final String BD_OBJ = Contexto.CanonicalPath + "/Bases/objetos/";
    private static final String BD_FUNC = Contexto.CanonicalPath + "/Bases/funciones/";
    private static final String BD_TABLA = Contexto.CanonicalPath + "/Bases/tablas/";
    private static XmlParser parser;
    private static Admon_archivo ar = new Admon_archivo();
    private static Ent levantado;

    public static void AgregarObjeto(agregar_objeto objeto, String identificador) {
        String contenidoTexto;
        try {
            contenidoTexto = ar.LeerRegistroBd(0, Contexto.EnUso.path_obj, ar.Tamano(Contexto.EnUso.path_obj));
            parser = new XmlParser(new ByteArrayInputStream(contenidoTexto.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado;
            if (levantado.existe(identificador)) {
                Simbolo t = levantado.Buscar(identificador);
                AgregarAtributosObjeto(objeto, t);
                Objeto ob = (Objeto) t.v;
                ar.EscribirRegistroBd(ob.seek, GenerarTextoObjetoNuevo(t), Contexto.EnUso.path_obj);
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
                .append("\"\"")
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

    private static void AgregarAtributosObjeto(agregar_objeto objeto, Simbolo s) {
        DepthFirstRetVisitor_usql<Simbolo> here = new DepthFirstRetVisitor_usql<>();
        here.levantado = levantado;
        Simbolo tablaTemporal = (Simbolo) objeto.accept(here);
        Tabla temporal = (Tabla) tablaTemporal.v;
        Objeto obj = (Objeto) s.v;
        for (int i = 0; i < temporal.valores.size(); i++) {
            Simbolo s1 = temporal.valores.get(i);
            if (obj.valor.existe(s1.nombre)) {
                Debuger.Debug("El objeto " + s.nombre + " ya tiene un atributo con el nombre " + s1.nombre);
            } else {
                obj.valor.insertar(s1.nombre, s1);
            }
        }
        Debuger.Debug("Agregando nuevos atributos a " + s.nombre + "...");
    }

    public static void AgregarTabla(agregar_tabla tabla, String identificador) {
        String contenidoTexto;
        try {
            contenidoTexto = ar.LeerRegistroBd(600, Contexto.EnUso.path, ar.Tamano(Contexto.EnUso.path));
            parser = new XmlParser(new ByteArrayInputStream(contenidoTexto.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado;
            if (levantado.existe(identificador)) {
                Simbolo t = levantado.Buscar(identificador);
                AgregarAtributosTabla(tabla, t);
                Tabla t1 = (Tabla) t.v;
                ar.EscribirRegistroBd(t1.seek, GenerarTextoTablaNueva(t), Contexto.EnUso.path);
            } else {
                Debuger.Debug("Error la tabla " + identificador + " no existe...");
            }
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch (NullPointerException ex) {
            Debuger.Debug("Error no se ha seleccionado una base de datos ...");
        }
    }

    private static void AgregarAtributosTabla(agregar_tabla tabla, Simbolo s) {
        DepthFirstRetVisitor_usql<Simbolo> here = new DepthFirstRetVisitor_usql<>();
        here.levantado = levantado;
        Simbolo tablaTemporal = tabla.accept(here);
        Tabla temporal = (Tabla) tablaTemporal.v;
        Tabla tablaExistente = (Tabla) s.v;
        for (int i = 0; i < temporal.valores.size(); i++) {
            Simbolo s1 = temporal.valores.get(i);
            if (Contexto.ExisteColumna(tablaExistente.valores, s1.nombre)) {
                Debuger.Debug("La tabla " + s.nombre + " ya tiene una columna con el nombre " + s1.nombre);
            } else {
                tablaExistente.valores.add(s1);
            }
        }
        Debuger.Debug("Agregando nuevos atributos a " + s.nombre + "...");
    }

    protected static String GenerarTextoTablaNueva(Simbolo tabla) {
        StringBuilder texto = new StringBuilder();
        Tabla t = (Tabla) tabla.v;
        texto.append("<tabla>")
                .append("<nombre>")
                .append(tabla.nombre)
                .append("</nombre>")
                .append("<permiso>")
                .append("\"").append("\"")
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

    public static void QuitarTabla(quitar quit, String identificador) {
        String archivo_bd;
        try {
            archivo_bd = ar.LeerRegistroBd(600, Contexto.EnUso.path, ar.Tamano(Contexto.EnUso.path));
            parser = new XmlParser(new ByteArrayInputStream(archivo_bd.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado;
            if (levantado.existe(identificador)) {
                Simbolo tabla = levantado.Buscar(identificador);
                QuitarAtributoTabla(quit, tabla);
                Tabla t1 = (Tabla) tabla.v;
                LimpiarEspacioTabla(t1.seek);
                ar.EscribirRegistroBd(t1.seek, GenerarTextoTablaNueva(tabla), Contexto.EnUso.path);
            } else {
                Debuger.Debug("La tabla con nombre " + identificador + " no existe...", true, null);
            }
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch (NullPointerException ex) {
            Debuger.Debug("Error no se ha seleccionado una base de datos ...");
        }

    }

    public static void QuitarObjeto(quitar quit, String identificador) {
        String archivo_bd;
        try {
            archivo_bd = ar.LeerRegistroBd(0, Contexto.EnUso.path_obj, ar.Tamano(Contexto.EnUso.path_obj));
            parser = new XmlParser(new ByteArrayInputStream(archivo_bd.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado;
            if (levantado.existe(identificador)) {
                Debuger.Debug("Alterando objeto " + identificador);
                Simbolo objeto = levantado.Buscar(identificador);
                QuitarAtributoObjeto(quit, objeto);
                Objeto t1 = (Objeto) objeto.v;
                LimpiarEspacioObjeto(t1.seek);
                ar.EscribirRegistroBd(t1.seek, GenerarTextoObjetoNuevo(objeto), Contexto.EnUso.path_obj);                
            } else {
                Debuger.Debug("El objeto " +  identificador + "no existe...");
            }
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch (NullPointerException ex) {
            Debuger.Debug("Error no se ha seleccionado una base de datos ...");
        }
    }

    private static void QuitarAtributoObjeto(quitar quit, Simbolo s) {
        DepthFirstRetVisitor_usql<Simbolo> here = new DepthFirstRetVisitor_usql<>();
        here.levantado = levantado;
        Simbolo tablaTemporal = quit.accept(here);
        Tabla temporal =  (Tabla) tablaTemporal.v;
        Objeto obj =  (Objeto) s.v;
        for (int i = 0; i < temporal.valores.size(); i++) {
            Simbolo s1 = temporal.valores.get(i);
            if (obj.valor.existe(s1.nombre)) {
                obj.valor.tabla.remove(s1.nombre);
            } else {
                Debuger.Debug("El objeto " + s.nombre + " no tiene ningun atributo con nombre " + s1.nombre);
            }
        }
        Debuger.Debug("Quitando atributos a " + s.nombre + "...");

    }

    private static void QuitarAtributoTabla(quitar quit, Simbolo s) {
        DepthFirstRetVisitor_usql<Simbolo> here = new DepthFirstRetVisitor_usql<>();
        here.levantado = levantado;
        Simbolo tablaTemporal = quit.accept(here);
        Tabla temporal = (Tabla) tablaTemporal.v;
        Tabla tablaExistente = (Tabla) s.v;
        for (int i = 0; i < temporal.valores.size(); i++) {
            Simbolo s1 = temporal.valores.get(i);
            if (Contexto.ExisteColumna(tablaExistente.valores, s1.nombre)) {
                int pos = Contexto.ObtenerPosicion(tablaExistente.valores, s1.nombre);
                tablaExistente.valores.remove(pos);
            } else {
                Debuger.Debug("La tabla " + s.nombre + " no tiene ningun atributo con nombre " + s1.nombre);
            }
        }
        Debuger.Debug("Quitando atributos a " + s.nombre + "...");
    }

    private static void LimpiarEspacioTabla(long seek) throws IOException {
        for (int i = 0; i < 500; i++) {
            ar.EscribirRegistroBd(seek + i, "\0", Contexto.EnUso.path);
        }
    }
    
    private static void LimpiarEspacioObjeto(long seek) throws IOException {
        for (int i = 0; i < 500; i++) {
            ar.EscribirRegistroBd(seek + i, "\0", Contexto.EnUso.path_obj);
        }
    }

    private static void LimpiarEspacioUsuario(long seek) throws IOException{
         for (int i = 0; i < 300; i++) {
            ar.EscribirRegistroBd(seek + i, "\0", BD_USUARIO);
        }
    }
    public static void AlterarUsuario(Simbolo s){
        String archivo_bd;
        try {
            archivo_bd = ar.LeerRegistroBd(0, BD_USUARIO, ar.Tamano(BD_USUARIO));
            parser = new XmlParser(new ByteArrayInputStream(archivo_bd.getBytes(StandardCharsets.UTF_8)));
            INode init = parser.Inicio();
            DepthFirstRetVisitor<Simbolo> v = new DepthFirstRetVisitor<>();
            init.accept(v);
            levantado = v.levantado_user;
            if (levantado.existe(s.nombre)) {
                Simbolo usuarioEncontrado =  levantado.Buscar(s.nombre);
                Usuario_ent usuarioE =  (Usuario_ent) usuarioEncontrado.v;
                CambiarPasswordUsuario(usuarioEncontrado, s);
                LimpiarEspacioUsuario(usuarioE.seek);
                ar.EscribirRegistroBd(usuarioE.seek,GenerarTextoUsuarioNuevo(usuarioEncontrado), BD_USUARIO);
            } else {
                Debuger.Debug("El usuario con nombre " +  s.nombre + " no existe...");
            }
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException ex) {
            Debuger.Debug(ex);
        } catch (NullPointerException ex) {
            Debuger.Debug("Error no se ha seleccionado una base de datos ...");
        }

    }
    
    private static void CambiarPasswordUsuario (Simbolo usuarioEncontrado, Simbolo informacionUsuario){
        Usuario_ent usuarioE = (Usuario_ent) usuarioEncontrado.v;
        Usuario_ent informacion = (Usuario_ent) informacionUsuario.v;
        usuarioE.clave =  informacion.clave;
        Debuger.Debug("Cambiando clave a " + usuarioE.nombre + "...");
    }
    
    protected static String GenerarTextoUsuarioNuevo(Simbolo usuario){
        StringBuilder texto =  new StringBuilder();
        Usuario_ent u  = (Usuario_ent) usuario.v;
        String clave  =  u.clave.replace("\"", "");
        texto.append("<usuario>")                                
                .append("<nombre>")
                .append(u.nombre)
                .append("</nombre>")
                .append("<seek>")
                .append(u.seek)
                .append("</seek>")
                .append("<clave>")
                .append("").append(u.clave).append("")
                .append("</clave>")
                .append("</usuario>");        
        return texto.toString();
    }
}
