package service;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Font;

import db.ConexionDB;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class ReporteService {

    private static final String CARPETA = "reportes/";

    // ── Paleta ───────────────────────────────────────────────────────
    private static final Color AZUL     = new Color(27,  58,  92);
    private static final Color AZUL_CLR = new Color(230, 241, 251);
    private static final Color CV       = new Color(26,  122, 26);
    private static final Color CV_CLR   = new Color(234, 243, 222);
    private static final Color CR       = new Color(192, 57,  43);
    private static final Color CR_CLR   = new Color(252, 235, 235);
    private static final Color CA       = new Color(192, 112, 0);
    private static final Color CA_CLR   = new Color(250, 238, 218);
    private static final Color GRIS     = new Color(247, 247, 247);
    private static final Color BLANCO   = Color.WHITE;

    // ── Fuentes (com.lowagie.text.Font) ──────────────────────────────
    private static final Font FT  = new Font(Font.HELVETICA, 13, Font.BOLD,   AZUL);
    private static final Font FST = new Font(Font.HELVETICA,  9, Font.NORMAL, new Color(80,80,80));
    private static final Font FC  = new Font(Font.HELVETICA,  9, Font.BOLD,   BLANCO);
    private static final Font FN  = new Font(Font.HELVETICA,  9, Font.NORMAL, Color.BLACK);
    private static final Font FB  = new Font(Font.HELVETICA,  9, Font.BOLD,   Color.BLACK);
    private static final Font FV  = new Font(Font.HELVETICA,  9, Font.BOLD,   CV);
    private static final Font FR  = new Font(Font.HELVETICA,  9, Font.BOLD,   CR);
    private static final Font FA  = new Font(Font.HELVETICA,  9, Font.BOLD,   CA);
    private static final Font FK  = new Font(Font.HELVETICA, 13, Font.BOLD,   AZUL);
    private static final Font FKL = new Font(Font.HELVETICA,  8, Font.NORMAL, new Color(100,100,100));
    private static final Font FCA = new Font(Font.HELVETICA,  9, Font.BOLD,   AZUL);

    // ════════════════════════════════════════════════════════════════
    // REPORTE 1 — Balance mensual con gráfico de barras agrupadas
    // ════════════════════════════════════════════════════════════════
    public void reporte1BalanceMensual(String idUsuario, String idPresupuesto,
                                       int anio, int mes) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("  Error: no se pudo conectar."); return; }
        try {
            int[] rango = rangoPresupuesto(conn, idPresupuesto);
            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            List<String[]> filas = new ArrayList<>();
            double totIng = 0, totGas = 0, totAho = 0;

            int a = rango[0], m = rango[1];
            while (a < rango[2] || (a == rango[2] && m <= rango[3])) {
                try (CallableStatement s = conn.prepareCall(
                        "{CALL sp_calcular_balance_mensual(?,?,?,?,?,?,?,?)}")) {
                    s.setString(1, idUsuario); s.setString(2, idPresupuesto);
                    s.setInt(3, a); s.setInt(4, m);
                    s.registerOutParameter(5, Types.DECIMAL);
                    s.registerOutParameter(6, Types.DECIMAL);
                    s.registerOutParameter(7, Types.DECIMAL);
                    s.registerOutParameter(8, Types.DECIMAL);
                    s.execute();
                    double ing = s.getDouble(5), gas = s.getDouble(6),
                           aho = s.getDouble(7), bal = s.getDouble(8);
                    String per = m + "/" + a;
                    dataset.addValue(ing, "Ingresos", per);
                    dataset.addValue(gas, "Gastos",   per);
                    dataset.addValue(aho, "Ahorros",  per);
                    filas.add(new String[]{per, fmt(ing), fmt(gas), fmt(aho), fmt(bal),
                                           bal >= 0 ? "POSITIVO" : "DEFICIT"});
                    totIng += ing; totGas += gas; totAho += aho;
                }
                m++; if (m > 12) { m = 1; a++; }
            }

            System.out.println();
            System.out.printf("  Ingresos: L.%,.2f  Gastos: L.%,.2f  Ahorros: L.%,.2f%n",
                    totIng, totGas, totAho);

            JFreeChart chart = ChartFactory.createBarChart(
                    "Resumen Mensual: Ingresos vs Gastos vs Ahorros",
                    "Mes/Año", "Monto (L.)", dataset,
                    PlotOrientation.VERTICAL, true, false, false);
            estilizarBarras(chart, new Color[]{
                new Color(46,109,164), new Color(192,57,43), new Color(39,174,96)});

            String nom = "reporte1_balance_" + anio + "_" + mes + ".pdf";
            Document doc = abrirDoc(nom, PageSize.A4);
            encabezado(doc, "Reporte 1 — Balance Mensual",
                    "Ingresos vs Gastos vs Ahorros", idUsuario, mes, anio);

            PdfPTable kpis = new PdfPTable(4);
            kpis.setWidthPercentage(100); kpis.setSpacingBefore(10); kpis.setSpacingAfter(10);
            double bal = totIng - totGas - totAho;
            kpi(kpis, "L. " + fmt(totIng), "Total Ingresos", AZUL_CLR);
            kpi(kpis, "L. " + fmt(totGas), "Total Gastos",   CR_CLR);
            kpi(kpis, "L. " + fmt(totAho), "Total Ahorros",  CV_CLR);
            kpi(kpis, "L. " + fmt(bal),    "Balance Global", bal >= 0 ? CV_CLR : CR_CLR);
            doc.add(kpis);

            doc.add(grafico(chart, 500, 220));
            doc.add(Chunk.NEWLINE);

            PdfPTable t = tabla(
                new String[]{"Mes/Año","Ingresos","Gastos","Ahorros","Balance","Estado"},
                new float[]{1.5f,2f,2f,2f,2f,1.5f});
            for (String[] f : filas) {
                boolean pos = f[5].equals("POSITIVO");
                celda(t, f[0],          GRIS,   FN, Element.ALIGN_CENTER);
                celda(t, "L. " + f[1],  BLANCO, FN, Element.ALIGN_RIGHT);
                celda(t, "L. " + f[2],  GRIS,   FN, Element.ALIGN_RIGHT);
                celda(t, "L. " + f[3],  BLANCO, FN, Element.ALIGN_RIGHT);
                celda(t, "L. " + f[4],  GRIS,   pos ? FV : FR, Element.ALIGN_RIGHT);
                t.addCell(celdaColor(f[5], pos ? CV_CLR : CR_CLR, pos ? FV : FR));
            }
            doc.add(t);
            pie(doc); doc.close(); abrir(nom);

        } catch (Exception e) {
            System.out.println("  Error R1: " + e.getMessage()); e.printStackTrace();
        } finally { cerrar(conn); }
    }

    // ════════════════════════════════════════════════════════════════
    // REPORTE 2 — Distribución de gastos con gráfico de dona
    // ════════════════════════════════════════════════════════════════
    public void reporte2GastosPorCategoria(String idUsuario, String idPresupuesto,
                                            int anio, int mes) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("  Error: no se pudo conectar."); return; }
        try (CallableStatement cats = conn.prepareCall("{CALL sp_listar_categorias(?,?)}")) {
            cats.setString(1, idUsuario); cats.setString(2, "gasto");
            ResultSet rs = cats.executeQuery();

            DefaultPieDataset dataset = new DefaultPieDataset();
            List<String[]> datos = new ArrayList<>();
            double total = 0;

            while (rs.next()) {
                String idC = rs.getString("id_categoria"), nom = rs.getString("nombre_categoria");
                double val = qDouble(conn,
                    "SELECT fn_obtener_total_ejecutado_categoria_mes(?,?,?,?) AS v",
                    idC, idPresupuesto, anio, mes);
                int txns = (int) qDouble(conn,
                    "SELECT COUNT(*) AS v FROM transaccion t " +
                    "INNER JOIN subcategoria s ON t.id_subcategoria=s.id_subcategoria " +
                    "WHERE s.id_categoria=? AND t.id_presupuesto=? AND t.anio=? AND t.mes=?",
                    idC, idPresupuesto, anio, mes);
                if (val > 0) {
                    dataset.setValue(nom, val);
                    datos.add(new String[]{nom, fmt(val), String.valueOf(txns)});
                    total += val;
                }
            }

            Color[] paleta = {new Color(46,109,164), new Color(192,57,43), new Color(39,174,96),
                              new Color(192,112,0),  new Color(155,89,182), new Color(22,160,133),
                              new Color(230,126,34), new Color(52,73,94)};

            JFreeChart chart = ChartFactory.createRingChart(
                    "Distribución de Gastos por Categoría", dataset, true, false, false);
            PiePlot plot = (PiePlot) chart.getPlot();
            plot.setBackgroundPaint(BLANCO); plot.setOutlineVisible(false);
            plot.setLabelGenerator(null);
            for (int i = 0; i < datos.size(); i++)
                plot.setSectionPaint(datos.get(i)[0], paleta[i % paleta.length]);
            chart.getLegend().setBackgroundPaint(BLANCO);
            chart.setBackgroundPaint(BLANCO);

            String nom = "reporte2_gastos_" + anio + "_" + mes + ".pdf";
            Document doc = abrirDoc(nom, PageSize.A4);
            encabezado(doc, "Reporte 2 — Distribución de Gastos",
                    "Por categoría — " + mes + "/" + anio, idUsuario, mes, anio);

            PdfPTable layout = new PdfPTable(2);
            layout.setWidthPercentage(100); layout.setSpacingBefore(10);
            layout.setWidths(new float[]{1.2f, 1f});

            PdfPCell gc = new PdfPCell(grafico(chart, 300, 260));
            gc.setBorder(Rectangle.NO_BORDER); gc.setPadding(4);
            layout.addCell(gc);

            PdfPTable tCats = tabla(new String[]{"Categoría","Monto","Txns","%"},
                                    new float[]{2.5f,2f,1f,1f});
            for (int i = 0; i < datos.size(); i++) {
                String[] d = datos.get(i);
                double v = Double.parseDouble(d[1].replace(",",""));
                double p = total > 0 ? (v / total) * 100 : 0;
                Color bg = (i % 2 == 0) ? BLANCO : GRIS;
                celda(tCats, d[0],                            bg, FN, Element.ALIGN_LEFT);
                celda(tCats, "L. " + d[1],                   bg, FN, Element.ALIGN_RIGHT);
                celda(tCats, d[2],                            bg, FN, Element.ALIGN_CENTER);
                celda(tCats, String.format("%.1f%%", p),      bg, FB, Element.ALIGN_RIGHT);
            }
            celda(tCats, "TOTAL",          AZUL_CLR, FB, Element.ALIGN_LEFT);
            celda(tCats, "L. " + fmt(total), AZUL_CLR, FB, Element.ALIGN_RIGHT);
            celda(tCats, "",               AZUL_CLR, FN, Element.ALIGN_CENTER);
            celda(tCats, "100%",           AZUL_CLR, FB, Element.ALIGN_RIGHT);

            PdfPCell tc = new PdfPCell(tCats);
            tc.setBorder(Rectangle.NO_BORDER); tc.setPadding(4);
            layout.addCell(tc);
            doc.add(layout);
            pie(doc); doc.close(); abrir(nom);

        } catch (Exception e) {
            System.out.println("  Error R2: " + e.getMessage()); e.printStackTrace();
        } finally { cerrar(conn); }
    }

    // ════════════════════════════════════════════════════════════════
    // REPORTE 3 — Cumplimiento con semáforo + barras comparativas
    // ════════════════════════════════════════════════════════════════
    public void reporte3Cumplimiento(String idUsuario, String idPresupuesto,
                                     int anio, int mes) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("  Error: no se pudo conectar."); return; }
        try (CallableStatement cats = conn.prepareCall("{CALL sp_listar_categorias(?,?)}")) {
            cats.setString(1, idUsuario); cats.setNull(2, Types.VARCHAR);
            ResultSet rsCats = cats.executeQuery();

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            String nom = "reporte3_cumplimiento_" + anio + "_" + mes + ".pdf";
            Document doc = abrirDoc(nom, PageSize.A4);
            encabezado(doc, "Reporte 3 — Cumplimiento de Presupuesto",
                    "Ejecutado vs Presupuestado — " + mes + "/" + anio, idUsuario, mes, anio);
            doc.add(new Paragraph("Semáforo: Verde < 80%  |  Amarillo 80-100%  |  Rojo > 100%",
                    new Font(Font.HELVETICA, 8, Font.ITALIC, new Color(100,100,100))));

            PdfPTable t = tabla(
                new String[]{"Categoría","Subcategoría","Presupuestado","Ejecutado","Diferencia","% Ejec."},
                new float[]{2f,2.5f,2f,2f,2f,1.5f});

            System.out.println();
            while (rsCats.next()) {
                String idC = rsCats.getString("id_categoria"),
                       nC  = rsCats.getString("nombre_categoria"),
                       tip = rsCats.getString("tipo_categoria");
                double tP = qDouble(conn,"SELECT fn_obtener_total_categoria_mes(?,?,?,?) AS v",
                                    idC,idPresupuesto,anio,mes);
                double tE = qDouble(conn,"SELECT fn_obtener_total_ejecutado_categoria_mes(?,?,?,?) AS v",
                                    idC,idPresupuesto,anio,mes);
                if (tP == 0 && tE == 0) continue;
                double porc = tP > 0 ? (tE / tP) * 100 : 0;
                dataset.addValue(tP, "Presupuestado", nC);
                dataset.addValue(tE, "Ejecutado",     nC);

                PdfPCell hdr = new PdfPCell(new Phrase(nC + " (" + tip + ")", FCA));
                hdr.setColspan(2); hdr.setBackgroundColor(AZUL_CLR); hdr.setPadding(5);
                t.addCell(hdr);
                celda(t, "L. " + fmt(tP), AZUL_CLR, FB, Element.ALIGN_RIGHT);
                celda(t, "L. " + fmt(tE), AZUL_CLR, FB, Element.ALIGN_RIGHT);
                double dif = tP - tE;
                celda(t, "L. " + fmt(dif), AZUL_CLR, dif >= 0 ? FV : FR, Element.ALIGN_RIGHT);
                t.addCell(celdaColor(String.format("%.1f%%", porc), semColor(porc), semFont(porc)));

                try (CallableStatement subs = conn.prepareCall("{CALL sp_listar_subcategorias_categoria(?)}")) {
                    subs.setString(1, idC);
                    ResultSet rS = subs.executeQuery();
                    boolean par = false;
                    while (rS.next()) {
                        String iS = rS.getString("id_subcategoria"),
                               nS = rS.getString("nombre_subcategoria");
                        double pS = qDouble(conn,
                            "SELECT IFNULL(monto_mensual,0) AS v FROM presupuestodetalle " +
                            "WHERE id_presupuesto=? AND id_subcategoria=? LIMIT 1",
                            idPresupuesto, iS, -1, -1);
                        double eS = qDouble(conn,
                            "SELECT fn_calcular_monto_ejecutado(?,?,?,?) AS v",
                            iS, idPresupuesto, anio, mes);
                        if (pS == 0 && eS == 0) continue;
                        double pP = pS > 0 ? (eS / pS) * 100 : 0;
                        Color bg = par ? GRIS : BLANCO;
                        celda(t, "",           bg, FN, Element.ALIGN_LEFT);
                        celda(t, "  " + nS,   bg, FN, Element.ALIGN_LEFT);
                        celda(t, "L. "+fmt(pS), bg, FN, Element.ALIGN_RIGHT);
                        celda(t, "L. "+fmt(eS), bg, FN, Element.ALIGN_RIGHT);
                        double dS = pS - eS;
                        celda(t, "L. "+fmt(dS), bg, dS >= 0 ? FV : FR, Element.ALIGN_RIGHT);
                        t.addCell(celdaColor(String.format("%.1f%%",pP), semColor(pP), semFont(pP)));
                        par = !par;
                    }
                }
            }
            doc.add(t);
            doc.add(Chunk.NEWLINE);

            JFreeChart chart = ChartFactory.createBarChart(
                    "Presupuestado vs Ejecutado por Categoría",
                    "Categoría","Monto (L.)", dataset,
                    PlotOrientation.HORIZONTAL, true, false, false);
            estilizarBarras(chart, new Color[]{new Color(46,109,164), new Color(192,57,43)});
            doc.add(grafico(chart, 480, 200));
            pie(doc); doc.close(); abrir(nom);

        } catch (Exception e) {
            System.out.println("  Error R3: " + e.getMessage()); e.printStackTrace();
        } finally { cerrar(conn); }
    }

    // ════════════════════════════════════════════════════════════════
    // REPORTE 4 — Tendencia con gráfico de líneas múltiples
    // ════════════════════════════════════════════════════════════════
    public void reporte4Tendencia(String idUsuario, String idPresupuesto,
                                   int aIni, int mIni, int aFin, int mFin) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("  Error: no se pudo conectar."); return; }

        List<int[]> periodos = new ArrayList<>();
        int a = aIni, m = mIni;
        while (a < aFin || (a == aFin && m <= mFin)) {
            periodos.add(new int[]{a, m}); m++; if (m > 12) { m = 1; a++; }
        }

        try (CallableStatement cats = conn.prepareCall("{CALL sp_listar_categorias(?,?)}")) {
            cats.setString(1, idUsuario); cats.setString(2, "gasto");
            ResultSet rsCats = cats.executeQuery();

            XYSeriesCollection dataset = new XYSeriesCollection();
            List<String> noms = new ArrayList<>();
            List<double[]> vals = new ArrayList<>();

            while (rsCats.next()) {
                String idC = rsCats.getString("id_categoria"),
                       nC  = rsCats.getString("nombre_categoria");
                XYSeries serie = new XYSeries(nC);
                double[] vs = new double[periodos.size()];
                for (int i = 0; i < periodos.size(); i++) {
                    double v = qDouble(conn,
                        "SELECT fn_obtener_total_ejecutado_categoria_mes(?,?,?,?) AS v",
                        idC, idPresupuesto, periodos.get(i)[0], periodos.get(i)[1]);
                    serie.add(i, v); vs[i] = v;
                }
                dataset.addSeries(serie); noms.add(nC); vals.add(vs);
            }

            JFreeChart chart = ChartFactory.createXYLineChart(
                    "Tendencia de Gastos por Categoría",
                    "Periodo", "Monto (L.)", dataset,
                    PlotOrientation.VERTICAL, true, false, false);
            XYPlot plot = chart.getXYPlot();
            plot.setBackgroundPaint(BLANCO);
            plot.setDomainGridlinePaint(new Color(220,220,220));
            plot.setRangeGridlinePaint(new Color(220,220,220));
            XYLineAndShapeRenderer ren = new XYLineAndShapeRenderer(true, true);
            Color[] lc = {new Color(46,109,164), new Color(192,57,43), new Color(39,174,96),
                          new Color(192,112,0),  new Color(155,89,182), new Color(22,160,133)};
            for (int i = 0; i < dataset.getSeriesCount(); i++) {
                ren.setSeriesPaint(i, lc[i % lc.length]);
                ren.setSeriesStroke(i, new java.awt.BasicStroke(2.5f));
            }
            plot.setRenderer(ren);

            String nom = "reporte4_tendencia_" + aIni + ".pdf";
            Document doc = abrirDoc(nom, PageSize.A4.rotate());
            encabezado(doc, "Reporte 4 — Tendencia de Gastos",
                    "Por categoría — " + mIni+"/"+aIni+" a "+mFin+"/"+aFin,
                    idUsuario, -1, -1);

            doc.add(grafico(chart, 700, 280));
            doc.add(Chunk.NEWLINE);

            int nCols = 1 + periodos.size();
            float[] ws = new float[nCols];
            ws[0] = 3f; for (int i = 1; i < nCols; i++) ws[i] = 2f;
            String[] cabs = new String[nCols];
            cabs[0] = "Categoría";
            for (int i = 0; i < periodos.size(); i++)
                cabs[i+1] = periodos.get(i)[1] + "/" + periodos.get(i)[0];
            PdfPTable t = tabla(cabs, ws);
            for (int i = 0; i < noms.size(); i++) {
                Color bg = (i % 2 == 0) ? BLANCO : GRIS;
                celda(t, noms.get(i), bg, FB, Element.ALIGN_LEFT);
                for (double v : vals.get(i)) celda(t, "L. " + fmt(v), bg, FN, Element.ALIGN_RIGHT);
            }
            doc.add(t);
            pie(doc); doc.close(); abrir(nom);

        } catch (Exception e) {
            System.out.println("  Error R4: " + e.getMessage()); e.printStackTrace();
        } finally { cerrar(conn); }
    }

    // ════════════════════════════════════════════════════════════════
    // REPORTE 5 — Obligaciones con pie de estado + tabla semáforo
    // ════════════════════════════════════════════════════════════════
    public void reporte5Obligaciones(String idUsuario, String idPresupuesto,
                                      int anio, int mes) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("  Error: no se pudo conectar."); return; }
        try (CallableStatement stmt = conn.prepareCall("{CALL sp_procesar_obligaciones_mes(?,?,?,?)}")) {
            stmt.setString(1,idUsuario); stmt.setInt(2,anio);
            stmt.setInt(3,mes); stmt.setString(4,idPresupuesto);
            ResultSet rs = stmt.executeQuery();

            int pendientes = 0, porVencer = 0, vencidas = 0;
            double sumaTotal = 0;
            List<String[]> filas = new ArrayList<>();

            while (rs.next()) {
                int dias = rs.getInt("dias_restantes");
                double monto = rs.getDouble("monto_fijo_mensual");
                sumaTotal += monto;
                String est;
                if      (dias < 0) { est = "VENCIDA";    vencidas++;   }
                else if (dias <= 3) { est = "POR VENCER"; porVencer++;  }
                else               { est = "PENDIENTE";  pendientes++; }
                filas.add(new String[]{rs.getString("nombre"),
                    rs.getString("nombre_subcategoria"), fmt(monto),
                    String.valueOf(rs.getInt("dia_vencimiento")), est, String.valueOf(dias)});
            }

            DefaultPieDataset pie = new DefaultPieDataset();
            if (pendientes > 0) pie.setValue("Pendientes ("  + pendientes + ")", pendientes);
            if (porVencer  > 0) pie.setValue("Por vencer ("  + porVencer  + ")", porVencer);
            if (vencidas   > 0) pie.setValue("Vencidas ("    + vencidas   + ")", vencidas);

            JFreeChart pieCh = ChartFactory.createPieChart(
                    "Estado de Obligaciones", pie, true, false, false);
            PiePlot pp = (PiePlot) pieCh.getPlot();
            pp.setBackgroundPaint(BLANCO); pp.setOutlineVisible(false);
            if (pendientes > 0) pp.setSectionPaint("Pendientes (" + pendientes + ")", CV);
            if (porVencer  > 0) pp.setSectionPaint("Por vencer ("  + porVencer  + ")", CA);
            if (vencidas   > 0) pp.setSectionPaint("Vencidas ("    + vencidas   + ")", CR);
            pieCh.setBackgroundPaint(BLANCO);

            String nom = "reporte5_obligaciones_" + anio + "_" + mes + ".pdf";
            Document doc = abrirDoc(nom, PageSize.A4);
            encabezado(doc, "Reporte 5 — Obligaciones Fijas",
                    "Estado de pagos — " + mes + "/" + anio, idUsuario, mes, anio);

            PdfPTable kpis = new PdfPTable(4);
            kpis.setWidthPercentage(100); kpis.setSpacingBefore(8); kpis.setSpacingAfter(8);
            kpi(kpis, String.valueOf(filas.size()), "Total",       AZUL_CLR);
            kpi(kpis, String.valueOf(pendientes),   "Pendientes",  CV_CLR);
            kpi(kpis, String.valueOf(porVencer),    "Por vencer",  CA_CLR);
            kpi(kpis, String.valueOf(vencidas),     "Vencidas",    CR_CLR);
            doc.add(kpis);

            PdfPTable layout = new PdfPTable(2);
            layout.setWidthPercentage(100); layout.setWidths(new float[]{1f, 1.8f});
            PdfPCell gc = new PdfPCell(grafico(pieCh, 220, 180));
            gc.setBorder(Rectangle.NO_BORDER); layout.addCell(gc);

            PdfPTable t = tabla(
                new String[]{"Obligación","Subcategoría","Monto","Día","Estado","Días"},
                new float[]{2.5f,2f,1.8f,1f,1.8f,1f});
            for (String[] f : filas) {
                int dias = Integer.parseInt(f[5]);
                Color bgE; Font fE;
                if      (f[4].equals("VENCIDA"))    { bgE = CR_CLR; fE = FR; }
                else if (f[4].equals("POR VENCER")) { bgE = CA_CLR; fE = FA; }
                else                                { bgE = CV_CLR; fE = FV; }
                celda(t, f[0], BLANCO, FN, Element.ALIGN_LEFT);
                celda(t, f[1], GRIS,   FN, Element.ALIGN_LEFT);
                celda(t, "L. "+f[2], BLANCO, FN, Element.ALIGN_RIGHT);
                celda(t, "Dia "+f[3], GRIS,  FN, Element.ALIGN_CENTER);
                t.addCell(celdaColor(f[4], bgE, fE));
                celda(t, dias < 0 ? Math.abs(dias)+"d retraso" : f[5]+"d",
                      dias < 0 ? CR_CLR : BLANCO, dias < 0 ? FR : FN, Element.ALIGN_CENTER);
            }
            celda(t, "TOTAL (" + filas.size() + ")", AZUL_CLR, FB, Element.ALIGN_LEFT);
            celda(t, "", AZUL_CLR, FN, Element.ALIGN_LEFT);
            celda(t, "L. " + fmt(sumaTotal), AZUL_CLR, FB, Element.ALIGN_RIGHT);
            for (int i = 0; i < 3; i++) celda(t, "", AZUL_CLR, FN, Element.ALIGN_LEFT);

            PdfPCell tcell = new PdfPCell(t);
            tcell.setBorder(Rectangle.NO_BORDER); layout.addCell(tcell);
            doc.add(layout);
            pie(doc); doc.close(); abrir(nom);

        } catch (Exception e) {
            System.out.println("  Error R5: " + e.getMessage()); e.printStackTrace();
        } finally { cerrar(conn); }
    }

    // ════════════════════════════════════════════════════════════════
    // REPORTE 6 — Metas de ahorro con barras de progreso horizontales
    // ════════════════════════════════════════════════════════════════
    public void reporte6Ahorros(String idUsuario, String idPresupuesto,
                                 int anio, int mes) {
        Connection conn = ConexionDB.conectar();
        if (conn == null) { System.out.println("  Error: no se pudo conectar."); return; }
        try (CallableStatement cats = conn.prepareCall("{CALL sp_listar_categorias(?,?)}")) {
            cats.setString(1, idUsuario); cats.setString(2, "ahorro");
            ResultSet rsCats = cats.executeQuery();

            DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            String nom = "reporte6_ahorros_" + anio + "_" + mes + ".pdf";
            Document doc = abrirDoc(nom, PageSize.A4);
            encabezado(doc, "Reporte 6 — Metas de Ahorro",
                    "Progreso — " + mes + "/" + anio, idUsuario, mes, anio);

            PdfPTable t = tabla(
                new String[]{"Meta","Obj. Mensual","Acumulado","Estado"},
                new float[]{2.5f,2f,2f,2f});

            System.out.println();
            boolean par = false;
            while (rsCats.next()) {
                String idC = rsCats.getString("id_categoria"),
                       nC  = rsCats.getString("nombre_categoria");
                double meta = qDouble(conn,
                    "SELECT fn_obtener_total_categoria_mes(?,?,?,?) AS v",
                    idC, idPresupuesto, anio, mes);
                double acum = qDouble(conn,
                    "SELECT fn_obtener_total_ejecutado_categoria_mes(?,?,?,?) AS v",
                    idC, idPresupuesto, anio, mes);
                double porc = meta > 0 ? Math.min((acum / meta) * 100, 100) : 0;
                String estado = porc >= 100 ? "Completada" : "En progreso";
                Font fE = porc >= 100 ? FV : porc >= 50 ? FA : FR;
                Color bgE = porc >= 100 ? CV_CLR : porc >= 50 ? CA_CLR : CR_CLR;
                dataset.addValue(porc, "Progreso (%)", nC);

                int bar = (int)(porc / 5);
                System.out.printf("  %-20s [%s%s] %.1f%%%n", nC,
                        "█".repeat(bar), "░".repeat(20 - bar), porc);

                Color bg = par ? GRIS : BLANCO;
                celda(t, nC,              bg,  FN, Element.ALIGN_LEFT);
                celda(t, "L. " + fmt(meta), bg, FN, Element.ALIGN_RIGHT);
                celda(t, "L. " + fmt(acum), bg, FB, Element.ALIGN_RIGHT);
                t.addCell(celdaColor(estado + " (" + String.format("%.1f%%", porc) + ")", bgE, fE));
                par = !par;
            }
            doc.add(t);
            doc.add(Chunk.NEWLINE);

            JFreeChart chart = ChartFactory.createBarChart(
                    "Progreso de Metas de Ahorro (%)",
                    "Meta", "% Completado", dataset,
                    PlotOrientation.HORIZONTAL, false, false, false);
            CategoryPlot plot = (CategoryPlot) chart.getPlot();
            plot.setBackgroundPaint(BLANCO);
            plot.setRangeGridlinePaint(new Color(220,220,220));
            BarRenderer br = (BarRenderer) plot.getRenderer();
            br.setSeriesPaint(0, new Color(39,174,96));
            br.setMaximumBarWidth(0.4);
            NumberAxis rAxis = (NumberAxis) plot.getRangeAxis();
            rAxis.setRange(0, 110);
            rAxis.setTickUnit(new org.jfree.chart.axis.NumberTickUnit(20));
            doc.add(grafico(chart, 460, 200));
            pie(doc); doc.close(); abrir(nom);

        } catch (Exception e) {
            System.out.println("  Error R6: " + e.getMessage()); e.printStackTrace();
        } finally { cerrar(conn); }
    }

    // ════════════════════════════════════════════════════════════════
    // HELPERS BD
    // ════════════════════════════════════════════════════════════════
    private double qDouble(Connection conn, String sql,
                            String p1, String p2, int p3, int p4) throws SQLException {
        try (PreparedStatement s = conn.prepareStatement(sql)) {
            s.setString(1, p1); s.setString(2, p2);
            if (p3 != -1) s.setInt(3, p3);
            if (p4 != -1) s.setInt(4, p4);
            ResultSet r = s.executeQuery();
            return r.next() ? r.getDouble("v") : 0;
        }
    }

    private int[] rangoPresupuesto(Connection conn, String idPres) throws SQLException {
        try (PreparedStatement s = conn.prepareStatement(
                "SELECT init_year,init_month,end_year,end_month " +
                "FROM presupuesto WHERE id_presupuesto=?")) {
            s.setString(1, idPres);
            ResultSet r = s.executeQuery();
            if (r.next()) return new int[]{r.getInt(1), r.getInt(2), r.getInt(3), r.getInt(4)};
        }
        return new int[]{2026, 1, 2026, 2};
    }

    // ════════════════════════════════════════════════════════════════
    // HELPERS PDF
    // ════════════════════════════════════════════════════════════════
    private Document abrirDoc(String nombre, Rectangle tam) throws Exception {
        new File(CARPETA).mkdirs();
        Document doc = new Document(tam, 36, 36, 50, 36);
        PdfWriter.getInstance(doc, new FileOutputStream(CARPETA + nombre));
        doc.open();
        return doc;
    }

    private void encabezado(Document doc, String titulo, String sub,
                             String idU, int mes, int anio) throws DocumentException {
        PdfPTable b = new PdfPTable(1);
        b.setWidthPercentage(100); b.setSpacingAfter(4);
        PdfPCell cb = new PdfPCell(new Phrase(titulo,
                new Font(Font.HELVETICA, 13, Font.BOLD, BLANCO)));
        cb.setBackgroundColor(AZUL); cb.setPadding(10); cb.setBorder(Rectangle.NO_BORDER);
        b.addCell(cb); doc.add(b);
        doc.add(new Paragraph(sub, FST));
        String per = (mes > 0 && anio > 0) ? mes + "/" + anio : "Todos los periodos";
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        doc.add(new Paragraph("Usuario: " + idU + "  |  Periodo: " + per + "  |  Generado: " + fecha,
                new Font(Font.HELVETICA, 7, Font.NORMAL, new Color(130,130,130))));
        doc.add(Chunk.NEWLINE);
    }

    private void kpi(PdfPTable t, String val, String lbl, Color bg) {
        PdfPCell c = new PdfPCell();
        c.setBackgroundColor(bg); c.setPadding(10);
        Paragraph p = new Paragraph();
        p.add(new Chunk(val + "\n", FK));
        p.add(new Chunk(lbl, FKL));
        p.setAlignment(Element.ALIGN_CENTER);
        c.addElement(p);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        t.addCell(c);
    }

    private PdfPTable tabla(String[] cabs, float[] ws) throws DocumentException {
        PdfPTable t = new PdfPTable(cabs.length);
        t.setWidthPercentage(100); t.setSpacingBefore(6); t.setWidths(ws);
        for (String c : cabs) {
            PdfPCell h = new PdfPCell(new Phrase(c, FC));
            h.setBackgroundColor(AZUL); h.setPadding(6);
            t.addCell(h);
        }
        return t;
    }

    private void celda(PdfPTable t, String txt, Color bg, Font f, int align) {
        PdfPCell c = new PdfPCell(new Phrase(txt, f));
        c.setBackgroundColor(bg); c.setPadding(4);
        c.setHorizontalAlignment(align); t.addCell(c);
    }

    private PdfPCell celdaColor(String txt, Color bg, Font f) {
        PdfPCell c = new PdfPCell(new Phrase(txt, f));
        c.setBackgroundColor(bg); c.setPadding(4);
        c.setHorizontalAlignment(Element.ALIGN_CENTER);
        return c;
    }

    private void pie(Document doc) throws DocumentException {
        doc.add(Chunk.NEWLINE);
        Paragraph p = new Paragraph(
            "Sistema de Presupuesto Personal — Teoría de Bases de Datos I — " +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            new Font(Font.HELVETICA, 7, Font.ITALIC, new Color(160,160,160)));
        p.setAlignment(Element.ALIGN_CENTER);
        doc.add(p);
    }

    private Image grafico(JFreeChart chart, int w, int h) throws Exception {
        chart.setBackgroundPaint(BLANCO);
        BufferedImage img = chart.createBufferedImage(w, h);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "PNG", baos);
        Image pdfImg = Image.getInstance(baos.toByteArray());
        pdfImg.setAlignment(Element.ALIGN_CENTER);
        return pdfImg;
    }

    private void estilizarBarras(JFreeChart chart, Color[] colores) {
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(BLANCO);
        plot.setDomainGridlinePaint(new Color(220,220,220));
        plot.setRangeGridlinePaint(new Color(220,220,220));
        BarRenderer r = (BarRenderer) plot.getRenderer();
        r.setMaximumBarWidth(0.3);
        for (int i = 0; i < colores.length; i++) r.setSeriesPaint(i, colores[i]);
        chart.getLegend().setBackgroundPaint(BLANCO);
        chart.setBackgroundPaint(BLANCO);
    }

    private Color semColor(double p) {
        return p > 100 ? CR_CLR : p >= 80 ? CA_CLR : CV_CLR;
    }

    private Font semFont(double p) {
        return p > 100 ? FR : p >= 80 ? FA : FV;
    }

    private String fmt(double v) { return String.format("%,.2f", v); }

    private void abrir(String nombre) {
        String ruta = new File(CARPETA + nombre).getAbsolutePath();
        System.out.println("\n  PDF generado: " + ruta);
        try {
            File f = new File(ruta);
            if (Desktop.isDesktopSupported() &&
                Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(f);
            } else {
                Runtime.getRuntime().exec("cmd /c start \"\" \"" + ruta + "\"");
            }
        } catch (Exception e) {
            System.out.println("  Abre manualmente: " + ruta);
        }
    }

    private void cerrar(Connection conn) {
        try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
    }
}
