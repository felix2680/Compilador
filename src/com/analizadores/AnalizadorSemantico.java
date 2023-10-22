package com.analizadores;

import java.util.ArrayList;

public class AnalizadorSemantico {

    private ArrayList<ErrorSemantico> errores = new ArrayList<>();

    public AnalizadorSemantico() {
    }

    public void dameErrores() {
        for (ErrorSemantico error : errores) {
            System.out.println(error.getMessage());
        }
    }

    public void analizar(ArrayList<Lexema> tokens) {
            analizarFuncion(tokens);
    }

    private void analizarFuncion(ArrayList<Lexema> tokens) {
        analizarTipoRetorno(tokens);
        analizarIdentificador(tokens);
        coincidir(tokens, "Parentesis abierto");
        analizarListaParametros(tokens);
        coincidir(tokens, "Parentesis cerrado");
        analizarBloque(tokens);
    }

    private void analizarTipoRetorno(ArrayList<Lexema> tokens) {
        coincidir(tokens, "Tipo");
    }

    private void analizarIdentificador(ArrayList<Lexema> tokens) {
        coincidir(tokens, "Identificador");
    }

    private void analizarListaParametros(ArrayList<Lexema> tokens) {
        if (!tokens.isEmpty() && !tokens.get(0).getDescripcion().equals("Parentesis cerrado")) {
            analizarParametro(tokens);
            while (!tokens.isEmpty() && tokens.get(0).getDescripcion().equals("Coma")) {
                coincidir(tokens, "Coma");
                analizarParametro(tokens);
            }
        }
    }

    private void analizarParametro(ArrayList<Lexema> tokens) {
        analizarTipoDato(tokens);
        analizarIdentificador(tokens);
    }

    private void analizarTipoDato(ArrayList<Lexema> tokens) {
        coincidir(tokens, "Tipo");
    }

    private void analizarListaSentencias(ArrayList<Lexema> tokens) {
        while (!tokens.isEmpty() && (tokens.get(0).getDescripcion().equals("Tipo")
                || tokens.get(0).getDescripcion().equals("Palabra reservada while")
                || tokens.get(0).getDescripcion().equals("Identificador"))) {
            analizarSentencia(tokens);
        }
    }

    private void analizarSentencia(ArrayList<Lexema> tokens) {
        if (!tokens.isEmpty()) {
            if (tokens.get(0).getDescripcion().equals("Tipo")) {
                analizarDeclaracion(tokens);
            } else if (tokens.get(0).getDescripcion().equals("Palabra reservada while")) {
                analizarEstructuraControl(tokens);
            } else if (tokens.get(0).getDescripcion().equals("Identificador")) {
                analizarAsignacion(tokens);
            } else {
                errores.add(new ErrorSemantico("Error: Sentencia no válida en la línea " + tokens.get(0).getNumLinea()));
                // Avanzar para evitar un bucle infinito en caso de error
                tokens.remove(0);
            }
        }
    }

    private void analizarDeclaracion(ArrayList<Lexema> tokens) {
        analizarTipoDato(tokens);
        analizarListaVariables(tokens);
        coincidir(tokens, "Punto y coma");
    }

    private void analizarAsignacion(ArrayList<Lexema> tokens) {
        analizarIdentificador(tokens);
        coincidir(tokens, "Operador de asignación");
        analizarExpresion(tokens);
        coincidir(tokens, "Punto y coma");
    }

    private void analizarListaVariables(ArrayList<Lexema> tokens) {
        analizarIdentificador(tokens);
        coincidir(tokens, "Operador de asignación");
        analizarConstante(tokens);
    }

    private void analizarConstante(ArrayList<Lexema> tokens) {
        coincidir(tokens, "Número Entero");
    }

    private void analizarEstructuraControl(ArrayList<Lexema> tokens) {
        coincidir(tokens, "Palabra reservada while");
        coincidir(tokens, "Parentesis abierto");
        analizarExpresion(tokens);
        coincidir(tokens, "Parentesis cerrado");
        analizarBloque(tokens);
    }

    private void analizarExpresion(ArrayList<Lexema> tokens) {
        analizarIdentificador(tokens);
        analizarOperador(tokens);
        analizarConstante(tokens);
    }

    private void analizarOperador(ArrayList<Lexema> tokens) {
        if (!tokens.isEmpty() && (tokens.get(0).getDescripcion().equals("Operador relacional")
                || tokens.get(0).getDescripcion().equals("Operador suma"))) {
            coincidir(tokens, tokens.get(0).getDescripcion());
        } else {
            errores.add(new ErrorSemantico("Operador esperado en la línea " + tokens.get(0).getNumLinea()));
            // Avanzar para evitar un bucle infinito en caso de error
            tokens.remove(0);
        }
    }

    private void analizarBloque(ArrayList<Lexema> tokens) {
        coincidir(tokens, "Llave abierta");
        analizarListaSentencias(tokens);
        coincidir(tokens, "Llave cerrada");
    }

    private void coincidir(ArrayList<Lexema> tokens, String descripcion) {
        if (!tokens.isEmpty() && tokens.get(0).getDescripcion().equals(descripcion)) {
            tokens.remove(0);
        } else {
            errores.add(new ErrorSemantico("Error de coincidencia: Se esperaba '" + descripcion + "' en la línea " + tokens.get(0).getNumLinea()));
            // Avanzar para evitar un bucle infinito en caso de error
        }
    }

    public ArrayList<ErrorSemantico> getErrores() {
        return errores;
    }

    public static class ErrorSemantico {
        private final String mensaje;

        public ErrorSemantico(String mensaje) {
            this.mensaje = mensaje;
        }

        public String getMessage() {
            return mensaje;
        }
    }
}
