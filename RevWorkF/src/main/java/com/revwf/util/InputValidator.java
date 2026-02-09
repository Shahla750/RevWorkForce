package com.revwf.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class InputValidator {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[0-9]{10}$");
    
    private static final Pattern EMPLOYEE_ID_PATTERN = 
        Pattern.compile("^[A-Z]{2,}[0-9]{3,}$");
    
    private static final SimpleDateFormat DATE_FORMAT = 
        new SimpleDateFormat("yyyy-MM-dd");
    
    static {
        DATE_FORMAT.setLenient(false);
    }
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate phone number format
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Validate employee ID format
     */
    public static boolean isValidEmployeeId(String employeeId) {
        return employeeId != null && EMPLOYEE_ID_PATTERN.matcher(employeeId).matches();
    }
    
    /**
     * Validate date format (yyyy-MM-dd)
     */
    public static boolean isValidDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return false;
        }
        
        try {
            DATE_FORMAT.parse(dateString);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    
    /**
     * Parse date string to Date object
     */
    public static Date parseDate(String dateString) throws ParseException {
        return DATE_FORMAT.parse(dateString);
    }
    
    /**
     * Format date to string
     */
    public static String formatDate(Date date) {
        return DATE_FORMAT.format(date);
    }
    
    /**
     * Validate string is not null or empty
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.trim().isEmpty();
    }
    
    /**
     * Validate numeric input
     */
    public static boolean isValidNumber(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate integer input
     */
    public static boolean isValidInteger(String str) {
        if (str == null || str.trim().isEmpty()) {
            return false;
        }
        
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate salary amount
     */
    public static boolean isValidSalary(String salaryStr) {
        if (!isValidNumber(salaryStr)) {
            return false;
        }
        
        double salary = Double.parseDouble(salaryStr);
        return salary >= 0 && salary <= 10000000; // Max 1 crore
    }
    
    /**
     * Sanitize input string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        
        return input.trim().replaceAll("[<>\"'&]", "");
    }
    
    /**
     * Validate menu choice
     */
    public static boolean isValidMenuChoice(String choice, int maxOption) {
        if (!isValidInteger(choice)) {
            return false;
        }
        
        int option = Integer.parseInt(choice);
        return option >= 1 && option <= maxOption;
    }
}