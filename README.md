# chat-app-backend

## Description

Hobby project for practise. :book:
</br> Chat application backend.

## Technology
- Spring
- Spring Security JWT
- REST
- WebSocket
- MySQL (prod) / H2 (dev)
- JPA

## Features
- Role and permission based JWT auth for Socket chanels and REST
- User can create private and group chanels & (add/leave/delete)
- Notify users and chanels about user activitations
  new conversation | message | type | like | join - leave users
- Convesrations details - messages - file paths stored in db | files in storage
- Auto resize images
- Custom group conversation profiles (name/image)

## Launch
- frontend: https://github.com/togetacross/chat-app-frontend
- set `files_storage.dir` in profile dev
- `run`

## License
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

