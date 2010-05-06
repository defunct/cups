package com.goodworkalan.cups.installer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

public class CupsInstaller {
    /**
     * Create a file by joinging the string values of the given objects with the
     * system file separator.
     * 
     * @param objects
     * @return
     */
    private static File file(Object...objects) {
        StringBuilder file = new StringBuilder();
        String separator = "";
        for (Object object : objects) {
            file.append(separator);
            file.append(object.toString());
            separator = File.separator;
        }
        return new File(file.toString());
    }

    private static boolean fetch(File full, String[] artifact, String suffix) throws IOException {
        URL url = null;
        try {
            url = new URL("http://github.com/downloads/bigeasy/" + artifact[0] + "/" + artifact[1] + "-" + artifact[2] + "." + suffix);
        } catch (MalformedURLException e) {
            // Never happens.
        }
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStream in = connection.getInputStream();
            byte[] buffer = new byte[4092];
            FileOutputStream out = new FileOutputStream(full);
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.close();
            return true;
        }
        return false;
    }
    /**
     * Install Java cups.
     * 
     * @param args
     */
    public static void main(String[] args) throws IOException {
        // Default library is the default Maven repository.
        File library = file(System.getProperty("user.home"), ".m2", "repository").getAbsoluteFile();

        boolean boot = false;
        boolean download = false;
        for (String arg :args) {
            if (arg.equals("--boot")) {
                boot = true;
            } else if (arg.equals("--download")) {
                download = true;
            } else if (arg.startsWith("--library=")) {
                library = file(arg.substring(2).split("=", 2)[1]);
                if (!library.isDirectory()) {
                    System.err.println("Bad library directory: " + library);
                }
            }
        }
        
        if (!(boot || download)) {
            boot = download = true;
        }
        
        String[][] artifacts = new String[][] {
                new String[] { "go-go", "go-go-boot", "0.1.2.11" },
                // Jav-a-Go-Go Boot is up top for a reason! 
                new String[] { "cups", "cups", "0.1.1.11" },
                new String[] { "go-go", "go-go", "0.1.4.11" },
                new String[] { "retry", "retry", "0.1" },
                new String[] { "ilk", "ilk", "0.1.0.1" },
                new String[] { "verbiage", "verbiage", "0.1.0.4" },
                new String[] { "danger", "danger", "0.1.0.1" },
                new String[] { "github4j", "github4j-downloads", "0.1" },
                new String[] { "class-boxer", "class-boxer", "0.1" },
                new String[] { "class-association", "class-association", "0.1" },
                new String[] { "infuse", "infuse", "0.1" },
                new String[] { "comfort-io", "comfort-io", "0.1.1" },
                new String[] { "infuse", "infuse", "0.1" },
                new String[] { "reflective", "reflective", "0.1" },
                new String[] { "madlib", "madlib", "0.1" }
        };
        if (download) {
            HttpURLConnection.setFollowRedirects(true);
            for (String[] artifact :artifacts) {
                File directory = file(library, "com", "github", "bigeasy", artifact[0], artifact[1], artifact[2]);
                if (!(directory.isDirectory() || directory.mkdirs())) {
                    throw new IOException("Cannot create directory: " + directory);
                }
                for (String suffix : new String[] { "dep", "jar" }) {
                    File full = file(directory, artifact[1] + "-" + artifact[2] + "." + suffix);
                    if (!fetch(full, artifact, suffix)) {
                        System.err.println("Unable to install "+ artifact[1] + "-" + artifact[2] + "." + suffix + ".");
                        System.exit(1);
                    }
                    System.out.println("Installed " + artifact[1] + "-" + artifact[2] + "." + suffix + ".");
                }
            }
        }
        if (boot) {
            String[] gogoBoot = artifacts[0];
            StringTokenizer tokenizer = new StringTokenizer(System.getProperty("java.ext.dirs"), File.pathSeparator);
            boolean written = false;
            while (tokenizer.hasMoreElements()) {
                File dir = file(tokenizer.nextToken());
                if (dir.canWrite()) {
                    for (File file : dir.listFiles()) {
                        if (file.getName().startsWith("go-go-boot")) {
                            System.out.println("Deleting old " + file.getAbsolutePath() + ".");
                            file.delete();
                        }
                    }
                }
                if (!written && dir.canWrite()) {
                    System.out.println("Installing " + file(dir, "go-go-boot-" + gogoBoot[2] + ".jar").getAbsolutePath() + ".");
                    written = true;
                    InputStream in = new FileInputStream(file(library, "com", "github", "bigeasy", "go-go", "go-go-boot", gogoBoot[2], "go-go-boot-" + gogoBoot[2] + ".jar"));
    
                    OutputStream out = new FileOutputStream(file(dir, "go-go-boot-" + gogoBoot[2] + ".jar"));
                    byte[] buffer = new byte[4092];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    in.close();
                    out.close();
                }
            }
        }
    }
}
