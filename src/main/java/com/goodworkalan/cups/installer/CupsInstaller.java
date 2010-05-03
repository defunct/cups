package com.goodworkalan.cups.installer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;

import com.sun.org.apache.xml.internal.utils.StringToIntTable;

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

    /**
     * Install Java cups.
     * 
     * @param args
     */
    public static void main(String[] args) throws IOException {
        // Default library is the default Maven repository.
        File library = file(System.getProperty("user.home"), ".m2", "repository").getAbsoluteFile();
        
        System.out.println(System.getProperty("os.arch"));
        System.out.println(System.getProperty("os.name"));
        System.out.println(System.getProperty("java.ext.dirs"));
        System.out.println("Hello, World!");
        
        String[][] artifacts = new String[][] {
                new String[] { "retry", "retry", "0.1" },
                new String[] { "verbiage", "verbiage", "0.1" },
                new String[] { "danger", "danger", "0.1" },
                new String[] { "class-boxer", "class-boxer", "0.1" },
                new String[] { "class-association", "class-association", "0.1" },
                new String[] { "infuse", "infuse", "0.1" },
                new String[] { "go-go", "go-go", "0.1.4" },
                new String[] { "go-go", "go-go-bootstrap", "0.1.2" },
                new String[] { "infuse", "infuse", "0.1" },
                new String[] { "madlib", "madlib", "0.1" },
                new String[] { "cups", "cups", "0.1" }
        };
        HttpURLConnection.setFollowRedirects(true);
        for (String[] artifact :artifacts) {
            File full = file(library, "com", "github", "bigeasy", artifact[0], artifact[1], artifact[1], artifact[0] + "-" + artifact[2] + ".jar");
            File directory = full.getParentFile();
            if (!(directory.isDirectory() || directory.mkdirs())) {
                throw new IOException("Cannot create directory: " + directory);
            }
            URL url = null;
            try {
                url = new URL("http://github.com/downloads/bigeasy/" + artifact[0] + "/" + artifact[1] + "-" + artifact[2] + ".jar");
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
            } else {
                System.err.println("Unable to install "+ artifact[1] + "-" + artifact[2] + ".jar.");
                System.exit(1);
            }
            System.out.println("Installed " + artifact[1] + "-" + artifact[2] + ".jar.");
            StringTokenizer tokenizer = new StringTokenizer(System.getProperty("java.ext.dirs"), File.pathSeparator);
            while (tokenizer.hasMoreElements()) {
                File file = file(tokenizer.nextToken());
                
            }
            for (String file : System.getProperty("java.ext.dirs").split(File.pathSeparator)) {
                
            }
        }
    }
}
