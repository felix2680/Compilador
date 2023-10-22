package com.analizadores;

import java.util.ArrayList;

public class AnalizadorSemantico {

    public void analizar(ArrayList<Lexema> tokens) throws SemanticException{
        verificarLlavesPuntoYComas(tokens);
    }
    public void verificarLlavesPuntoYComas(ArrayList<Lexema> tokens) throws SemanticException {
        int llaveAbierta = 0;
        int llaveCerrada = 0;
        int puntoYComa = 0;
        int lineaActual = 1;

        for (Lexema token : tokens) {
            String descripcion = token.getDescripcion();
            
            switch (descripcion) {
                case "Llave abierta" -> llaveAbierta++;
                case "Llave cerrada" -> llaveCerrada++;
                case "Punto y coma" -> puntoYComa++;
                default -> {
                }
            }

            // Actualizar la línea actual según el token actual
            lineaActual = token.getNumLinea();
        }

        if (llaveAbierta != llaveCerrada) {
            throw new SemanticException("Error: Las llaves no están balanceadas en la línea " + lineaActual + ".");
        }

        if (puntoYComa < 2) {
            throw new SemanticException("Error: Falta punto y coma al final de las sentencias en la línea " + lineaActual + ".");
        }
    }

    public class SemanticException extends Exception {
        public SemanticException(String message) {
            super(message);
        }
    }
}
