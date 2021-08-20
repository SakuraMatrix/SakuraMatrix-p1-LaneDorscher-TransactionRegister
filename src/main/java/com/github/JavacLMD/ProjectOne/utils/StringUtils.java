package com.github.JavacLMD.ProjectOne.utils;
import java.util.regex.Pattern;

public class StringUtils {

    //checks if a string is an email
    public static boolean isEmail(String text) {
        try {
            String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";

            Pattern pat = Pattern.compile(emailRegex);
            if (text == null)
                return false;
            return pat.matcher(text).matches();
        } catch (Exception e) {
            return false;
        }
    }

    //checks if a string is an integer
    public static boolean isInteger(String text) {
        try {
            Integer.parseInt(text);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
