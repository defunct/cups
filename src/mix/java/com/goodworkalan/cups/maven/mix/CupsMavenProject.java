package com.goodworkalan.mix.cups;

import com.goodworkalan.mix.ProjectModule;
import com.goodworkalan.mix.builder.Builder;
import com.goodworkalan.mix.builder.JavaProject;

public class CupsMavenProject extends ProjectModule {
    @Override
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces("com.github.bigeasy.cups/cups-maven/0.1.1.3")
                .main()
                    .depends()
                        .include("com.github.bigeasy.cups/cups/0.1.+0")
                        .include("com.github.bigeasy.comfort-xml/comfort-xml/0.1.+0")
                        .end()
                    .end()
                .test()
                    .depends()
                        .include("org.testng/testng-jdk15/5.10")
                        .end()
                    .end()
                .end()
            .end();
    }
}
