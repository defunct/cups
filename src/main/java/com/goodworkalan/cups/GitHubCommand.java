package com.goodworkalan.cups;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.goodworkalan.github4j.downloads.Download;
import com.goodworkalan.github4j.downloads.GitHubDownloadException;
import com.goodworkalan.github4j.downloads.GitHubDownloads;
import com.goodworkalan.go.go.Argument;
import com.goodworkalan.go.go.Command;
import com.goodworkalan.go.go.Commandable;
import com.goodworkalan.go.go.Environment;
import com.goodworkalan.go.go.library.Artifact;
import com.goodworkalan.go.go.library.ArtifactPart;
import com.goodworkalan.go.go.library.Artifacts;
import com.goodworkalan.go.go.library.Include;
import com.goodworkalan.go.go.version.VersionSelector;
import com.goodworkalan.ilk.Ilk;


@Command(parent = CupsCommand.class, name = "github")
public class GitHubCommand implements Commandable {
	private final static Pattern GITHUB_GROUP = Pattern.compile("com\\.github\\.\\w[-\\w\\d]*\\.\\w[-\\w\\d]*");

	private final static Pattern EXTRACT_VERSION = Pattern.compile("^(?:\\w[-_\\w\\d]*\\-)+((?:\\.?\\d+)+)(.*?)$");

	/** If true, install even if there is already an artifact installed. */
	@Argument
    public boolean force;
    
    /** The library in which to install. */
    @Argument
    public File library;
    
    /** Whether or not to return a list of build dependencies. */
    @Argument
    public boolean buildDependencies;
    
    private boolean isGitHubProject(Artifact artifact) {
        return GITHUB_GROUP.matcher(artifact.getGroup()).matches();
    }
    
    private String[] getRepository(Artifact artifact) {
        String[] split = artifact.getGroup().split("\\.");
        return new String[] { split[2], split[3] };
    }
    
    public Artifact getArtifact(Environment env, File library, Include include, String...suffixes) {
        Artifact prototype = include.getArtifact();
        String prefix = prototype.getName() + "-"; 
        String[] repository = getRepository(prototype);
        
        Map<String, Set<String>> byVersion = new HashMap<String, Set<String>>();
        try {
            for (Download download : GitHubDownloads.getDownloads(repository[0], repository[1])) {
                String fileName = download.getFileName();
                if (fileName.startsWith(prefix)) {
                    Matcher matcher = EXTRACT_VERSION.matcher(fileName);
                    if (matcher.matches()) {
                        String number = matcher.group(1);
                        Set<String> candidates = byVersion.get(number);
                        if (candidates == null) {
                            candidates = new HashSet<String>();
                            byVersion.put(number, candidates);
                        }
                        candidates.add(matcher.group(2));
                    }
                }
            }
        } catch (GitHubDownloadException e) {
            throw new CupsError(GitHubCommand.class, "delete", e);
        }
        env.debug("candidates", byVersion.keySet());
        VersionSelector versionSelector = new VersionSelector(prototype.getVersion());
        String selected;
        while ((selected = versionSelector.select(byVersion.keySet())) != null) {
            Set<String> available = byVersion.remove(selected);
            for (String suffix : suffixes) {
                if (!available.contains(Artifact.suffix(suffix))) {
                    continue;
                }
            }
            return new Artifact(prototype.getGroup(), prototype.getName(), selected);
        }
        return null;
    }
    
    private boolean download(Environment env, Artifact artifact, File library, String suffix, boolean required) {
        try {
            File full = new File(library, artifact.getPath(suffix));
            File directory = full.getParentFile();
            if (!(directory.isDirectory() || directory.mkdirs())) {
                throw new CupsError(GitHubCommand.class, "mkdirs");
            }
            String[] repository = getRepository(artifact);
            URL url = new URL("http://github.com/downloads/" + repository[0] + "/" + repository[1] + "/" + artifact.getFileName(suffix));
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
            } else if (required || connection.getResponseCode() != HttpURLConnection.HTTP_NOT_FOUND) {
                throw new CupsError(GitHubCommand.class, "http", connection.getResponseCode());
            }
            return false;
        } catch (IOException e) {
            throw new CupsError(GitHubCommand.class, "fetch");
        }
    }
    
    private static String[] in(String...strings) {
        return strings;
    }
    
    public void execute(Environment env) {
    	if (buildDependencies) {
            env.output(new Ilk<List<Include>>() {}, Collections.<Include>emptyList());
    	} else {
    		install(env);
    	}
    }
    
    private void install(Environment env) {
    	library = env.library.getDirectories()[0];
    	List<Include> includes = new ArrayList<Include>();
        for (String argument : env.remaining) {
            Include include = new Include(new Artifact(argument));
            Artifact artifact = include.getArtifact();
            if (!isGitHubProject(artifact)) {
                new Result('!', artifact).print(env.io.out);
                continue;
            }
            Result result = new Result('~', artifact);
            ArtifactPart artifactPart = env.library.getArtifactPart(new Include(artifact), "dep", "jar");
            if (force || artifactPart == null) {
            	File dir = artifactPart == null ? library : artifactPart.getLibraryDirectory();
            	Artifact found = getArtifact(env, library, include, "dep", "jar");
            	if (found != null) {
            		for (String suffix : in("dep", "jar")) {
            			result.suffixes.add(suffix);
            			download(env, found, library, suffix, true);
            		}
            		for (String suffix : in("sources/jar", "javadoc/jar")) {
            			if (download(env, found, library, suffix, false)) {
            				result.suffixes.add(suffix);
            			}
            		}
            		includes.addAll(Artifacts.read(new File(dir, found.getPath( "dep"))));
            		result.artifact = found;
            		result.flag = '@';
                } else {
                    result.flag = '!';
                }
            } else {
            	includes.addAll(Artifacts.read(new File(artifactPart.getLibraryDirectory(), artifactPart.getArtifact().getPath( "dep"))));
            }
            result.print(env.io.out);
            env.io.out.flush();
        }
        env.output(new Ilk<List<Include>>() {}, includes);
    }
}
