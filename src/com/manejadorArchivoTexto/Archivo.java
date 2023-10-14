package com.manejadorArchivoTexto;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JOptionPane;

public class Archivo {

    private static final String RUTA_ARCHIVO = "src/com/manejadorArchivoTexto/codigo.txt";

    public static void abrirArchivo() {
        try {
            File archivo = new File(RUTA_ARCHIVO);

            // Verifica si Desktop es soportado en este entorno
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();

                // Asegúrate de que el archivo existe y es un archivo
                if (archivo.exists() && archivo.isFile()) {
                    desktop.open(archivo);  // Abre el archivo con la aplicación predeterminada
                } else {
                    JOptionPane.showMessageDialog(null, "El archivo no existe o no es un archivo válido.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "El soporte para Desktop no está disponible en este entorno.");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al abrir el archivo: " + e.getMessage());
        }
    }

    public static String leerArchivo() {
        // Inicializar un StringBuilder para almacenar el contenido del archivo
        StringBuilder contenido = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(RUTA_ARCHIVO)))) {
            String linea;
            boolean primeraLinea = true;

            while ((linea = br.readLine()) != null) {
                if (!primeraLinea) {
                    // Agregar salto de línea solo si no es la primera línea
                    contenido.append("\n");
                } else {
                    primeraLinea = false;
                }
                contenido.append(linea);
            }
        } catch (IOException e) {
            // Capturar cualquier excepción que ocurra durante la lectura del archivo
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }

        // Retornar el contenido del archivo como una cadena de texto
        return contenido.toString();
    }

    public static void escribirArchivo() {

    }
}
