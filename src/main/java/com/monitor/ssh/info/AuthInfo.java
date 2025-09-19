package com.monitor.ssh.info;

public record AuthInfo(String user, String ip, int port, boolean success) {

    @Override
    public String toString() {
        return String.format("AuthInfo{user='%s', ip='%s', port=%d, success=%s}",
                user, ip, port, success);
    }
}
