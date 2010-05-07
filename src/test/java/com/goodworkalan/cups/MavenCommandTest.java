package com.goodworkalan.cups;

import static com.goodworkalan.comfort.io.Files.file;

import java.io.File;
import java.util.Collections;

import org.testng.annotations.Test;

import com.goodworkalan.go.go.Go;

public class MavenCommandTest {
    @Test
    public void execute() {
        File home = new File(System.getProperty("user.home"));
        File directory = file(home, ".m2", "repository");
        Go.execute(Collections.singletonList(directory), 
            "cups", "maven",
            "--library=target/test/library",
            "--artifact=org.slf4j/slf4j-api/1.4.2",
            "--uri=http://repo2.maven.org/maven2/",
            "--recurse"
            );
    }
}
