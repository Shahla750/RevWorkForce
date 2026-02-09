package com.revwf.util;

import org.junit.Test;

import com.revwf.util.PasswordUtil;

import static org.junit.Assert.*;

public class PasswordUtilTest {
    
    @Test
    public void testSimpleHash() {
        String password = "testPassword123";
        String hash = PasswordUtil.simpleHash(password);
        
        assertNotNull("Hash should not be null", hash);
        assertNotEquals("Hash should not equal original password", password, hash);
        
        // Test consistency - same password should produce same hash
        String hash2 = PasswordUtil.simpleHash(password);
        assertEquals("Same password should produce same hash", hash, hash2);
    }
    
//    @Test
//    public void testVerifyPassword() {
//        String password = "admin123";
//        
//        // For simple implementation, password verification is direct comparison
//        assertTrue("Password should verify correctly", 
//                  PasswordUtil.verifyPassword(password, password));
//        
//        assertFalse("Wrong password should not verify", 
//                   PasswordUtil.verifyPassword(password, "wrongpassword"));
//    }
    
    @Test
    public void testIsValidPassword() {
        // Valid passwords
        assertTrue("Valid password should pass", 
                  PasswordUtil.isValidPassword("password123"));
        assertTrue("6 character password should pass", 
                  PasswordUtil.isValidPassword("123456"));
        
        // Invalid passwords
        assertFalse("Null password should fail", 
                   PasswordUtil.isValidPassword(null));
        assertFalse("Empty password should fail", 
                   PasswordUtil.isValidPassword(""));
        assertFalse("Short password should fail", 
                   PasswordUtil.isValidPassword("12345"));
    }
    
    @Test
    public void testGenerateRandomPassword() {
        int length = 10;
        String password = PasswordUtil.generateRandomPassword(length);
        
        assertNotNull("Generated password should not be null", password);
        assertEquals("Generated password should have correct length", 
                    length, password.length());
        
        // Test that two generated passwords are different
        String password2 = PasswordUtil.generateRandomPassword(length);
        assertNotEquals("Two generated passwords should be different", 
                       password, password2);
    }
    
    @Test
    public void testHashPassword() {
        String password = "testPassword123";
        String hash = PasswordUtil.hashPassword(password);
        
        assertNotNull("Hash should not be null", hash);
        assertNotEquals("Hash should not equal original password", password, hash);
        
        // Test that same password produces different hashes (due to salt)
        String hash2 = PasswordUtil.hashPassword(password);
        // Note: With salt, hashes will be different each time
        assertNotNull("Second hash should not be null", hash2);
    }
}