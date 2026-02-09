package com.revwf.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConfig {
    private static final Logger logger = LogManager.getLogger(DatabaseConfig.class);
    private static Properties properties;
    private static DatabaseConfig instance;
    
    private String url;
    private String username;
    private String password;
    private String driver;
    
    private DatabaseConfig() {
        loadProperties();
        initializeConfig();
    }
    
    public static DatabaseConfig getInstance() {
        if (instance == null) {
            synchronized (DatabaseConfig.class) {
                if (instance == null) {
                    instance = new DatabaseConfig();
                }
            }
        }
        return instance;
    }
    
    private void loadProperties() {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                logger.error("Unable to find config.properties");
                throw new RuntimeException("Configuration file not found");
            }
            properties.load(input);
            logger.info("Configuration loaded successfully");
        } catch (IOException e) {
            logger.error("Error loading configuration: " + e.getMessage());
            throw new RuntimeException("Failed to load configuration", e);
        }
    }
    
    private void initializeConfig() {
        this.url = properties.getProperty("db.url");
        this.username = properties.getProperty("db.username");
        this.password = properties.getProperty("db.password");
        this.driver = properties.getProperty("db.driver");
        
        try {
            Class.forName(driver);
            logger.info("Database driver loaded successfully");
        } catch (ClassNotFoundException e) {
            logger.error("Database driver not found: " + e.getMessage());
            throw new RuntimeException("Database driver not found", e);
        }
    }
    
    public Connection getConnection() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            logger.debug("Database connection established");
            return connection;
        } catch (SQLException e) {
            logger.error("Failed to establish database connection: " + e.getMessage());
            throw e;
        }
    }
    
    public void testConnection() {
        try (Connection connection = getConnection()) {
            if (connection != null && !connection.isClosed()) {
                logger.info("Database connection test successful");
                System.out.println("✓ Database connection successful");
            }
        } catch (SQLException e) {
            logger.error("Database connection test failed: " + e.getMessage());
            System.err.println("✗ Database connection failed: " + e.getMessage());
            throw new RuntimeException("Database connection test failed", e);
        }
    }
    
    public String getProperty(String key) {
        return properties.getProperty(key);
    }
    
    // Getters
    public String getUrl() {
        return url;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getDriver() {
        return driver;
    }
}