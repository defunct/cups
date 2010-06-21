package com.goodworkalan.cups;

import java.io.File;
import java.util.Collections;

import org.testng.annotations.Test;

import com.goodworkalan.comfort.io.Files;
import com.goodworkalan.go.go.Go;

// TODO Document.
public class GitHubCommandTest {
    // TODO Document.
    @Test
    public void get() {
        File home = new File(System.getProperty("user.home"));
        File directory = Files.file(home, ".m2", "repository");
        Go.execute(Collections.singletonList(directory), "cups", "github", "--force", "com.github.bigeasy.mix/mix/0.+1");
    }
}
