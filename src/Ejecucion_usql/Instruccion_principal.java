/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Ejecucion_usql;

import Analizador_usql.ParseException_usql;
import Analizador_usql.UsqlParser;
import Entorno.Simbolo;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import proyecto.Contexto;
import syntaxtree.INode_usql;
import visitor.DepthFirstRetVisitor_usql;

/**
 *
 * @author mike
 */
public class Instruccion_principal {

    /**
     *
     * @param instruccion
     * @throws IOException
     * @throws ParseException_usql
     */
    public synchronized static void instruccionPrincipal(String instruccion) throws IOException, ParseException_usql {
        Contexto.ObtenerPathCononico();
        UsqlParser a = new UsqlParser(new ByteArrayInputStream(instruccion.getBytes(StandardCharsets.UTF_8)));
        INode_usql init = a.Inicio();
        DepthFirstRetVisitor_usql<Simbolo> v = new DepthFirstRetVisitor_usql<>();
        v.BackUpDump = init;
        init.accept(v);
    }
}
