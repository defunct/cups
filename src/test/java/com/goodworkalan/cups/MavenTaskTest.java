package com.goodworkalan.cups;

import java.io.File;

import org.testng.annotations.Test;

import com.goodworkalan.glob.Files;
import com.goodworkalan.go.go.CommandInterpreter;

public class MavenTaskTest {
    @Test
     public void execute() {
        File library = new File("target/test/library");
        Files.delete(library);
        library.mkdirs();
        
        CommandInterpreter ci = new CommandInterpreter();
        ci.execute("cups", "maven",
                "--library=target/test/library",
                "--artifact=org.eclipse.jetty/jetty-plus/7.0.0.RC3",
                "--uri=http://repo2.maven.org/maven2/",
                "--recurse");
    }
}
