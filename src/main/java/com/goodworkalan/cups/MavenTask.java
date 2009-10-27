package com.goodworkalan.cups;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.goodworkalan.go.go.Argument;
import com.goodworkalan.go.go.Artifact;
import com.goodworkalan.go.go.Artifacts;
import com.goodworkalan.go.go.Command;
import com.goodworkalan.go.go.Commandable;
import com.goodworkalan.go.go.Environment;
import com.goodworkalan.go.go.Include;

@Command(parent = CupsTask.class)
public class MavenTask implements Commandable {
    /** The list of Maven repositories to query. */
    private final List<URI> uris = new ArrayList<URI>();
    
    /** The library where new artifacts will be installed */
    private File library;
    
    /** The list of artifacts to install. */
    private final List<Artifact> artifacts = new ArrayList<Artifact>(); 
    
    /** Force reinstall. */
    private boolean force;
    
    /** Recursively search for dependencies. */
    private boolean recurse;
    
    @Argument
    public void addUri(URI uri) {
        uris.add(uri);
    }
    
    @Argument
    public void addArtifact(Artifact artifact) {
        artifacts.add(artifact);
    }
    
    @Argument
    public void addForce(boolean force) {
        this.force = force;
    }
    
    @Argument
    public void addLibrary(File library) {
        this.library = library;
    }
    
    @Argument
    public void addRecurse(boolean recurse) {
        this.recurse = recurse;
    }
    
    private LinkedList<Result> download(MavenResolver resolver, Artifact artifact) {
        LinkedList<Result> results = new LinkedList<Result>();
        PomReader reader = new PomReader(resolver.getLibraries());
        File pom = resolver.find(artifact, "pom");
        if (pom != null) {
            results.addLast(new Result('*', artifact, "pom"));
        } else if (!resolver.download(artifact, "pom")) {
            results.addLast(new Result('!', artifact));
            return results;
        } else {
            results.addLast(new Result('@', artifact, "pom"));
        }
        Artifact current = artifact;
        Artifact parent = reader.getParent(current);
        if (parent != null) {
            results.addAll(download(resolver, parent));
            if (results.getLast().flag == '!') {
                return results;
            }
        }
        List<Artifact> dependencies = reader.getImmediateDependencies(artifact);
        IO.flatten(library, artifact, dependencies);
        for (String suffix : new String[] { "jar", "sources/jar", "javadoc/jar" }) {
            if (resolver.download(artifact, suffix)) {
                results.getFirst().suffixes.add(suffix);
            }
        }
        return results;
    }
    
    public void execute(Environment env) {
        if (library == null) {
            if (System.getProperty("user.home") == null) {
                throw new CupsException(0);
            }
            File home = new File(System.getProperty("user.home"));
            if (!home.isDirectory()) {
                throw new CupsException(0);
            }
            library = new File(home, ".m2/repository");
            if (!library.isDirectory() && !library.mkdir()) {
                throw new CupsException(0);
            }
        }

        LinkedList<Include> includes = new LinkedList<Include>();
        MavenResolver resolver = new MavenResolver(library, Collections.<File>emptyList(), uris);

        for (Artifact artifact : artifacts) {
            LinkedList<Result> results = new LinkedList<Result>();
            File found = resolver.find(artifact, "dep");
            if (force || found == null) {
                results.addAll(download(resolver, artifact));
            } else {
                results.add(new Result('*', artifact));
            }
            for (Result result : results) {
                if (result.flag != '!') {
                    includes.addAll(Artifacts.read(new File(resolver.find(result.artifact, "dep"), result.artifact.getPath("dep"))));
                }
                result.print(env.io.out);
            }
            env.io.out.flush();
        }
        
        Set<Artifact> seen = new HashSet<Artifact>();
        while (!includes.isEmpty()) {
            Include include = includes.removeFirst();
            Artifact artifact = include.getArtifact();
            if (seen.contains(artifact)) {
                continue;
            }
            seen.add(artifact);
            File found = resolver.find(artifact, "dep");
            LinkedList<Result> results = new LinkedList<Result>();
            if (found == null || ! new File(found, artifact.getPath("jar")).exists()) {
                if (recurse) {
                    results.addAll(download(resolver, artifact));
                } else {
                    results.add(new Result('!', artifact));
                }
            } else {
                results.add(new Result('*', artifact));
                includes.addAll(Artifacts.read(new File(found, artifact.getPath("dep"))));
            }
            for (Result result : results) {
                if (result.flag != '!') {
                    includes.addAll(Artifacts.read(new File(resolver.find(result.artifact, "dep"), artifact.getPath("dep"))));
                }
                result.print(env.io.out);
            }
            env.io.out.flush();
        }
    }
}
