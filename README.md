# MyChat
Мессенджер для Android на Kotlin + Firebase

Установка: 
Установочный файл находится в папке apk/release

Что удалось реализовать:
1. Создан репозиторий на GitHub для переодического отслеживания задачи
2. Организован сервер для работы (Firebase)
3. На сервере спроектирована БД, а именно:
  3.1 Таблица "User" (идентификатор, логин, пароль)
  3.2 Таблица "Chats" (идентификатор чата, история переписки в нём)
4. Реализовано мобильное приложение для обмена сообщениями.
  4.1 Отправка сообщения
  4.2 Чтение сообщений
  4.3 Создание одностороннего чата с заранее известным пользователем
5. Поддержка более 2х собеседников в чате
6. Регистрация пользователей
7. Просмотр логина в сообщениях
8. Поддержка автоматического обновления у клиента