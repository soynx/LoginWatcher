package com.monitor.ssh.info;

public record AuthInfo(
        String user,
        String ip,
        int port,
        boolean success,
        String authMethod,
        String eventType,
        String description,
        String rawLine
) {

    @Override
    public String toString() {
        return String.format(
                "AuthInfo{user='%s', ip='%s', port=%d, success=%s, authMethod='%s', eventType='%s', description='%s'}",
                user, ip, port, success, authMethod, eventType, description
        );
    }
}
