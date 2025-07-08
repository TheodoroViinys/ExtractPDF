package com.estudosspring.projeto.utils;

import com.estudosspring.projeto.enums.DOC_TYPE;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UrlUtils {
    private static final Logger log = LogManager.getLogger(UrlUtils.class);
    private static Pattern googleDocPattern = Pattern.compile("https://docs.google.com/document/d/([a-zA-Z0-9_-]+)");
    private static Pattern googleDrivePattern = Pattern.compile("https://drive.google.com/file/d/([a-zA-Z0-9_-]+)");

    public static boolean isAbsoluteUrl(String url){
        try {
            URI uri = new URI(url);
            return uri.isAbsolute();
        } catch (URISyntaxException e) {
            log.error("Error when trying to verify the veracity of the url because: ", e);
        }
        return false;
    }

    @Nullable
    public static String getGoogleDocUrlDownload(@Nonnull String file, @Nonnull DOC_TYPE format) throws MalformedURLException {

        if (isAbsoluteUrl(file)){

            Matcher docMatcher = googleDocPattern.matcher(file);
            Matcher driveMatcher = googleDrivePattern.matcher(file);

            if (docMatcher.find()) {

                return docMatcher.group() + "/export?format=" + format.name().toLowerCase();

            } else if (driveMatcher.find()) {
                return "https://drive.usercontent.google.com/download?id=" + driveMatcher.group(1) + "&export=download";

            }
        }


        return null;
    }

}
