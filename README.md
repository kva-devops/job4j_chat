# Приложение Chat
[![Build Status](https://app.travis-ci.com/kva-devops/job4j_chat.svg?branch=master)](https://app.travis-ci.com/kva-devops/job4j_chat)
[![codecov](https://codecov.io/gh/kva-devops/job4j_chat/branch/master/graph/badge.svg?token=XRFMCDZPRH)](https://codecov.io/gh/kva-devops/job4j_chat)

## О проекте
#### Описание
Учебный проект - "Чат с комнатами". Приложение реализует функционал интернет-чата с комнатами.
В проекте делается упор на следующие темы: RESTfull-архитектуры в Spring, JWT-авторизации, валидация моделей в Spring REST   
Перед началом работы необходимо авторизоваться или зарегистрироваться в системе.
В системе используются 4 модели данных: Person, Role, Room, Message.
Соответствующие таблицы в базе данных создаются и заполняются в скрипте: *chat/db/update_001.sql*

#### Технологии
>JDK14, Maven, Spring Boot, Spring Data, PostgreSQL, REST API, JWT-авторизация

## Сборка 
0. Скачайте исходники
1. Создайте базу данных в соответствии с настройками, указанными в файле *chat/src/main/resources/application.properties*. 
2. Разверните таблицы и заполните входные данные из файла *db/update_001.sql*
2. Осуществите сборку приложения: `mvn clean install`
3. Запустите приложение из консоли командой: `java -jar chat/target/chat-1.0.jar`

## Как пользоваться
После запуска приложения в нем необходимо авторизоваться. Данные для авторизации можно взять из файла:
*chat/db/update_001.sql*

![login](images/Selection_233.png)

После авторизации генерируется уникальный токен, который нужно добавить в Headers.

Формат записи токена: `Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9...`

![token](images/Selection_234.png)

Создаем новое сообщение с указанием нужной комнаты:

![newMessage](images/Selection_235.png)

Проверяем базу данных *Messages*, видим, что добавилась новая запись.

![checkDatabase](images/Selection_236.png)

Авторизуемся под другим пользователем и проделываем аналогичные действия:

![checkDatabase](images/Selection_237.png)

![checkDatabase](images/Selection_238.png)

Проверяем таблицу в базе данных:

![checkDatabase](images/Selection_239.png)

## Контакты

Кутявин Владимир

email: tribuna87@mail.ru

skype: tribuna87