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
            }
            case "-" -> {
                lexema.setToken(5);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador resta");
            }
            case "*" -> {
                lexema.setToken(6);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador de multiplicación");
            }
            case "/" -> {
                lexema.setToken(6);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador de división");
            }
            case "<", "<=", ">", ">=" -> {
                lexema.setToken(7);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador relacional");
            }
            case "||" -> {
                lexema.setToken(8);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador lógico OR");
            }
            case "&&" -> {
                lexema.setToken(9);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador lógico AND");
            }
            case "!" -> {
                lexema.setToken(10);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador de negacion");
            }
            case "==", "!=" -> {
                lexema.setToken(11);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador igualdad");
            }
            case ";" -> {
                lexema.setToken(12);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Punto y coma");
            }
            case "," -> {
                lexema.setToken(13);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Coma");
            }
            case "(" -> {
                lexema.setToken(14);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Parentesis abierto");
            }
            case ")" -> {
                lexema.setToken(15);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Parentesis cerrado");
            }
            case "{" -> {
                lexema.setToken(16);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Llave abierta");
            }
            case "}" -> {
                lexema.setToken(17);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Llave cerrada");
            }
            case "=" -> {
                lexema.setToken(18);
                lexema.setLexema(palabra);
                lexema.setDescripcion("Operador de asignación");
            }
            default -> {
                if (esReservada(palabra)) {
                    switch (palabra) {
                        case "int", "float", "void","char" -> {
                            lexema.setToken(4);
                            lexema.setLexema(palabra);
                            lexema.setDescripcion("Tipo");
                        }
                        case "if" -> {
                            lexema.setToken(19);
                            lexema.setLexema(palabra);
                            lexema.setDescripcion("Palabra reservada " + palabra);
                        }
                        case "while" -> {
                            lexema.setToken(20);
                            lexema.setLexema(palabra);
                            lexema.setDescripcion("Palabra reservada " + palabra);
                        }
                        case "return" -> {
                            lexema.setToken(21);
                            lexema.setLexema(palabra);
                            lexema.setDescripcion("Palabra reservada " + palabra);
                        }
                        case "else" -> {
                            lexema.setToken(22);
                            lexema.setLexema(palabra);
                            lexema.setDescripcion("Palabra reservada " + palabra);
                        }
                        case "$" -> {
                            lexema.setToken(23);
                            lexema.setLexema(palabra);
                            lexema.setDescripcion("fin de cadena");
                        }
                    }
                } else if (esEspacio(palabra) || esSaltoLinea(palabra)) {
                    lexema = null;
                } else if (esIdentificador(palabra)) {
                    lexema.setToken(0);
                    lexema.setLexema(palabra);
                    lexema.setDescripcion("Identificador");
                } else if (esNumeroEntero(palabra)) {
                    lexema.setToken(1);
                    lexema.setLexema(palabra);
                    lexema.setDescripcion("Número Entero");
                } else if (esNumeroReal(palabra)) {
                    lexema.setToken(2);
                    lexema.setLexema(palabra);
                    lexema.setDescripcion("Número Real");
                } else {
                    lexema.setToken(-1);
                    lexema.setLexema(palabra);
                    lexema.setDescripcion("Desconocido");
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
            String token = tokens.nextToken();
            if (esSaltoLinea(token)) {
                numLinea++;
                continue;
            }
            lexema = identificarToken(token, lexema);

            // Si se identifica un lexema, agregarlo a la lista de lexemas
            if (lexema != null) {
                lexema.setNumLinea(numLinea);
                listaLexemas.add(lexema);
            }
        }
        return listaLexemas;
    }
}
