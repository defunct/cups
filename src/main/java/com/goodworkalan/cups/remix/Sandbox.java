package com.goodworkalan.cups.remix;

import static com.goodworkalan.comfort.io.Files.file;

import java.io.File;

import com.goodworkalan.go.go.Commandable;
import com.goodworkalan.go.go.Environment;

public class Sandbox {
    public File directory;
    
    public Class<? extends Commandable> recipeClass;
    
    public void mix(Environment env) {
        env.executor.run(env.io, "mix", "--working-directory=" + file(directory, "remixed").getAbsolutePath(), "--project-module=" + recipeClass.getCanonicalName(), "install");
    }
}
