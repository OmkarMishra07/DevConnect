# 🚀 DevConnect

> **A collaborative developer platform for discovering projects, finding teammates, managing tasks, and communicating with fellow developers.**

DevConnect is a full-stack developer collaboration platform designed to help developers **build teams, discover projects, connect with other developers, and collaborate in real time**.

The backend is built using **Java + Spring Boot**, with **PostgreSQL/Supabase** for persistent data storage and **JWT-based authentication** for secure API access.

---

## ✨ Features

### 🔐 Authentication & Security

* User registration and login
* JWT-based authentication
* Secure password handling
* Role-based authorization
* Protected API endpoints
* User profile management

### 👨‍💻 Developer Networking

* Create and manage developer profiles
* Connect with other developers
* Send, accept, and reject friend requests
* View existing connections
* Maintain developer relationships

### 🚀 Project Collaboration

* Create and publish projects
* Add project descriptions and technology stacks
* Define project status and team size
* Explore available projects
* Search projects using keywords
* Join existing projects
* Send project join requests
* Invite developers to projects
* Approve or reject join requests
* Leave projects
* Remove project members

### 📋 Project Workspace

Each project can have its own collaborative workspace containing:

* Project members
* Project messages
* Tasks
* Task assignments
* Task status management
* Deadlines
* Task tags
* Project activity

### 💬 Communication

* Create developer groups
* Add members to groups
* Group messaging
* Retrieve group conversations
* Project-specific messaging

### 📊 Dashboard

The backend provides dashboard information including:

* Created projects
* Joined projects
* Project requests
* Project workspace information
* Collaboration data

---

## 🏗️ Architecture

DevConnect follows a layered Spring Boot architecture:

```text
                ┌──────────────────────┐
                │      Frontend        │
                │   React Application  │
                └──────────┬───────────┘
                           │
                           │ REST API
                           ▼
                ┌──────────────────────┐
                │    Spring Boot       │
                │      Backend         │
                └──────────┬───────────┘
                           │
          ┌────────────────┼────────────────┐
          │                │                │
          ▼                ▼                ▼
   Controllers         Services       Security Layer
          │                │                │
          └────────────────┼────────────────┘
                           │
                           ▼
                  Spring Data JPA
                           │
                           ▼
                ┌──────────────────────┐
                │ PostgreSQL / Supabase│
                └──────────────────────┘
```

---

## 🛠️ Tech Stack

### Backend

* **Java 17**
* **Spring Boot 3.4.1**
* Spring Web
* Spring Data JPA
* Spring Security
* WebSocket
* Hibernate
* Maven

### Database

* **PostgreSQL**
* **Supabase**
* HikariCP connection pooling

### Security

* **JWT (JSON Web Tokens)**
* Spring Security
* Role-based authorization

### Supporting Libraries

* Lombok
* MapStruct
* ModelMapper
* SpringDoc OpenAPI / Swagger
* Bean Validation
* JUnit / Spring Boot Test

### Deployment

* Docker
* Maven
* Environment-based configuration

The project's Maven configuration currently targets Java/Spring Boot and includes Spring Web, JPA, Security, WebSocket, PostgreSQL, JWT, validation, and OpenAPI dependencies.

---

## 📁 Project Structure

```text
DevConnect/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/MergeX/
│   │   │       │
│   │   │       ├── Dto/
│   │   │       │   └── Data Transfer Objects
│   │   │       │
│   │   │       ├── Security/
│   │   │       │   ├── AuthService.java
│   │   │       │   ├── AuthUtil.java
│   │   │       │   ├── JwtAuthFilter.java
│   │   │       │   ├── JwtService.java
│   │   │       │   └── WebSecurityConf.java
│   │   │       │
│   │   │       ├── controller/
│   │   │       │   ├── AuthController.java
│   │   │       │   ├── ChatController.java
│   │   │       │   ├── ProjectController.java
│   │   │       │   ├── UserController.java
│   │   │       │   └── AdminController.java
│   │   │       │
│   │   │       ├── service/
│   │   │       │   ├── UserService.java
│   │   │       │   ├── ProjectService.java
│   │   │       │   └── ChatService.java
│   │   │       │
│   │   │       ├── model/
│   │   │       │   ├── User.java
│   │   │       │   ├── Project.java
│   │   │       │   ├── ProjectTask.java
│   │   │       │   ├── Friendship.java
│   │   │       │   ├── ChatGroup.java
│   │   │       │   └── GroupMessage.java
│   │   │       │
│   │   │       ├── repository/
│   │   │       │
│   │   │       ├── mapper/
│   │   │       │
│   │   │       ├── exceptions/
│   │   │       │
│   │   │       ├── config/
│   │   │       │
│   │   │       └── api/
│   │   │
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│
├── Dockerfile
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

---

## 🔑 Authentication Flow

DevConnect uses JWT authentication to secure protected endpoints.

```text
User
 │
 │ Login / Signup
 ▼
AuthController
 │
 ▼
AuthService
 │
 ▼
JWT Generation
 │
 ▼
Client receives JWT
 │
 │ Authorization: Bearer <token>
 ▼
JwtAuthFilter
 │
 ▼
Spring Security
 │
 ▼
Protected API
```

JWT configuration is supplied through environment variables rather than hard-coding secrets.

---

## 📌 Core API Endpoints

### Authentication

```http
POST /auth/signup
POST /auth/login
```

### Projects

```http
POST   /projects/create
PUT    /projects/{projectId}
GET    /projects
GET    /projects/get-all
GET    /projects/my-created
GET    /projects/my-joined
GET    /projects/my-requests
GET    /projects/dashboard-summary
GET    /projects/{projectId}/workspace
```

### Project Collaboration

```http
POST   /projects/{projectId}/join
POST   /projects/{projectId}/request-access
POST   /projects/{projectId}/invite/{userId}
POST   /projects/requests/{requestId}/approve
POST   /projects/requests/{requestId}/reject
POST   /projects/{projectId}/leave
DELETE /projects/{projectId}/members/{userId}
```

### Project Tasks

```http
POST  /projects/{projectId}/tasks
PATCH /projects/tasks/{taskId}/status
```

### Project Messaging

```http
POST /projects/{projectId}/messages
```

### Developer Networking

```http
POST /chat/friend-request/{friendId}
POST /chat/friend-request/{friendshipId}/accept
POST /chat/friend-request/{friendshipId}/reject

GET /chat/friends
GET /chat/friend-requests/pending
```

### Group Chat

```http
POST /chat/groups
GET  /chat/groups

POST /chat/groups/{groupId}/messages
GET  /chat/groups/{groupId}/messages
```

---

## ⚙️ Getting Started

### Prerequisites

Make sure you have the following installed:

* Java 17+
* Maven
* PostgreSQL / Supabase account
* Git
* Docker *(optional)*

---

### 1. Clone the Repository

```bash
git clone https://github.com/OmkarMishra07/DevConnect.git

cd DevConnect
```

---

### 2. Configure Environment Variables

The application uses environment variables for database credentials and JWT configuration.

Set the following variables:

```env
SPRING_DATASOURCE_URL=your_postgresql_jdbc_url
SPRING_DATASOURCE_USERNAME=your_database_username
SPRING_DATASOURCE_PASSWORD=your_database_password
JWT_SECRET_KEY=your_secure_jwt_secret
```

> **Never commit database credentials or JWT secrets to GitHub.**

---

### 3. Run the Application

Using Maven:

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The backend runs on:

```text
http://localhost:8090
```

---

## 🐳 Running with Docker

Build the Docker image:

```bash
docker build -t devconnect .
```

Run the container:

```bash
docker run -p 8090:8090 \
  -e SPRING_DATASOURCE_URL="your_database_url" \
  -e SPRING_DATASOURCE_USERNAME="your_username" \
  -e SPRING_DATASOURCE_PASSWORD="your_password" \
  -e JWT_SECRET_KEY="your_jwt_secret" \
  devconnect
```

---

## 🔎 API Documentation

The project includes **SpringDoc OpenAPI** support.

After starting the application, Swagger UI can be accessed through:

```text
http://localhost:8090/swagger-ui/index.html
```

---

## 🗄️ Database

DevConnect uses PostgreSQL as its relational database.

The application uses:

* Spring Data JPA
* Hibernate ORM
* PostgreSQL Driver
* HikariCP connection pooling

The current configuration supports environment-based database configuration and Hibernate schema updates.

### Main Entities

```text
User
 │
 ├── Projects
 │      ├── Participants
 │      ├── Tasks
 │      ├── Messages
 │      └── Join Requests
 │
 ├── Friendships
 │
 └── Chat Groups
        └── Group Messages
```

---

## 🔐 Security

Security is implemented using:

* JWT authentication
* Spring Security
* JWT request filtering
* Role-based authorization
* Protected project/admin operations

For example:

```java
@PreAuthorize("hasRole('ADMIN')")
```

is used to restrict administrative operations.

---

## 📈 Project Workflow

```text
                    ┌───────────────┐
                    │     User      │
                    └───────┬───────┘
                            │
                  ┌─────────▼─────────┐
                  │ Register / Login  │
                  └─────────┬─────────┘
                            │
                            ▼
                    Developer Profile
                            │
             ┌──────────────┼──────────────┐
             │              │              │
             ▼              ▼              ▼
        Find Users      Explore        Create
        & Friends       Projects       Project
                            │              │
                            └──────┬───────┘
                                   │
                              Join / Invite
                                   │
                                   ▼
                           Project Workspace
                                   │
                    ┌──────────────┼──────────────┐
                    │              │              │
                    ▼              ▼              ▼
                  Tasks         Messages       Members
                    │
                    ▼
              Track Progress
```

---

## 🎯 Why DevConnect?

Finding the right people to work with can be difficult when developers have ideas but lack teammates with complementary skills.

DevConnect addresses this by bringing together:

* **Developer networking**
* **Project discovery**
* **Team formation**
* **Task management**
* **Project communication**
* **Developer-to-developer connections**

into one platform.

---

## 🚀 Future Improvements

Potential improvements include:

* Real-time WebSocket notifications
* Advanced project recommendation system
* Developer skill-based matching
* GitHub profile integration
* GitHub repository integration
* Project activity feeds
* File sharing
* Code collaboration
* Email notifications
* Advanced search and filtering
* Project analytics
* Deployment with CI/CD

---

## 👨‍💻 Author

**Omkar Mishra**

Computer Science / IoT Engineering Student
Interested in **Backend Development, Full-Stack Development, Java, Spring Boot and Distributed Systems**.

### Connect

* GitHub: [@OmkarMishra07](https://github.com/OmkarMishra07)

---

## ⭐ Support

If you find this project useful, consider giving the repository a ⭐ on GitHub.

**Built with Java, Spring Boot, PostgreSQL and a focus on developer collaboration.**
