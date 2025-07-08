package com.estudosspring.projeto.utils;

import com.estudosspring.projeto.enums.DOC_TYPE;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.util.Strings;

import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DocUtils {

    private static Pattern googleDriveType = Pattern.compile("^" + "([A-Z]+) - GOOGLE DRIVE" + "$");

    public static DOC_TYPE verifyTypeDoc(byte[] body) throws Exception {


        if (body[0] == (byte) 0x25 && body[1] == (byte) 0x50 && body[2] == (byte) 0x44 && body[3] == (byte) 0x46) {
            return DOC_TYPE.PDF;

        } else if (body[0] == (byte) 0xD0 && body[1] == (byte) 0xCF && body[2] == (byte) 0x11 && body[3] == (byte) 0xE0 && body[4] == (byte) 0xA1 && body[5] == (byte) 0xB1 && body[6] == (byte) 0x1A && body[7] == (byte) 0xE1) {
            return DOC_TYPE.EPUB;

        } else if (body[0] == (byte) 0x50 && body[1] == (byte) 0x4B && body[2] == (byte) 0x03 && body[3] == (byte) 0x04) {
            return DOC_TYPE.DOCX;

        } else if (new String(body).contains("PNG")) {
            return DOC_TYPE.PNG;

        } else if (new String(body).contains("<!DOCTYPE html>")) {
            return DOC_TYPE.HTML;

        } else {
            return DOC_TYPE.DEFAULT;
        }
    }

    public static DOC_TYPE verifyTypeDoc(String fileName) {

        if (Strings.isBlank(fileName)) {
            return DOC_TYPE.DEFAULT;
        }

        String upperCase = FilenameUtils.getExtension(fileName).toUpperCase();

        try {
            return DOC_TYPE.valueOf(upperCase);
        }catch (IllegalArgumentException ie){

            Matcher matcher = googleDriveType.matcher(upperCase);

            if (matcher.find()) {
                return DOC_TYPE.valueOf(matcher.group(1));
            }

        }catch (Exception e){
            log.error("Tipo de arquivo não encontrado");
        }

        return DOC_TYPE.DEFAULT;
    }

    public static DOC_TYPE verifyTypeDoc(InputStream stream) throws Exception {
        DOC_TYPE docType = verifyTypeDoc(stream.readAllBytes());
        return docType;
    }
}
