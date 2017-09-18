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
import java.util.HashMap;
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
    private static Tabla t;
    private static LinkedList<Simbolo> valoresInsertar =  new LinkedList<>();
    private static LinkedList<Simbolo> valoresInsertarEspecial =  new LinkedList<>();
    private static LinkedList<Integer> posiciones =  new LinkedList<>();
    private static LinkedList<Integer> posicionesQueNo =  new LinkedList<>();
    
    protected static Tabla LeerTabla(String identificador) {
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

    protected static void levantarRegistros (String path, Cuerpo_tabla cp){
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
        t = LeerTabla(n.f3.tokenImage);
        Cuerpo_tabla cp =  new Cuerpo_tabla();
        levantarRegistros(t.path.replace("\"", ""), cp);
        LinkedList<Simbolo> valores = new LinkedList<>();
        LinkedList<String> columnas = new LinkedList<>();
        if (t != null) {
            t.cuerpo =  cp;
            AgregarNulo();
            asignarListas(n, este, valores, columnas);
            if(columnas.isEmpty()){
               insertarNormal(valores);
            } else {
               obtenerValoresInsertar(columnas, t);
               iniciarValoresEspecial();
               insertarEspecial(valores,columnas);
            }
        } else {
            Debuger.Debug("Error al insertar, la tabla con nombre " + n.f3.tokenImage + " no existe...", false, null);
        }        
        posiciones.clear();
        posicionesQueNo.clear();
        valoresInsertarEspecial.clear();
    }
    
    private static void iniciarValoresEspecial(){
        for(Simbolo t: t.valores){
            valoresInsertarEspecial.add(new Simbolo(t.nombre, t.tipo, new Texto("","")));
        }
    }
    
    private static void insertarEspecial (LinkedList<Simbolo> valores, LinkedList<String> columna){
        //para los que no van solo si acepta null
        if (valores.size() == columna.size()) {

            Boolean continua = verificaTiposEspecial(valores);

            if (continua) {
                continua = aceptaNulos();
            }
            if (continua) {
                continua = valoresAuto(t.valores);
            }
            if (continua) {
                continua = verificarUnicosEspecial(valores);
            }
            if (continua) {
                continua = VerificarForaneaEspecial(valores);
            }
            if (continua) {

                asignarValoresQueVienen(valores);
                valoresInsertar = valoresInsertarEspecial;
                String r = generarTextoRegistro();
                try {
                    ar.EscribirRegistroBd(500 * t.cuerpo.registros.size(), r, t.path.replace("\"", ""));
                } catch (IOException ex) {
                    Debuger.Debug(ex);
                }
                Debuger.Debug("registro insertado correctamente", false, null);

            }
        } else {
            Debuger.Debug("los valores no coinciden con la cantidad de columnas", false, null);
        }

        valoresInsertar.clear();
        valoresInsertarEspecial.clear();
        posiciones.clear();
        posicionesQueNo.clear();
    }

    private static void asignarValoresQueVienen(LinkedList<Simbolo> valores){
        int j =0;
        for(Integer i : posiciones){
            valores.get(j).v.Tipo = valoresInsertarEspecial.get(i).tipo;
            valoresInsertarEspecial.get(i).v = valores.get(j).v;
            j++;        
        }
    }
    
    private static boolean valoresAuto(LinkedList<Simbolo> valores) {
        boolean continua = true;
        for (int i = 0; i < valores.size(); i++) {
            Simbolo a = valores.get(i);
            Columna c = (Columna) a.v;
            for (Simbolo s : c.atributos) {
                if (s.nombre.equals("auto")) {
                    continua = EsNumerico(a.tipo);
                    if (continua) {
                        Simbolo insertar = new Simbolo(a.nombre, a.tipo, null);
                        FijarValorAutoincremento(a.nombre, insertar);
                        valoresInsertarEspecial.get(i).nombre = a.nombre;
                        valoresInsertarEspecial.get(i).tipo = a.tipo;
                        valoresInsertarEspecial.get(i).v = insertar.v;
                    }
                }
            }
        }
        return continua;
    }
    
    
    
    private static Boolean aceptaNulos(){
        Boolean continuar = true;
        for(Integer i : posicionesQueNo){
          continuar = tieneNulo((Columna)t.valores.get(i).v, i, t.valores.get(i));
        }
        return continuar;
    }
    
    private static Boolean tieneNulo(Columna c, int pos, Simbolo in){
        boolean esauto = contieneAuto(c.atributos);
        boolean esprim = contienePrimaria(c.atributos);
        if(esprim && !esauto){
            Debuger.Debug("Error la llave primaria no puede ser nula", false, null);
            return false;
        }
        for(Simbolo s : c.atributos){            
            if(s.nombre.equals("nulo")){
                boolean a =  s.v.ABool();
                if(a){
                    valoresInsertarEspecial.get(pos).nombre = in.nombre;
                    valoresInsertarEspecial.get(pos).v = in.v;
                    valoresInsertarEspecial.get(pos).tipo = in.tipo;
                    return a;
                }
                
            }
        }
        return true;
    }
    
    protected static void obtenerValoresInsertar(LinkedList<String> listaColumnas, Tabla tabla) {
        int j = 0;
        for(String nombre :  listaColumnas) {
            int i = Contexto.ObtenerPosicion(tabla.valores, nombre);
            posiciones.add(i);            
        }
        for(Simbolo s : tabla.valores){
            posicionesQueNo.add(j);
            j++;
        }
        for(Integer  i : posiciones){
            if(posicionesQueNo.contains(i)){
                posicionesQueNo.remove(i);
            }
        }                
    }
    
    
    private static LinkedList<Simbolo> obtenerValoresAinsertar(insertar n, DepthFirstRetVisitor_usql este, int which, LinkedList<Simbolo> valores) {
        switch (which) {
            case 0:
                syntaxtree.NodeSequence vals1 = (syntaxtree.NodeSequence) n.f5.choice;
                syntaxtree.NodeListOptional resto1 = (syntaxtree.NodeListOptional) vals1.nodes.get(6);
                valores.add((Simbolo)((lista_expresion)vals1.nodes.get(5)).f0.accept(este));
                for(INode_usql node : resto1.nodes){
                    syntaxtree.NodeSequence ns =  (syntaxtree.NodeSequence) node;
                    valores.add((Simbolo)((lista_expresion)ns.nodes.get(1)).f0.accept(este));
                }
                return valores;
            case 1:
                syntaxtree.NodeSequence vals = (syntaxtree.NodeSequence) n.f5.choice;
                syntaxtree.NodeListOptional resto = (syntaxtree.NodeListOptional) vals.nodes.get(1);
                valores.add((Simbolo)((lista_expresion)vals.nodes.get(0)).f0.accept(este));
                for(INode_usql node : resto.nodes){
                    syntaxtree.NodeSequence ns =  (syntaxtree.NodeSequence) node;
                    valores.add((Simbolo)((lista_expresion)ns.nodes.get(1)).f0.accept(este));
                }
                return valores;
        }
        return null;
    }

    private static LinkedList<String> obtenerColumnas(insertar n, DepthFirstRetVisitor_usql este, LinkedList<String> columnas) {
        syntaxtree.NodeSequence vals1 = (syntaxtree.NodeSequence) n.f5.choice;
        syntaxtree.NodeListOptional resto = (syntaxtree.NodeListOptional) vals1.nodes.get(1);
        columnas.add(((syntaxtree.NodeToken) vals1.nodes.get(0)).tokenImage);
        for (INode_usql node : resto.nodes) {
            syntaxtree.NodeSequence ns = (syntaxtree.NodeSequence) node;
            columnas.add(((syntaxtree.NodeToken)ns.nodes.get(1)).tokenImage);
        }
        return columnas;
    }

    @SuppressWarnings("UnusedAssignment")
    private static void asignarListas(insertar n, DepthFirstRetVisitor_usql este, LinkedList<Simbolo> valores, LinkedList<String> columnas){
         switch(n.f5.which){
                case 0:
                    valores = obtenerValoresAinsertar(n, este, 0,valores);
                    columnas = obtenerColumnas(n, este, columnas);
                    break;
                case 1:
                    valores = obtenerValoresAinsertar(n, este, 1,valores);
                    break;
            }
    }

    @SuppressWarnings("UnnecessaryReturnStatement")
    private static void insertarNormal(LinkedList<Simbolo> valores){        
        boolean continuar =  false;
        if(valores.size() <= t.valores.size()){
           continuar =  RealizarVerificaciones(valores);           
           if(continuar){
               String r = generarTextoRegistro();
               try {
                   ar.EscribirRegistroBd(500*t.cuerpo.registros.size(), r, t.path.replace("\"", ""));
               } catch (IOException ex) {
                   Debuger.Debug(ex);
               }
           } else {
               valoresInsertar.clear();
               Debuger.Debug("Hay errores en la operacion de insercion en la tabla proceso fallido ...", false, null);
               return;
           }
        } else {
            Debuger.Debug("La cantidad de valores no es correcta... ", false, null);
            Debuger.Debug("No se completo el proceso de insercion... ", false, null);
            valoresInsertar.clear();
            return;
        }
        valoresInsertar.clear();
    }

    @SuppressWarnings("UnusedAssignment")
    private static Boolean RealizarVerificaciones(LinkedList<Simbolo> valores) {
        Boolean continuar = false;
        continuar = verificarValoresQueNoVienen(valores.size()); 
        if(continuar){
            continuar = VerificarTipos(valores);        
        }else{
            Debuger.Debug("Existen valores que no aceptan valores null...", false, null);
        }
        if(continuar){
            continuar = VerificarPrimaria(valores);
        }     
        if(continuar){
           continuar = VerificarForanea(valores);  
        } 
        if(continuar){
            continuar = VerificarUnicos(valores);
        }
        return continuar;
    }

    private static Boolean VerificarUnicos(LinkedList<Simbolo> valores){
        Boolean continuar =  true;
        int dif = t.valores.size() - valores.size();
         for (int i = dif; i < t.valores.size(); i++) {
            Columna c =  (Columna) t.valores.get(i).v;
            String valor =  valores.get((dif==0)?i:i-dif).v.ACadena();
            boolean esUnica =  contieneUnico(c.atributos);
            if(esUnica){
                continuar =  VerificarUnico(valor, i);
            }
         }
        return continuar;
    }
    
    private static Boolean verificarUnicosEspecial(LinkedList<Simbolo> valores){
        Boolean continuar =  true;
        for (int i = 0; i <  posiciones.size(); i++) {
            Columna c =  (Columna) t.valores.get(posiciones.get(i)).v;
            String valor =  valores.get(i).v.ACadena();
            boolean esUnica =  contieneUnico(c.atributos);
            if(esUnica){
                continuar =  VerificarUnico(valor, i);
            }
         }
        return continuar;
    }
    
    protected static Boolean VerificarUnico(String valor, int posColum){
        boolean unica = true;
        for(LinkedList<Simbolo> fila :  t.cuerpo.registros){
            Simbolo data =  fila.get(posColum);
            if(data.v.ACadena().equals(valor)){
                unica =  false;
                break;
            }
        }
        return unica;
    }
    
    protected static Boolean contieneUnico(LinkedList<Simbolo> valores){
         for(Simbolo s : valores){
            if(s.nombre.equals("unico"))
                return true;
        }
        return false;  
    }
    
    private static Boolean VerificarForanea(LinkedList<Simbolo> valores) {
        Boolean continuar = true;
        int dif = t.valores.size() - valores.size();
        for (int i = dif; i < t.valores.size(); i++) {
            Columna c =  (Columna) t.valores.get(i).v;
            String valor =  valores.get((dif==0)?i:i-dif).v.ACadena();
            boolean esForanea =  contieneForanea(c.atributos);
            if(esForanea){
                String ref =  obtenerForanea(c.atributos);
                continuar = VerificarIntegridadReferencial(valor, ref);
                if(!continuar){
                    Debuger.Debug("Violacion de integridad referencial...", false, null);
                    break;                    
                }                
            }
        }
        return continuar;
    }
    
    protected static boolean VerificarForaneaEspecial(LinkedList<Simbolo> valores) {
        Boolean continuar = true;        
        for (int i = 0; i < posiciones.size(); i++) {
            Columna c =  (Columna) t.valores.get(posiciones.get(i)).v;
            String valor =  valores.get(i).v.ACadena();
            boolean esForanea =  contieneForanea(c.atributos);
            if(esForanea){
                String ref =  obtenerForanea(c.atributos);
                continuar = VerificarIntegridadReferencial(valor, ref);
                if(!continuar){
                    Debuger.Debug("Violacion de integridad referencial...", false, null);
                    break;                    
                }                
            }
        }
        return continuar;
    }
    
    protected static Boolean VerificarIntegridadReferencial(String valor, String ref) {
        boolean existeForanea = false;
        String [] datos =  ref.split("#");
        String nombreTabla =  datos[0];
        String nombreColumna =  datos[1];
        Tabla t =  LeerTabla(nombreTabla);
        Cuerpo_tabla cp =  new Cuerpo_tabla();
        levantarRegistros(t.path.replace("\"", ""), cp);
        t.cuerpo = cp;
        int posColum =  Contexto.ObtenerPosicion(t.valores, nombreColumna);
        for (LinkedList<Simbolo> fila : t.cuerpo.registros) {
            Simbolo data = fila.get(posColum);
            if (data.v.ACadena().equals(valor)) {
                existeForanea = true;
                break;
            }
        }
        return existeForanea;
    }

    protected static Boolean contieneForanea(LinkedList<Simbolo> valores){
         for(Simbolo s : valores){
            if(s.nombre.equals("for"))
                return true;
        }
        return false;        
    }
    
     protected static String obtenerForanea(LinkedList<Simbolo> valores){
         for(Simbolo s : valores){
            if(s.nombre.equals("for"))
                return s.v.ACadena();
        }
        return "";        
    }   
    
    private static Boolean VerificarPrimaria(LinkedList<Simbolo> valores){
        Boolean continuar = true;
        int dif =  t.valores.size() - valores.size();        
        for(int i =  dif; i< t.valores.size(); i++){
            Columna c =  (Columna) t.valores.get(i).v;
            String valor =  valores.get((dif==0)?i:i-dif).v.ACadena();
            boolean esPrimaria =  contienePrimaria(c.atributos);
            if(esPrimaria){
                continuar =  VerificarUnicidad(valor, i);
                if(!continuar){
                    Debuger.Debug("Violacion de integridad de entidad la llave primaria debe de ser unica...", false, null);
                    break;
                }
                    
            }
        }
        return continuar;
    }
    
    private static boolean VerificarUnicidad(String valor, int posColum){
        boolean unica = true;
        for(LinkedList<Simbolo> fila :  t.cuerpo.registros){
            Simbolo data =  fila.get(posColum);
            if(data.v.ACadena().equals(valor)){
                unica =  false;
                break;
            }
        }
        return unica;
    }
    
    private static String nombreColumnaPrimaria(){        
        for(Simbolo s :  t.valores){
            Columna c =  (Columna) s.v;
            if(contienePrimaria(c.atributos)){
                return s.nombre;
            }
        }
        return "";
    }
    private static Boolean verificarValoresQueNoVienen(int tamVal) {
        int dif = t.valores.size() - tamVal;
        boolean omitir = true;        
        for (int i = 0; i < dif; i++) {
            Columna c = (Columna) t.valores.get(i).v;            
            Simbolo insertar = new Simbolo(t.valores.get(i).nombre, t.valores.get(i).tipo, null);
            boolean unico =  contieneUnico(c.atributos);
            boolean primaria =  contienePrimaria(c.atributos);
            for (Simbolo s : c.atributos) {
                if (s.nombre.equals("nulo")) {
                    omitir = s.v.ABool();
                    if (omitir == true && !unico && !primaria) {
                        insertar.v = new Texto("", Contexto.TEX);
                        valoresInsertar.add(insertar);                        
                    }else omitir =  false;                    
                }
                if (s.nombre.equals("auto")) {
                    if ((omitir = EsNumerico(t.valores.get(i).tipo))){                        
                        FijarValorAutoincremento(t.valores.get(i).nombre, insertar);
                        valoresInsertar.add(insertar);
                    }
                    break;
                }               
            }
            if(omitir==false)return omitir;
        }
        return omitir;
    }

    private static void FijarValorAutoincremento(String nombreColumna, Simbolo insertar) {
        int posColumna = Contexto.ObtenerPosicion(t.valores, nombreColumna);
        if (t.cuerpo.registros.isEmpty()) {
            int val = 0;
            insertar.v = new Texto(Integer.toString(val), Contexto.TEX);
        } else {
            LinkedList<Simbolo> ultimoReg = t.cuerpo.registros.getLast();
            int val = ultimoReg.get(posColumna).v.AEntero() + 1;
            insertar.v = new Texto(Integer.toString(val), Contexto.TEX);
        }

    }

    private static void AgregarNulo() {
        for (Simbolo val : t.valores) {
            Columna c = (Columna) val.v;
            if (!Contexto.TieneNulo(c.atributos)) {
                Simbolo nulo = new Simbolo("nulo", Contexto.BOl, new Bool("1", Contexto.BOl));
                c.atributos.add(nulo);
            }
        }

    }
    
    private static boolean EsNumerico(String tipo) {
        if (tipo.equals(Contexto.ENT) || tipo.equals(Contexto.DOB)) {
            return true;
        } else {
            Debuger.Debug("Error los valores autoincrementables deben de ser de tipo numerico", false, null);
            return false;
        }
    }
    
    private static boolean VerificarTipos(LinkedList<Simbolo> valores){
        int dif =  t.valores.size() - valores.size();
        Instruccion_declarar dec  = new Instruccion_declarar(null);
        boolean CoincidenTipos =  false;
        Simbolo insertar;
        for(int i =  dif; i < t.valores.size(); i++){
            Simbolo val =  t.valores.get(i);
            Simbolo aux =  new Simbolo(val.nombre, val.tipo, null);           
            Simbolo vald =  valores.get((dif==0)?i:i-dif);
            if(dec.comprobarTipos(val, vald)){
                CoincidenTipos = true; 
                insertar  =  new Simbolo (val.nombre,val.tipo,null);
                insertar.v =  dec.v;
                valoresInsertar.add(insertar);
            }else if (ComprobarTipoObjeto(aux, vald)){
                CoincidenTipos = true;
                insertar =  new Simbolo(val.nombre, val.tipo, null);
                insertar.v =  vald.v;
                valoresInsertar.add(insertar);
            }else{
                Debuger.Debug("Error de tipos al insertar...", false, null);
               return false;
            }
        }        
        return CoincidenTipos;
    }
    
    protected  static boolean verificaTiposEspecial(LinkedList<Simbolo> valores){
        Instruccion_declarar dec  = new Instruccion_declarar(null);
        boolean CoincidenTipos =  false;
        Simbolo insertar;
        for(int i =  0; i < posiciones.size(); i++){
            Simbolo val =  t.valores.get(posiciones.get(i));
            Simbolo aux =  new Simbolo(val.nombre, val.tipo, null);           
            Simbolo vald =  valores.get(i);
            if(dec.comprobarTipos(val, vald)){
                CoincidenTipos = true; 
                insertar  =  new Simbolo (val.nombre,val.tipo,null);
                insertar.v =  dec.v;
                valoresInsertar.add(insertar);
            }else if (ComprobarTipoObjeto(aux, vald)){
                CoincidenTipos = true;
                insertar =  new Simbolo(val.nombre, val.tipo, null);
                insertar.v =  vald.v;
                valoresInsertar.add(insertar);
            }else{
                Debuger.Debug("Error de tipos al insertar...", false, null);
               return false;
            }
        }        
        return CoincidenTipos;
    }
    
     protected static boolean ComprobarTipoObjeto(Simbolo iz, Simbolo exp){
        if(Contexto.EsObjeto(iz.tipo) && exp.v.Tipo.equals(Contexto.OBJ)){
            Objeto o =  (Objeto) exp.v;         
            if(iz.tipo.equals(exp.tipo)){
                Objeto nuevo =  new Objeto("");
                nuevo.valor.tabla =  new HashMap<>(o.valor.tabla);
                iz.v =  nuevo;
                return true;
            } else {
                Debuger.Debug("Error de tipos objetos no son del mismo tipo", false, null);                
                return false;
            }
        }
        return false;
    }
     
    
    
    private static Boolean contienePrimaria(LinkedList<Simbolo> valores){
         for(Simbolo s : valores){
            if(s.nombre.equals("prim"))
                return true;
        }
        return false;
    }
    
    private static boolean contieneAuto(LinkedList<Simbolo> valores){
        for(Simbolo s: valores){
            if(s.nombre.equals("auto"))
                return true;
        }
        return false;
    }
    
    protected  static String generarTextoRegistro(){
        StringBuilder texto =  new StringBuilder();
        texto.append("<rows>");
        for(Simbolo s :  valoresInsertar){
            if(Contexto.EsObjeto(s.tipo)){
                 texto.append("<")
                        .append("@"+s.nombre)
                        .append(">");
                Objeto o =  (Objeto) s.v;
                for(Simbolo ob :  o.valor.tabla.values()){
                    texto.append("<")
                        .append("@"+ob.nombre)
                        .append(">")
                        .append("\"")
                        .append(ob.v.ACadena())
                        .append("\"")
                        .append("</")
                        .append("@"+ob.nombre)
                        .append(">");
                }
                texto.append("</")
                        .append("@"+s.nombre)
                        .append(">");
            } else {
                texto.append("<")
                        .append("@"+s.nombre)
                        .append(">")
                        .append("\"")
                        .append(s.v.ACadena())
                        .append("\"")
                        .append("</")
                        .append("@"+s.nombre)
                        .append(">");
            }
        }
        valoresInsertar.clear();
        texto.append("</rows>");
        return texto.toString();
    }
}
