package com.goodworkalan.cups;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.goodworkalan.go.go.Argument;
import com.goodworkalan.go.go.Command;
import com.goodworkalan.go.go.Commandable;
import com.goodworkalan.go.go.Environment;
import com.goodworkalan.go.go.library.Artifact;
import com.goodworkalan.go.go.library.Artifacts;
import com.goodworkalan.go.go.library.Include;


@Command(parent = CupsCommand.class, name = "github")
public class GitHubCommand implements Commandable {
    private final List<Artifact> artifacts = new ArrayList<Artifact>();
    
    @Argument
    public void addArtifact(Artifact artifact) {
        artifacts.add(artifact);
    }
    
    @Argument
    public boolean recurse;
    
    @Argument
    public boolean force;
    
    @Argument
    public boolean input;
    
    private final static Pattern GITHUB_GROUP = Pattern.compile("com\\.github\\.\\w[-\\w\\d]*\\.\\w[-\\w\\d]*");

    private boolean isGitHubProject(Artifact artifact) {
        return GITHUB_GROUP.matcher(artifact.getGroup()).matches();
    }
    
    private String[] getRepository(Artifact artifact) {
        String[] split = artifact.getGroup().split("\\.");
        return new String[] { split[2], split[3] };
    }

    private boolean fetch(File library, Artifact artifact, String suffix) {
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
        File library = env.library.getDirectories()[0];
        List<Include> current = new ArrayList<Include>();
        for (Artifact artifact : artifacts) {
            current.add(new Include(artifact));
        }
        Set<Artifact> seen = new HashSet<Artifact>();
        List<Include> next = new ArrayList<Include>();
        while (!current.isEmpty()) {
            for (Include include : current) {
                Artifact artifact = include.getArtifact();
                if (seen.contains(artifact)) {
                    continue;
                }
                seen.add(artifact);
                if (!isGitHubProject(artifact)) {
                    new Result('!', artifact).print(env.io.out);
                    continue;
                }
                Result result = new Result('~', artifact);
                File dir = env.library.getArtifactDirectory(artifact, "dep");
                if (force || dir == null) {
                    if (dir == null) {
                        dir = library;
                    }
                    if (fetch(dir, artifact, "dep")) {
                        result.suffixes.add("dep");
                        if (fetch(dir, artifact, "jar")) {
                            result.suffixes.add("jar");
                            for (String suffix : in("sources/jar", "javadoc/jar")) {
                                if (fetch(dir, artifact, suffix)) {
                                    result.suffixes.add(suffix);
                                }
                            }
                        }
                    }
                    result.flag = result.suffixes.size() < 2 ? '!' : '@';
                    if (result.flag == '@' && recurse) {
                        next.addAll(Artifacts.read(new File(dir, artifact.getPath( "dep"))));
                    }
                } else if (recurse) { 
                    next.addAll(Artifacts.read(new File(dir, artifact.getPath( "dep"))));
                }
                result.print(env.io.out);
                env.io.out.flush();
            }
            current = next;
            next = new ArrayList<Include>();
        }
    }
}
