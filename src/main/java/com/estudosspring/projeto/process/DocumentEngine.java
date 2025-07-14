package com.estudosspring.projeto.process;

import com.aspose.words.ImageData;
import com.aspose.words.NodeCollection;
import com.aspose.words.NodeType;
import com.aspose.words.Shape;
import com.estudosspring.projeto.dto.ImagePropertyDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class DocumentEngine  {

    @Autowired
    private ObjectMapper mapper;

    public DocumentEngine() {
        ImageIO.setUseCache(true);
    }

    public String loadPDF(InputStream file) throws Exception {
        PDDocument doc = Loader.loadPDF(file.readAllBytes());
        List<ImagePropertyDTO> images = new ArrayList<>();
        for (PDPage page : doc.getPages()) {

            for (COSName xObjectName : page.getResources().getXObjectNames()) {
                if (page.getResources().getXObject(xObjectName) instanceof PDImageXObject image){
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    ImageIO.write(image.getImage(), "png", out);
                    images.add(new ImagePropertyDTO(image.getWidth(), image.getHeight(), out.toByteArray() ));

                    out.close();
                }
            }
        }


        return mapper.valueToTree(images).toPrettyString();
    }

    public List<ImagePropertyDTO> loadDOCX(InputStream stream) throws IOException {
        List<XWPFPictureData> allPictures = new XWPFDocument(stream).getAllPictures();

        List<ImagePropertyDTO> images = new ArrayList<>();

        for (XWPFPictureData picture : allPictures) {
            Image image = new ImageIcon(picture.getData()).getImage();
            images.add(new ImagePropertyDTO(image.getWidth(null), image.getHeight(null), picture.getData()));
        }

        return images;
    }

    public List<ImagePropertyDTO> loadEPUB(InputStream inputStream) throws Exception {
        com.aspose.words.Document nodes = new com.aspose.words.Document(inputStream);

        NodeCollection<Shape> childNodes = nodes.getChildNodes(NodeType.SHAPE, true);
        List<ImagePropertyDTO> images = new ArrayList<>();
        for (Shape shape : childNodes) {
            if (shape.hasImage()) {
                ImageData imageData = shape.getImageData();
                images.add(
                        new ImagePropertyDTO(
                                imageData.getImageSize().getWidthPixels(),
                                imageData.getImageSize().getHeightPixels(),
                                imageData.toByteArray()
                        )
                );
            }
        }

        return images;
    }

}
