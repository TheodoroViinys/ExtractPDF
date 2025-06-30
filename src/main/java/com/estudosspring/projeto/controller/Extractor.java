package com.estudosspring.projeto.controller;


import com.estudosspring.projeto.dto.ImagePropertyDTO;
import com.estudosspring.projeto.enums.DOC_TYPE;
import com.estudosspring.projeto.process.OutputStreamDocument;
import com.estudosspring.projeto.process.PDFEngine;
import com.estudosspring.projeto.records.FileRecord;
import com.estudosspring.projeto.services.PDFConverter;
import com.estudosspring.projeto.utils.DocUtils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/extrator")
public class Extractor {

    private static final Logger log = LogManager.getLogger(Extractor.class);
    private HttpClient client;
    private HttpRequest request;
    private HttpResponse<byte[]> response;

    @GetMapping("/docker")
    public ResponseEntity<String> docker(){
        return ResponseEntity.ok("Everything is ok! 🐋");
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("Everything is ok! 😁");
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
                    return PDFConverter.docxToPDF(file.getBytes());

                }
                default -> {
                    return null;
                }
            }

        } catch (Exception ex) {
            log.error("\n- The action could not be performed because: ", ex);
        }
        return List.of();
    }

    private List<ImagePropertyDTO> convertToPDF(File file) throws IOException {
        List<ImagePropertyDTO> dtos = PDFConverter.docxToPDF(file);
        return dtos;
    }

    private List<ImagePropertyDTO> load(byte[] file) throws IOException {
        PDDocument doc = Loader.loadPDF(file);
        PDFEngine engine = new PDFEngine(doc);
        return engine.getImagePropertyDTOs();
    }

    private List<ImagePropertyDTO> load(PDDocument doc) throws IOException {
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
