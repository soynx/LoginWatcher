# LoginWatcher

Sends Telegram Notifications over a Telegram-Bot when detecting a Login-attempt.

### Config:
configurable over ENV:

- ``AUTH_LOG_PATH`` Path to your Linux Auth Log
- ``TELEGRAM_TOKEN`` Token of your telegram Bot
- ``TELEGRAM_CHAT_ID`` Chat-Id of your preferred Telegram Chat
- ``SSH_HOST`` The host domain / ip the container uses to connect to the host system
- ``SSH_PORT`` The port of your SSH server (default 22)
- ``SSH_USER`` The username for SSH login
- ``SSH_PASSWORD`` The Password for SSH login


### Requirements:
- Docker-CLI
- Docker Compose
- Own Telegram Bot
- rsyslog
- ssh server on host system

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