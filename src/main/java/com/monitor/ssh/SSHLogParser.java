package com.monitor.ssh;

import com.monitor.ssh.info.AuthInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SSHLogParser {
    private static final Logger logger = LoggerFactory.getLogger(SSHLogParser.class);

    private static final Pattern SUCCESS_PATTERN = Pattern.compile(
            "Accepted (\\S+) for (\\S+) from ([\\d.:a-fA-F]+) port (\\d+)"
    );

    private static final Pattern FAIL_PATTERN = Pattern.compile(
            "Failed (\\S+) for (?:invalid user )?(\\S+) from ([\\d.:a-fA-F]+) port (\\d+)"
    );

    private static final Pattern INVALID_USER_PATTERN = Pattern.compile(
            "Invalid user (\\S+) from ([\\d.:a-fA-F]+) port (\\d+)"
    );

    private static final Pattern DISCONNECT_PATTERN = Pattern.compile(
            "Disconnected from (?:invalid user )?(\\S+) ([\\d.:a-fA-F]+) port (\\d+)"
    );

    public static AuthInfo parseLine(String line) {
        if (line == null || line.isEmpty()) return null;

        Matcher mSuccess = SUCCESS_PATTERN.matcher(line);
        if (mSuccess.find()) {
            String method = mSuccess.group(1);
            String user = mSuccess.group(2);
            String ip = mSuccess.group(3);
            int port = Integer.parseInt(mSuccess.group(4));
            return new AuthInfo(
                    user,
                    ip,
                    port,
                    true,
                    method,
                    "SUCCESS",
                    String.format("Successful login via %s", method),
                    line
            );
        }

        Matcher mFail = FAIL_PATTERN.matcher(line);
        if (mFail.find()) {
            String method = mFail.group(1);
            String user = mFail.group(2);
            String ip = mFail.group(3);
            int port = Integer.parseInt(mFail.group(4));
            return new AuthInfo(
                    user,
                    ip,
                    port,
                    false,
                    method,
                    "FAILURE",
                    String.format("Failed login via %s", method),
                    line
            );
        }

        Matcher mInvalid = INVALID_USER_PATTERN.matcher(line);
        if (mInvalid.find()) {
            String user = mInvalid.group(1);
            String ip = mInvalid.group(2);
            int port = Integer.parseInt(mInvalid.group(3));
            return new AuthInfo(
                    user,
                    ip,
                    port,
                    false,
                    "unknown",
                    "INVALID_USER",
                    "Attempted login with invalid user",
                    line
            );
        }

        Matcher mDisconnect = DISCONNECT_PATTERN.matcher(line);
        if (mDisconnect.find()) {
            String user = mDisconnect.group(1);
            String ip = mDisconnect.group(2);
            int port = Integer.parseInt(mDisconnect.group(3));
            return new AuthInfo(
                    user,
                    ip,
                    port,
                    false,
                    "unknown",
                    "DISCONNECT",
                    "Disconnected before authentication",
                    line
            );
        }

        logger.trace("Unmatched log line: {}", line);
        return null;
    }
}
