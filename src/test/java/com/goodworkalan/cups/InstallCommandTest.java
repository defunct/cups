package com.goodworkalan.cups;

import java.io.File;
import java.util.Collections;

import org.testng.annotations.Test;

import com.goodworkalan.comfort.io.Files;
import com.goodworkalan.go.go.Go;

/**
 * Unit tests for the {@link InstallCommand} class.
 *
 * @author Alan Gutierrez
 */
public class InstallCommandTest {
	/** Test install. */
    @Test
    public void install() {
        File home = new File(System.getProperty("user.home"));
        File directory = Files.file(home, ".m2", "repository");
        Go.execute(Collections.singletonList(directory), "cups", "install", "--recursive", "--force", "com.github.bigeasy.mix/mix");
    }
}
