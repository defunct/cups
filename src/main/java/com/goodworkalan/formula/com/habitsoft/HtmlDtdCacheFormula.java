package com.goodworkalan.formula.com.habitsoft;

import static com.goodworkalan.comfort.io.Files.file;

import com.goodworkalan.cups.remix.Sandbox;
import com.goodworkalan.go.go.Commandable;
import com.goodworkalan.go.go.Environment;
import com.goodworkalan.mix.ProjectModule;
import com.goodworkalan.mix.builder.Builder;
import com.goodworkalan.mix.cookbook.JavaProject;
import com.goodworkalan.spawn.Spawn;

/**
 * Formula for html-dtd-cache.
 * 
 * @author Alan Gutierrez
 */
public class HtmlDtdCacheFormula implements Commandable, ProjectModule {
	/**
	 * Create the Mix project for html-dtd-cache.
	 * 
	 * @param env
	 *            The environment.
	 */
    public void execute(Environment env) {
        Sandbox sandbox = env.get(Sandbox.class, 1);
        Spawn spawn = new Spawn();
        spawn.setWorkingDirectory(sandbox.directory);
        spawn.$$("svn", "checkout", "http://java-xhtml-cache-dtds-entityresolver.googlecode.com/svn/trunk/", "html-cache-dtds");
        file(sandbox.directory, "remixed").mkdirs();
        file(sandbox.directory, "html-cache-dtds", "src").renameTo(file(sandbox.directory, "remixed", "src"));
        spawn.setWorkingDirectory(file(sandbox.directory, "remixed"));
        spawn.$(getClass().getResourceAsStream("html-dtd-cache.patch")).$("git", "apply").run();
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
                .produces("com.habitsoft/html-dtd-cache/1.0")
                .end()
            .end();
    }
}
