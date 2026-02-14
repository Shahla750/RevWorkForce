RevWorkforce – Human Resource Management System

📌 Project Overview

RevWorkforce is a console-based Human Resource Management System (HRMS) developed using Core Java and Oracle Database.
The application follows a layered MVC architecture with DAO pattern and manages the complete employee lifecycle, including authentication, leave management, performance tracking, and notifications.

This project was developed as an enterprise-style Java application with focus on clean architecture, security, database integrity, and coding standards.
🛠 Technology Stack
CategoryTechnologyProgramming LanguageJava 17IDEIntelliJ IDEA / EclipseDatabaseOracle DatabaseDatabase AccessJDBCBuild ToolMavenLoggingLog4j2Unit TestingJUnit 4ArchitectureMVC + DAO Pattern

⚠️ Note: Requirement mentioned Java 21 and JUnit 5, but Java 17 and JUnit 4 were used due to compatibility and stability.


📂 Project Structure
RevWorkforce/

│
├── src/main/java/com/revworkforce
│   ├── view        (Presentation Layer)
│   ├── service     (Business Logic Layer)
│   ├── dao         (Data Access Layer)
│   ├── model       (Entity Classes)
│   ├── util        (Utility & Config classes)
│
├── src/main/resources
│   ├── schema.sql
│   ├── data.sql
│   ├── log4j2.xml
│
├── pom.xml
└── README.md


🧱 Architecture Overview
The application follows a three-tier layered architecture:
1️⃣ Presentation Layer (View Package)
Handles user interaction and menu-driven console UI.

LoginView – User authentication

AdminDashboard – Administrative operations

ManagerDashboard – Team and leave approvals

EmployeeDashboard – Employee self-service


2️⃣ Business Logic Layer (Service Package)
Contains business rules and transaction management.

AuthService – Authentication & authorization

EmployeeService – Employee management

LeaveService – Leave workflow handling

PerformanceService – Reviews and goals


NotificationService – System notifications



3️⃣ Data Access Layer (DAO Package)
Handles all database interactions using JDBC.


UserDAO


EmployeeDAO


LeaveDAO


PerformanceDAO


NotificationDAO


✔ Uses PreparedStatement to prevent SQL injection
✔ Ensures clean separation of concerns

🗄 Database Design
📊 Database Schema
The system uses 12 normalized tables:
Core Tables


Users


Employees


Departments


Designations


Leave Management


Leave_Types


Leave_Balances


Leave_Applications


Performance Management


Performance_Reviews


Goals


Goal_Progress


Others


Notifications


Company_Holidays


✔ Primary Keys, Foreign Keys
✔ CHECK and UNIQUE constraints
✔ Self-referencing manager–employee hierarchy

⚙️ Key Features
🔐 Authentication Module


Secure login


SHA-256 password hashing


Role-based access control (Admin, Manager, Employee)


👥 Employee Management


Full CRUD operations


Department & designation mapping


🏖 Leave Management


Apply / approve / reject workflow


Automatic leave balance updates


📈 Performance Management


Annual reviews


Self-assessment


Manager feedback


Goal tracking


🔔 Notification System


Leave status updates


Performance reminders



📚 Collections Framework Usage
The Java Collections Framework is used extensively:


ArrayList for dynamic data storage


List interface for abstraction


Stream API for:


Filtering pending leave requests


Calculating leave balances


Transforming result sets





🔒 Security Implementation


SHA-256 password hashing


JDBC PreparedStatement to prevent SQL injection


Input validation (email, phone, dates)


Role-based authorization


Session handling



📝 Logging
Log4j2 is configured with:


Console Appender


File Appender


Rolling File Appender (10MB rotation)


Log Levels:


DEBUG


INFO


WARN


ERROR



🧪 Testing


Unit testing done using JUnit 4


Focused on service and DAO layers



⚠️ Upgrade to JUnit 5 planned


✅ Project Requirements Compliance
✔ Implemented


Menu-driven console application


Layered architecture (MVC + DAO)


JDBC with transaction management


Oracle SQL scripts for schema & master data


Constraints (PK, FK, CHECK, UNIQUE)

Log4j2 logging

Maven dependency management

⚠️ Partially Implemented

Java version (17 instead of 21)

JUnit 4 instead of JUnit 5

toString() implemented, but equals() & hashCode() pending

Limited inline documentation

❌ Not Implemented (Planned Enhancements)

Stored procedures & functions

Database triggers

Custom user-defined exceptions

Comparable / Comparator usage

File-based text storage

UML Class Diagram

Test data & I/O format documentation


🚀 Future Enhancements

Upgrade to Java 21 & JUnit 5

Add stored procedures, triggers

Implement custom exceptions

Add UML class diagram

Improve documentation coverage

Introduce file-based backup storage



