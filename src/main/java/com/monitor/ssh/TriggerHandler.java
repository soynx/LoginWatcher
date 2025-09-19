package com.monitor.ssh;

import com.monitor.ssh.info.AuthInfo;

public interface TriggerHandler {
    void trigger(AuthInfo info);
}
