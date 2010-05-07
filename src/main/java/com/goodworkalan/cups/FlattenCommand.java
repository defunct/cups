package com.goodworkalan.cups;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;

import com.goodworkalan.comfort.io.Find;
import com.goodworkalan.cups.pom.PomReader;
import com.goodworkalan.go.go.Argument;
import com.goodworkalan.go.go.Command;
import com.goodworkalan.go.go.Commandable;
import com.goodworkalan.go.go.Environment;
import com.goodworkalan.go.go.GoException;
import com.goodworkalan.go.go.library.Artifact;

/**
 * Convert a Maven repository into a Jav-a-Go-Go library by converting Maven POM
 * files into Jav-a-Go-Go dependency files.
 */
@Command(parent = CupsCommand.class)
public class FlattenCommand implements Commandable {
    /**
     * Whether or not to force the creation of dependency files if they already
     * exist.
     */
    @Argument
    public boolean force;

    /**
     * Recurse the Maven repository structure converting Maven POM files into
     * Jav-a-Go-Go dependency files. Conversions occur if the Jav-a-Go-Go file
     * does not already exist or if the force argument is true.
     * 
     * @param environment
     *            The execution environment.
     */
    public void execute(Environment environment) {
        LinkedList<String> args = new LinkedList<String>(environment.remaining);
        String repository = args.removeFirst();
        File directory = new File(repository);
        if (directory.isDirectory()) {
            if (args.isEmpty()) {
                flatten(environment, directory, new Find().include("**/*.pom"));
            } else {
                Find find = new Find();
                for (String arg : args) {
                    Artifact artifact = new Artifact(arg);
                    if (artifact != null) {
                        find.include(artifact.getPath("pom"));
                    }
                }
                flatten(environment, directory, find);
            }
        }
    }
    
    // TODO Document.
    private void flatten(Environment environment, File directory, Find find) {
        PomReader reader = new PomReader(Collections.singletonList(directory));
        for (String file : find.find(directory)) {
            Artifact artifact;
            try {
                artifact = new Artifact(new File(file));
            } catch (GoException e) {
                continue;
            }
            if (artifact != null) {
                File deps = new File(directory, artifact.getPath("dep"));
                if (artifact.getPath("pom").equals(file.toString()) && (force || !deps.exists())) {
                    flatten(environment, reader, artifact, deps);
                }
            }
        }
    }

    /**
     * Write the artifact dependencies to the given dependency file. This method
     * was extracted to test I/O failure.
     * 
     * @param env
     *            The execution environment.
     * @param reader
     *            The POM reader.
     * @param artifact
     *            The artifact to whose dependencies will be read from a Maven
     *            POM.
     * @param deps
     *            The dependency file.
     */
    void flatten(Environment env, PomReader reader, Artifact artifact, File deps) {
        System.out.println(artifact);
        try {
            FileWriter writer = new FileWriter(deps);
            for (Artifact dependency : reader.getImmediateDependencies(artifact)) {
                writer.write("+ ");
                writer.write(dependency.getGroup());
                writer.write(" ");
                writer.write(dependency.getName());
                writer.write(" ");
                writer.write(dependency.getVersion());
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            deps.delete();
            // FIXME Use error bundle.
            env.io.err.println("Unable to flatten POM for artifact " + artifact.toString());
            e.printStackTrace(env.io.err);
        }
    }
}
