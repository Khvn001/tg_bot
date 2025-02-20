# Telegram Bot for Online Shop  

This project is a Telegram bot designed for managing an online shop. It uses various technologies and frameworks, including **Spring Boot, JPA, Hibernate, PostgreSQL**, and integrates with the **Telegram API**. The bot allows users to interact with an online marketplace, manage their orders, view products, and more.  

## Table of Contents  
- [Getting Started](#getting-started)  
- [Prerequisites](#prerequisites)  
- [Installation](#installation)  
- [Configuration](#configuration)  
- [Usage](#usage)  
- [Project Structure](#project-structure)  
- [Contributing](#contributing)  
- [License](#license)  

## Getting Started  
These instructions will help you set up the project on your local machine for development and testing.  

## Prerequisites  
- **Java 11** or higher  
- **Docker** (optional, for running PostgreSQL)  
- **PostgreSQL**  

## Installation  

### Clone the repository:  
```sh
git clone https://github.com/Khvn001/tg_bot.git  
cd tg_bot  
```  

### Build the project using Gradle:  
```sh
./gradlew build  
```  

### Run PostgreSQL using Docker:  
```sh
docker run --name tg_bot_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=postgres -p 5432:5432 -d postgres  
```  

### Apply database migrations:  
```sh
./gradlew flywayMigrate  
```  

## Configuration  
Configure the bot by setting the necessary environment variables or by editing the `application.yml` file located in `service-client/src/main/resources`.  

#### Required environment variables:  
- `BOT_NAME`: The name of your Telegram bot.  
- `BOT_TOKEN`: The token provided by BotFather.  
- `POSTGRES_JDBC_URL`: The JDBC URL for your PostgreSQL database.  
- `POSTGRES_USERNAME`: The username for your PostgreSQL database.  
- `POSTGRES_PASSWORD`: The password for your PostgreSQL database.  
- `AWS_S3_ACCESS_KEY`: The access key for your AWS S3 bucket.  
- `AWS_S3_SECRET_KEY`: The secret key for your AWS S3 bucket.  

### Example `application.yml` snippet:  
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/postgres
    username: postgres
    password: postgres

bot:
  name: BotName
  token: BotToken

aws:
  s3:
    accessKey: AWS_S3_ACCESS_KEY
    secretKey: AWS_S3_SECRET_KEY
```  

## Usage  

### Run the application:  
```sh
./gradlew bootRun  
```  
The bot will start and connect to Telegram. You can interact with it using the Telegram app.  

## Project Structure  
The project is divided into several packages, each responsible for different functionalities of the bot.  

### **Core Packages**  
- `com.telegrambot.marketplace` – Contains the main application class and configuration files.  
- `com.telegrambot.marketplace.command` – Handles various bot commands and updates.  
- `com.telegrambot.marketplace.config` – Contains configuration classes for the bot, JPA, and other services.  
- `com.telegrambot.marketplace.dto` – Data transfer objects used for communication between different layers of the application.  
- `com.telegrambot.marketplace.entity` – JPA entities representing the database structure.  
- `com.telegrambot.marketplace.repository` – Spring Data JPA repositories for accessing the database.  
- `com.telegrambot.marketplace.service` – Service layer classes for business logic and interaction with repositories.  

### **Main Classes and Files**  
- `TelegramBotApplication.java` – The main class for starting the Spring Boot application.  
- `BotConfig.java` – Configuration class for the bot's settings.  
- `CommandConfig.java` – Configuration class for registering commands.  
- `JpaConfig.java` – Configuration class for JPA and Hibernate settings.  
- `application.yml` – Configuration file for application properties.  

## License  
This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.  
```
