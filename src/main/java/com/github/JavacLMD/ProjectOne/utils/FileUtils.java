package com.github.JavacLMD.ProjectOne.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static String resourceFileToString(String resourcePath) {
        try {
            Path path = Paths.get(FileUtils.class.getResource(resourcePath).toURI());
            log.debug("Found " + path);
            return Files.readString(path);
        } catch (URISyntaxException | IOException | NullPointerException e) {
            log.error(e.getMessage());
            return "";
        }
    }

}
