/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyecto;

import Analizador_ply.ParseException;
import Analizador_ply.PlyParser;
import Analizador_usql.ParseException_usql;
import Ejecucion_usql.Instruccion_crear;
import Ejecucion_usql.Instruccion_login;
import Ejecucion_usql.Instruccion_principal;
import Entorno.Objeto;
import Entorno.Simbolo;
import arbolply.INode;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import visitorply.DepthFirstRetVisitor;

/**
 *
 * @author mike
 */
public class HiloServidor implements Runnable {

    private Socket socket;

    public HiloServidor(String cadenaCliente, Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            String texto = obtenerPeticion();            
            Simbolo contenido = obtenerContenido(texto);
            ejecutarAccion(contenido);
            this.socket.getOutputStream().write(Contexto.PaqueteRespuesta.getBytes());
            this.socket.close();
            Contexto.PaqueteRespuesta = "";
        } catch (IOException ex) {
            Debuger.Debug(ex);
        } catch (ParseException_usql ex) {
            Debuger.Debug(ex.getMessage(),false, null);    
            Instruccion_crear.GenerarRespuestaCrear(ex.getMessage(),1);
            
        } 
    }
    
    private void ejecutarAccion(Simbolo s) throws IOException, ParseException_usql{
        Objeto obj =  (Objeto) s.v;
        Integer cual = obj.valor.Buscar("validar").v.AEntero();
        switch(cual){
            case 1500:
                Objeto login =  (Objeto) obj.valor.Buscar("login").v;
                String nombre = login.valor.Buscar("nombre").v.ACadena();
                String clave =  login.valor.Buscar("clave").v.ACadena();
                Instruccion_login.IniciarSesion(nombre, clave);
                break;
            case 1600:
                break;
            case 1800:
                Simbolo inst = obj.valor.Buscar("instruccion");
                Instruccion_principal.instruccionPrincipal(inst.v.ACadena().replace("#", "\""));
                break;
        }        
    }

    private Simbolo obtenerContenido(String texto) throws FileNotFoundException {
        PlyParser parser;
        Simbolo accept = null;
        System.out.println("Reading from standard input. . .");        
        parser = new PlyParser(new ByteArrayInputStream(texto.getBytes(StandardCharsets.UTF_8)));
        try {
            INode root = parser.Inicio();
            accept = root.accept(new DepthFirstRetVisitor<>());
            System.out.println("Parsed successfully.");
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            System.out.println("Encountered errors during parse.");
        }
        return accept;
    }

    private synchronized String obtenerPeticion() throws IOException {
        String texto1 = "";
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream()));
            String inputLine, outputLine;
            while ((inputLine = in.readLine()) != null) {
                texto1 += inputLine;
            }
            Debuger.Debug(texto1);
        } catch (IOException ex) {
            Logger.getLogger(HiloServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return texto1;
    }

}
