package com.revwf.util;

import org.junit.Test;

import com.revwf.util.InputValidator;

import static org.junit.Assert.*;

public class InputValidatorTest {
    
    @Test
    public void testValidEmail() {
        assertTrue("Valid email should pass", 
                  InputValidator.isValidEmail("test@example.com"));
        assertTrue("Valid email with subdomain should pass", 
                  InputValidator.isValidEmail("user@mail.company.com"));
        assertTrue("Valid email with numbers should pass", 
                  InputValidator.isValidEmail("user123@test.org"));
    }
    
    @Test
    public void testInvalidEmail() {
        assertFalse("Email without @ should fail", 
                   InputValidator.isValidEmail("testexample.com"));
        assertFalse("Email without domain should fail", 
                   InputValidator.isValidEmail("test@"));
        assertFalse("Email without username should fail", 
                   InputValidator.isValidEmail("@example.com"));
        assertFalse("Null email should fail", 
                   InputValidator.isValidEmail(null));
        assertFalse("Empty email should fail", 
                   InputValidator.isValidEmail(""));
    }
    
    @Test
    public void testValidPhone() {
        assertTrue("10 digit phone should pass", 
                  InputValidator.isValidPhone("9876543210"));
        assertTrue("Phone with all same digits should pass", 
                  InputValidator.isValidPhone("1111111111"));
    }
    
    @Test
    public void testInvalidPhone() {
        assertFalse("9 digit phone should fail", 
                   InputValidator.isValidPhone("987654321"));
        assertFalse("11 digit phone should fail", 
                   InputValidator.isValidPhone("98765432101"));
        assertFalse("Phone with letters should fail", 
                   InputValidator.isValidPhone("987654321a"));
        assertFalse("Phone with special chars should fail", 
                   InputValidator.isValidPhone("9876-54321"));
        assertFalse("Null phone should fail", 
                   InputValidator.isValidPhone(null));
        assertFalse("Empty phone should fail", 
                   InputValidator.isValidPhone(""));
    }
    
    @Test
    public void testValidEmployeeId() {
        assertTrue("Valid employee ID should pass", 
                  InputValidator.isValidEmployeeId("EMP001"));
        assertTrue("Admin ID should pass", 
                  InputValidator.isValidEmployeeId("ADMIN001"));
        assertTrue("Manager ID should pass", 
                  InputValidator.isValidEmployeeId("MGR001"));
    }
    
    @Test
    public void testInvalidEmployeeId() {
        assertFalse("ID with only numbers should fail", 
                   InputValidator.isValidEmployeeId("123456"));
        assertFalse("ID with only letters should fail", 
                   InputValidator.isValidEmployeeId("EMPLOYEE"));
        assertFalse("ID starting with number should fail", 
                   InputValidator.isValidEmployeeId("1EMP001"));
        assertFalse("Short ID should fail", 
                   InputValidator.isValidEmployeeId("E1"));
        assertFalse("Null ID should fail", 
                   InputValidator.isValidEmployeeId(null));
        assertFalse("Empty ID should fail", 
                   InputValidator.isValidEmployeeId(""));
    }
    
    @Test
    public void testValidDate() {
        assertTrue("Valid date should pass", 
                  InputValidator.isValidDate("2024-01-15"));
        assertTrue("Valid date should pass", 
                  InputValidator.isValidDate("2023-12-31"));
        assertTrue("Valid leap year date should pass", 
                  InputValidator.isValidDate("2024-02-29"));
    }
    
    @Test
    public void testInvalidDate() {
        assertFalse("Invalid date format should fail", 
                   InputValidator.isValidDate("15-01-2024"));
        assertFalse("Invalid date format should fail", 
                   InputValidator.isValidDate("2024/01/15"));
        assertFalse("Invalid month should fail", 
                   InputValidator.isValidDate("2024-13-01"));
        assertFalse("Invalid day should fail", 
                   InputValidator.isValidDate("2024-02-30"));
        assertFalse("Non-leap year Feb 29 should fail", 
                   InputValidator.isValidDate("2023-02-29"));
        assertFalse("Null date should fail", 
                   InputValidator.isValidDate(null));
        assertFalse("Empty date should fail", 
                   InputValidator.isValidDate(""));
    }
    
    @Test
    public void testIsNotEmpty() {
        assertTrue("Non-empty string should pass", 
                  InputValidator.isNotEmpty("test"));
        assertTrue("String with spaces should pass", 
                  InputValidator.isNotEmpty("test string"));
        
        assertFalse("Empty string should fail", 
                   InputValidator.isNotEmpty(""));
        assertFalse("String with only spaces should fail", 
                   InputValidator.isNotEmpty("   "));
        assertFalse("Null string should fail", 
                   InputValidator.isNotEmpty(null));
    }
    
    @Test
    public void testValidNumber() {
        assertTrue("Integer should pass", 
                  InputValidator.isValidNumber("123"));
        assertTrue("Decimal should pass", 
                  InputValidator.isValidNumber("123.45"));
        assertTrue("Negative number should pass", 
                  InputValidator.isValidNumber("-123.45"));
        assertTrue("Zero should pass", 
                  InputValidator.isValidNumber("0"));
    }
    
    @Test
    public void testInvalidNumber() {
        assertFalse("Text should fail", 
                   InputValidator.isValidNumber("abc"));
        assertFalse("Number with text should fail", 
                   InputValidator.isValidNumber("123abc"));
        assertFalse("Empty string should fail", 
                   InputValidator.isValidNumber(""));
        assertFalse("Null should fail", 
                   InputValidator.isValidNumber(null));
    }
    
    @Test
    public void testValidInteger() {
        assertTrue("Positive integer should pass", 
                  InputValidator.isValidInteger("123"));
        assertTrue("Negative integer should pass", 
                  InputValidator.isValidInteger("-123"));
        assertTrue("Zero should pass", 
                  InputValidator.isValidInteger("0"));
    }
    
    @Test
    public void testInvalidInteger() {
        assertFalse("Decimal should fail", 
                   InputValidator.isValidInteger("123.45"));
        assertFalse("Text should fail", 
                   InputValidator.isValidInteger("abc"));
        assertFalse("Empty string should fail", 
                   InputValidator.isValidInteger(""));
        assertFalse("Null should fail", 
                   InputValidator.isValidInteger(null));
    }
    
    @Test
    public void testValidSalary() {
        assertTrue("Valid salary should pass", 
                  InputValidator.isValidSalary("50000"));
        assertTrue("High salary should pass", 
                  InputValidator.isValidSalary("1000000"));
        assertTrue("Zero salary should pass", 
                  InputValidator.isValidSalary("0"));
    }
    
    @Test
    public void testInvalidSalary() {
        assertFalse("Negative salary should fail", 
                   InputValidator.isValidSalary("-1000"));
        assertFalse("Too high salary should fail", 
                   InputValidator.isValidSalary("20000000"));
        assertFalse("Non-numeric salary should fail", 
                   InputValidator.isValidSalary("abc"));
        assertFalse("Null salary should fail", 
                   InputValidator.isValidSalary(null));
    }
    
    @Test
    public void testSanitizeInput() {
        assertEquals("Normal text should remain same", 
                    "normal text", InputValidator.sanitizeInput("normal text"));
        assertEquals("HTML chars should be removed", 
                    "test", InputValidator.sanitizeInput("te<st>"));
        assertEquals("Quotes should be removed", 
                    "test", InputValidator.sanitizeInput("te\"st'"));
        assertNull("Null input should return null", 
                  InputValidator.sanitizeInput(null));
    }
    
    @Test
    public void testValidMenuChoice() {
        assertTrue("Valid choice should pass", 
                  InputValidator.isValidMenuChoice("1", 5));
        assertTrue("Max choice should pass", 
                  InputValidator.isValidMenuChoice("5", 5));
        
        assertFalse("Zero choice should fail", 
                   InputValidator.isValidMenuChoice("0", 5));
        assertFalse("Too high choice should fail", 
                   InputValidator.isValidMenuChoice("6", 5));
        assertFalse("Non-numeric choice should fail", 
                   InputValidator.isValidMenuChoice("abc", 5));
        assertFalse("Null choice should fail", 
                   InputValidator.isValidMenuChoice(null, 5));
    }
}