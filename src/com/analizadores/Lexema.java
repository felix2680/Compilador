package com.analizadores;

public class Lexema {
    private int token;
    private String lexema;
    private String descripcion;


    public int getToken() {
        return token;
    }

    public void setToken(int token) {
        this.token = token;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

}
