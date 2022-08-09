# Application "Chat with rooms"
[![Build Status](https://app.travis-ci.com/kva-devops/job4j_chat.svg?branch=master)](https://app.travis-ci.com/kva-devops/job4j_chat)
[![codecov](https://codecov.io/gh/kva-devops/job4j_chat/branch/master/graph/badge.svg?token=XRFMCDZPRH)](https://codecov.io/gh/kva-devops/job4j_chat)

## About project
#### Description
Educational project - "Chat with rooms". The application implements the functionality of an Internet chat with rooms.
The project focuses on the following topics: REST-full architectures in Spring, JWT authorization, model validation in Spring REST.
Before starting work, you must log in or register in the system.
The system uses 4 data models: Person, Role, Room, Message.
The corresponding tables in the database are created and filled in the script: *chat/db/update_001.sql*

#### Technologies
>JDK14, Maven, Spring Boot, Spring Data, PostgreSQL, REST API, JWT-auth

## Init 
0. Download sources
1. Create a database according to the settings specified in the *chat/src/main/resources/application.properties* file.
2. Expand the tables and fill in the input data from the file *db/update_001.sql*
2. Build the application: `mvn clean install`
3. Run the application from the console with the command: `java -jar chat/target/chat-1.0.jar`

## How use
After launching the application, you need to log in to it. Authorization data can be taken from the file:
*chat/db/update_001.sql*

![login](images/Selection_233.png)

After authorization, a unique token is generated, which must be added to Headers.

Token entry format: `Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9...`

![token](images/Selection_234.png)

Create a new message indicating the desired room:

![newMessage](images/Selection_235.png)

Checking the *Messages* database, we see that a new entry has been added.

![checkDatabase](images/Selection_236.png)

Log in as a different user and do the same:

![checkDatabase](images/Selection_237.png)

![checkDatabase](images/Selection_238.png)

Checking the table in the database:

![checkDatabase](images/Selection_239.png)

## Contact

Kutiavin Vladimir

telegram: @kutiavinvladimir