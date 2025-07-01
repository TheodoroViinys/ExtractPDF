package com.estudosspring.projeto.services;

import com.estudosspring.projeto.dto.ImagePropertyDTO;
import com.estudosspring.projeto.process.PDFEngine;
import com.spire.doc.PdfConformanceLevel;
import com.spire.doc.ToPdfParameterList;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import com.spire.doc.Document;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class PDFConverter {

    private static ToPdfParameterList parameter = new ToPdfParameterList();

    public static List<ImagePropertyDTO> docxToPDF(byte[] bytes) throws IOException {

        Document doc = new Document(new ByteArrayInputStream(bytes));
        parameter.setPdfConformanceLevel(PdfConformanceLevel.Pdf_A_1_A);

        PDFEngine engine = new PDFEngine(Loader.loadPDF(bytes));
        return engine.getImagePropertyDTOs();
    }
}
