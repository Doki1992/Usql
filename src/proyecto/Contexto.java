/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto;

import Entorno.Bd;
import Entorno.Ent;
import Entorno.Objeto;
import Entorno.Simbolo;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

/**
 *
 * @author mike
 */
public class Contexto {
    public  static Bd EnUso = null;//new Bd(0, "@MIKE", BD_BASES + "@MIKE.bd", "");
    public static final int ENTERO=0;
    public static final int BOOL=1;
    public static final int TEXTO=2;
    public static final int DOBLE=3;
    public static final int DATE=4;
    public static final int DATEHORA=5;
    public static final int BD=-10;
    public static final int TABLA=-20;
    
    public static final String ENT = "int";
    public static final String BOl = "bool";
    public static final String DOB = "doble";
    public static final String TEX = "texto";
    public static final String DAT = "date";
    public static final String DATH = "dateh";
    public static final String DB = "BD";
    public static final String COL = "COL";
    public static final String CUE = "CUE";    
    public static final String FP = "FP";
    public static final String OBJ = "OBJ";
    public static final String TB = "TB";
    public static final String ACCESO = "Acceso";
    public static final String USUARIO = "Usuario";
    public static final String RETORNO = "retorno";    
    public static final String DETENER = "detener";
    private static final String BD_BACKUP = "" + "/Bases/backup/";
    public static boolean Backup = false;
    public static String contenidoBitacora = "";     
    private static final File FILE =  new File(".");
    public static  String CanonicalPath;
    
    public static void LimpiarBackUp(String path, int size, Admon_archivo ar) throws IOException{
        for(int i  = 0; i< size; i++){
            ar.EscribirRegistroBd(size, " ",path);
        }
    }
    public static void EscribirBdDump(String nuevo) throws IOException{
        String path_bd_back =  FILE.getCanonicalPath()+BD_BACKUP+EnUso.nombre+".dump";
        Admon_archivo ar =  new Admon_archivo();
        String viejo = ar.LeerRegistroBd(0, path_bd_back, ar.Tamano(path_bd_back));
        LimpiarBackUp(path_bd_back, ar.Tamano(path_bd_back), ar);
        viejo += nuevo;        
        ar.EscribirRegistroBd(0, viejo, path_bd_back);        
    }
    
    public static void ObtenerPathCononico() throws IOException{
         CanonicalPath = FILE.getCanonicalPath();
    }
    public static int GeneraTipo(String tipo){
        int t = 0;
        for (int c =  0 ; c <  tipo.length(); c++){
            t += tipo.charAt(c)*6;            
        }
        t = t/2;
        return t;
    }
    
    
    public static String GetTipo(String tipo){
        if(tipo.equalsIgnoreCase(TEX)){
            return TEX;
        }else if(tipo.equalsIgnoreCase(ENT)){
            return ENT;            
        }else if(tipo.equals(BOl)){
            return BOl;
        }else if(tipo.equalsIgnoreCase(DOB)){
            return DOB;
        }else if(tipo.equalsIgnoreCase(DAT)){
            return DAT;
        }else if(tipo.equalsIgnoreCase( DATH)){
            return DATH;
        }else{
            return tipo;
        }
    }
    public static boolean ExisteColumna(LinkedList<Simbolo> valores, String columna){
        return valores.stream().anyMatch((s) -> (s.nombre.equals(columna)));
    }
    
    public static int  ObtenerPosicion(LinkedList<Simbolo> valores, String columna){
        int i = 0;
        for(Simbolo s: valores){
            if(s.nombre.equals(columna)){
                return i;
            }
            i++;
        }
        return 0;
    }
    
    public static boolean TieneNulo(LinkedList<Simbolo> valores){
        for(Simbolo s :  valores){
            if(s.nombre.equals("nulo")){
                return true;
            }
        }
        return false;
    }
    
    public static boolean EsObjeto(String tipo){
        if(ENT.equals(tipo)){
            return false;
        }else if(DOB.equals(tipo)){
            return false;
        }else if(TEX.equals(tipo)){
            return false;
        }else if(BOl.equals(tipo)){
            return false;
        }else if(DAT.equals(tipo)){
            return false;
        }else if(DATH.equals(tipo)){
            return false;
        }else{
            return true;
        }
    }

    public static Simbolo DevolverSegunTipoObjetoUsql(Simbolo padre, String h, String p, Ent global) {
        Simbolo iz =  new Simbolo("temp", "", null);
        iz = (padre ==  null)?global.Buscar(p + "." + h):padre;
        if (padre != null) {
            switch (padre.v.Tipo) {
                case OBJ:
                    Objeto ob1 = (Objeto) padre.v;
                    Simbolo hijo = ob1.valor.Buscar(h);
                    if (hijo != null) {
                        iz.v = hijo.v;
                        iz.tipo = hijo.tipo;
                    } else {
                        Debuger.Debug("El objeto con nombre " + p + " no tiene ningun atributo con nombre " + h + "...", false, null);
                    }
                    break;
                case TB:
                    iz = global.Buscar(padre.nombre);
                    break;
            }
        } else {
            Debuger.Debug("El objeto con nombre " + p + " no existe...", false, null);
        }
        return iz;
    }

}
