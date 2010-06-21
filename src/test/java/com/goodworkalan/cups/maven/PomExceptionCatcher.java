package com.goodworkalan.cups.maven;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import com.goodworkalan.cups.maven.PomException;

// TODO Document.
public class PomExceptionCatcher {
    // TODO Document.
    private final int code;

    // TODO Document.
    private final Runnable runnable;

    // TODO Document.
    public PomExceptionCatcher(int code, Runnable runnable) {
        this.code = code;
        this.runnable = runnable;
    }

    // TODO Document.
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