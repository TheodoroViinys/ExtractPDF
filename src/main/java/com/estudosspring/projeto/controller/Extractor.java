package com.estudosspring.projeto.controller;


import com.aspose.words.Document;
import com.aspose.words.DocumentVisitor;
import com.estudosspring.projeto.dto.ImagePropertyDTO;
import com.estudosspring.projeto.enums.DOC_TYPE;
import com.estudosspring.projeto.exceptions.InvalidFormatException;
import com.estudosspring.projeto.process.OutputStreamDocument;
import com.estudosspring.projeto.process.PDFEngine;
import com.estudosspring.projeto.utils.DocUtils;
import jakarta.servlet.http.HttpServletRequest;
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
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RestController
@RequestMapping("/extractor")
public class Extractor {

    private static final Logger log = LogManager.getLogger(Extractor.class);
    private HttpClient client;
    private HttpRequest request;
    private HttpResponse<byte[]> response;

    @GetMapping("/test")
    public ResponseEntity<Object> test() {
        return ResponseEntity.ok("Everything is ok! 😁");
    }

    private void doRequest(String file) throws IOException, InterruptedException {
        client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.ALWAYS).build();
        request = HttpRequest.newBuilder(URI.create(file)).GET().build();
        response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    @GetMapping("/go")
    public void go(HttpServletResponse res, HttpServletRequest req) {
        try {
            String sys = req.getHeader("User-Agent");

            if (sys.contains("linux") || sys.contains("Linux")) {
                res.sendRedirect("https://youtu.be/dQw4w9WgXcQ?si=d_h0qpbTxXMjZ7Z4");

            } else if (sys.contains("windows") || sys.contains("Windows")) {
                res.sendRedirect("https://youtu.be/P6antjcBFZ4?si=6K92uF4O5oP3mNj_");

            } else if (sys.contains("mac") || sys.contains("Mac") || sys.contains("MacOs")) {
                res.sendRedirect("https://youtu.be/kqKDnZtOFYk?si=NAMN5LEg--EMzq9s");

            } else {
                res.sendRedirect("https://youtube.com/shorts/pwVprFivVfA?si=z9JB2J7a03_VrQY7");

            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    @PostMapping(path = "/file/path", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public List<ImagePropertyDTO> inspectType(@RequestParam("file") MultipartFile file) throws IOException {

        try {

            DOC_TYPE type = DocUtils.verifyTypeDoc(file.getBytes());

            switch (type) {
                case PDF -> {
                    return load(file.getBytes());
                }
                case DOCX -> {
                    return loadDocx(file.getInputStream());

                }

                case EPUB -> {
                    return loadEPub(file.getInputStream());
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

    private List<ImagePropertyDTO> loadEPub(InputStream inputStream) throws Exception {
        Document nodes = new Document(inputStream);
        return null;
    }

    private List<ImagePropertyDTO> loadDocx(InputStream stream) throws IOException {
        List<XWPFPictureData> allPictures = new XWPFDocument(stream).getAllPictures();

        List<ImagePropertyDTO> images = new ArrayList<>();

        for (XWPFPictureData picture : allPictures) {
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
    private List<ImagePropertyDTO> getArchiveFromWeb(@RequestParam("file") String file) throws IOException, InterruptedException {

        Matcher matcher = Pattern.compile("https://docs.google.com/document/d/([a-zA-Z0-9_-]+)").matcher(file);

        boolean isGoogleDoc = matcher.find();

        if (isGoogleDoc){
            file = matcher.group() + "/export?format=pdf";
        }

        doRequest(file);

        List<ImagePropertyDTO> dtoList = new ArrayList<>();

        if (response.statusCode() == 200) {

            //testar doc também https://revistauox.paginas.ufsc.br/files/2013/05/Modelo-de-artigo-para-a-Revista-uox.doc
            DOC_TYPE type = DocUtils.verifyTypeDoc(response.body());

            switch (type) {
                case PDF -> {
                    OutputStreamDocument outputStreamDocument = new OutputStreamDocument();
                    outputStreamDocument.write(response.body());
                    dtoList = outputStreamDocument.getImagePropertyDTOS();
                }
                case DOCX -> {
                    dtoList = loadDocx(new ByteArrayInputStream(response.body()));
                }

                case EPUB -> {

                }

                case DEFAULT -> {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(response.body())));
                    bufferedReader.readLine();
                }
            }

        }

        return dtoList;
    }

}
