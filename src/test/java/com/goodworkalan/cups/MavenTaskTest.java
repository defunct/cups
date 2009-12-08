package com.goodworkalan.cups;

import java.io.File;
import java.util.Collections;

import org.testng.annotations.Test;

import com.goodworkalan.comfort.io.Files;
import com.goodworkalan.go.go.CommandInterpreter;
import com.goodworkalan.go.go.ErrorCatcher;

public class MavenTaskTest {
    @Test
     public void execute() {
        File library = new File("target/test/library");
        Files.delete(library);
        library.mkdirs();
        
        CommandInterpreter ci = new CommandInterpreter(new ErrorCatcher(), Collections.<File>emptyList());
        ci.execute("cups", "maven",
                "--library=target/test/library",
                "--artifact=org.eclipse.jetty/jetty-plus/7.0.0.RC3",
                "--uri=http://repo2.maven.org/maven2/",
                "--recurse");
    }
}
