package com.goodworkalan.danger.mix;

import com.goodworkalan.mix.ProjectModule;
import com.goodworkalan.mix.builder.Builder;
import com.goodworkalan.mix.builder.JavaProject;

public class CupsInstallerProject extends ProjectModule {
    @Override
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces("com.github.bigeasy.cups/cups-installer/0.1.1.14")
                .test()
                    .depends()
                        .include("org.testng/testng-jdk15/5.10")
                        .end()
                    .end()
                .end()
            .end();
    }
}
