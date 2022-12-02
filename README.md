# chat-app-backend

## Description

Hobby project for practise.

Spring Boot backend chat application.

## Technology

- Spring Boot
- Spring Security
- Websocket
- Mysql prod / H2 dev

## Features

The project include:
- Security
JWT REST auth, Secure JWT Socket chanels, authority(Role) and handle permissions for conversations.
- Create private/group conversations
- Handle permissions for conversations add/delete user
- Handle user conversation actions message/type/seen/join/leave/
- Notify users with Socket secured private/topic chanels
- Handle file upload/download - conversations and profile
- Reduce uploaded image sizes/quality
- Store datas and user/convesration images in Mysql and files in file system.
- Further REST endpoints for get initial datas
- Handling errors

## Launch

- application-dev.properties
set --> files_storage.dir=(your path)
- application.properties
set --> spring.profiles.active=dev
- run project

## License
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
