package com.goodworkalan.danger.mix;

import com.goodworkalan.go.go.Environment;
import com.goodworkalan.go.go.library.Include;
import com.goodworkalan.go.go.library.ArtifactPart;
import com.goodworkalan.go.go.library.ResolutionPart;
import com.goodworkalan.go.go.library.PathPart;
import com.goodworkalan.mix.Mix;
import com.goodworkalan.mix.Project;
import com.goodworkalan.mix.ProjectModule;
import com.goodworkalan.go.go.Commandable;
import com.goodworkalan.mix.builder.Builder;
import com.goodworkalan.mix.cookbook.JavaProject;
import com.goodworkalan.comfort.io.Files;
import java.util.Collections;
import java.util.TreeSet;
import java.util.Set;
import java.io.File;

/**
 * Builds the project definition for Cups Installer.
 *
 * @author Alan Gutierrez
 */
public class CupsInstallerProject implements ProjectModule {
    /**
     * Build the project definition for Cups Installer.
     *
     * @param builder
     *          The project builder.
     */
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces("com.github.bigeasy.cups/cups-installer/0.1.1.29")
                .depends()
                    .development("org.testng/testng-jdk15/5.10")
                    .end()
                .end()
            .end();
        builder
            .recipe("dependencies")
                .executable(new Commandable() {
                    public void execute(Environment env) {
                        Set<String> artifacts = new TreeSet<String>();
                        for (String pattern : new String[] { 
                            "com.github.bigeasy.go-go/go-go-boot/+0",
                            "com.github.bigeasy.cups/cups/+0"
                        }) {
                            ArtifactPart artifactPart = env.library.getArtifactPart(new Include(pattern), "dep", "jar");
                            for (PathPart part : env.library.resolve(Collections.<PathPart>singleton(new ResolutionPart(artifactPart.getArtifact())))) {
                                artifacts.add(part.getArtifact().toString()); 
                            }
                        }
                        File dependencies = new File("src/main/resources/com/goodworkalan/cups/installer/dependencies.txt");
                        dependencies.getParentFile().mkdirs();
                        Files.pour(dependencies, artifacts);
                        env.io.out.println(artifacts);
                    }
                })
                .end()
            .end();
    }
}
