package com.goodworkalan.mix.cups;

import com.goodworkalan.mix.ProjectModule;
import com.goodworkalan.mix.builder.Builder;
import com.goodworkalan.mix.builder.JavaProject;

public class CupsProject extends ProjectModule {
    @Override
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces("com.github.bigeasy.cups/cups/0.1.1.6")
                .main()
                    .depends()
                        .include("com.github.bigeasy.github4j/github4j-downloads/0.+1")
                        .include("com.github.bigeasy.go-go/go-go/0.+1")
                        .include("com.github.bigeasy.comfort-io/comfort-io/0.+1")
                        .include("com.github.bigeasy.madlib/madlib/0.+1")
                        .include("com.github.bigeasy.danger/danger/0.+1")
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
