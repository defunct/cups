package com.goodworkalan.cups.maven;

import static com.goodworkalan.comfort.io.Files.file;

import java.io.File;
import java.util.Collections;

import org.testng.annotations.Test;

import com.goodworkalan.comfort.io.Files;
import com.goodworkalan.go.go.Go;

public class MavenCommandTest {
    @Test
    public void execute() {
        File home = new File(System.getProperty("user.home"));
        File directory = file(home, ".m2", "repository");
        Files.unlink(new File("target/test/library"));
        Go.execute(Collections.singletonList(directory), 
            "cups", "maven",
            "--library=target/test/library",
            "--uri=http://repo2.maven.org/maven2/",
            "--recurse",
            "org.slf4j/slf4j-api/1.4.2"
            );
        Go.execute(Collections.singletonList(directory), 
            "cups", "maven",
            "--library=target/test/library",
            "--uri=http://repo2.maven.org/maven2/",
            "--recurse",
            "org.testng/testng/5.10/jdk15"
            );
        Go.execute(Collections.singletonList(directory), 
            "cups", "maven",
            "--library=target/test/library",
            "--uri=http://repo2.maven.org/maven2/",
            "--recurse",
            "org.testng/testng/5.10/jdk15"
            );
        Go.execute(Collections.singletonList(directory), 
            "cups", "maven",
            "--library=target/test/library",
            "--uri=http://repo2.maven.org/maven2/",
            "--force",
            "org.testng/testng/5.10/jdk15"
            );
    }
}
