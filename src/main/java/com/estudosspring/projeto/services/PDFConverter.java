package com.estudosspring.projeto.services;

import com.estudosspring.projeto.dto.ImagePropertyDTO;
import com.estudosspring.projeto.process.PDFEngine;
import com.spire.doc.PdfConformanceLevel;
import com.spire.doc.ToPdfParameterList;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import com.spire.doc.Document;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class PDFConverter {

    private static ToPdfParameterList parameter = new ToPdfParameterList();

    private static Path path = Path.of("C:\\Users\\ADMIN\\TEMP_PDF");
    private static File temp;

    static {
        try {
            temp = File.createTempFile("temp", ".pdf", path.toFile());
            //TODO criar uma função chamada "private String getPath()" que ira retornar todo esse escopo do path e file como caminho
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<ImagePropertyDTO> docxToPDF(File file) throws IOException {

        Document doc = new Document(file.getAbsolutePath()) {
        };

        parameter.setPdfConformanceLevel(PdfConformanceLevel.Pdf_A_1_A);

        if (!path.toFile().exists()) {
            path.toFile().mkdirs();
        }

        doc.saveToFile(temp.getAbsolutePath(), parameter);

        PDFEngine engine = new PDFEngine(Loader.loadPDF(temp));

        return engine.getImagePropertyDTOs();
    }

    public static List<ImagePropertyDTO> docxToPDF(byte[] bytes) throws IOException {

        Document doc = new Document(new ByteArrayInputStream(bytes));
        parameter.setPdfConformanceLevel(PdfConformanceLevel.Pdf_A_1_A);

        if (!path.toFile().exists()) {
            path.toFile().mkdirs();
        }

        doc.saveToFile(temp.getAbsolutePath(), parameter);

        PDFEngine engine = new PDFEngine(Loader.loadPDF(temp));
        return engine.getImagePropertyDTOs();
    }
}
