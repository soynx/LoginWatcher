# LoginWatcher

Sends Telegram Notifications over a Telegram-Bot when detecting a Login-attempt.

## Configuration

The project is fully configurable via **environment variables** (ENV).  
Below is a list of all available configuration parameters.


### SSH Connection

| Variable                     | Description                                                                             | Default | Required                                  |
|------------------------------|-----------------------------------------------------------------------------------------|---------|-------------------------------------------|
| `SSH_HOST`                   | The host domain or IP address used by the container to connect to the monitored system. | —       | ✅                                         |
| `SSH_PORT`                   | The port of the SSH server.                                                             | `22`    | ❌                                         |
| `SSH_USERNAME`               | The SSH username used for login.                                                        | —       | ✅                                         |
| `SSH_PASSWORD`               | The SSH password used for login (alternative to key authentication).                    | —       | ⚠️ Required if no private key is provided |
| `SSH_PRIVATE_KEY`            | Path or content of the SSH private key used for authentication.                         | —       | ⚠️ Required if no password is provided    |
| `SSH_PRIVATE_KEY_PASSPHRASE` | Optional passphrase for the SSH private key.                                            | —       | ❌                                         |

> There must be **either** a password **or** a private key set for SSH authentication.

---

### Telegram Notifications

| Variable            | Description                                                      | Default | Required |
|---------------------|------------------------------------------------------------------|---------|----------|
| `TELEGRAM_TOKEN`    | Token of your Telegram Bot.                                      | —       | ✅        |
| `TELEGRAM_CHAT_ID`  | Chat ID of the preferred Telegram chat to receive notifications. | —       | ✅        |
| `TELEGRAM_BOT_NAME` | Name of the Telegram bot used in messages.                       | —       | ✅        |

---

### Log Monitoring

| Variable        | Description                                                                        | Default | Required |
|-----------------|------------------------------------------------------------------------------------|---------|----------|
| `AUTH_LOG_PATH` | Path to your Linux authentication log file (e.g., `/var/log/auth_monitoring.log`). | —       | ✅        |

---

### Notification Triggers

These environment variables control which events will trigger notifications.  
Each variable accepts `"true"` or `"false"`. If omitted or set incorrectly, notifications are disabled by default.

| Variable                 | Description                                                                                              | Default | Required |
|--------------------------|----------------------------------------------------------------------------------------------------------|---------|----------|
| `NOTIFY_WHITELIST`       | List of IP-Addresses that will not send a Notification. (Use ``;`` for separation of multiple Addresses) | `true`  | ❌        |
| `NOTIFY_SUCCESS`         | Notify when a login succeeds.                                                                            | `true`  | ❌        |
| `NOTIFY_FAIL`            | Notify when a login attempt fails.                                                                       | `true`  | ❌        |
| `NOTIFY_DISCONNECT`      | Notify when a session disconnects.                                                                       | `true`  | ❌        |
| `NOTIFY_INVALID_USER`    | Notify when an invalid user attempts to log in.                                                          | `true`  | ❌        |
| `NOTIFY_CLOSE_SESSION`   | Notify when a session is closed.                                                                         | `true`  | ❌        |
| `NOTIFY_STARTUP`         | Notify when the app starts                                                                               | `true`  | ❌        |
| `NOTIFY_SHUTDOWN`        | Notify when the app shuts down                                                                           | `true`  | ❌        |
| `NOTIFY_IGNORE_CONTENTS` | Strings to ignore in log entries (useful for filtering noise).                                           | —       | ❌        |
| `NOTIFY_SHOW_LOG`        | Shows the Log inside the Telegram Message                                                                | `false` | ❌        |


---


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
   
5. Configure your compose file
### RUN:
then run:
> docker compose up -d