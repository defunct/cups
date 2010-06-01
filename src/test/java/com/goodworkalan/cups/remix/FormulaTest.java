package com.goodworkalan.cups.remix;

import static com.goodworkalan.comfort.io.Files.file;

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.testng.annotations.Test;

import com.goodworkalan.go.go.Go;

public class FormulaTest {
    @Test
    public void execute() {
        Go.execute(getLibrary(), "cups", "remix", "javax.inject/inject");
        Go.execute(getLibrary(), "cups", "remix", "com.habitsoft/html-dtd-cache");
    }

    private List<File> getLibrary() {
        File home = new File(System.getProperty("user.home"));
        File directory = file(home, ".m2", "repository");
        List<File> library = Collections.singletonList(directory);
        return library;
    }
}
