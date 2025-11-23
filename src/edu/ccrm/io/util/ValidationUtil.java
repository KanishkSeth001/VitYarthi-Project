package edu.ccrm.io.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern REG_NO_PATTERN = 
        Pattern.compile("^[A-Za-z0-9]{2,20}$");
    
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    public static boolean isValidRegNo(String regNo) {
        return regNo != null && REG_NO_PATTERN.matcher(regNo).matches();
    }
    
    public static boolean isValidCredits(int credits) {
        return credits > 0 && credits <= 6;
    }
}