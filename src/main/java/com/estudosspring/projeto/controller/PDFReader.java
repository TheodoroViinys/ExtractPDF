package com.estudosspring.projeto.controller;

import com.estudosspring.projeto.enums.DOC_TYPE;
import com.estudosspring.projeto.exceptions.InvalidFormatException;
import org.apache.commons.io.FilenameUtils;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/search")
public class PDFReader {

    @GetMapping(path = "/archiver/word", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity findFileByWord(@RequestParam("word") String word, @RequestParam("files") List<MultipartFile> files) {

        try {
            List<String> founded = new ArrayList<>();

            for (MultipartFile file : files) {

                String type = FilenameUtils.getExtension(file.getOriginalFilename()).toUpperCase();

                switch (DOC_TYPE.valueOf(type)){

                    case PDF -> {
                        PDDocument doc = Loader.loadPDF(file.getBytes());
                        PDFTextStripper stripper = new PDFTextStripper();

                        if (stripper.getText(doc).toLowerCase().contains(word.toLowerCase())) {
                            founded.add(file.getOriginalFilename());
                        }
                    }

                    case DOCX -> {
                        XWPFDocument doc = new XWPFDocument(file.getInputStream());
                        String text = doc.getParagraphs().stream().map(XWPFParagraph::getText).collect(Collectors.joining("\n"));

                        if (text.toLowerCase().contains(word.toLowerCase())){
                            founded.add(file.getOriginalFilename());
                        }
                    }

                    default -> {
                        throw new InvalidFormatException();
                    }
                }

            }

            if (founded.isEmpty()) {
                return ResponseEntity.ok(List.of("A palavra não foi encontrada em nenhum arquivo"));
            }

            return ResponseEntity.accepted().body(founded);

        } catch (Exception e) {
            return ResponseEntity.of(
                    ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpStatus.CONFLICT.value()), e.getMessage())
            ).build();
        }
    }
}
