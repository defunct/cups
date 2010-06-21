package com.goodworkalan.cups;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.goodworkalan.go.go.library.Artifact;

// TODO Document.
public class Result {
    // TODO Document.
    public Artifact artifact;
    
    // TODO Document.
    public char flag;
    
    // TODO Document.
    public final List<String> suffixes = new ArrayList<String>();
    
    // TODO Document.
    public final List<Artifact> excludes = new ArrayList<Artifact>();
    
    // TODO Document.
    public Result(char flag, Artifact artifact, String...suffix) {
        this.flag = flag;
        this.artifact = artifact;
        this.suffixes.addAll(Arrays.asList(suffix));
    }
    
    // TODO Document.
    public String toString() {
    	StringBuilder string = new StringBuilder();
    	string.append(flag).append(' ').append(artifact);
    	string.toString();
        if (!suffixes.isEmpty()) {
            string.append(" (");
            String separator = "";
            for (String suffix : suffixes) {
            	string.append(separator);
            	string.append(suffix);
                separator = ",";
            }
            string.append(")");
        }
        String separator = " ";
        for (Artifact exclude : excludes) {
        	string.append(separator).append(exclude);
        	separator = ",";
        }
        return string.toString();
    }

    // TODO Document.
    public void print(PrintStream out) {
        out.print(flag);
        out.print(" ");
        out.print(artifact);
        if (!suffixes.isEmpty()) {
            out.print(" ");
            String separator = "";
            for (String suffix : suffixes) {
                out.print(separator);
                out.print(suffix);
                separator = ",";
            }
        }
        out.println();
        for (Artifact exclude : excludes) {
            out.print("- ");
            out.print(exclude);
            out.println();
        }
    }
}
