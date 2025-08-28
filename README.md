# Recipes Assessment

This project contains a **Spring Boot backend** and an **Angular frontend** for managing recipes.

## Project Structure
- `backend/` → Spring Boot APIs
- `frontend/` → Angular UI


## Running the Project

### Backend
```bash
cd backend
mvn spring-boot:run
```
Backend runs at **http://localhost:8080**

### Frontend
```bash
cd frontend
npm install
ng serve
```
Frontend runs at **http://localhost:4200**

## Database Setup
The schema file is provided in `schema.sql`.  
Run the following in your MySQL terminal:

```sql
SOURCE schema.sql;
```





---
**Author:** konda venkateswar reddy
**College:** Sree Vidyanikethan Engineering College  
**Assessment:** Recipes Project
