package com.example.demo.service;

import com.example.demo.entity.Certificate;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.awt.Color;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;

import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

@Service
public class CertificatePdfService {

    private static final String VERIFY_BASE_URL =
            "http://localhost:8080/certificates/verify/";

    public byte[] generateCertificatePdf(Certificate certificate) {

        try {

            ByteArrayOutputStream out = new ByteArrayOutputStream();

            /*
            -This creates memory space to hold PDF data
            -We are NOT saving file on disk
            -Everything is generated in RAM
            */

            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);

            document.open();

            // add border
            // this create a rectangle with 36pt margin on all sides
            Rectangle rect = new Rectangle(
                    36, 36,
                    PageSize.A4.getWidth() - 36,
                    PageSize.A4.getHeight() - 36
            );
            rect.setBorder(Rectangle.BOX);
            rect.setBorderWidth(3);
            rect.setBorderColor(Color.BLACK);
            document.add(rect);

            // LMS Name(This adds top branding.)
            Font lmsFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph lms = new Paragraph("LearnTrust LMS", lmsFont);
            lms.setAlignment(Element.ALIGN_CENTER);
            document.add(lms);

            document.add(new Paragraph("\n"));

            // Big bold title in center.
            Font titleFont = new Font(Font.HELVETICA, 28, Font.BOLD);
            Paragraph title =
                    new Paragraph("CERTIFICATE OF COMPLETION", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph("\n\n"));

            /* 
                -This is dynamic.
                -It is coming from database.
                -It prints the student name in big blue font. */
            Font nameFont =
                    new Font(Font.HELVETICA, 24, Font.BOLD, Color.BLUE);

            Paragraph studentName =
                    new Paragraph(
                            certificate.getStudent().getName().toUpperCase(),
                            nameFont
                    );

            studentName.setAlignment(Element.ALIGN_CENTER);
            document.add(studentName);

            document.add(new Paragraph("\n"));

            // COURSE (fetched from DB through JPA relationship)
            Font normalFont = new Font(Font.HELVETICA, 16);

            Paragraph courseText =
                    new Paragraph(
                            "has successfully completed the course\n\n" +
                                    certificate.getCourse().getTitle(),
                            normalFont
                    );

            courseText.setAlignment(Element.ALIGN_CENTER);
            document.add(courseText);

            document.add(new Paragraph("\n\n"));

            // Issue Date (fetched from DB through JPA relationship.)
            Paragraph issued =
                    new Paragraph(
                            "Issued on: " + certificate.getIssuedAt(),
                            normalFont
                    );

            issued.setAlignment(Element.ALIGN_CENTER);
            document.add(issued);

            document.add(new Paragraph("\n"));

            // Certificate Number
            Paragraph certNo =
                    new Paragraph(
                            "Certificate No: LT-" + certificate.getId(),
                            normalFont
                    );

            certNo.setAlignment(Element.ALIGN_CENTER);
            document.add(certNo);

            document.add(new Paragraph("\n\n"));

            //  QR Code (ZXing)
            /* 
               Java library used to generate and read barcodes and QR codes.*/
            String verifyUrl =
                    VERIFY_BASE_URL + certificate.getCertificateHash();

                    /* 
                    ZXing generates a QR code pattern.

                    MultiFormatWriter() → class that generates barcode/QR

                   .encode() → converts text into QR format

                   verifyUrl → the data stored in QR    

                    BarcodeFormat.QR_CODE → specifies QR (not barcode)

                   200, 200 → width and height in pixels */

            BitMatrix matrix =
                    new MultiFormatWriter().encode(
                            verifyUrl,
                            BarcodeFormat.QR_CODE,
                            200,
                            200
                    );

            BufferedImage qrImage =
                    MatrixToImageWriter.toBufferedImage(matrix);
            
                    /* 
                    
                    This step:

                      Converts image to PNG format

                       Stores it inside memory (qrOut)
                    
         */
            ByteArrayOutputStream qrOut =
                    new ByteArrayOutputStream();

            ImageIO.write(qrImage, "PNG", qrOut);

            Image pdfQrImage =
                    Image.getInstance(qrOut.toByteArray());

            pdfQrImage.setAlignment(Image.ALIGN_CENTER);

            document.add(pdfQrImage);

            document.add(new Paragraph("\n\n"));

            //  Blockchain Section
            Font smallFont = new Font(Font.HELVETICA, 9);

            document.add(new Paragraph("Blockchain Proof", smallFont));
            document.add(new Paragraph(
                    "Transaction Hash: " +
                            certificate.getBlockchainTransactionHash(),
                    smallFont
            ));

            document.add(new Paragraph(
                    "Certificate Hash: " +
                            certificate.getCertificateHash(),
                    smallFont
            ));

            if (certificate.getTokenId() != null) {
                document.add(new Paragraph(
                        "Token ID: " +
                                certificate.getTokenId(),
                        smallFont
                ));
            }

            document.close(); //close document to finalize PDF

            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating certificate PDF", e);
        }
    }
}
