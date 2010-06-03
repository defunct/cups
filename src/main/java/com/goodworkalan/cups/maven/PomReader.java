package com.goodworkalan.cups.maven;

import static com.goodworkalan.cups.maven.PomException.POM_FILE_NOT_FOUND;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.goodworkalan.comfort.xml.Document;
import com.goodworkalan.comfort.xml.Element;
import com.goodworkalan.comfort.xml.Serializer;
import com.goodworkalan.go.go.library.Artifact;
import com.goodworkalan.madlib.VariableProperties;

/**
 * Read a POM and extract only the immediate, production dependencies.
 * <p>
 * Note that you once made an attempt to extract this to its own package, but
 * once you've replaced {@link Artifact} with a string list and look at it
 * again, it is apparent that this class has a very specific purpose, to extract
 * dependencies for the sake of Cups.
 * <p>
 * Anyone who would stumble upon a POM reader is probably not trying to strip
 * and discard POM, the way that Cups seeks to strip and discard POM. They'll be
 * interested in the different dependency scopes, the classifiers, the project
 * license, the committer names and their email addresses and their family vacation
 * photographs and everything else that makes POM so special.
 * <p> 
 * This class is only interested in the produciton dependencies.
 * 
 * @author Alan Gutierrez
 */
public class PomReader {
    private final Map<Artifact, Document> documents = new HashMap<Artifact, Document>();

    /** The Maven repository directory. */
    private final File library;

    /**
     * Create a POM reader that reads Maven POMs from the Maven located in the
     * given library directory.
     * 
     * @param library
     *            The library directory.
     */
    public PomReader(File library) {
        this.library = library;
    }
    
    void getMetaData(Document document, Properties properties, Map<String, Artifact> dependencies, Set<String> optionals) {
        Artifact parent = getParent(document);
        if (parent != null) {
            getDependencyManagement(parse(parent), parent, dependencies, optionals);
        }
        for (Element element : document.elements("/*[local-name() = 'project']/*[local-name() = 'properties']")) {
            for (Element property : element.elements()) {
                properties.put(property.getLocalName(), property.getText());
            }
        }
    }
    
    public Artifact getParent(Artifact artifact) {
        Document document = parse(artifact);
        return getParent(document);
    }

    private Artifact getParent(Document document) {
        for (Element element : document.elements("/*[local-name() = 'project']/*[local-name() = 'parent' and *[local-name() = 'artifactId'] and *[local-name() = 'groupId'] and *[local-name() = 'version']]")) {
            return new Artifact(
                    element.getText("*[local-name() = 'groupId']"),
                    element.getText("*[local-name() = 'artifactId']"),
                    element.getText("*[local-name() = 'version']")
                    );
        }
        return null;
    }

    Document parse(final Artifact artifact) {
        Document document = documents.get(artifact);
        if (document != null) {
            return document;
        }
        File file = new File(library, artifact.getPath("pom"));
        if (!file.exists()) {
            throw new PomException(POM_FILE_NOT_FOUND, artifact);
        }
        Serializer serializer = new Serializer();
        serializer.setNamespaceAware(false);
        document = serializer.load(file);
        documents.put(artifact, document);
        return document;
    }
    
    void getDependencyManagement(Document document, Artifact artifact, Map<String, Artifact> dependencies, Set<String> optionals) {
        Properties properties = new Properties();
        properties.setProperty("project.groupId", artifact.getGroup());
        properties.setProperty("project.artifactId", artifact.getName());
        properties.setProperty("project.version", artifact.getVersion());
        getMetaData(document, properties, dependencies, optionals);
        VariableProperties variables = new VariableProperties(properties);
        for (Element element : document.elements("/*[local-name() = 'project']/*[local-name() = 'dependencyManagement']/*[local-name() = 'dependencies']/*[local-name() = 'dependency' and *[local-name() = 'groupId'] and *[local-name() = 'artifactId']]")) {
            String artifactId = variables.getValue(element.getText("*[local-name() = 'artifactId']"));
            String version = variables.getValue(element.getText("*[local-name() = 'version']"));
            String groupId = variables.getValue(element.getText("*[local-name() = 'groupId']"));
            String scope = variables.getValue(element.getText("*[local-name() = 'scope']"));
            String optional = variables.getValue(element.getText("*[local-name() = 'optional']"));
            if (version != null && required(scope, optional)) {
                dependencies.put(groupId + "/" + artifactId, new Artifact(groupId, artifactId, version));
            } else if (optional(scope, optional)) {
                optionals.add(groupId + "/" + artifactId);
            }
        }
    }

    static final boolean optional(String scope, String optional) {
        return "test".equals(scope) || "provided".equals(scope) || "true".equals(optional);
    }

    static final boolean required(String scope, String optional) {
        return (scope == null || scope.equals("compile") || scope.equals("runtime")) && (optional == null || !"true".equals(optional));
    }
    
    public List<Artifact> getImmediateDependencies(Artifact artifact) {
        List<Artifact> artifacts = new ArrayList<Artifact>();
        Map<String, Artifact> dependencies = new HashMap<String, Artifact>();
        Set<String> optionals = new HashSet<String>();
        Properties properties = new Properties();
        properties.setProperty("project.groupId", artifact.getGroup());
        properties.setProperty("project.artifactId", artifact.getName());
        properties.setProperty("project.version", artifact.getVersion());
        properties.setProperty("groupId", artifact.getGroup());
        properties.setProperty("artifactId", artifact.getName());
        properties.setProperty("version", artifact.getVersion());
        Document document = parse(artifact);
        getMetaData(document, properties, dependencies, optionals);
        getDependencyManagement(document, artifact, dependencies, optionals);
        VariableProperties variables = new VariableProperties(properties);
        for (Element element : document.elements("/*[local-name() = 'project']/*[local-name() = 'dependencies']/*[local-name() = 'dependency' and *[local-name() = 'groupId'] and *[local-name() = 'artifactId']]")) {
            String artifactId = variables.getValue(element.getText("*[local-name() = 'artifactId']"));
            String version = variables.getValue(element.getText("*[local-name() = 'version']"));
            String groupId = variables.getValue(element.getText("*[local-name() = 'groupId']"));
            String scope = variables.getValue(element.getText("*[local-name() = 'scope']"));
            String optional = variables.getValue(element.getText("*[local-name() = 'optional']"));
            if (required(scope, optional)) {
                String key = groupId + "/" + artifactId;
                if (!optionals.contains(key)) {
                    Artifact provided = dependencies.get(key);
                    if (provided == null) {
                        provided = new Artifact(groupId, artifactId, version);
                    }
                    artifacts.add(provided);
                }
            }
        }
        return artifacts;
    }
}
