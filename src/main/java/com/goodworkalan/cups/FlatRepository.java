package com.goodworkalan.cups;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import com.goodworkalan.go.go.Artifact;
import com.goodworkalan.go.go.GoException;
import com.goodworkalan.go.go.Library;

public class FlatRepository {
    public void fetch(URI uri, Library library, Artifact artifact, String suffix, String extension) {
        File dest = null; // library.getFile(artifact, suffix, extension);
        try {
            if (!dest.exists()) {
                byte[] buffer = new byte[4092];
                FileOutputStream out = new FileOutputStream(dest);
                InputStream in = uri.resolve(artifact.getFileName("jar")).toURL().openStream();
                int read;
                while ((read = in.read(buffer)) != -1) {
                    out.write(buffer, 0, read);
                }
            }
        } catch (IOException e) {
            throw new GoException(0, e);
        }
    }
}
