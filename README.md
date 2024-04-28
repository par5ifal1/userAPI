# UserAPI Project

## Overview

UserAPI is a RESTful API developed using Spring Boot. It provides a set of endpoints for managing user data. The API allows you to create, update, delete, and search for users based on their birth date.

## Prerequisites

- Java 17
- Maven
- MySQL

## Installation

1. Clone the repository:
```bash
git clone https://github.com/yourusername/userAPI.git
```
2. Navigate to the project directory:
```bash
cd userAPI
```
3. Build the project with Maven:
```bash
mvn clean install
```
4. Run the application:
```bash
mvn spring-boot:run
```

## Database Configuration

To connect your own database, update the following properties in the `application.properties` file:
```bash
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.url=your_db_url
```
## API Endpoints
- **POST /newUser:** Creates a new user.
- **PUT /user/{id}/update:** Updates an existing user.
- **DELETE /user/{id}/delete:** Deletes a user.
- **GET /searchUsers:** Finds users by birth date.

## Running Tests
To run the tests, use the following command:
```bash
mvn test
```
## Technologies Used
- Spring Boot
- Maven
- MySQL