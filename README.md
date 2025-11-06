Expense Sharing API
-----------------------------------------------

Design and implement a backend system where multiple users can share expenses within
groups. Each user logs in using OAuth. Whenever an expense is added, balances among group
members should be updated transactional.

API should be scalable, efficient, and capable of handling high concurrency.

## Features
- [User registration and authentication (JWT-based)]
- [Role-based access control]
- [Users can create and manage groups. ]
- [Add and track expenses for users and groups] 
- [Calculate and settle shared expenses]
- [Maintain balance sheet per group and per user. ]
- [Settle payments (transactional updates for payer and receiver).]
- [OAuth2 login integration]

## Database Design (Expected Entities/You can add more as per your thought)
- [User (id, name, email, oauthId)]
- [Group (id, name)]
- [Expense (id, amount, createdBy, groupId)]
- [Settlement (payerId, receiverId, amount, expenseId)]
- [Deliverables:]
- [REST APIs (/groups, /expenses, /settlements)]
- [Postman collection]
- [Sample DB schema]

## Transactions:
1. Ensure ACID consistency: if settlement fails, changes should not be applied.
2. Handle concurrent updates (e.g., 2 users updating the same expense).

## Programming Language / Framework / Libraries
- Java 21 
- Spring Boot 3+
- Spring Security
- Spring Data JPA
- Maven
- JWT for Authentication
- Google OAuth2 for login
- H2 for persistence
- Lombok

## Setup
- Clone the application dev branch from Github https://github.com/cvsnehankita/expense-sharing-api/tree/dev
- Setup your local with Java 21, and maven. 
- Also update your JAVA_HOME and Path environments
- Use below maven command to compile and run the application
  - mvn clean install 
  - mvn spring-boot:run


## Local Apis for Testing
- BaseURL: http://localhost:8080
- http://localhost:8080/register
- http://localhost:8080/login
- http://localhost:8080/logi/actuator/health
- http://localhost:8080/groups
- http://localhost:8080/groups/id
- http://localhost:8080/groups?name=GroupA&userIds=1,2
- http://localhost:8080/expense
- http://localhost:8080/groups/id
- http://localhost:8080/api/expense/group/name

## Endpoints
- POST /register - Register user
- POST /login - Login with JWT
- POST /groups - Create group
- POST /expenses - Add expense
- POST /settlements - Settle payment
- GET /expenses/group/{id}/balances - View balances