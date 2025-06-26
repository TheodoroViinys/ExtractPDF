package com.estudosspring.projeto.process;

import com.estudosspring.projeto.dto.ImagePropertyDTO;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFEngine extends PDFStreamEngine {

    private List<BufferedImage> images = new ArrayList<>();
    private List<ImagePropertyDTO> imagePropertyDTOs = new ArrayList<>();
    private PDDocument document;

    public PDFEngine(PDDocument document) throws IOException {
        this.document = document;
        processDocument(document);
        document.close();
    }

    private void processDocument(PDDocument document) throws IOException {
        for(PDPage page : document.getPages()){
            processPage(page);
        }
    }

    @Override
    protected void processOperator(Operator operator, List<COSBase> operands) throws IOException {

        for (var base : operands) {
            if (base instanceof COSName name && operator.getName().equals("Do")) {
                PDImageXObject thumbnail = PDImageXObject.createThumbnail(getResources().getXObject(name).getCOSObject());
                try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    ImageIO.write(thumbnail.getImage(), "PNG", baos);
                    byte[] imageBytes = baos.toByteArray();
                    imagePropertyDTOs.add(new ImagePropertyDTO(thumbnail.getWidth(), thumbnail.getHeight(), imageBytes));
                };
            }
        }

        if (imagePropertyDTOs.isEmpty()) {
            super.processOperator(operator, operands);
        }
    }

    public List<BufferedImage> getImages() {
        return images;
    }

    public List<ImagePropertyDTO> getImagePropertyDTOs() {
        return imagePropertyDTOs;
    }
}
