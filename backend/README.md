# Backend (Spring Boot + MySQL)

## MySQL (Workbench) setup
Create a schema:
```sql
CREATE DATABASE recipes_db;
```
Ensure user `root` with password `Venky@2135` has access.

## Run
```bash
cd backend
mvn spring-boot:run
```
The loader reads `src/main/resources/data/US_recipes.json` and inserts into MySQL.

APIs:
- GET http://localhost:8080/api/recipes?page=1&limit=15
- GET http://localhost:8080/api/recipes/search?calories=<=400&title=pie&rating=>=4.5

Swagger UI: http://localhost:8080/swagger-ui/index.html
