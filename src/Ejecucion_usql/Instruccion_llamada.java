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
import static Ejecucion_usql.Instruccion_login.generarArbol;
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


public class Instruccion_llamada {
    private static final String bd_maestro = Contexto.CanonicalPath+"/Bases/maestro.bd";
    private static final String BD_USUARIO = Contexto.CanonicalPath+"/Bases/usuario.bd";
    private static final String BD_BASES = Contexto.CanonicalPath+"/Bases/bases/";
    private static final String BD_OBJ = Contexto.CanonicalPath+"/Bases/objetos/";
    private static final String BD_FUNC = Contexto.CanonicalPath+"/Bases/funciones/";
    private static final String BD_TABLA =Contexto.CanonicalPath+ "/Bases/tablas/";
    private static XmlParser parser;
    private static Admon_archivo ar = new Admon_archivo();
    private static Ent levantado;
    
    public static Simbolo ejecutarMetodo(){
        return null;
    }
}
