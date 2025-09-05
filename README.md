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
- rsyslog

### Installation:

1. Install ``rsyslog``
    > sudo apt update && sudo apt install rsyslog

2. Edit the rsyslog config (most likely at ``/etc/rsyslog.conf``)
    > auth,authpriv.*    /var/log/auth_monitoring.log
3. Restart ``rsyslog``
    > sudo systemctl restart rsyslog

4. rename your adjusted Docker compose file:
    > mv docker-compose.yml.template docker-compose.yml

### RUN:
then run:
> docker compose up -d