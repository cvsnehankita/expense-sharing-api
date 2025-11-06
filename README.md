Expense Sharing API
-----------------------------------------------

Design and implement a backend system where multiple users can share expenses within
groups. Each user logs in using OAuth. Whenever an expense is added, balances among group
members should be updated transactionally.

API should be scalable, efficient, and capable of handling high concurrency.

Features
1. Create and manage groups. 
2. Add expense (e.g., "Dinner 100 AED split among 4 people"). 
3. Maintain balance sheet per group and per user. 
4. Settle payments (transactional updates for payer and receiver).

Database Design (Expected Entities/You can add more as per your thought):
1. User (id, name, email, oauthId)
2. Group (id, name)
3. Expense (id, amount, createdBy, groupId)
4. Settlement (payerId, receiverId, amount, expenseId)
5. Deliverables:
6. REST APIs (/groups, /expenses, /settlements)
7. Postman collection
8. Sample DB schema

Transactions:
1. Ensure ACID consistency: if settlement fails, changes should not be applied.
2. Handle concurrent updates (e.g., 2 users updating the same expense).

Programming Language / Framework
• Java 21 with Spring Boot 3+
• Maven
• Spring Security + Google OAuth2 for login
• Spring Data JPA + H2 for persistence

Local Api
1. http://localhost:8080/register
2. http://localhost:8080/login
3. http://localhost:8080/logi/actuator/health
4. http://localhost:8080/groups
5. http://localhost:8080/groups/1
6. http://localhost:8080/groups?name=GroupA&userIds=1,2
7. http://localhost:8080/expense
8. http://localhost:8080/groups/1
9. http://localhost:8080/api/expense/group/Team1