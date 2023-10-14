package com.analizadores;

public class AnalizadorSintactico {

    public static void funcion() throws GrammarException {
        tipo_retorno();
        identificador();
        coincidir("");
    }
    public static void tipo_retorno()throws GrammarException{
        
    }
    
    public static void identificador()throws GrammarException{
        
    }
    public static void coincidir(String caracter){
        
    }
    class GrammarException extends Exception {

        public GrammarException() {
            super("Cadena no válida");
        }
    }

}
