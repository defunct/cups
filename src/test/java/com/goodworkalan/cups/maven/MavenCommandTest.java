package com.goodworkalan.cups.maven;

import static com.goodworkalan.comfort.io.Files.file;

import java.io.File;
import java.util.Collections;

import org.testng.annotations.Test;

import com.goodworkalan.comfort.io.Files;
import com.goodworkalan.go.go.Go;

// TODO Document.
public class MavenCommandTest {
    // TODO Document.
    @Test
    public void execute() {
        File home = new File(System.getProperty("user.home"));
        File directory = file(home, ".m2", "repository");
        Files.unlink(new File("target/test/library"));
        Go.execute(Collections.singletonList(directory), 
                "cups", "install", "--library=target/test/library", "org.hibernate/hibernate-core/3.3.1.GA"
                );
        Go.execute(Collections.singletonList(directory), 
            "cups", "install", "--library=target/test/library", "--recursive", "org.slf4j/slf4j-api/1.4.2"
            );
        Go.execute(Collections.singletonList(directory), 
            "cups", "install", "--library=target/test/library", "--recursive", "org.testng/testng-jdk15/5.10"
            );
        Go.execute(Collections.singletonList(directory), 
            "cups", "install", "--library=target/test/library", "--recursive", "org.testng/testng-jdk15/5.10"
            );
        Go.execute(Collections.singletonList(directory), 
            "cups", "install", "--library=target/test/library", "--recursive", "--force", "org.testng/testng-jdk15/5.10"
            );
    }
}
