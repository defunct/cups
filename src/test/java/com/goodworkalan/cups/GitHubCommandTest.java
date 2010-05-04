package com.goodworkalan.cups;

import java.io.File;
import java.util.Collections;

import org.testng.annotations.Test;

import com.goodworkalan.comfort.io.Files;
import com.goodworkalan.go.go.Go;

public class GitHubCommandTest {
    @Test
    public void get() {
        File home = new File(System.getProperty("user.home"));
        File directory = Files.file(home, ".m2", "repository");
        Go.execute(Collections.singletonList(directory), "cups", "github", "--artifact=com.github.bigeasy.mix/mix/0.+1", "--force", "--recurse");
    }
}
