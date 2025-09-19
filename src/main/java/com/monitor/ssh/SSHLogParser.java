package com.monitor.ssh;

import com.monitor.ssh.info.AuthInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SSHLogParser {
    private static final Logger logger = LoggerFactory.getLogger(SSHLogParser.class);
    private static final Pattern SUCCESS_PATTERN = Pattern.compile(
            "Accepted \\w+ for (\\S+) from (\\S+) port (\\d+)"
    );
    private static final Pattern FAIL_PATTERN = Pattern.compile(
            "Failed \\w+ for (?:invalid user )?(\\S+) from (\\S+) port (\\d+)"
    );

    public static AuthInfo parseLine(String line) {
        Matcher mSuccess = SUCCESS_PATTERN.matcher(line);
        if (mSuccess.find()) {
            String user = mSuccess.group(1);
            String ip = mSuccess.group(2);
            int port = Integer.parseInt(mSuccess.group(3));
            return new AuthInfo(user, ip, port, true);
        }

        Matcher mFail = FAIL_PATTERN.matcher(line);
        if (mFail.find()) {
            String user = mFail.group(1);
            String ip = mFail.group(2);
            int port = Integer.parseInt(mFail.group(3));
            return new AuthInfo(user, ip, port, false);
        }

        logger.debug("Log line '{}' could not be parsed into AuthInfo object!", line);
        return null; // Zeile nicht relevant
    }
}
