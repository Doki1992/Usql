/* Generated by JTB 1.4.4 */
package visitor;

import syntaxtree.*;

public interface IVoidVisitor {

  public void visit(final NodeList n);

  public void visit(final NodeListOptional n);

  public void visit(final NodeOptional n);

  public void visit(final NodeSequence n);

  public void visit(final NodeToken n);

  public void visit(final Inicio n);

  public void visit(final listasentencias n);

  public void visit(final eliminar_usuario n);

  public void visit(final eliminar_tabla n);

  public void visit(final eliminar_base n);

  public void visit(final eliminar_objeto n);

  public void visit(final alterar_usuario n);

  public void visit(final alterar_tabla n);

  public void visit(final alterar_objeto n);

  public void visit(final agregar_tabla n);

  public void visit(final quitar n);

  public void visit(final agregar_objeto n);

  public void visit(final crear_base n);

  public void visit(final crear_tabla n);

  public void visit(final lista_atributos n);

  public void visit(final tipo n);

  public void visit(final complemento_campo n);

  public void visit(final crear_objeto n);

  public void visit(final lista_componentes n);

  public void visit(final crear_procedimiento n);

  public void visit(final lista_parametros n);

  public void visit(final crear_funcion n);

  public void visit(final lista_sentencias_p n);

  public void visit(final Retorno n);

  public void visit(final crear_usuario n);

  public void visit(final insertar n);

  public void visit(final lista_expresion n);

  public void visit(final expresion n);

  public void visit(final g n);

  public void visit(final t n);

  public void visit(final f n);

  public void visit(final h n);

  public void visit(final exp_relacional n);

  public void visit(final exp_logica n);

  public void visit(final exp_or n);

  public void visit(final exp_and n);

  public void visit(final actualizar n);

  public void visit(final borrar n);

  public void visit(final seleccionar n);

  public void visit(final otorgar n);

  public void visit(final denegar n);

  public void visit(final Declarar n);

  public void visit(final asignacion n);

  public void visit(final Si n);

  public void visit(final Selecciona n);

  public void visit(final caso n);

  public void visit(final Para n);

  public void visit(final Mientras n);

  public void visit(final Detener n);

  public void visit(final Imprimir n);

  public void visit(final Fecha n);

  public void visit(final Fecha_hora n);

  public void visit(final Contar n);

  public void visit(final Backup_dump n);

  public void visit(final Restaurar n);

}