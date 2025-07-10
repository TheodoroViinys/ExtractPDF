package com.estudosspring.projeto.controller;


import com.estudosspring.projeto.enums.DOC_TYPE;
import com.estudosspring.projeto.exceptions.InvalidFormatException;
import com.estudosspring.projeto.process.DocumentEngine;
import com.estudosspring.projeto.utils.DocUtils;
import com.estudosspring.projeto.utils.UrlUtils;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@RestController
@RequestMapping("/extractor")
public class Extractor {

    private static final Logger log = LogManager.getLogger(Extractor.class);
    private HttpResponse<byte[]> response;

    @Autowired
    private DocumentEngine engine;
    @Autowired
    private ObjectNode node;

    @GetMapping("/test")
    public ResponseEntity<Object> test() {
        return ResponseEntity.ok("Everything is ok! 😁");
    }

    @PostMapping(path = "/file/path", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> getArchive(@RequestParam("file") MultipartFile file) {

        try {

            DOC_TYPE type = DocUtils.verifyTypeDoc(file.getOriginalFilename());

            switch (type) {
                case PDF -> {
                    return ResponseEntity.ok(engine.loadPDF(file.getInputStream()));
                }
                case DOCX -> {
                    return ResponseEntity.ok(engine.loadDOCX(file.getInputStream()));
                }

                case EPUB -> {
                    return ResponseEntity.ok(engine.loadEPUB(file.getInputStream()));
                }

                default -> {
                    throw new InvalidFormatException();
                }
            }

        } catch (InvalidFormatException e) {

            String extension = FilenameUtils.getExtension(file.getOriginalFilename());

            log.error("{} file : {}", e, extension);
            return ResponseEntity.internalServerError().body(e.getBody());

        } catch (Exception ex) {
            log.error("\n- The action could not be performed because: ", ex);
        }

        node.removeAll();
        node.put("description", "file not found");

        return ResponseEntity.internalServerError().body(node);
    }

    @PostMapping("/file/web")
    public ResponseEntity<?> getArchiveFromWeb(@RequestParam("file") String file) {

        try {

            doRequest(file);

            if (response.statusCode() == 200) {

                DOC_TYPE type = DocUtils.verifyTypeDoc(response.body());

                switch (type) {
                    case PDF -> {
                        return ResponseEntity.ok(engine.loadPDF(new ByteArrayInputStream(response.body())));
                    }

                    case DOCX -> {
                        return ResponseEntity.ok(engine.loadDOCX(new ByteArrayInputStream(response.body())));
                    }

                    case EPUB -> {
                        return ResponseEntity.ok(engine.loadEPUB(new ByteArrayInputStream(response.body())));
                    }
                    case HTML -> {
                        String url = UrlUtils.getGoogleDocUrlDownload(file, DOC_TYPE.PDF);

                        if (url != null) {
                            return getArchiveFromWeb(url);
                        }
                    }

                    case DEFAULT -> {
                        throw new InvalidFormatException();
                    }
                }

            }
        } catch (InvalidFormatException e) {
            log.error(e);
            return ResponseEntity.internalServerError().body(e.getBody());

        } catch (Exception ex) {
            log.error("\n- The action could not be performed because: ", ex);
        }

        node.removeAll();
        node.put("description", "file not found");

        return ResponseEntity.internalServerError().body(node);
    }

    private void doRequest(String file) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        HttpRequest request = HttpRequest.newBuilder(URI.create(file)).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
    }

}
