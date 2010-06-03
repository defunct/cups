package com.goodworkalan.cups.installer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
        String login = artifact[0].split("\\.")[2];
        String group = artifact[0].split("\\.")[3];
        URL url = null;
        try {
            url = new URL("http://github.com/downloads/" + login + "/" + group + "/" + artifact[1] + "-" + artifact[2] + "." + suffix);
        } catch (MalformedURLException e) {
            // Never happens.
        }
        System.out.println(url);
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
        
        // Here's a list of the bootstrap dependencies for Jav-a-Go-Go.
        BufferedReader reader = new BufferedReader(new InputStreamReader(CupsInstaller.class.getResourceAsStream("dependencies.txt")));

        List<String> lines = new ArrayList<String>();
        // Locate them and create a list of URIs.
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        
        String[][] artifacts = new String[lines.size()][];
        for (int i = 0, stop = artifacts.length; i < stop; i++) {
            artifacts[i] = lines.get(i).split("/");
        }

        if (download) {
            HttpURLConnection.setFollowRedirects(true);
            for (String[] artifact :artifacts) {
                String group = artifact[0].replace(".", File.separator);
                File directory = file(library, group, artifact[1], artifact[2]);
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
            String[] gogoBoot = null;
            for (int i = 0, stop = artifacts.length; i < stop && gogoBoot == null; i++) {
                if (artifacts[i][1].equals("go-go-boot")) {
                    gogoBoot = artifacts[i];
                }
            }
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
                    String group = gogoBoot[0].replace(".", File.separator);
                    InputStream in = new FileInputStream(file(library, group, "go-go-boot", gogoBoot[2], "go-go-boot-" + gogoBoot[2] + ".jar"));
    
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
