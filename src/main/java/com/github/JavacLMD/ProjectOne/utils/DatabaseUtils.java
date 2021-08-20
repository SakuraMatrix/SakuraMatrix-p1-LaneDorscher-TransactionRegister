package com.github.JavacLMD.ProjectOne.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseUtils {
    private static final Logger log = LoggerFactory.getLogger(DatabaseUtils.class);

    public static String[] getStatementsFromString(String fileString) {
        String[] statements = fileString.split(";");
        for (int i = 0; i < statements.length; i++) {
            statements[i] = statements[i].trim() + ";";
        }
        if (log.isDebugEnabled())
            log.debug("Found " + statements.length + " statements in string! ");
        return statements;
    }

    public static String[] getStatementsFromFile(String resourcePath) {
        return getStatementsFromString(FileUtils.resourceFileToString(resourcePath));
    }

}
