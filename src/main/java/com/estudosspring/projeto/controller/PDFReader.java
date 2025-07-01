package com.estudosspring.projeto.controller;

import com.estudosspring.projeto.enums.DOC_TYPE;
import com.estudosspring.projeto.exceptions.InvalidFormatException;
import com.estudosspring.projeto.services.PDFConverter;
import lombok.val;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/search")
public class PDFReader {

    @GetMapping(path = "/archiver/word", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity findFileByWord(@RequestParam("word") String word, @RequestParam("files") List<MultipartFile> files) {

        try {
            List<String> founded = new ArrayList<>();

            for (MultipartFile file : files) {

                if (Objects.requireNonNull(file.getContentType()).contains(DOC_TYPE.PDF.name().toLowerCase())) {
                    PDDocument doc = Loader.loadPDF(file.getBytes());
                    PDFTextStripper stripper = new PDFTextStripper();

                    if (stripper.getText(doc).toLowerCase().contains(word.toLowerCase())) {
                        founded.add(file.getOriginalFilename());
                    }

                } else if (Objects.requireNonNull(file.getContentType()).contains(DOC_TYPE.DOCX.name().toLowerCase())) {
                    val imagePropertyDTOS = PDFConverter.docxToPDF(file.getBytes());
                } else{
                    throw new InvalidFormatException();
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
