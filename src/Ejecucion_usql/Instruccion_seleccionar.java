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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;

public class Instruccion_seleccionar {

    private static String ordeneraPor = "";
    private static boolean esDecendente = true;
    private static boolean esTodo = false;
    private static LinkedList<String> listaColumnas = new LinkedList<>();
    private static LinkedList<String> listaNombreTablas = new LinkedList<>();
    private static LinkedList<Simbolo> listaTablas = new LinkedList<>();
    private static exp_logica exp = null;
    private static DepthFirstRetVisitor_usql este = null;
    private static Cuerpo_tabla producto_cartesiano = new Cuerpo_tabla();
    private static Ent valoresTabla;
    private static Cuerpo_tabla resultado = new Cuerpo_tabla();
    private static LinkedList<Integer> posiciones = new LinkedList<>();

    private static void limpiarComponentes() {
        ordeneraPor = "";
        esDecendente = false;
        esTodo = false;
        listaColumnas = new LinkedList<>();
        listaNombreTablas = new LinkedList<>();
        listaTablas = new LinkedList<>();
        exp = null;
        este = null;
        producto_cartesiano = new Cuerpo_tabla();
        valoresTabla = null;
        resultado = null;
        posiciones = new LinkedList<>();
    }

    public static Simbolo SeleccionarRegistro(seleccionar n, DepthFirstRetVisitor_usql este) throws java.text.ParseException {
        IniciarComponentesConsulta(n);
        Simbolo vista = new Simbolo("temp", Contexto.TB, null);
        if (!listaTablas.isEmpty()) {
            CrearVista(vista);
            valoresTabla = new Ent(este.obtenerGlobal());
            este.vaciarGlobal();
            este.fijarGlobal(valoresTabla);
            Cuerpo_tabla resultado_temporal = new Cuerpo_tabla();
            int it = 0;
            for (LinkedList<Simbolo> reg : ((Tabla) vista.v).cuerpo.registros) {
                AsignaValores((Tabla) vista.v, reg);
                if (((Simbolo) exp.accept(este)).v.ABool()) {
                    resultado_temporal.registros.add(reg);
                }
            }
            ((Tabla) vista.v).cuerpo = resultado_temporal;
            CrearResultadoFinal(((Tabla) vista.v));
            imprimir((Tabla) vista.v);
            limpiarComponentes();
        } else {
            Debuger.Debug("Enserio Mike enserio !", false, null);
        }
        return vista;
    }

    private static void imprimir(Tabla t) {
        for (LinkedList<Simbolo> reg : t.cuerpo.registros) {
            imprimirRegistro(reg, t.valores);
        }
    }

    private static void CrearVista(Simbolo vista) {
        Tabla temp = new Tabla("");
        temp.valores = GenerarEncabezadoConsulta();
        CrearSuperTabla();
        temp.cuerpo = producto_cartesiano;
        vista.v = temp;
    }

    private static LinkedList<Simbolo> aplicarOperadorPi(Tabla t) {
        LinkedList<Simbolo> nuevoValores = new LinkedList<>();
        if (!esTodo) {
            int size = t.valores.size();
            for (int i = 0; i < size; i++) {
                Simbolo colum = t.valores.get(i);
                for (String nom : listaColumnas) {
                    if ((colum.nombre.contains(nom) && colum.nombre.contains(".")) || colum.nombre.equals(nom)) {
                        nuevoValores.add(colum);
                        posiciones.add(i);
                    }
                }
            }
        } else {
            nuevoValores = t.valores;
        }
        return nuevoValores;
    }

    private static Cuerpo_tabla generarNuevoEncabezado(Tabla t) {
        if (posiciones.isEmpty()) {
            for (LinkedList<Simbolo> reg : t.cuerpo.registros) {
                resultado.registros.add(reg);
            }
        } else {
            for (LinkedList<Simbolo> reg : t.cuerpo.registros) {
                LinkedList<Simbolo> regNuevo = new LinkedList<>();
                for (Integer i : posiciones) {
                    regNuevo.add(reg.get(i));
                }
                resultado.registros.add(regNuevo);
            }
        }
        return resultado;
    }

    private static void CrearResultadoFinal(Tabla t) throws java.text.ParseException {
        t.valores = aplicarOperadorPi(t);
        t.cuerpo = generarNuevoEncabezado(t);
        if (!esDecendente) {
            int posReg = 0;
            if (Contexto.ExisteColumna(t.valores, ordeneraPor)) {
                posReg = Contexto.ObtenerPosicion(t.valores, ordeneraPor);
                crearListaOrdenadaAsc(t.cuerpo, posReg, t.valores.get(posReg).tipo);
            } else {
                Debuger.Debug("Error la columna con nombre " + ordeneraPor + " no existe..", false, null);
            }
        } else {
            int posReg = 0;
            if (Contexto.ExisteColumna(t.valores, ordeneraPor)) {
                posReg = Contexto.ObtenerPosicion(t.valores, ordeneraPor);
                crearListaOrdenadaDesc(t.cuerpo, posReg, t.valores.get(posReg).tipo);
            } else {
                Debuger.Debug("Error la columna con nombre " + ordeneraPor + " no existe..", false, null);
            }
        }
    }

    private static void crearListaOrdenadaDesc(Cuerpo_tabla cuerpo, int posReg, String tipo) throws java.text.ParseException {
        Simbolo val;
        for (int j = 0; j < cuerpo.registros.size(); j++) {
            switch (tipo) {
                case Contexto.TEX:
                    val = cuerpo.registros.get(j).get(posReg);
                    inicio:
                    for (int i = 0; i < cuerpo.registros.size(); i++) {
                        Simbolo aux = cuerpo.registros.get(i).get(posReg);
                        if (val.v.ACadena().compareTo(aux.v.ACadena()) < 0) {
                            LinkedList<Simbolo> auxreg = cuerpo.registros.get(j);
                            cuerpo.registros.set(j, cuerpo.registros.get(i));
                            cuerpo.registros.set(i, auxreg);
                        }
                    }
                    break;
                case Contexto.BOl:

                    break;
                case Contexto.ENT:
                    val = cuerpo.registros.get(j).get(posReg);
                    inicio:
                    for (int i = 0; i < cuerpo.registros.size(); i++) {
                        Simbolo aux = cuerpo.registros.get(i).get(posReg);
                        if (val.v.AEntero() < aux.v.AEntero()) {
                            LinkedList<Simbolo> auxreg = cuerpo.registros.get(j);
                            cuerpo.registros.set(j, cuerpo.registros.get(i));
                            cuerpo.registros.set(i, auxreg);
                        }
                    }
                    break;
                case Contexto.DOB:
                    val = cuerpo.registros.get(j).get(posReg);
                    inicio:
                    for (int i = 0; i < cuerpo.registros.size(); i++) {
                        Simbolo aux = cuerpo.registros.get(i).get(posReg);
                        if (val.v.AEntero() < aux.v.AEntero()) {
                            LinkedList<Simbolo> auxreg = cuerpo.registros.get(j);
                            cuerpo.registros.set(j, cuerpo.registros.get(i));
                            cuerpo.registros.set(i, auxreg);
                        }
                    }
                    break;
                case Contexto.DAT:
                    SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy");
                    Date d1,
                     d2;
                    val = cuerpo.registros.get(j).get(posReg);
                    inicio:
                    for (int i = 0; i < cuerpo.registros.size(); i++) {
                        Simbolo aux = cuerpo.registros.get(i).get(posReg);
                        d1 = dd.parse(val.v.ACadena());
                        d2 = dd.parse(aux.v.ACadena());
                        if (d1.compareTo(d2) < 0) {
                            LinkedList<Simbolo> auxreg = cuerpo.registros.get(j);
                            cuerpo.registros.set(j, cuerpo.registros.get(i));
                            cuerpo.registros.set(i, auxreg);
                        }
                    }
                    break;
                case Contexto.DATH:
                    SimpleDateFormat dd1 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    val = cuerpo.registros.get(j).get(posReg);
                    inicio:
                    for (int i = 0; i < cuerpo.registros.size(); i++) {
                        Simbolo aux = cuerpo.registros.get(i).get(posReg);
                        d1 = dd1.parse(val.v.ACadena());
                        d2 = dd1.parse(aux.v.ACadena());
                        if (d1.compareTo(d2) < 0) {
                            LinkedList<Simbolo> auxreg = cuerpo.registros.get(j);
                            cuerpo.registros.set(j, cuerpo.registros.get(i));
                            cuerpo.registros.set(i, auxreg);
                        }
                    }
                    break;
                default:
            }
        }
    }

    private static void crearListaOrdenadaAsc(Cuerpo_tabla cuerpo, int posReg, String tipo) throws java.text.ParseException {
        Simbolo val;
        for (int j = 0; j < cuerpo.registros.size(); j++) {
            switch (tipo) {
                case Contexto.TEX:
                    val = cuerpo.registros.get(j).get(posReg);
                    inicio:
                    for (int i = 0; i < cuerpo.registros.size(); i++) {
                        Simbolo aux = cuerpo.registros.get(i).get(posReg);
                        if (val.v.ACadena().compareTo(aux.v.ACadena()) > 0) {
                            LinkedList<Simbolo> auxreg = cuerpo.registros.get(j);
                            cuerpo.registros.set(j, cuerpo.registros.get(i));
                            cuerpo.registros.set(i, auxreg);
                        }
                    }
                    break;
                case Contexto.BOl:

                    break;
                case Contexto.ENT:
                    val = cuerpo.registros.get(j).get(posReg);
                    inicio:
                    for (int i = 0; i < cuerpo.registros.size(); i++) {
                        Simbolo aux = cuerpo.registros.get(i).get(posReg);
                        if (val.v.AEntero() > aux.v.AEntero()) {
                            LinkedList<Simbolo> auxreg = cuerpo.registros.get(j);
                            cuerpo.registros.set(j, cuerpo.registros.get(i));
                            cuerpo.registros.set(i, auxreg);
                        }
                    }
                    break;
                case Contexto.DOB:
                    val = cuerpo.registros.get(j).get(posReg);
                    inicio:
                    for (int i = 0; i < cuerpo.registros.size(); i++) {
                        Simbolo aux = cuerpo.registros.get(i).get(posReg);
                        if (val.v.AEntero() > aux.v.AEntero()) {
                            LinkedList<Simbolo> auxreg = cuerpo.registros.get(j);
                            cuerpo.registros.set(j, cuerpo.registros.get(i));
                            cuerpo.registros.set(i, auxreg);
                        }
                    }
                    break;
                case Contexto.DAT:
                    SimpleDateFormat dd = new SimpleDateFormat("dd-MM-yyyy");
                    Date d1,
                     d2;
                    val = cuerpo.registros.get(j).get(posReg);
                    inicio:
                    for (int i = 0; i < cuerpo.registros.size(); i++) {
                        Simbolo aux = cuerpo.registros.get(i).get(posReg);
                        d1 = dd.parse(val.v.ACadena());
                        d2 = dd.parse(aux.v.ACadena());
                        if (d1.compareTo(d2) > 0) {
                            LinkedList<Simbolo> auxreg = cuerpo.registros.get(j);
                            cuerpo.registros.set(j, cuerpo.registros.get(i));
                            cuerpo.registros.set(i, auxreg);
                        }
                    }
                    break;
                case Contexto.DATH:
                    SimpleDateFormat dd1 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                    val = cuerpo.registros.get(j).get(posReg);
                    inicio:
                    for (int i = 0; i < cuerpo.registros.size(); i++) {
                        Simbolo aux = cuerpo.registros.get(i).get(posReg);
                        d1 = dd1.parse(val.v.ACadena());
                        d2 = dd1.parse(aux.v.ACadena());
                        if (d1.compareTo(d2) > 0) {
                            LinkedList<Simbolo> auxreg = cuerpo.registros.get(j);
                            cuerpo.registros.set(j, cuerpo.registros.get(i));
                            cuerpo.registros.set(i, auxreg);
                        }
                    }

                    break;
                default:
            }
        }
    }

    private static void AsignaValores(Tabla t, LinkedList<Simbolo> registro) {
        for (int i = 0; i < t.valores.size(); i++) {
            Simbolo s = t.valores.get(i);
            Simbolo val = registro.get(i);
            switch (s.tipo) {
                case Contexto.TEX:
                    s.v = new Texto(val.v.ACadena(), s.tipo);
                    break;
                case Contexto.BOl:
                    s.v = new Bool(val.v.ACadena(), s.tipo);
                    break;
                case Contexto.ENT:
                    s.v = new Entero(val.v.ACadena(), s.tipo);
                    break;
                case Contexto.DOB:
                    s.v = new Doble(val.v.ACadena(), s.tipo);
                    break;
                case Contexto.DAT:
                    s.v = new Fecha_tipo(val.v.ACadena(), s.tipo);
                    break;
                case Contexto.DATH:
                    s.v = new Fecha_hora_tipo(val.v.ACadena(), s.tipo);
                    break;
                default:
                    s.v = new Objeto(Contexto.OBJ);
                    s.v = val.v;
            }
            valoresTabla.tabla.put(s.nombre, s);
        }
    }

    protected static void IniciarComponentesConsulta(seleccionar n) {
        syntaxtree.NodeChoice primeraColumna = n.f1;
        syntaxtree.NodeListOptional otrasColumnas = n.f2;
        syntaxtree.NodeToken primerTabla = n.f4;
        syntaxtree.NodeListOptional otrasTablas = n.f5;
        syntaxtree.NodeOptional choice2 = n.f6;
        llenarColumnas(primeraColumna, otrasColumnas);
        llenarTablas(primerTabla, otrasTablas);
        asignarOtrosValores(choice2);
    }

    private static void llenarColumnas(syntaxtree.NodeChoice primerColumna, syntaxtree.NodeListOptional otrasColumnas) {
        String primerc = "";
        switch (primerColumna.which) {
            case 0:
                primerc = ((syntaxtree.NodeToken) primerColumna.choice).tokenImage;
                listaColumnas.add(primerc);
                break;
            case 1:
                esTodo = true;
                break;
        }
        for (syntaxtree.INode_usql node : otrasColumnas.nodes) {
            syntaxtree.NodeSequence ns = (syntaxtree.NodeSequence) node;
            primerc = ((syntaxtree.NodeToken) ns.nodes.get(1)).tokenImage;
            listaColumnas.add(primerc);
        }
    }

    private static void llenarTablas(syntaxtree.NodeToken primerTabla, syntaxtree.NodeListOptional otrasTablas) {
        String primertabla = primerTabla.tokenImage;
        listaNombreTablas.add(primertabla);
        for (syntaxtree.INode_usql node : otrasTablas.nodes) {
            syntaxtree.NodeSequence ns = (syntaxtree.NodeSequence) node;
            primertabla = ((syntaxtree.NodeToken) ns.nodes.get(1)).tokenImage;
            listaNombreTablas.add(primertabla);
        }

        for (String nombre : listaNombreTablas) {
            Simbolo tabla = new Simbolo(nombre, Contexto.TB, null);
            tabla.v = Instruccion_insetar.LeerTabla(nombre);
            if (tabla.v != null) {
                listaTablas.add(tabla);
                Tabla tt = (Tabla) tabla.v;
                Instruccion_insetar.levantarRegistros(tt.path.replace("\"", ""), tt.cuerpo);
            }
        }
    }

    private static void asignarOtrosValores(syntaxtree.NodeOptional opcional) {
        syntaxtree.NodeSequence ns = (syntaxtree.NodeSequence) opcional.node;
        if (opcional.present()) {
            exp = (exp_logica) ns.nodes.get(1);
            syntaxtree.NodeOptional ns1 = (syntaxtree.NodeOptional) ns.nodes.get(2);
            if (ns1.present()) {
                syntaxtree.NodeSequence ns2 = (syntaxtree.NodeSequence) ns1.node;
                syntaxtree.NodeOptional ns3 = (syntaxtree.NodeOptional) ns2.nodes.get(3);
                ordeneraPor = ((syntaxtree.NodeToken) ns2.nodes.get(2)).tokenImage;
                if (ns3.present()) {
                    syntaxtree.NodeChoice nc = (syntaxtree.NodeChoice) ns3.node;
                    switch (nc.which) {
                        case 0:
                            esDecendente = false;
                            break;
                        case 1:
                            esDecendente = true;
                            break;
                    }
                }
            }
        }

    }

    private static LinkedList<Simbolo> GenerarEncabezadoConsulta() {
        LinkedList<Simbolo> encabezado = new LinkedList<>();
        for (Simbolo s : listaTablas) {
            Tabla t = (Tabla) s.v;
            for (Simbolo ss : t.valores) {
                if (contieneEncabezado(ss.nombre, encabezado)) {
                    ss.nombre = s.nombre + "." + ss.nombre;
                    encabezado.add(ss);
                } else {
                    encabezado.add(ss);
                }
            }
        }
        return encabezado;
    }

    private static boolean contieneEncabezado(String nombre, LinkedList<Simbolo> valores) {
        return valores.stream().anyMatch((Simbolo a) -> a.nombre.equals(nombre));
    }

    private static void RealizarProdcutoCartesiano(Cuerpo_tabla cp1, Cuerpo_tabla cp2) {
        LinkedList<LinkedList<Simbolo>> temp = new LinkedList<>();
        if (cp1.registros.isEmpty()) {
            cp1.registros.addAll(cp2.registros);
        } else {
            for (LinkedList<Simbolo> registro : cp1.registros) {
                for (LinkedList<Simbolo> registro1 : cp2.registros) {
                    LinkedList<Simbolo> regTemp = new LinkedList<>();
                    regTemp.addAll(registro);
                    regTemp.addAll(registro1);
                    temp.add(regTemp);
                }
            }
            cp1.registros.clear();
            cp1.registros.addAll(temp);
        }
    }

    private static void CrearSuperTabla() {
        for (Simbolo s : listaTablas) {
            Tabla t = (Tabla) s.v;
            RealizarProdcutoCartesiano(producto_cartesiano, t.cuerpo);
        }
    }

    private static void imprimirRegistro(LinkedList<Simbolo> valores, LinkedList<Simbolo> encabezado) {
        String encabe = "";
        for (Simbolo s : encabezado) {
            encabe += s.nombre + "                  ";
        }
        Debuger.Debug(encabe);
        encabe = "";
        for (Simbolo s : valores) {
            encabe += s.v.ACadena() + "                 ";
        }
        Debuger.Debug(encabe);

    }
}
