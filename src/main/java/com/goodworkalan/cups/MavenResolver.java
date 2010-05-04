package com.goodworkalan.cups;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.goodworkalan.go.go.GoException;
import com.goodworkalan.go.go.library.Artifact;

public class MavenResolver {
    /** The list of Maven repositories to query. */
    private final List<URI> uris;
    
    /** The library path. */
    private final List<File> libraries;
    
    private final Map<Artifact, URI> found = new HashMap<Artifact, URI>();
    
    public MavenResolver(File destination, List<File> path, List<URI> uris) {
        List<File> libraries = new ArrayList<File>();
        libraries.add(destination);
        libraries.addAll(path);
        
        this.uris = uris;
        this.libraries = libraries;
    }
    
    public List<File> getLibraries() {
        return libraries;
    }
    
    public File find(Artifact artifact, String suffix) {
        for (File library : libraries) {
            if (new File(library, artifact.getPath(suffix)).exists()) {            
                return library;
            }
        }
        return null;
    }

    public boolean fetch(URI uri, Artifact artifact, String suffix) {
        try {
            File full = new File(libraries.get(0), artifact.getPath(suffix));
            File directory = full.getParentFile();
            if (!(directory.isDirectory() || directory.mkdirs())) {
                throw new CupsError(MavenResolver.class, "mkdirs");
            }
            URL url = uri.resolve(artifact.getPath(suffix)).toURL();
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

    public boolean download(Artifact artifact, String suffix) {
        URI uri = found.get(artifact);
        if (uri == null) {
            if (suffix.equals("pom")) {
                for (URI test : uris) {
                    if (fetch(test, artifact, suffix)) {
                        found.put(artifact, test);
                        return true;
                    }
                }
                return false;
            }
            if (download(artifact, "pom")) {
                return download(artifact, suffix);
            }
            return false;
        }
        return fetch(uri, artifact, suffix);
    }
}
