# Knowledge Platform - Backend

This backend serves as the core API and data management layer for the Knowledge Platform, built with Spring Boot and MySQL. It features JWT-based authentication, CRUD operations for articles, and an integration with GroqCloud for AI-assisted writing, summarization, and tag generation.

## 1️⃣ Approach

### Architecture Overview
The backend follows a standard N-tier architecture:
- **Controller Layer:** Handles incoming HTTP REST requests and routing.
- **Service Layer:** Contains the core business logic and external integrations (e.g., GroqCloud AI).
- **Data Access Layer (Repository):** Interfaces with the MySQL database using Spring Data JPA.
- **Security Layer:** Manages user authentication, password hashing (BCrypt), and JWT token validation.

### Folder Structure
```text
src/main/java/com/mayank/knowledgeplatform/
├── controller/     # REST API endpoints (Auth, AI, Articles)
├── dto/            # Data Transfer Objects for request/response payloads
├── exception/      # Global exception handling and custom exceptions
├── model/          # JPA Entities (User, Article)
├── repository/     # Spring Data JPA Repositories
├── security/       # JWT filters, entry points, and Spring Security config
└── service/        # Business logic and AI integration
```

### Key Design Decisions
- **Spring Security & JWT:** Chose stateless JWT authentication to allow for scalable and secure API access without server-side session management.
- **Exception Handling:** Implemented a robust `GlobalExceptionHandler` to gracefully catch and format errors (e.g., parsing `BadCredentialsException` into clean 401 JSON responses) for the frontend.
- **AI Integration:** Abstracted AI interactions into a dedicated `AIService`. Connected to the GroqCloud API using `RestTemplate` for high-speed AI text generation, passing prompts formatted for Llama-3.1-8b.
- **Database:** Used MySQL with Hibernate for robust relational data storage and ORM capabilities.

## 2️⃣ AI Usage

- **AI Tools Used:** DeepMind's Antigravity Agent (Google AI Assistant).
- **Where AI Helped:**
  - **Code Generation:** Bootstrapping the Spring Boot project structure, setting up Spring Security, JWT utilities, JPA Repositories, and the base Controller/Service patterns.
  - **Refactoring:** Organizing DTOs, cleaning up exception handlers, and migrating from mock AI responses to a live GroqCloud API integration.
  - **UI/API Coordination:** Identifying and fixing a 404 error where the backend was incorrectly extracting the username instead of the email from the JWT principal for author lookups.
  - **Troubleshooting:** Identifying and resolving HTTP 500 errors, such as explicitly handling `BadCredentialsException` to return a 401 Unauthorized status.
- **What Was Reviewed/Corrected Manually:**
  - **JWT Security Filters:** Generated the baseline Spring Security config with AI, but manually implemented the complete `AuthTokenFilter` logic. Specifically designed how the stateless token is parsed from the `Authorization` header, validated, and injected into the `SecurityContextHolder` to secure the API routes.
  - **Global Exception Handling:** Built the `GlobalExceptionHandler` to cleanly intercept and map system exceptions (like `BadCredentialsException`) into structured, frontend-friendly 401 JSON responses instead of generic 500 server crashes.
  - **AI Service Integration:** Used AI for the basic models, but manually handled the core integration with the GroqCloud API using `RestTemplate`. Engineered the dynamic request payload generation, handled error fallbacks, and parsed the complex JSON nested responses back into cleaner DTOs for the application layer.

## 3️⃣ Setup Instructions

### Prerequisites
- **Java:** JDK 17+
- **Database:** MySQL 8.x
- **Build Tool:** Maven

### Environment Variables
You must configure the following properties in your `src/main/resources/application.properties` or environment variables:

```properties
# MySQL Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/knowledge_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD

# JWT Config (256-bit secret key)
app.jwt.secret=YOUR_JWT_SECRET
app.jwt.expirationMs=86400000

# GroqCloud API configuration for AI features
groq.api.key=YOUR_GROQ_API_KEY
groq.api.url=https://api.groq.com/openai/v1/chat/completions
groq.api.model=llama-3.1-8b-instant
```

### Backend Setup
1. **Database:** Create a local MySQL database named `knowledge_db`. The tables will auto-generate via Hibernate (`spring.jpa.hibernate.ddl-auto=update`).
2. **Build the project:**
   ```bash
   mvn clean install
   ```
3. **Run the Server:**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```
   The backend API will start on `http://localhost:8080`.
