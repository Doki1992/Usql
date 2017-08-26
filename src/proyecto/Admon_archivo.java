/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto;
import java.io.*;
/**
 *
 * @author mike
 */
public class Admon_archivo {
    

    public Admon_archivo() {        
    }
   
    
    public void EscribirRegistroBd(long seek, String cadena,String path) throws FileNotFoundException, IOException{
        RandomAccessFile  file = NuevoFile(path);
        file.seek(seek);
        file.write(cadena.getBytes());                      
    }
    
    public String LeerRegistroBd(long seek,String path,int cuanto) throws FileNotFoundException, IOException{
        String ret;
        RandomAccessFile file = NuevoFile(path);
        file.seek(seek);
        byte b [] = new byte[(int)file.length()];
        int cuantos = (file.length()<cuanto)?(int)file.length():cuanto;
        file.read(b, 0, cuantos);
        LimpiarVector(b);
        ret =  new String(b);        
        return ret;
    }
    
    private void LimpiarVector(byte [] array){
        for(int c = 0; c<array.length; c++){
            if(array[c] == 0){
                array[c] = ' ';
            }
        }
    }
    
    public int Tamano (String path) throws FileNotFoundException, IOException{
        return (int)new RandomAccessFile(path, "rw").length();
    }
    
    
    private RandomAccessFile NuevoFile(String path) throws FileNotFoundException{
        return new RandomAccessFile(path, "rw");
    }
}
