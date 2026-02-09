package com.revwf.service;

import org.junit.Before;
import org.junit.Test;

import com.revwf.model.UserRole;
import com.revwf.service.AuthService;

import static org.junit.Assert.*;

public class AuthServiceTest {

    private AuthService authService;

    @Before
    public void setUp() {
        authService = new AuthService();
    }

    @Test
    public void testLoginWithValidCredentials() {
        // Test with default admin credentials
        boolean result = authService.login("ADMIN001", "admin123");
        assertTrue("Login should succeed with valid credentials", result);
        assertTrue("User should be logged in", authService.isLoggedIn());
        assertNotNull("Current user should not be null", authService.getCurrentUser());
    }

    @Test
    public void testLoginWithInvalidCredentials() {
        boolean result = authService.login("INVALID", "wrongpassword");
        assertFalse("Login should fail with invalid credentials", result);
        assertFalse("User should not be logged in", authService.isLoggedIn());
        assertNull("Current user should be null", authService.getCurrentUser());
    }

    @Test
    public void testLoginWithEmptyCredentials() {
        boolean result = authService.login("", "");
        assertFalse("Login should fail with empty credentials", result);
        assertFalse("User should not be logged in", authService.isLoggedIn());
    }

    @Test
    public void testLoginWithNullCredentials() {
        boolean result = authService.login(null, null);
        assertFalse("Login should fail with null credentials", result);
        assertFalse("User should not be logged in", authService.isLoggedIn());
    }

    @Test
    public void testLogout() {
        // First login
        authService.login("ADMIN001", "admin123");
        assertTrue("User should be logged in", authService.isLoggedIn());

        // Then logout
        authService.logout();
        assertFalse("User should be logged out", authService.isLoggedIn());
        assertNull("Current user should be null after logout", authService.getCurrentUser());
    }

    @Test
    public void testRoleChecking() {
        // Login as admin
        authService.login("ADMIN001", "admin123");

        assertTrue("Should be admin", authService.isAdmin());
        assertTrue("Should have admin role", authService.hasRole(UserRole.ADMIN));
        assertFalse("Should not be manager", authService.isManager());
        assertFalse("Should not be employee", authService.isEmployee());
    }

    @Test
    public void testSessionInfo() {
        // Test without login
        String sessionInfo = authService.getSessionInfo();
        assertEquals("Should show not logged in", "Not logged in", sessionInfo);

        // Test with login
        authService.login("ADMIN001", "admin123");
        sessionInfo = authService.getSessionInfo();
        assertTrue("Should contain employee ID", sessionInfo.contains("ADMIN001"));
        assertTrue("Should contain role", sessionInfo.contains("Admin"));
    }

    @Test
    public void testChangePasswordWithoutLogin() {
        boolean result = authService.changePassword("oldpass", "newpass");
        assertFalse("Password change should fail without login", result);
    }

    @Test
    public void testMultipleLoginAttempts() {
        // First login
        boolean result1 = authService.login("ADMIN001", "admin123");
        assertTrue("First login should succeed", result1);

        // Second login (should replace current session)
        boolean result2 = authService.login("ADMIN001", "admin123");
        assertTrue("Second login should succeed", result2);

        assertTrue("User should still be logged in", authService.isLoggedIn());
    }

    @Test
    public void testIsLoggedInInitialState() {
        assertFalse("User should not be logged in initially", authService.isLoggedIn());
        assertNull("Current user should be null initially", authService.getCurrentUser());
    }

    @Test
    public void testRoleCheckingWithoutLogin() {
        assertFalse("Should not be admin without login", authService.isAdmin());
        assertFalse("Should not be manager without login", authService.isManager());
        assertFalse("Should not be employee without login", authService.isEmployee());
        assertFalse("Should not have any role without login", authService.hasRole(UserRole.ADMIN));
    }
}