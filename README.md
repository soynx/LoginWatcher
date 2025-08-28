# LoginWatcher

Sends Telegram Notifications over a Telegram-Bot when detecting a Login-attempt.

### Config:
configurable over ENV:

- ``AUTH_LOG_PATH`` Path to your Linux Auth Log
- ``TELEGRAM_TOKEN`` Token of your telegram Bot
- ``TELEGRAM_CHAT_ID`` Chat-Id of your preferred Telegram Chat


### Requirements:
- Docker-CLI
- Docker Compose
- Own Telegram Bot

### RUN:

rename your adjusted Docker compose file:
> mv docker-compose.yml.template docker-compose.yml

then run:
> docker compose up -d