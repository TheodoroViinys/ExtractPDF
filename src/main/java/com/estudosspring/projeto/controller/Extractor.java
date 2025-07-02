package com.estudosspring.projeto.controller;


import com.estudosspring.projeto.dto.ImagePropertyDTO;
import com.estudosspring.projeto.enums.DOC_TYPE;
import com.estudosspring.projeto.exceptions.InvalidFormatException;
import com.estudosspring.projeto.process.OutputStreamDocument;
import com.estudosspring.projeto.process.PDFEngine;
import com.estudosspring.projeto.records.FileRecord;
import com.estudosspring.projeto.services.PDFConverter;
import com.estudosspring.projeto.utils.DocUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/extractor")
public class Extractor {

    private static final Logger log = LogManager.getLogger(Extractor.class);
    private HttpClient client;
    private HttpRequest request;
    private HttpResponse<byte[]> response;

    @GetMapping("/test")
    public ResponseEntity<Object> test(){
        return ResponseEntity.ok("Everything is ok! 😁");
    }

    @GetMapping("/go")
    public void go(HttpServletResponse req){
        try {
            req.sendRedirect("https://youtu.be/dQw4w9WgXcQ?si=d_h0qpbTxXMjZ7Z4");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @PostMapping(path = "/file/path", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public List<ImagePropertyDTO> inspectType(@RequestParam("file") MultipartFile file) throws IOException {

        try {
            System.out.println("Arquivo recebido: " + file.getOriginalFilename());
            System.out.println("Tipo: " + file.getContentType());
            
            DOC_TYPE type = DocUtils.verifyTypeDoc(file.getBytes());

            switch (type) {
                case PDF -> {
                    return load(file.getBytes());
                }
                case DOCX -> {
                    return loadDocx(file.getInputStream());

                }
                default -> {
                    throw new InvalidFormatException();
                }
            }

        } catch (Exception ex) {
            log.error("\n- The action could not be performed because: ", ex);
        }
        return List.of();
    }

    private List<ImagePropertyDTO> loadDocx(InputStream stream) throws IOException {
        List<XWPFPictureData> allPictures = new XWPFDocument(stream).getAllPictures();

        List<ImagePropertyDTO> images = new ArrayList<>();

        for (XWPFPictureData picture : allPictures){
            Image image = new ImageIcon(picture.getData()).getImage();
            images.add(new ImagePropertyDTO(image.getWidth(null), image.getHeight(null), picture.getData()));
        }

        return images;
    }

    private List<ImagePropertyDTO> load(byte[] file) throws IOException {
        PDDocument doc = Loader.loadPDF(file);
        PDFEngine engine = new PDFEngine(doc);
        return engine.getImagePropertyDTOs();
    }

    @PostMapping("/file/web")
    private List<ImagePropertyDTO> getArchiveFromWeb(@RequestBody FileRecord file) throws IOException, InterruptedException {
        client = HttpClient.newHttpClient();
        request = HttpRequest.newBuilder(URI.create(file.path())).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

        List<ImagePropertyDTO> dtoList = new ArrayList<>();

        if (response.statusCode() == 200) {


            DOC_TYPE type = DocUtils.verifyTypeDoc(response.body());

            switch (type) {
                case PDF -> {
                    OutputStreamDocument outputStreamDocument = new OutputStreamDocument();
                    outputStreamDocument.write(response.body());
                    dtoList = outputStreamDocument.getImagePropertyDTOS();
                }
                case DOCX -> {
                    dtoList = PDFConverter.docxToPDF(response.body());
                }
            }


        }

        return dtoList;
    }

}
