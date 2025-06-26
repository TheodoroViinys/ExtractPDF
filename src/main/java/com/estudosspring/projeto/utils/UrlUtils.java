package com.estudosspring.projeto.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.URISyntaxException;

public class UrlUtils {
    private static final Logger log = LogManager.getLogger(UrlUtils.class);

    public static boolean isAbsoluteUrl(String url){
        try {
            URI uri = new URI(url);
            return uri.isAbsolute();
        } catch (URISyntaxException e) {
            log.error("Error when trying to verify the veracity of the url because: ", e);
        }
        return false;
    }

}
