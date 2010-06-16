package com.goodworkalan.mix.cups;

import com.goodworkalan.mix.ProjectModule;
import com.goodworkalan.mix.builder.Builder;
import com.goodworkalan.mix.cookbook.JavaProject;

/**
 * Builds the project definition for Cups Maven.
 *
 * @author Alan Gutierrez
 */
public class CupsMavenProject implements ProjectModule {
    /**
     * Build the project definition for Cups Maven.
     *
     * @param builder
     *          The project builder.
     */
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces("com.github.bigeasy.cups/cups-maven/0.1.1.9")
                .depends()
                    .production("com.github.bigeasy.cups/cups/0.1.+0")
                    .production("com.github.bigeasy.madlib/madlib/0.+1")
                    .production("com.github.bigeasy.comfort-xml/comfort-xml/0.1.+0")
                    .development("org.testng/testng-jdk15/5.10")
                    .end()
                .end()
            .end();
    }
}
