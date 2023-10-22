package com.analizadores;

import java.util.ArrayList;
import java.util.List;

public class AnalizadorSemantico {

    private int posicion;
    private ArrayList<Lexema> tokens;
    private List<ErrorSemantico> erroresSemanticos;

    public AnalizadorSemantico() {
        this.erroresSemanticos = new ArrayList<>();
    }

    public void funcion() {
        tipo_retorno();
        identificador();
        coincidir("Parentesis abierto");
        lista_parametros();
        coincidir("Parentesis cerrado");
        bloque();
    }

    public void tipo_retorno() {
        String preanalisis = getPreanalisis();
        int numeroLinea = (posicion < tokens.size()) ? tokens.get(posicion).getNumLinea() : -1;
        if (!preanalisis.equals("Tipo")) {
            erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Se esperaba un tipo de retorno."));
        }
        coincidir("Tipo");
    }

    public void identificador() {
        String preanalisis = getPreanalisis();
        int numeroLinea = (posicion < tokens.size()) ? tokens.get(posicion).getNumLinea() : -1;
        if (!preanalisis.equals("Identificador")) {
            erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Se esperaba un identificador."));
        } else {
            coincidir("Identificador");
        }
    }

    public void lista_parametros() {
        while (getPreanalisis().equals("Tipo")) {
            parametro();
            if (getPreanalisis().equals("Coma")) {
                coincidir("Coma");
            }
        }
    }

    public void parametro() {
        tipo_dato();
        identificador();
    }

    public void tipo_dato() {
        String preanalisis = getPreanalisis();
        int numeroLinea = (posicion < tokens.size()) ? tokens.get(posicion).getNumLinea() : -1;
        if (!preanalisis.equals("Tipo")) {
            erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Se esperaba un tipo de dato."));
        } else {
            coincidir("Tipo");
        }
    }

    public void lista_sentencias() {
        while (getPreanalisis().equals("Palabra reservada while")
                || getPreanalisis().equals("Tipo")
                || getPreanalisis().equals("Identificador")) {
            sentencia();
        }
    }

    public void sentencia() {
        String preanalisis = getPreanalisis();
        int numeroLinea = (posicion < tokens.size()) ? tokens.get(posicion).getNumLinea() : -1;
        switch (preanalisis) {
            case "Tipo" ->
                declaracion();
            case "Palabra reservada while" ->
                estructura_control();
            case "Identificador" ->
                asignacion();
            default ->
                erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Sentencia no válida."));
        }
    }

    public void declaracion() {
        tipo_dato();
        lista_variables();
        coincidir("Punto y coma");
    }

    public void asignacion() {
        String preanalisis = getPreanalisis();
        int numeroLinea = (posicion < tokens.size()) ? tokens.get(posicion).getNumLinea() : -1;
        if (preanalisis.equals("Identificador")) {
            coincidir("Identificador");
            coincidir("Operador de asignación");
            expresion();
            coincidir("Punto y coma");
        } else {
            erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Se esperaba un identificador antes del operador de asignación."));
        }
    }

    public void lista_variables() {
        identificador();
        coincidir("Operador de asignación");
        constante();
    }

    public void constante() {
        String preanalisis = getPreanalisis();
        int numeroLinea = (posicion < tokens.size()) ? tokens.get(posicion).getNumLinea() : -1;
        if (!preanalisis.equals("Número Entero")) {
            erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Se esperaba una constante entera."));
        } else {
            coincidir("Número Entero");
        }
    }

    public void estructura_control() {
        coincidir("Palabra reservada while");
        coincidir("Parentesis abierto");
        expresion();
        coincidir("Parentesis cerrado");
        bloque();
    }

    public void expresion() {
        identificador();
        operador();
        constante();
    }

    public void operador() {
        String preanalisis = getPreanalisis();
        int numeroLinea = (posicion < tokens.size()) ? tokens.get(posicion).getNumLinea() : -1;
        if (!preanalisis.equals("Operador relacional") && !preanalisis.equals("Operador suma")) {
            erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Operador no válido."));
        } else {
            coincidir(preanalisis);
        }
    }

    public void bloque() {
        coincidir("Llave abierta");
        lista_sentencias();

        if (getPreanalisis().equals("Llave cerrada")) {
            coincidir("Llave cerrada");
        } else {
            int numeroLinea = (posicion-1 < tokens.size()) ? tokens.get((posicion-1)).getNumLinea() : -1;
            erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Falta la llave cerrada."));
        }
    }

    public void coincidir(String cadena) {
        int numeroLinea = (posicion < tokens.size()) ? tokens.get(posicion).getNumLinea() : -1;
        if (posicion < tokens.size() && cadena.equals(tokens.get(posicion).getDescripcion())) {
            posicion++;
        } else {
            erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: No coincide con '" + cadena + "'."));
        }
    }

    public void analizar(ArrayList<Lexema> tokens) {
        this.tokens = tokens;
        funcion();
    }

    private String getPreanalisis() {
        return (posicion < tokens.size()) ? tokens.get(posicion).getDescripcion() : "";
    }

    public void getErrores() {
        for (ErrorSemantico error : erroresSemanticos) {
            System.out.println("Línea " + error.getNumeroLinea() + ": " + error.getMensaje());
        }
    }

    public static void main(String[] args) {
        // Ejemplo de uso
        ArrayList<Lexema> tokens = new ArrayList<>();
        // Agregar tokens al ArrayList tokens
        AnalizadorSemantico analizador = new AnalizadorSemantico();
        analizador.analizar(tokens);
        analizador.getErrores();
    }
}

class ErrorSemantico {

    private int numeroLinea;
    private String mensaje;

    public ErrorSemantico(int numeroLinea, String mensaje) {
        this.numeroLinea = numeroLinea;
        this.mensaje = mensaje;
    }

    public int getNumeroLinea() {
        return numeroLinea;
    }

    public String getMensaje() {
        return mensaje;
    }
}
