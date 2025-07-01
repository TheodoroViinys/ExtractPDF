package com.estudosspring.projeto.controller;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/search")
public class PDFReader {

    @GetMapping(path = "/archiver/{word}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity findFileByWord(@PathVariable String word, @RequestParam("files") List<MultipartFile> files){

        try{
            List<String> founded = new ArrayList<>();

            for (MultipartFile file : files){
                PDDocument doc = Loader.loadPDF(file.getBytes());
                PDFTextStripper stripper = new PDFTextStripper();

                if (stripper.getText(doc).toLowerCase().contains(word.toLowerCase())){
                    founded.add(file.getName());
                }
            }

            if (founded.isEmpty()){
                return ResponseEntity.ok(List.of("A palavra não foi encontrada em nenhum arquivo"));
            }

            return ResponseEntity.accepted().body(founded);

        }catch (Exception e){
            return ResponseEntity.of(
                    ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(HttpStatus.CONFLICT.value()), e.getMessage())
            ).build();
        }
    }
}
