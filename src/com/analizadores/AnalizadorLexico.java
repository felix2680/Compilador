package com.analizadores;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AnalizadorLexico {

    private static final ArrayList<Lexema> listaLexemas = new ArrayList<>();
    private static int numLinea = 1;
    private static final String PALABRAS_RESERVADAS[] = {"int", "float", "String", "char", "double", "else",
        "while", "for", "break", "if", "return", "void"};

    private static boolean esReservada(String palabra) {
        for (String p : PALABRAS_RESERVADAS) { //recorre en los elementos del arreglo
            if (palabra.equals(p)) { //si palabra es igual a una a un elemento del arreglo, retorna verdadero
                return true;
            }
        }
        return false;
    }
    private static boolean esIdentificador(String palabra) {
        Pattern p = Pattern.compile("^([a-zA-Z][a-zA-Z\\d_]*)$");
        Matcher m = p.matcher(palabra);
        return m.matches();
    }

    private static boolean esNumeroEntero(String palabra) {
        Pattern p = Pattern.compile("^[\\d]+$");
        Matcher m = p.matcher(palabra);
        return m.matches();
    }

    private static boolean esNumeroReal(String palabra) {
        Pattern p = Pattern.compile("^[\\d]+[\\.][\\d]+$");
        Matcher m = p.matcher(palabra);
        return m.matches();
    }

    private static boolean esEspacio(String palabra) {
        Pattern p = Pattern.compile("^[\s]+$");
        Matcher m = p.matcher(palabra);
        return m.matches();
    }

    private static boolean esSaltoLinea(String palabra) {
        Pattern p = Pattern.compile("^[\n]+$");
        Matcher m = p.matcher(palabra);
        return m.matches();
    }

    public static Lexema identificarToken(String palabra, Lexema lexema) {
        switch (palabra) {
            case "+" -> {
                lexema.setToken(5);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador suma");
                lexema.setNumLinea(numLinea);
            }
            case "-" -> {
                lexema.setToken(5);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador resta");
                lexema.setNumLinea(numLinea);
            }
            case "*" -> {
                lexema.setToken(6);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador de multiplicación");
                lexema.setNumLinea(numLinea);
            }
            case "/" -> {
                lexema.setToken(6);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador de división");
                lexema.setNumLinea(numLinea);
            }
            case "<", "<=", ">", ">=" -> {
                lexema.setToken(7);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador relacional");
                lexema.setNumLinea(numLinea);
            }
            case "||" -> {
                lexema.setToken(8);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador lógico OR");
                lexema.setNumLinea(numLinea);
            }
            case "&&" -> {
                lexema.setToken(9);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador lógico AND");
                lexema.setNumLinea(numLinea);
            }
            case "!" -> {
                lexema.setToken(10);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador de negacion");
                lexema.setNumLinea(numLinea);
            }
            case "==", "!=" -> {
                lexema.setToken(11);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador igualdad");
                lexema.setNumLinea(numLinea);
            }
            case ";" -> {
                lexema.setToken(12);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Punto y coma");
                lexema.setNumLinea(numLinea);
            }
            case "," -> {
                lexema.setToken(13);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Coma");
                lexema.setNumLinea(numLinea);
            }
            case "(" -> {
                lexema.setToken(14);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Parentesis abierto");
                lexema.setNumLinea(numLinea);lexema.setNumLinea(numLinea);
            }
            case ")" -> {
                lexema.setToken(15);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Parentesis cerrado");
                lexema.setNumLinea(numLinea);
            }
            case "{" -> {
                lexema.setToken(16);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Llave abierta");
                lexema.setNumLinea(numLinea);
            }
            case "}" -> {
                lexema.setToken(17);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Llave cerrada");
                lexema.setNumLinea(numLinea);
            }
            case "=" -> {
                lexema.setToken(18);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador de asignación");
                lexema.setNumLinea(numLinea);
            }
            default -> {
                if (esReservada(palabra)) {
                    switch (palabra) {
                        case "int", "float", "void" -> {
                            lexema.setToken(4);
                            lexema.setLexema(palabra);
                            lexema.setDescripcion("Tipo");
                            lexema.setNumLinea(numLinea);
                        }
                        case "if" -> {
                            lexema.setToken(19);
                            lexema.setLexema(palabra);
                            lexema.setDescripcion("Palabra reservada " + palabra);
                            lexema.setNumLinea(numLinea);
                        }
                        case "while" -> {
                            lexema.setToken(20);
                            lexema.setLexema(palabra);
                            lexema.setDescripcion("Palabra reservada " + palabra);
                            lexema.setNumLinea(numLinea);
                        }
                        case "return" -> {
                            lexema.setToken(21);
                            lexema.setLexema(palabra);
                            lexema.setDescripcion("Palabra reservada " + palabra);
                            lexema.setNumLinea(numLinea);
                        }
                        case "else" -> {
                            lexema.setToken(22);
                            lexema.setLexema(palabra);
                            lexema.setDescripcion("Palabra reservada " + palabra);
                            lexema.setNumLinea(numLinea);
                        }
                        case "$" -> {
                            lexema.setToken(23);
                            lexema.setLexema(palabra);
                            lexema.setDescripcion("fin de cadena");
                            lexema.setNumLinea(numLinea);
                        }
                    }
                } else if (esEspacio(palabra) || esSaltoLinea(palabra)) {
                    if(esSaltoLinea(palabra)){
                        numLinea++;
                    }
                    lexema = null;
                } else if (esIdentificador(palabra)) {
                    lexema.setToken(0);
                    lexema.setLexema(palabra);
                    lexema.setDescripcion("Identificador");
                    lexema.setNumLinea(numLinea);
                } else if (esNumeroEntero(palabra)) {
                    lexema.setToken(1);
                    lexema.setLexema(palabra);
                    lexema.setDescripcion("Número Entero");
                    lexema.setNumLinea(numLinea);
                } else if (esNumeroReal(palabra)) {
                    lexema.setToken(2);
                    lexema.setLexema(palabra);
                    lexema.setDescripcion("Número Real");
                    lexema.setNumLinea(numLinea);
                } else {
                    lexema.setToken(-1);
                    lexema.setLexema(palabra);
                    lexema.setDescripcion("Desconocido");
                    lexema.setNumLinea(numLinea);
                }
            }
        }
        return lexema;
    }

    public static ArrayList<Lexema> analizar(String palabra) {
        String delimitadores = "\s|\n|\t|(|)|{|}|[|]|;|\"|,|+|-|=";
        StringTokenizer tokens = new StringTokenizer(palabra, delimitadores, true);
        numLinea = 1;
        while (tokens.hasMoreTokens()) {
            Lexema lexema = new Lexema();
            lexema = identificarToken(tokens.nextToken(), lexema);
            // Si se identifica un lexema, agregarlo a la lista de lexemas
            if (lexema != null) {
                listaLexemas.add(lexema);
            }
        }
        return listaLexemas;
    }
}
