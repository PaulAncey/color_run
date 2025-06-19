package fr.esgi.color_run.util;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.layout.properties.VerticalAlignment;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Div;

import fr.esgi.color_run.business.Course;
import fr.esgi.color_run.business.Participation;
import fr.esgi.color_run.business.Utilisateur;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

/**
 * Utilitaire pour générer des documents PDF (dossards) avec iText 7
 */
public class PDFGenerator {

    // Couleurs du thème Color Run
    private static final Color COLOR_PRIMARY = new DeviceRgb(255, 90, 95); // #FF5A5F
    private static final Color COLOR_SECONDARY = new DeviceRgb(108, 117, 125); // #6C757D
    private static final Color COLOR_LIGHT = new DeviceRgb(248, 249, 250); // #F8F9FA

    /**
     * Génère un dossard au format PDF
     * @param participation La participation pour laquelle générer le dossard
     * @param utilisateur L'utilisateur participant
     * @param course La course concernée
     * @return Le contenu du PDF sous forme de tableau d'octets
     */
    public static byte[] generateBibNumber(Participation participation, Utilisateur utilisateur, Course course) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc, PageSize.A4);
            
            // Marges
            document.setMargins(36, 36, 36, 36);
            
            // Polices
            PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont bibFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            
            // === EN-TÊTE ===
            addHeader(document, titleFont, normalFont);
            
            // === TITRE DE L'ÉVÉNEMENT ===
            Paragraph eventTitle = new Paragraph(course.getNom())
                    .setFont(titleFont)
                    .setFontSize(24)
                    .setFontColor(COLOR_PRIMARY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(20)
                    .setMarginBottom(10);
            document.add(eventTitle);
            
            // === INFORMATIONS DE LA COURSE ===
            String dateStr = course.getDateCourse().toString();
            String heureStr = course.getHeureDebut().toString();
            Paragraph eventInfo = new Paragraph(String.format("%s à %s - %s", dateStr, heureStr, course.getVille()))
                    .setFont(normalFont)
                    .setFontSize(14)
                    .setFontColor(COLOR_SECONDARY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(30);
            document.add(eventInfo);
            
            // === NUMÉRO DE DOSSARD (PRINCIPAL) ===
            Div bibSection = new Div();
            bibSection.setBorder(Border.NO_BORDER)
                     .setBackgroundColor(COLOR_LIGHT)
                     .setPadding(20)
                     .setMarginBottom(20);
            
            Paragraph bibLabel = new Paragraph("NUMÉRO DE DOSSARD")
                    .setFont(normalFont)
                    .setFontSize(12)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            bibSection.add(bibLabel);
            
            Paragraph bibNumber = new Paragraph(String.valueOf(participation.getNumeroDossard()))
                    .setFont(bibFont)
                    .setFontSize(72)
                    .setFontColor(COLOR_PRIMARY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(10);
            bibSection.add(bibNumber);
            
            document.add(bibSection);
            
            // === INFORMATIONS DU PARTICIPANT ===
            Paragraph participantTitle = new Paragraph("PARTICIPANT")
                    .setFont(titleFont)
                    .setFontSize(16)
                    .setMarginTop(20)
                    .setMarginBottom(10);
            document.add(participantTitle);
            
            Paragraph participantName = new Paragraph(String.format("%s %s", utilisateur.getPrenom(), utilisateur.getNom()))
                    .setFont(bibFont)
                    .setFontSize(20)
                    .setFontColor(COLOR_PRIMARY)
                    .setMarginBottom(15);
            document.add(participantName);
            
            // === TABLE DES DÉTAILS ===
            Table detailsTable = new Table(2);
            detailsTable.setWidth(UnitValue.createPercentValue(100));
            detailsTable.setMarginBottom(20);
            
            addTableRow(detailsTable, "Distance:", String.format("%.1f km", course.getDistanceKm()), normalFont);
            addTableRow(detailsTable, "Prix:", String.format("%.2f €", course.getPrix()), normalFont);
            addTableRow(detailsTable, "Obstacles:", course.isAvecObstacles() ? "Oui" : "Non", normalFont);
            addTableRow(detailsTable, "Adresse:", course.getAdresse(), normalFont);
            
            if (course.getCauseSoutenue() != null && !course.getCauseSoutenue().isEmpty()) {
                addTableRow(detailsTable, "Cause soutenue:", course.getCauseSoutenue(), normalFont);
            }
            
            document.add(detailsTable);
            
            // === PIED DE PAGE ===
            addFooter(document, normalFont, participation);
            
            document.close();
            return baos.toByteArray();
            
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du dossard PDF", e);
        }
    }
    
    /**
     * Ajoute l'en-tête du document
     */
    private static void addHeader(Document document, PdfFont titleFont, PdfFont normalFont) {
        try {
            Paragraph header = new Paragraph("COLOR RUN")
                    .setFont(titleFont)
                    .setFontSize(28)
                    .setFontColor(COLOR_PRIMARY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(5);
            document.add(header);
            
            Paragraph subHeader = new Paragraph("DOSSARD OFFICIEL")
                    .setFont(normalFont)
                    .setFontSize(12)
                    .setFontColor(COLOR_SECONDARY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginBottom(20);
            document.add(subHeader);
        } catch (Exception e) {
            // En cas d'erreur, ajouter un en-tête simple
            document.add(new Paragraph("COLOR RUN - DOSSARD OFFICIEL"));
        }
    }
    
    /**
     * Ajoute une ligne au tableau des détails
     */
    private static void addTableRow(Table table, String label, String value, PdfFont font) {
        try {
            Cell labelCell = new Cell()
                    .add(new Paragraph(label))
                    .setFont(font)
                    .setFontSize(12)
                    .setBorder(Border.NO_BORDER)
                    .setPaddingBottom(8)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);
            
            Cell valueCell = new Cell()
                    .add(new Paragraph(value))
                    .setFont(font)
                    .setFontSize(12)
                    .setBorder(Border.NO_BORDER)
                    .setPaddingBottom(8)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE);
            
            table.addCell(labelCell);
            table.addCell(valueCell);
        } catch (Exception e) {
            // En cas d'erreur, ajouter des cellules simples
            table.addCell(label);
            table.addCell(value);
        }
    }
    
    /**
     * Ajoute le pied de page
     */
    private static void addFooter(Document document, PdfFont normalFont, Participation participation) {
        try {
            Paragraph footer = new Paragraph(String.format("Dossard généré le %s - ID: %d", 
                    java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                    participation.getIdParticipation()))
                    .setFont(normalFont)
                    .setFontSize(8)
                    .setFontColor(COLOR_SECONDARY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(30);
            document.add(footer);
            
            Paragraph website = new Paragraph("www.colorrun.fr")
                    .setFont(normalFont)
                    .setFontSize(10)
                    .setFontColor(COLOR_PRIMARY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(10);
            document.add(website);
        } catch (Exception e) {
            // Pied de page simple en cas d'erreur
            document.add(new Paragraph("Color Run - www.colorrun.fr"));
        }
    }
}