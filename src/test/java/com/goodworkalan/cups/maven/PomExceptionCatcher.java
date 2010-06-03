package com.goodworkalan.cups.maven;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import com.goodworkalan.cups.maven.PomException;

public class PomExceptionCatcher {
    private final int code;

    private final Runnable runnable;

    public PomExceptionCatcher(int code, Runnable runnable) {
        this.code = code;
        this.runnable = runnable;
    }

    public void run() {
        try {
            runnable.run();
        } catch (PomException e) {
            assertEquals(e.getCode(), code);
            if (e.getMessage().contains("meta error")) {
                fail("No message for error code: " + e.getCode());
            }
            System.out.println(e.getMessage());
            return;
        }
        fail("Expected exception not thrown.");
    }
}