package com.GUI;

import com.analizadores.AnalizadorLexico;
import com.analizadores.AnalizadorSemantico;
import com.analizadores.AnalizadorSintactico;
import com.analizadores.Lexema;
import com.manejadorArchivoTexto.ManejadorArchivo;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

public class VentanaAnalizador extends JFrame {

    private ArrayList<Lexema> listaLexemas;

    public final int ANCHO_VENTANA = 855, ALTO_VENTANA = 720;
    public final int ANCHO_PANEL = 855, ALTO_PANEL = 890;
    private JTextArea areaCodigo, areaRespuestaSintactico, areaRespuestaSemantico;
    private final JScrollPane scrollAreaCodigo, scrollAreaRespuestaSintactico, scrollAreaRespuestaSemantico;
    private final JScrollPane scrollTabla;
    private JButton btnAnalizar, btnLimpiar, btnAbrir;
    private final JTable tabla;
    private final DefaultTableModel modeloTabla;
    private NumeroLinea numLinea;
    private JPanel panelPrincipal;

    public VentanaAnalizador() {
        // Crea un panel principal
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(null);

        // Agrega los componentes al panel principal
        areaCodigo = new JTextArea(10, 35);
        areaCodigo.setSize(200, 300);
        areaCodigo.setLineWrap(true);
        panelPrincipal.add(areaCodigo);

        scrollAreaCodigo = new JScrollPane(areaCodigo);
        scrollAreaCodigo.setBounds(50, 50, 740, 300);
        numLinea = new NumeroLinea(areaCodigo);
        scrollAreaCodigo.setRowHeaderView(numLinea);
        panelPrincipal.add(scrollAreaCodigo);

        areaRespuestaSintactico = new JTextArea(10, 35);
        areaRespuestaSintactico.setSize(200, 300);
        areaRespuestaSintactico.setLineWrap(true);
        areaRespuestaSintactico.setEditable(false);
        panelPrincipal.add(areaRespuestaSintactico);

        scrollAreaRespuestaSintactico = new JScrollPane(areaRespuestaSintactico);
        scrollAreaRespuestaSintactico.setBounds(420, 450, 370, 200);
        panelPrincipal.add(scrollAreaRespuestaSintactico);

        areaRespuestaSemantico = new JTextArea(10, 35);
        areaRespuestaSemantico.setSize(200, 300);
        areaRespuestaSemantico.setLineWrap(true);
        areaRespuestaSemantico.setEditable(false);
        areaRespuestaSemantico.setForeground(Color.red);
        areaRespuestaSemantico.setFont(new Font("Arial", Font.BOLD, 15));
        panelPrincipal.add(areaRespuestaSemantico);

        scrollAreaRespuestaSemantico = new JScrollPane(areaRespuestaSemantico);
        scrollAreaRespuestaSemantico.setBounds(50, 650, 740, 200);
        panelPrincipal.add(scrollAreaRespuestaSemantico);

        btnAnalizar = new JButton("ANALIZAR");
        btnAnalizar.setBounds(50, 380, 120, 35);
        btnAnalizar.setIcon(new ImageIcon("src/com/Imagenes/analizando.png"));
        btnAnalizar.setIconTextGap(2);
        btnAnalizar.addActionListener(this::actionPerformed);
        panelPrincipal.add(btnAnalizar);

        btnAbrir = new JButton("ABRIR");
        btnAbrir.setBounds(50, 5, 120, 35);
        btnAbrir.setIcon(new ImageIcon("src/com/Imagenes/abrir-documento.png"));
        btnAbrir.setIconTextGap(2);
        btnAbrir.addActionListener(this::actionPerformed);
        panelPrincipal.add(btnAbrir);

        btnLimpiar = new JButton("LIMPIAR");
        btnLimpiar.setBounds(180, 380, 120, 35);
        btnLimpiar.setIcon(new ImageIcon("src/com/Imagenes/limpiar.png"));
        btnLimpiar.setIconTextGap(2);
        btnLimpiar.addActionListener(this::actionPerformed);
        panelPrincipal.add(btnLimpiar);

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
        panelPrincipal.add(scrollTabla);

        listaLexemas = new ArrayList<>();

        // Agrega el panel principal a un JScrollPane vertical
        JScrollPane scrollPrincipal = new JScrollPane(panelPrincipal, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Agrega el JScrollPane al JFrame
        add(scrollPrincipal);

        // Configuración adicional de la ventana
        setSize(ANCHO_VENTANA, ALTO_VENTANA);
        setTitle("Analizador Lexico - Sintactico v2");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        panelPrincipal.setPreferredSize(new Dimension(ANCHO_PANEL, ALTO_PANEL));
        scrollPrincipal.getViewport().setPreferredSize(new Dimension(ANCHO_PANEL, ALTO_PANEL));
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
                areaRespuestaSintactico.setForeground(Color.decode("#0e822c"));
                areaRespuestaSintactico.setFont(new Font("Arial", Font.BOLD, 15));
                areaRespuestaSintactico.setText("El codigo es válido");
            } catch (AnalizadorSintactico.GrammarException ex) {
                areaRespuestaSintactico.setForeground(Color.red);
                areaRespuestaSintactico.setFont(new Font("Arial", Font.BOLD, 15));
                areaRespuestaSintactico.setText(ex.getMessage());
            } finally {
                AnalizadorSemantico as = new AnalizadorSemantico();
                as.analizar(listaLexemas);
                areaRespuestaSemantico.setText(as.getErrores().toString());
            }
        } else if (e.getSource() == btnLimpiar) {
            eliminarDatosTabla();
            areaCodigo.setText("");
            areaRespuestaSintactico.setText("");
        } else if (e.getSource() == btnAbrir) {
            ManejadorArchivo.abrirArchivo();
        }
    }

    public void llenarTabla(ArrayList<Lexema> lexemas) {
        // Añade las nuevas filas al modelo
        for (Lexema lexema : lexemas) {
            modeloTabla.addRow(new Object[]{lexema.getLexema(), lexema.getDescripcion(), lexema.getToken()});
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
