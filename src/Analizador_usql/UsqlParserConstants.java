/* Generated By:JavaCC: Do not edit this line. UsqlParserConstants.java */
package Analizador_usql;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface UsqlParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int LINE_COMMENT = 6;
  /** RegularExpression Id. */
  int MULTI_LINE_COMMENT = 7;
  /** RegularExpression Id. */
  int VERDADERO = 8;
  /** RegularExpression Id. */
  int FALSO = 9;
  /** RegularExpression Id. */
  int S_NUMBERO = 10;
  /** RegularExpression Id. */
  int FLOAT = 11;
  /** RegularExpression Id. */
  int INTEGER = 12;
  /** RegularExpression Id. */
  int DATE = 13;
  /** RegularExpression Id. */
  int HORA = 14;
  /** RegularExpression Id. */
  int DIGITO = 15;
  /** RegularExpression Id. */
  int K_text = 16;
  /** RegularExpression Id. */
  int K_integer = 17;
  /** RegularExpression Id. */
  int K_double = 18;
  /** RegularExpression Id. */
  int K_bool = 19;
  /** RegularExpression Id. */
  int K_date = 20;
  /** RegularExpression Id. */
  int K_datetime = 21;
  /** RegularExpression Id. */
  int K_suma = 22;
  /** RegularExpression Id. */
  int K_resta = 23;
  /** RegularExpression Id. */
  int K_multiplicacion = 24;
  /** RegularExpression Id. */
  int K_division = 25;
  /** RegularExpression Id. */
  int K_potencia = 26;
  /** RegularExpression Id. */
  int K_igualda = 27;
  /** RegularExpression Id. */
  int K_asig = 28;
  /** RegularExpression Id. */
  int K_diferente = 29;
  /** RegularExpression Id. */
  int K_menor = 30;
  /** RegularExpression Id. */
  int K_mayor = 31;
  /** RegularExpression Id. */
  int K_menor_igual = 32;
  /** RegularExpression Id. */
  int K_mayor_igual = 33;
  /** RegularExpression Id. */
  int K_or = 34;
  /** RegularExpression Id. */
  int K_and = 35;
  /** RegularExpression Id. */
  int K_not = 36;
  /** RegularExpression Id. */
  int K_pariz = 37;
  /** RegularExpression Id. */
  int K_parder = 38;
  /** RegularExpression Id. */
  int K_crear = 39;
  /** RegularExpression Id. */
  int K_base = 40;
  /** RegularExpression Id. */
  int K_pcoma = 41;
  /** RegularExpression Id. */
  int K_tabla = 42;
  /** RegularExpression Id. */
  int K_coma = 43;
  /** RegularExpression Id. */
  int K_nulo = 44;
  /** RegularExpression Id. */
  int K_no_nulo = 45;
  /** RegularExpression Id. */
  int K_autoincrementable = 46;
  /** RegularExpression Id. */
  int K_llave_primaria = 47;
  /** RegularExpression Id. */
  int K_llave_foranea = 48;
  /** RegularExpression Id. */
  int K_unico = 49;
  /** RegularExpression Id. */
  int K_objeto = 50;
  /** RegularExpression Id. */
  int K_procedimiento = 51;
  /** RegularExpression Id. */
  int K_llaveiz = 52;
  /** RegularExpression Id. */
  int K_llaveder = 53;
  /** RegularExpression Id. */
  int K_funcion = 54;
  /** RegularExpression Id. */
  int K_retorno = 55;
  /** RegularExpression Id. */
  int K_usuario = 56;
  /** RegularExpression Id. */
  int K_colocar = 57;
  /** RegularExpression Id. */
  int K_pw = 58;
  /** RegularExpression Id. */
  int K_usar = 59;
  /** RegularExpression Id. */
  int K_alterar = 60;
  /** RegularExpression Id. */
  int K_agregar = 61;
  /** RegularExpression Id. */
  int K_quitar = 62;
  /** RegularExpression Id. */
  int K_cambiar = 63;
  /** RegularExpression Id. */
  int K_eliminar = 64;
  /** RegularExpression Id. */
  int K_insertar = 65;
  /** RegularExpression Id. */
  int K_en = 66;
  /** RegularExpression Id. */
  int K_actualizar = 67;
  /** RegularExpression Id. */
  int K_borrar = 68;
  /** RegularExpression Id. */
  int K_seleccionar = 69;
  /** RegularExpression Id. */
  int K_ordenar = 70;
  /** RegularExpression Id. */
  int K_asc = 71;
  /** RegularExpression Id. */
  int K_desc = 72;
  /** RegularExpression Id. */
  int K_por = 73;
  /** RegularExpression Id. */
  int K_donde = 74;
  /** RegularExpression Id. */
  int K_otorgar = 75;
  /** RegularExpression Id. */
  int K_permisos = 76;
  /** RegularExpression Id. */
  int K_denegar = 77;
  /** RegularExpression Id. */
  int K_declarar = 78;
  /** RegularExpression Id. */
  int K_si = 79;
  /** RegularExpression Id. */
  int K_sino = 80;
  /** RegularExpression Id. */
  int K_selecciona = 81;
  /** RegularExpression Id. */
  int K_dos_puntos = 82;
  /** RegularExpression Id. */
  int K_caso = 83;
  /** RegularExpression Id. */
  int K_defecto = 84;
  /** RegularExpression Id. */
  int K_detener = 85;
  /** RegularExpression Id. */
  int K_para = 86;
  /** RegularExpression Id. */
  int K_mientras = 87;
  /** RegularExpression Id. */
  int K_imprimir = 88;
  /** RegularExpression Id. */
  int K_fecha = 89;
  /** RegularExpression Id. */
  int K_fecha_hora = 90;
  /** RegularExpression Id. */
  int K_contar = 91;
  /** RegularExpression Id. */
  int K_backup = 92;
  /** RegularExpression Id. */
  int K_usqldump = 93;
  /** RegularExpression Id. */
  int K_completo = 94;
  /** RegularExpression Id. */
  int K_restaurar = 95;
  /** RegularExpression Id. */
  int K_valores = 96;
  /** RegularExpression Id. */
  int K_de = 97;
  /** RegularExpression Id. */
  int K_verdadero = 98;
  /** RegularExpression Id. */
  int K_falso = 99;
  /** RegularExpression Id. */
  int S_IDENTIFICADOR = 100;
  /** RegularExpression Id. */
  int LETRA = 101;
  /** RegularExpression Id. */
  int ESPECIAL = 102;
  /** RegularExpression Id. */
  int CADENA_LITERAL = 103;

  /** Lexical state. */
  int DEFAULT = 0;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "\" \"",
    "\"\\t\"",
    "\"\\n\"",
    "\"\\r\"",
    "\"\\f\"",
    "<LINE_COMMENT>",
    "<MULTI_LINE_COMMENT>",
    "\"1\"",
    "\"0\"",
    "<S_NUMBERO>",
    "<FLOAT>",
    "<INTEGER>",
    "<DATE>",
    "<HORA>",
    "<DIGITO>",
    "\"text\"",
    "\"integer\"",
    "\"double\"",
    "\"bool\"",
    "\"date\"",
    "\"datetime\"",
    "\"+\"",
    "\"-\"",
    "\"*\"",
    "\"/\"",
    "\"^\"",
    "\"==\"",
    "\"=\"",
    "\"!=\"",
    "\"<\"",
    "\">\"",
    "\"<=\"",
    "\">=\"",
    "\"||\"",
    "\"&&\"",
    "\"!\"",
    "\"(\"",
    "\")\"",
    "\"crear\"",
    "\"base_datos\"",
    "\";\"",
    "\"tabla\"",
    "\",\"",
    "\"nulo\"",
    "\"no nulo\"",
    "\"autoincrementable\"",
    "\"llave_primaria\"",
    "\"llave_foranea\"",
    "\"unico\"",
    "\"objeto\"",
    "\"procedimiento\"",
    "\"{\"",
    "\"}\"",
    "\"funcion\"",
    "\"retorno\"",
    "\"usuario\"",
    "\"colocar\"",
    "\"password\"",
    "\"usar\"",
    "\"alterar\"",
    "\"agregar\"",
    "\"quitar\"",
    "\"cambiar\"",
    "\"eliminar\"",
    "\"insertar\"",
    "\"en\"",
    "\"actualizar\"",
    "\"borrar\"",
    "\"seleccionar\"",
    "\"ordenar\"",
    "\"asc\"",
    "\"desc\"",
    "\"por\"",
    "\"donde\"",
    "\"otorgar\"",
    "\"permisos\"",
    "\"denegar\"",
    "\"declarar\"",
    "\"si\"",
    "\"sino\"",
    "\"selecciona\"",
    "\":\"",
    "\"caso\"",
    "\"defecto\"",
    "\"detener\"",
    "\"para\"",
    "\"mientras\"",
    "\"imprimir\"",
    "\"fecha\"",
    "\"fecha_hora\"",
    "\"contar\"",
    "\"backup\"",
    "\"usqldump\"",
    "\"completo\"",
    "\"restaurar\"",
    "\"valores\"",
    "\"de\"",
    "<K_verdadero>",
    "<K_falso>",
    "<S_IDENTIFICADOR>",
    "<LETRA>",
    "<ESPECIAL>",
    "<CADENA_LITERAL>",
    "\".\"",
    "\"++\"",
    "\"--\"",
  };

}