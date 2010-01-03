package com.goodworkalan.danger.mix;

import com.goodworkalan.go.go.Artifact;
import com.goodworkalan.mix.ProjectModule;
import com.goodworkalan.mix.builder.Builder;
import com.goodworkalan.mix.builder.JavaProject;

public class CupsInstallerProject extends ProjectModule {
    @Override
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces(new Artifact("com.github.bigeasy.cups/cups-installer/0.1"))
                .test()
                    .depends()
                        .artifact(new Artifact("org.testng/testng/5.10/jdk15"))
                        .end()
                    .end()
                .end()
            .end();
    }
}
