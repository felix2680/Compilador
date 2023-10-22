package com.GUI;

import com.analizadores.AnalizadorLexico;
import com.analizadores.AnalizadorSemantico;
import com.analizadores.AnalizadorSintactico;
import com.analizadores.Lexema;
import com.manejadorArchivoTexto.ManejadorArchivo;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

public class VentanaAnalizador extends JFrame {

    private ArrayList<Lexema> listaLexemas;

    public final int ANCHO_VENTANA = 855, ALTO_VENTANA = 700;
    private JTextArea areaCodigo, areaRespuesta;
    private final JScrollPane scrollAreaCodigo, scrollAreaRespuesta;
    private final JScrollPane scrollTabla;
    private JButton btnAnalizar, btnLimpiar, btnAbrir;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private NumeroLinea numLinea;

    public VentanaAnalizador() {

        setLayout(null);
        areaCodigo = new JTextArea(10, 35);
        areaCodigo.setSize(200, 300);
        areaCodigo.setLineWrap(true);

        scrollAreaCodigo = new JScrollPane(areaCodigo);
        scrollAreaCodigo.setBounds(50, 50, 740, 300);
        numLinea = new NumeroLinea(areaCodigo);
        scrollAreaCodigo.setRowHeaderView(numLinea);
        add(scrollAreaCodigo);

        areaRespuesta = new JTextArea(10, 35);
        areaRespuesta.setSize(200, 300);
        areaRespuesta.setLineWrap(true);
        areaRespuesta.setEditable(false);

        scrollAreaRespuesta = new JScrollPane(areaRespuesta);
        scrollAreaRespuesta.setBounds(420, 450, 370, 200);
        add(scrollAreaRespuesta);

        btnAnalizar = new JButton("ANALIZAR");
        btnAnalizar.setBounds(50, 380, 120, 35);
        btnAnalizar.setIcon(new ImageIcon("src/com/Imagenes/analizando.png"));
        btnAnalizar.setIconTextGap(2);
        btnAnalizar.addActionListener(this::actionPerformed);
        add(btnAnalizar);

        btnAbrir = new JButton("ABRIR");
        btnAbrir.setBounds(50, 5, 120, 35);
        btnAbrir.setIcon(new ImageIcon("src/com/Imagenes/abrir-documento.png"));
        btnAbrir.setIconTextGap(2);
        btnAbrir.addActionListener(this::actionPerformed);
        add(btnAbrir);

        btnLimpiar = new JButton("LIMPIAR");
        btnLimpiar.setBounds(180, 380, 120, 35);
        btnLimpiar.setIcon(new ImageIcon("src/com/Imagenes/limpiar.png"));
        btnLimpiar.setIconTextGap(2);
        btnLimpiar.addActionListener(this::actionPerformed);
        add(btnLimpiar);

        modeloTabla = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Hacer que todas las celdas sean no editables
                return false;
            }
        };

        modeloTabla.addColumn("LEXEMA");
        modeloTabla.addColumn("TOKEN");
        modeloTabla.addColumn("TIPO");

        tabla = new JTable(modeloTabla);
        tabla.getColumnModel().getColumn(0).setPreferredWidth(5);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(5);
        scrollTabla = new JScrollPane(tabla);

        scrollTabla.setBounds(50, 450, 370, 200);
        add(scrollTabla);

        listaLexemas = new ArrayList<>();

        setSize(ANCHO_VENTANA, ALTO_VENTANA);
        setTitle("Analizador Lexico - Sintactico v2");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAnalizar) {
            eliminarDatosTabla();
            if (areaCodigo.getText().isEmpty()) {
                areaCodigo.setText(ManejadorArchivo.leerArchivo());
            }
            listaLexemas = AnalizadorLexico.analizar(areaCodigo.getText());
            llenarTabla(listaLexemas);
            try {
                AnalizadorSintactico a = new AnalizadorSintactico();
                a.analizar(listaLexemas);
                areaRespuesta.setForeground(Color.decode("#0e822c"));
                areaRespuesta.setFont(new Font("Arial", Font.BOLD, 15));
                areaRespuesta.setText("El codigo es válido");
            } catch (AnalizadorSintactico.GrammarException ex) {
                areaRespuesta.setForeground(Color.red);
                areaRespuesta.setFont(new Font("Arial", Font.BOLD, 15));
                areaRespuesta.setText(ex.getMessage());
            }finally{
                AnalizadorSemantico as = new AnalizadorSemantico();
                try {
                    as.analizar(listaLexemas);
                } catch (AnalizadorSemantico.SemanticException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        } else if (e.getSource() == btnLimpiar) {
            eliminarDatosTabla();
            areaCodigo.setText("");
            areaRespuesta.setText("");
        } else if (e.getSource() == btnAbrir) {
            ManejadorArchivo.abrirArchivo();
        }
    }

    public void llenarTabla(ArrayList<Lexema> lexemas) {
        // Añade las nuevas filas al modelo
        for (Lexema lexema : lexemas) {
            modeloTabla.addRow(new Object[]{lexema.getLexema(), lexema.getDescripcion(), lexema.getToken()});
            System.out.println(lexema.getNumLinea());
        }
    }

    public void eliminarDatosTabla() {
        listaLexemas.clear();
        DefaultTableModel model = (DefaultTableModel) tabla.getModel();
        // Elimina todas las filas existentes en la tabla
        int rowCount = model.getRowCount();
        for (int i = rowCount - 1; i >= 0; i--) {
            model.removeRow(i);
        }
    }
}
