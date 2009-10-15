package com.goodworkalan.mix.cups;

import java.net.URI;

import com.goodworkalan.go.go.Artifact;
import com.goodworkalan.mix.BasicJavaModule;

public class CupsModule extends BasicJavaModule {
    public CupsModule() {
        super(new Artifact("com.goodworkalan", "cups", "0.1"));
        addDependency(new Artifact("com.goodworkalan", "go-go", "0.1"));
        addDependency(new Artifact("com.goodworkalan", "madlib", "0.1"));
        addDependency(new Artifact("com.goodworkalan", "cassandra", "0.7"));
        addTestDependency(new Artifact("org.testng", "testng", "5.10"));
    }
}
