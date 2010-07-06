package com.goodworkalan.cups;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.goodworkalan.go.go.library.Artifact;

// TODO Document.
public class IO {
    // TODO Document.
    public static Map<String, Artifact> read(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        Map<String, Artifact> map = new LinkedHashMap<String, Artifact>();
        String line = null;
        int count = 0;
        try {
            while ((line = reader.readLine()) != null) {
                count++;
                    String trimmed = line.trim();
                    if (trimmed.length() == 0 || trimmed.startsWith("#")) {
                        continue;
                    }
                    if ("+-@!".contains(trimmed.substring(0, 1))) {
                        String[] pair = trimmed.split("\\s+");
                        if (pair.length != 2) {
                            throw new CupsError(IO.class, "bad.line");
                        }
                        map.put(pair[0], new Artifact(pair[1]));
                    } else {
                        throw new CupsError(IO.class, "bad.line");
                    }
            }
        } catch (IOException e) {
            throw new CupsError(IO.class, "read.io", e);
        }
        return map;
    }
    
    // TODO Document.
    public static void flatten(File library, Artifact artifact, List<Artifact> dependencies) {
        File file = new File(library, artifact.getPath("dep"));
        File directory = file.getParentFile();
        if (!(directory.isDirectory() || directory.mkdirs())) {
            throw new CupsError(IO.class, "mkdir", directory);
        }
        try {
            Writer writer = new FileWriter(file);
            for (Artifact dependency : dependencies) {
                writer.write("+ ");
                writer.write(dependency.toString());
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            throw new CupsError(IO.class, "write.io", file);
        }
    }
}
