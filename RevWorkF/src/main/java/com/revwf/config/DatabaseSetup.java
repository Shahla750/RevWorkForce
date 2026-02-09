package com.revwf.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {
    private static final Logger logger = LogManager.getLogger(DatabaseSetup.class);
    
    public static void main(String[] args) {
        DatabaseSetup setup = new DatabaseSetup();
        setup.setupDatabase();
    }
    
    public void setupDatabase() {
        System.out.println("Starting database setup...");
        
        try {
            // Test connection first
            DatabaseConfig.getInstance().testConnection();
            
            // Execute schema script
            executeSchemaScript();
            
            System.out.println("✓ Database setup completed successfully!");
            logger.info("Database setup completed successfully");
            
        } catch (Exception e) {
            System.err.println("✗ Database setup failed: " + e.getMessage());
            logger.error("Database setup failed", e);
            System.exit(1);
        }
    }
    
    private void executeSchemaScript() throws SQLException, IOException {
        System.out.println("Executing database schema...");
        
        try (Connection connection = DatabaseConfig.getInstance().getConnection()) {
            // Read schema file from resources or external file
            String schemaScript = readSchemaFromResources();
            
            if (schemaScript == null || schemaScript.trim().isEmpty()) {
                throw new IOException("Schema script is empty or not found");
            }
            
            // Split script by semicolons and execute each statement
            String[] statements = schemaScript.split(";");
            
            try (Statement stmt = connection.createStatement()) {
                for (String sql : statements) {
                    sql = sql.trim();
                    if (!sql.isEmpty() && !sql.startsWith("--")) {
                        try {
                            stmt.execute(sql);
                            logger.debug("Executed SQL: " + sql.substring(0, Math.min(50, sql.length())) + "...");
                        } catch (SQLException e) {
                            // Log warning for non-critical errors (like table already exists)
                            if (e.getMessage().contains("already exists") || 
                                e.getMessage().contains("does not exist")) {
                                logger.warn("SQL Warning: " + e.getMessage());
                            } else {
                                logger.error("SQL Error: " + e.getMessage() + " for statement: " + sql);
                                throw e;
                            }
                        }
                    }
                }
            }
            
            connection.commit();
            System.out.println("✓ Schema executed successfully");
        }
    }
    
    private String readSchemaFromResources() throws IOException {
        // Try to read from resources first
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("schema.sql")) {
            if (is != null) {
                return readFromInputStream(is);
            }
        }
        
        // If not found in resources, provide a basic schema
        return getBasicSchema();
    }
    
    private String readFromInputStream(InputStream is) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }
    
    private String getBasicSchema() {
        return "-- Basic schema will be created\n" +
               "-- Please ensure your schema.sql file is in the resources folder or sql/ directory\n";
    }
}