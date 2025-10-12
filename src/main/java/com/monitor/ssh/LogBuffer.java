package com.monitor.ssh;

import java.util.ArrayDeque;
import java.util.Deque;

public class LogBuffer {
    private final Deque<String> logs;
    private final int maxSize;

    public LogBuffer(int maxSize) {
        this.logs = new ArrayDeque<>(maxSize);
        this.maxSize = maxSize;
    }

    /**
     * Adds a new log entry to the buffer.
     * If the buffer is full, the oldest log will be removed.
     */
    public synchronized void addLog(String logEntry) {
        if (logs.size() == maxSize) {
            logs.removeFirst(); // Remove oldest
        }
        logs.addLast(logEntry);
    }

    /**
     * Checks whether the buffer already contains the given log entry.
     * Returns true if it exists, false otherwise.
     */
    public synchronized boolean contains(String logEntry) {
        return logs.contains(logEntry);
    }

    /**
     * Returns a snapshot of the current logs as an array.
     */
    public synchronized String[] getLogs() {
        return logs.toArray(new String[0]);
    }

    /**
     * Returns the current number of logs in the buffer.
     */
    public synchronized int size() {
        return logs.size();
    }
}
