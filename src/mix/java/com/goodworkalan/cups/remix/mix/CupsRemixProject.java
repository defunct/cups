package com.goodworkalan.mix.cups;

import com.goodworkalan.mix.ProjectModule;
import com.goodworkalan.mix.builder.Builder;
import com.goodworkalan.mix.cookbook.JavaProject;

/**
 * Build definition for Cups Remix.
 *
 * @author Alan Gutierrez
 */
public class CupsRemixProject implements ProjectModule {
    /**
     * Build the project definition for Cups Remix.
     *
     * @param builder
     *          The project builder.
     */
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces("com.github.bigeasy.cups/cups-remix/0.1.1.3")
                .depends()
                    .production("com.github.bigeasy.cups/cups/0.1.+0")
                    .production("com.github.bigeasy.mix/mix/0.+1")
                    .production("com.github.bigeasy.spawn/spawn/0.+1")
                    .production("com.github.bigeasy.comfort-xml/comfort-xml/0.1.+0")
                    .development("org.testng/testng-jdk15/5.10")
                    .end()
                .end()
            .end();
    }
}
