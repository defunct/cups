package com.goodworkalan.formula.javax.inject;

import static com.goodworkalan.comfort.io.Files.file;

import com.goodworkalan.cups.remix.Sandbox;
import com.goodworkalan.go.go.Commandable;
import com.goodworkalan.go.go.Environment;
import com.goodworkalan.mix.ProjectModule;
import com.goodworkalan.mix.builder.Builder;
import com.goodworkalan.mix.cookbook.JavaProject;
import com.goodworkalan.spawn.Spawn;

public class InjectFormula implements Commandable, ProjectModule {
    /**
     * Create the project directory for javax.inject.
     */
    public void execute(Environment env) {
        Sandbox sandbox = env.get(Sandbox.class, 1);
        Spawn spawn = new Spawn();
        spawn.setWorkingDirectory(sandbox.directory);
        spawn.$$("svn", "checkout", "http://atinject.googlecode.com/svn/trunk/", "atinject");
        file(sandbox.directory, "remixed", "src", "main").mkdirs();
        file(sandbox.directory, "atinject", "src").renameTo(file(sandbox.directory, "remixed", "src", "main", "java"));
    }

    /**
     * Build the project definition for Java Inject.
     *
     * @param builder
     *          The project builder.
     */
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces("javax.inject/inject/1.0")
                .end()
            .end();
    }
}
