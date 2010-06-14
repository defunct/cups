package com.goodworkalan.cups.maven;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.goodworkalan.cups.CupsCommand;
import com.goodworkalan.cups.CupsError;
import com.goodworkalan.cups.IO;
import com.goodworkalan.cups.Result;
import com.goodworkalan.go.go.Argument;
import com.goodworkalan.go.go.Command;
import com.goodworkalan.go.go.Commandable;
import com.goodworkalan.go.go.Environment;
import com.goodworkalan.go.go.GoException;
import com.goodworkalan.go.go.library.Artifact;
import com.goodworkalan.go.go.library.Artifacts;
import com.goodworkalan.go.go.library.Include;
import com.goodworkalan.ilk.Ilk;

/**
 * Download an artifact form a Maven repository.
 * <p>
 * Loads the artifacts given on the command line. Artifacts must be full Maven
 * artifact strings in the from <code>group/name/version</code>, for example
 * <code>org.slf4j/slf4j-api/1.4.2</code>. This utility is <strong>does
 * not</strong> use version matching and <strong>cannot</strong> select a
 * version based on a version selector pattern.
 * <p>
 * The command will attempt to download dependencies from a Maven repository
 * downloading the POM and converting it to a Jav-a-Go-Go dependencies file, and
 * downloading the class file jar. It will attempt to download the source jar
 * and javadoc jar, but they are not required.
 * <p>
 * Jav-a-Go-Go artifacts do not share the notion of classifiers with Maven, so
 * for Maven artifacts with a classifiers, the classifier is appended to the
 * artifact name. For example, <code>org.testng/testng/5.10/jdk15</code> becomes
 * <code>org.testng/testng-jdk15/5.10</code>. You will refer to TestNG 5.10
 * using the latter artifact pattern in Jav-a-Go-Go, Cups and Mix.
 * <p>
 * The command will generate a report on standard out indicating the success of
 * the download operation.
 * 
 * @author Alan Gutierrez
 */
@Command(parent = CupsCommand.class)
public class MavenCommand implements Commandable {
    /** The map of artifacts to Maven repository URI where they have been found. */
    private final Map<Artifact, URI> found = new HashMap<Artifact, URI>();

    /** The list of Maven repositories to query. */
    private final List<URI> uris = new ArrayList<URI>();

    /** The library where new artifacts will be installed. */
    @Argument
    public File library;
    
    /** Force reinstall. */
    @Argument
    public boolean force;
    
    /** Show build dependencies (always empty for Maven). */
    @Argument
    public boolean buildDependencies;
    
    /**
     * Add a URI to the list of Maven repositories to search.
     * 
     * @param uri
     *            The Maven repository URI.
     */
    @Argument
    public void addUri(URI uri) {
        uris.add(uri);
    }

    /**
     * Generate a classified suffix if the given <code>classifier</code> is not
     * <code>null</code>. Otherwise, return the given <code>suffix</code> as is.
     * 
     * @param suffix
     *            The slash separated file name suffix.
     * @param classifier
     *            The Maven classifier.
     * @return The classified suffix or the given suffix if the classifier is
     *         <code>null</code>.
     */
    private static String classify(String suffix, String classifier) {
        return classifier == null ? suffix : classifier + "/" + suffix;
    }

    /**
     * Fetch the <code>source</code> Maven artifact with the given
     * <code>suffix</code> to the <code>destination</code> Jav-a-Go-Go artifact
     * adjusting for the given <code>classifier</code> from the given Maven
     * repository <code>uri</code>. The separate <code>source</code> and
     * <code>destination</code> artifacts are necessary because classified Maven
     * artifacts are renamed with the classifier as part of the artifact name.
     * <p>
     * The <code>classifier</code> can be an empty string, in which case, the
     * <code>suffix</code> is applied to both the <code>source</code> and
     * <code>destination</code>, otherwise, the classifier is used to find the
     * source Maven file, but it is not applied to the file name of the
     * Jav-a-Go-Go file because the classifier will already have been folded
     * into the <code>destination</code> artifact name.
     * <p>
     * The <code>suffix</code> is in the slash separated suffix format where the
     * slash represents the division between any suffix appended to the file
     * name and the file extensions. Thus, <code>sources/tar.gz</code> is
     * converted into the string <code>-sources.tar.gz</code> and appended to
     * the base file name for the artifact.
     * 
     * @param uri
     *            The URI of the Maven repository.
     * @param source
     *            The Maven artifact.
     * @param destination
     *            The Jav-a-Go-Go artifact.
     * @param suffix
     *            The slash separated file suffix.
     * @param classifier
     *            The Maven classifier or the empty string if there is no
     *            classifier.
     * @return <code>true</code> if the file was found at the Maven repository,
     *         <code>false</code> if the HTTP response was any of the HTTP error
     *         responses.
     */
    private boolean fetch(URI uri, Artifact source, Artifact destination, String suffix, String classifier) {
        try {
            File full = new File(library, destination.getPath(suffix));
            File directory = full.getParentFile();
            if (!(directory.isDirectory() || directory.mkdirs())) {
                throw new CupsError(MavenCommand.class, "mkdirs");
            }
            URL url = uri.resolve(source.getPath(classify(suffix, classifier))).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = connection.getInputStream();
                byte[] buffer = new byte[4092];
                FileOutputStream out = new FileOutputStream(full);
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
                return true;
            }
            return false;
        } catch (IOException e) {
            throw new GoException(0, e);
        }
    }

    /**
     * Fetch the <code>source</code> Maven artifact with the given
     * <code>suffix</code> to the <code>destination</code> Jav-a-Go-Go artifact
     * adjusting for the given <code>classifier</code> from the first Maven
     * repository in the list of Maven repository URIs that hosts a POM for the
     * Maven artifact. The separate <code>source</code> and
     * <code>destination</code> artifacts are necessary because classified Maven
     * artifacts are renamed with the classifier as part of the artifact name.
     * <p>
     * The <code>classifier</code> can be an empty string, in which case, the
     * <code>suffix</code> is applied to both the <code>source</code> and
     * <code>destination</code>, otherwise, the classifier is used to find the
     * source Maven file, but it is not applied to the file name of the
     * Jav-a-Go-Go file because the classifier will already have been folded
     * into the <code>destination</code> artifact name.
     * <p>
     * The <code>suffix</code> is in the slash separated suffix format where the
     * slash represents the division between any suffix appended to the file
     * name and the file extensions. Thus, <code>sources/tar.gz</code> is
     * converted into the string <code>-sources.tar.gz</code> and appended to
     * the base file name for the artifact.
     * 
     * @param uri
     *            The URI of the Maven repository.
     * @param source
     *            The Maven artifact.
     * @param destination
     *            The Jav-a-Go-Go artifact.
     * @param suffix
     *            The slash separated file suffix.
     * @param classifier
     *            The Maven classifier or the empty string if there is no
     *            classifier.
     * @return <code>true</code> if the file was found at the Maven repository,
     *         <code>false</code> if the HTTP response was any of the HTTP error
     *         responses.
     */
    private boolean fetch(Artifact source, Artifact destination, String suffix, String classifier) {
        URI uri = found.get(source);
        if (uri == null) {
            if (suffix.equals("pom")) {
                for (URI test : uris) {
                    if (fetch(test, source, destination, suffix, classifier)) {
                        found.put(source, test);
                        return true;
                    }
                }
                return false;
            }
            if (fetch(source, destination, "pom", null)) {
                return fetch(source, destination, suffix, classifier);
            }
            return false;
        }
        return fetch(uri, source, destination, suffix, classifier);
    }

    /**
     * Download the <code>source</code> Maven artifact files to the
     * <code>destination</code> Jav-a-Go-Go artifact adjusting for the given
     * <code>classifier</code> from the first Maven repository in the list of
     * Maven repository URIs that hosts a POM for the Maven artifact.
     * <p>
     * This method will download the POM and convert it to a dependencies file,
     * download the classes JAR, adjusting for the classifier if any, and
     * attempt to download sources and javadoc jars, if they are available.
     * <p>
     * This method return a linked list of <code>Result</code> instances, with a
     * <code>Result</code> for the Maven artifact any parent POMs to the Maven
     * artifact POM.
     * 
     * @param source
     *            The Maven artifact.
     * @param destination
     *            The Jav-a-Go-Go artifact.
     * @param suffix
     *            The slash separated file suffix.
     * @param classifier
     *            The Maven classifier or the empty string if there is no
     *            classifier.
     * @return A list of <code>Result</code> instances for the Maven artifact
     *         and any parent artifacts.
     */
    private LinkedList<Result> download(Artifact source, Artifact destination, String classifier) {
        LinkedList<Result> results = new LinkedList<Result>();
        PomReader reader = new PomReader(library);
        File pom = new File(library, destination.getPath("pom"));
        if (pom.exists() && !force) {
            results.addLast(new Result('*', destination, "pom"));
        } else if (!fetch(source, destination, "pom", null)) {
            results.addLast(new Result('!', destination));
            return results;
        } else {
            results.addLast(new Result('@', destination, "pom"));
        }
        Artifact current = destination;
        Artifact parent = reader.getParent(current);
        if (parent != null) {
            results.addAll(download(parent, parent, null));
            if (results.getLast().flag == '!') {
                return results;
            }
        }
        List<Artifact> dependencies = reader.getImmediateDependencies(destination);
        IO.flatten(library, destination, dependencies);
        if (fetch(source, destination, "jar", classifier)) {
            results.getFirst().suffixes.add("jar");
        }
        for (String suffix : new String[] { "sources/jar", "javadoc/jar" }) {
            if (fetch(source, destination, suffix, null)) {
                results.getFirst().suffixes.add(suffix);
            }
        }
        return results;
    }
    
    public void execute(Environment env) {
    	if (buildDependencies) {
            env.output(new Ilk<List<Include>>() {}, Collections.<Include>emptyList());
    	} else {
    		install(env);
    	}
    }

    /**
     * Download the artifacts given on the command line.
     * 
     * @param env
     *            The environment.
     */
    public void install(Environment env) {
        if (library == null) {
            if (System.getProperty("user.home") == null) {
                throw new CupsError(MavenCommand.class, "no.user.home");
            }
            File home = new File(System.getProperty("user.home"));
            library = new File(home, ".m2/repository");
        }
        if (!library.isDirectory() && !library.mkdirs()) {
            throw new CupsError(MavenCommand.class, "cannot.create.library");
        }

        Properties classified;
		try {
			classified = getClassified();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
        
        LinkedList<Include> includes = new LinkedList<Include>();

        for (String pattern : env.remaining) {
            env.debug("artifact", pattern);
            
            pattern = classified.getProperty(pattern, pattern);

            // If the artifact pattern has four parts, Convert a four part,
            // classified Maven artifact string into a three part Jav-a-Go-Go
            // artifact string, where the classifier is folded into the 

            String classifier = null;
            String[] split;
            if ((split = pattern.split("/")).length == 4) {
                classifier = split[3];
                pattern = pattern.substring(0, pattern.lastIndexOf('/'));
            }
            Artifact source = new Artifact(pattern);
            Artifact destination;
            if (classifier == null) {
                destination = source;
            } else {
                destination = new Artifact(source.getGroup(), source.getName() + "-" + classifier, source.getVersion()); 
            }
            LinkedList<Result> results = new LinkedList<Result>();
            if (force || ! new File(library, destination.getPath("dep")).exists()) {
                results.addAll(download(source, destination, classifier));
            } else {
                results.add(new Result('*', destination));
            }
            for (Result result : results) {
                if (result.flag != '!') {
                    includes.addAll(Artifacts.read(new File(library, result.artifact.getPath("dep"))));
                }
                env.io.out.println(result);
            }
            env.io.out.flush();
        }

        env.output(new Ilk<List<Include>>() {}, includes);
    }
    
    public Properties getClassified() throws IOException {
        Properties all = new Properties();
        Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources("META-INF/services/com.goodworkalan.cups.maven.classified.properties");
        while (urls.hasMoreElements()) {
        	URL url = urls.nextElement();
        	Properties properties = new Properties();
        	properties.load(url.openStream());
        	for (Object key : properties.keySet()) {
        		if (!all.containsKey(key)) {
        			all.put(key, properties.get(key));
        		}
        	}
        }
        return all;
    }
}
