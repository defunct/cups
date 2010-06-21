package com.goodworkalan.cups.maven;

import static com.goodworkalan.comfort.io.Files.file;

import java.io.File;
import java.util.Collections;

import org.testng.annotations.Test;

import com.goodworkalan.comfort.io.Files;
import com.goodworkalan.comfort.io.Find;
import com.goodworkalan.go.go.Go;

// TODO Document.
public class FlattenCommandTest {
    // TODO Document.
    @Test
    public void flatten() {
        Find find = new Find().include("**/*.dep");
        File poms = new File("src/test/poms");
        for (String fileName : find.find(poms)) {
            Files.unlink(new File(poms, fileName));
        }
        File home = new File(System.getProperty("user.home"));
        File directory = file(home, ".m2", "repository");
        Go.execute(Collections.singletonList(directory), "cups", "flatten", "src/test/poms");
    }
}
