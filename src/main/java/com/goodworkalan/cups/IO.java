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

import com.goodworkalan.go.go.Artifact;

public class IO {
    public Map<String, Artifact> read(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        Map<String, Artifact> map = new LinkedHashMap<String, Artifact>();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.length() == 0 || line.startsWith("#")) {
                    continue;
                }
                if ("+-@!".contains(line.substring(0, 1))) {
                    String[] pair = line.split("\\s+");
                    if (pair.length != 2) {
                        throw new CupsException(0);
                    }
                    map.put(pair[0], new Artifact(pair[1]));
                }
            }
        } catch (IOException e) {
            throw new CupsException(0, e);
        }
        return map;
    }
    
    public static void flatten(File library, Artifact artifact, List<Artifact> dependencies) {
        File file = new File(library, artifact.getPath("dep"));
        File directory = file.getParentFile();
        if (!(directory.isDirectory() || directory.mkdirs())) {
            throw new CupsException(0);
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
            throw new CupsException(0, e);
        }
    }
}
