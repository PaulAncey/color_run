package fr.esgi.color_run.util;

import fr.esgi.color_run.business.Course;
import fr.esgi.color_run.business.Participation;
import fr.esgi.color_run.business.Utilisateur;

/**
 * Utilitaire pour générer des documents PDF (dossards, etc.)
 * Note: Cette classe est un exemple et nécessite une bibliothèque comme iText ou PDFBox
 * pour fonctionner réellement. À implémenter selon vos besoins.
 */
public class PDFGenerator {

    /**
     * Génère un dossard au format PDF
     * @param participation La participation pour laquelle générer le dossard
     * @param utilisateur L'utilisateur participant
     * @param course La course concernée
     * @return Le contenu du PDF sous forme de tableau d'octets
     */
    public static byte[] generateBibNumber(Participation participation, Utilisateur utilisateur, Course course) {
        // Dans une implémentation réelle, utilisez une bibliothèque comme iText ou PDFBox
        // pour générer le PDF avec le design du dossard
        
        // Exemple avec iText (à implémenter):
        /*
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);
            
            document.open();
            
            // Ajouter le logo de l'événement
            // document.add(new Image(...));
            
            // Ajouter le titre de l'événement
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
            Paragraph title = new Paragraph(course.getNom(), titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            // Ajouter la date et le lieu
            Font infoFont = new Font(Font.FontFamily.HELVETICA, 16);
            Paragraph info = new Paragraph(course.getDateCourse() + " - " + course.getVille(), infoFont);
            info.setAlignment(Element.ALIGN_CENTER);
            document.add(info);
            
            // Ajouter le numéro de dossard (grande taille)
            Font bibFont = new Font(Font.FontFamily.HELVETICA, 72, Font.BOLD);
            Paragraph bib = new Paragraph(String.valueOf(participation.getNumeroDossard()), bibFont);
            bib.setAlignment(Element.ALIGN_CENTER);
            document.add(bib);
            
            // Ajouter le nom du participant
            Font nameFont = new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD);
            Paragraph name = new Paragraph(utilisateur.getPrenom() + " " + utilisateur.getNom(), nameFont);
            name.setAlignment(Element.ALIGN_CENTER);
            document.add(name);
            
            // Ajouter un QR code pour scanner le dossard
            // BarcodeQRCode qrCode = new BarcodeQRCode(String.valueOf(participation.getIdParticipation()), 100, 100, null);
            // Image qrCodeImage = qrCode.getImage();
            // qrCodeImage.setAlignment(Element.ALIGN_CENTER);
            // document.add(qrCodeImage);
            
            document.close();
            
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la génération du dossard PDF", e);
        }
        */
        
        // Pour cet exemple, retourner un tableau vide (à remplacer par l'implémentation réelle)
        return new byte[0];
    }
}