/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLServerSocket;
import jdk.nashorn.internal.runtime.Debug;

/**
 *
 * @author mike
 */
public class Servidor {
    
    public Servidor(){
        try {
            ServerSocket serversocket =  new ServerSocket(9999);
            Debuger.Debug("Servidor encendido...", false, null);
            while (true) {                
                Socket cliente =  serversocket.accept();
                Runnable hiloCliente  = new HiloServidor("", cliente);
                Thread hilo =  new Thread(hiloCliente);
                hilo.start();                
            }
        } catch (IOException ex) {
            Debuger.Debug(ex);
        }
    }
}
