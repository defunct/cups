package com.goodworkalan.mix.cups;

import com.goodworkalan.go.go.Artifact;
import com.goodworkalan.mix.ProjectModule;
import com.goodworkalan.mix.builder.Builder;
import com.goodworkalan.mix.builder.JavaProject;

public class CupsProject extends ProjectModule {
    @Override
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces(new Artifact("com.goodworkalan/cups/0.1"))
                .main()
                    .depends()
                        .artifact(new Artifact("com.goodworkalan/go-go/0.1.1"))
                        .artifact(new Artifact("com.goodworkalan/madlib/0.1"))
                        .artifact(new Artifact("com.goodworkalan/comfort-io/0.1.1"))
                        .artifact(new Artifact("com.goodworkalan/cassandra/0.7"))
                        .end()
                    .end()
                .test()
                    .depends()
                        .artifact(new Artifact("org.testng/testng/5.10"))
                        .end()
                    .end()
                .end()
            .end();
    }
}
