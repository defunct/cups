package com.goodworkalan.cups;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.goodworkalan.go.go.library.Artifact;

public class Result {
    public Artifact artifact;
    
    public char flag;
    
    public final List<String> suffixes = new ArrayList<String>();
    
    public final List<Artifact> excludes = new ArrayList<Artifact>();
    
    public Result(char flag, Artifact artifact, String...suffix) {
        this.flag = flag;
        this.artifact = artifact;
        this.suffixes.addAll(Arrays.asList(suffix));
    }
    
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
