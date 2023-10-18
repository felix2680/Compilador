package com.analizadores;

import java.util.ArrayList;

public class AnalizadorSintactico {

    private int posicion;
    private ArrayList<Lexema> tokens;

    public AnalizadorSintactico() {
        this.posicion = 0;
    }

    public void funcion() throws GrammarException {
        tipo_retorno();
        identificador();
        coincidir("Parentesis abierto");
        lista_parametros();
        coincidir("Parentesis cerrado");
        coincidir("Llave abierta");
        lista_sentencias();
        coincidir("Llave cerrada");
    }

    public void tipo_retorno() throws GrammarException {
        String preanalisis = getPreanalisis();
        if (preanalisis.equals("Tipo")) {
            coincidir("Tipo");
        } else {
            throw new GrammarException();
        }
    }

    public void identificador() throws GrammarException {
        String preanalisis = getPreanalisis();
        if (preanalisis.equals("Identificador")) {
            System.out.println("Entrando a indentificador");
            coincidir("Identificador");
        } else {
            throw new GrammarException();
        }
    }

    public void lista_parametros() throws GrammarException {
        // Gramática: lista_parametros : epsilon | parametro (',' parametro)*
        while (getPreanalisis().equals("Tipo")) {
            parametro();
            if (getPreanalisis().equals("Coma")) {
                coincidir("Coma");
            }
        }
    }

    public void parametro() throws GrammarException {
        tipo_dato();
        identificador();
    }

    public void tipo_dato() throws GrammarException {
        String preanalisis = getPreanalisis();
        if (preanalisis.equals("Tipo")) {
            System.out.println("coincidir tipo");
            coincidir("Tipo");
        } else {
            throw new GrammarException();
        }
    }

    public void lista_sentencias() throws GrammarException {
        // Gramática: lista_sentencias : sentencia lista_sentencias | epsilon
        while (getPreanalisis().equals("Palabra reservada while")
                || getPreanalisis().equals("Tipo")
                || getPreanalisis().equals("Identificador")) {
            System.out.println("Lista sentencias");
            sentencia();
        }
    }

    public void sentencia() throws GrammarException {
        // Gramática: sentencia : declaracion | estructura_control | asignacion
        String preanalisis = getPreanalisis();
        switch (preanalisis) {
            case "Tipo" -> {
                System.out.println("entrando a declaracion");
                declaracion();
            }
            case "Palabra reservada while" -> {
                estructura_control();
            }
            case "Identificador" -> {
                System.out.println("Entrando a asignacion");
                asignacion();
            }
            default -> throw new GrammarException();
        }
    }

    public void declaracion() throws GrammarException {
        // Gramática: declaracion : tipo_dato lista_variables ';'
        tipo_dato();
        lista_variables();
        coincidir("Punto y coma");
    }

    public void asignacion() throws GrammarException {
        // Gramática: asignacion : identificador '=' expresion ';'
        identificador();
        coincidir("Operador de asignación");
        expresion();
        coincidir("Punto y coma");
    }

    public void lista_variables() throws GrammarException {
        identificador();
        coincidir("Operador de asignación");
        constante();
    }

    public void constante() throws GrammarException {
        String preanalisis = getPreanalisis();
        if (preanalisis.equals("Número Entero")) {
            System.out.println("Entrando a numero entero");
            coincidir("Número Entero");
        } else {
            throw new GrammarException();
        }
    }

    public void estructura_control() throws GrammarException {
        System.out.println("Entrando a esturctura de control");
        // Gramática: estructura_control : while
        coincidir("Palabra reservada while");
        coincidir("Parentesis abierto");
        expresion();
        coincidir("Parentesis cerrado");
        bloque();
    }

    public void expresion() throws GrammarException {
        identificador();
        operador();
        constante();
    }

    public void operador() throws GrammarException {
        String preanalisis = getPreanalisis();
        System.out.println(preanalisis);
        switch (preanalisis) {
            case "Operador relacional" -> coincidir("Operador relacional");
            case "Operador suma" -> coincidir("Operador suma");
            default -> throw new GrammarException();
        }
    }

    public void bloque() throws GrammarException {
        // Gramática: bloque : '{' lista_sentencias '}'
        coincidir("Llave abierta");
        lista_sentencias();
        coincidir("Llave cerrada");
    }

    public void coincidir(String cadena) throws GrammarException {
        if (posicion < tokens.size() && cadena.equals(tokens.get(posicion).getDescripcion())) {
            posicion++;
        } else {
            throw new GrammarException();
        }
    }

    public void analizar(ArrayList<Lexema> tokens) throws GrammarException {
        this.tokens = tokens;
        funcion();
        // Asegurarse de que se hayan consumido todos los tokens
         if (posicion != tokens.size()) {
            throw new GrammarException();
        }
    }

    private String getPreanalisis() {
        return (posicion < tokens.size()) ? tokens.get(posicion).getDescripcion() : "";
    }

    public class GrammarException extends Exception {

        public GrammarException() {
            super("El código tiene errores sintácticos");
        }
    }

}
