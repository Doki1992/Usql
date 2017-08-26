/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto;

import Entorno.Bd;
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
    public static final String FP = "FP";
    public static final String OBJ = "OBJ";
    public static final String TB = "TB";
    public static final String USUARIO = "Usuario";
    
    private static final File FILE =  new File(".");
    public static  String CanonicalPath;
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
        for(Simbolo s: valores){
            if(s.nombre.equals(columna)){
                return true;
            }
        }
        return false;
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
    
    
}
