/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mike
 */
public class HiloServidor implements Runnable {

    private Socket socket;
    public  HiloServidor(String cadenaCliente, Socket socket){
        this.socket =  socket;
    }
    
    @Override
    public void run() {        
        try {
            byte bytes [] =  new byte[this.socket.getReceiveBufferSize()];
            this.socket.getInputStream().read(bytes);
            String texto =  new String(bytes);
            Debuger.Debug(texto);            
            this.socket.close();            
        } catch (IOException ex) {
            Debuger.Debug(ex);
        }
    }
    
}
