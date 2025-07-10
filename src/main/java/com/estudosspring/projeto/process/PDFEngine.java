package com.estudosspring.projeto.process;

import com.estudosspring.projeto.dto.ImagePropertyDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.stream.Streams;
import org.apache.pdfbox.contentstream.PDFStreamEngine;
import org.apache.pdfbox.contentstream.operator.Operator;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class PDFEngine extends PDFStreamEngine {


    private List<ImagePropertyDTO> imagePropertyDTOs;

    protected void processPDF(PDDocument document) throws IOException {
        ImageIO.setUseCache(false);
        imagePropertyDTOs = new ArrayList<>();
        processDocument(document);
        document.close();
    }

    private void processDocument(PDDocument document) throws IOException {
        Streams.of(document.getPages()).parallel().forEach(p -> {
            try {
                processPage(p);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });

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
                }
                ;
            }
        }

        if (imagePropertyDTOs.isEmpty()) {
            super.processOperator(operator, operands);
        }
    }


    protected List<ImagePropertyDTO> getImagePropertyDTOs() {
        return imagePropertyDTOs;
    }
}
