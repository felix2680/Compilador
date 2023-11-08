package com.analizadores;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AnalizadorSemantico {

    private int posicion;
    private ArrayList<Lexema> tokens;
    private final List<ErrorSemantico> erroresSemanticos;
    private final Map<String, Integer> variablesDeclaradas; // Tabla de símbolos para variables declaradas
    private final Map<String, String> tablaSimbolos;
    private final Set<String> variablesNoUtilizadas; // Conjunto de variables declaradas pero no utilizadas

    public AnalizadorSemantico() {
        this.erroresSemanticos = new ArrayList<>();
        this.variablesDeclaradas = new HashMap<>();
        this.tablaSimbolos = new HashMap<>();
        this.variablesNoUtilizadas = new HashSet<>();
    }

    private void funcion() {
        tipo_retorno();
        String nombreFuncion = identificador(); // Obtener el nombre de la función
        coincidir("Parentesis abierto");
        lista_parametros();
        coincidir("Parentesis cerrado");
        // Agregar la función a la tabla de símbolos
        variablesDeclaradas.put(nombreFuncion, getNumeroLinea());
        bloque();
    }

    private void tipo_retorno() {
        String preanalisis = getPreanalisis();
        int numeroLinea = getNumeroLinea();
        if (!preanalisis.equals("Tipo")) {
            erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Se esperaba un tipo de retorno."));
        }
        coincidir("Tipo");
    }

    private String identificador() {
        String preanalisis = getPreanalisis();
        int numeroLinea = getNumeroLinea();
        if (!preanalisis.equals("Identificador")) {
            erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Se esperaba un identificador."));
            return ""; // Devuelve un valor vacío en caso de error
        } else {
            String nombreVariable = tokens.get(posicion).getLexema();
            // Verificar si el identificador ya está en la tabla de símbolos
            coincidir("Identificador");
            return nombreVariable; // Devuelve el nombre del identificador
        }
    }

    private void lista_parametros() {
        while (getPreanalisis().equals("Tipo")) {
            parametro();
            if (getPreanalisis().equals("Coma")) {
                coincidir("Coma");
            }
        }
    }

    private void parametro() {
        tipo_dato();
        identificador();
    }

    private String tipo_dato() {
        String preanalisis = getPreanalisis();
        int numeroLinea = getNumeroLinea();
        String tipoDato = tokens.get(posicion).getLexema();
        if (!preanalisis.equals("Tipo")) {
            erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Se esperaba un tipo de dato."));
        } else {
            coincidir("Tipo");
        }
        return tipoDato;
    }

    private void lista_sentencias() {
        while (getPreanalisis().equals("Palabra reservada while")
                || getPreanalisis().equals("Tipo")
                || getPreanalisis().equals("Identificador")) {
            sentencia();
        }
    }

    private void sentencia() {
        String preanalisis = getPreanalisis();
        int numeroLinea = getNumeroLinea();
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

    private void declaracion() {
        tipo_dato();
        lista_variables();
        coincidir("Punto y coma");
    }

    private void asignacion() {
        String preanalisis = getPreanalisis();
        int numeroLinea = getNumeroLinea();
        if (preanalisis.equals("Identificador")) {
            String nombreVariable = tokens.get(posicion).getLexema();
            // Verificar si la variable se ha declarado previamente
            if (!variablesDeclaradas.containsKey(nombreVariable)) {
                erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Variable '" + nombreVariable + "' no declarada."));
            }
            coincidir("Identificador");
            coincidir("Operador de asignación");
            expresion();
            coincidir("Punto y coma");
            // Marcar la variable como utilizada
            variablesNoUtilizadas.remove(nombreVariable);
        } else {
            erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Se esperaba un identificador antes del operador de asignación."));
        }
    }

    private void lista_variables() {
        String nombreVariable = tokens.get(posicion).getLexema();
        String tipoDato = tokens.get(posicion - 1).getLexema();

        coincidir("Identificador");

        if (!variablesDeclaradas.containsKey(nombreVariable)) {
            variablesDeclaradas.put(nombreVariable, getNumeroLinea());
            variablesNoUtilizadas.add(nombreVariable);
            tablaSimbolos.put(nombreVariable, tipoDato);
        } else {
            int numeroLinea = getNumeroLinea();
            erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Variable '" + nombreVariable + "' ya ha sido declarada previamente."));
        }

        if (getPreanalisis().equals("Operador de asignación")) {
            coincidir("Operador de asignación");
            constante();
        }
    }

    private void constante() {
        String preanalisis = getPreanalisis();
        String nombreVariable = tokens.get(posicion - 2).getLexema();
        int numeroLinea = getNumeroLinea();

        String tipoDatoVariable = tablaSimbolos.get(nombreVariable);

        if ((preanalisis.equals("Número Entero") && "int".equals(tipoDatoVariable))
                || (preanalisis.equals("Número Real") && "float".equals(tipoDatoVariable))) {
            coincidir(preanalisis);
        } else {
            erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Tipo de dato incompatible."));
        }
    }

    private void estructura_control() {
        coincidir("Palabra reservada while");
        coincidir("Parentesis abierto");
        expresion();
        coincidir("Parentesis cerrado");
        bloque();
    }

    private void expresion() {
        String nombreVariable = identificador();
        if (!variablesDeclaradas.containsKey(nombreVariable)) {
            int numeroLinea = getNumeroLinea();
            erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Variable '" + nombreVariable + "' no declarada."));
        }
        operador();
        constante();
        variablesNoUtilizadas.remove(nombreVariable);
    }

    private void operador() {
        String preanalisis = getPreanalisis();
        int numeroLinea = getNumeroLinea();
        if (!preanalisis.equals("Operador relacional") && !preanalisis.equals("Operador suma")) {
            erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Operador no válido."));
        } else {
            coincidir(preanalisis);
        }
    }

    private void bloque() {
        coincidir("Llave abierta");
        lista_sentencias();

        if (getPreanalisis().equals("Llave cerrada")) {
            coincidir("Llave cerrada");
        } else {
            int numeroLinea = (posicion - 1 < tokens.size()) ? tokens.get((posicion - 1)).getNumLinea() : -1;
            erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Falta la llave cerrada."));
        }
    }

    private void coincidir(String cadena) {
        int numeroLinea = getNumeroLinea();
        if (posicion < tokens.size() && cadena.equals(tokens.get(posicion).getDescripcion())) {
            posicion++;
        } else {
            erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Falta '" + cadena + "'."));
        }
    }

    private int getNumeroLinea() {
        if (posicion < tokens.size() && posicion > 0) {
            if (tokens.get(posicion).getNumLinea() > tokens.get((posicion - 1)).getNumLinea()) {
                return tokens.get((posicion - 1)).getNumLinea();
            } else {
                return tokens.get(posicion).getNumLinea();
            }
        } else {
            return tokens.get(posicion).getNumLinea();
        }
    }

    public void analizar(ArrayList<Lexema> tokens) {
        this.tokens = tokens;
        funcion();
        verificarVariablesNoUtilizadas();
    }

    private void verificarVariablesNoUtilizadas() {
        for (String variable : variablesDeclaradas.keySet()) {
            if (variablesNoUtilizadas.contains(variable)) {
                int numeroLinea = variablesDeclaradas.get(variable);
                erroresSemanticos.add(new ErrorSemantico(numeroLinea, "Error semántico: Variable '" + variable + "' declarada pero no utilizada."));
            }
        }
    }

    private String getPreanalisis() {
        return (posicion < tokens.size()) ? tokens.get(posicion).getDescripcion() : "";
    }

    public StringBuilder getErrores() {
        StringBuilder listaErrores = new StringBuilder();

        for (ErrorSemantico error : erroresSemanticos) {
            listaErrores.append("Línea ").append(error.getNumeroLinea())
                    .append(": ").append(error.getMensaje()).append("\n");
        }

        return listaErrores;
    }
}

class ErrorSemantico {

    private final int numeroLinea;
    private final String mensaje;

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
