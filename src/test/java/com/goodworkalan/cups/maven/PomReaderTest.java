package com.goodworkalan.cups.maven;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import java.io.File;
import java.util.List;

import org.testng.annotations.Test;

import com.goodworkalan.cups.maven.PomException;
import com.goodworkalan.cups.maven.PomReader;
import com.goodworkalan.go.go.library.Artifact;

public class PomReaderTest {
    @Test
    public void parent() {
        PomReader poms = getPomReader();
        Artifact parent = poms.getParent(new Artifact("org.slf4j/slf4j-api/1.4.2"));
        assertNotNull(parent);
        assertEquals(parent.getGroup(), "org.slf4j");
        assertEquals(parent.getName(), "slf4j-parent");
        assertEquals(parent.getVersion(), "1.4.2");
    }
    
    @Test
    public void nullParent() {
        assertNull(getPomReader().getParent(new Artifact("com.broken/broken/0.1")));
    }

    private PomReader getPomReader() {
        PomReader poms = new PomReader(new File("src/test/poms").getAbsoluteFile());
        return poms;
    }
    
    @Test
    public void artifactNotFound() {
        new PomExceptionCatcher(PomException.POM_FILE_NOT_FOUND, new Runnable() {
            public void run() {
                getPomReader().getParent(new Artifact("com.broken/broken/8.1"));
                
            }
        }).run();
    }
    
    @Test
    public void immediateDependenices() {
        List<Artifact> artifacts = getPomReader().getImmediateDependencies(new Artifact("org.eclipse.jetty/jetty-project/7.0.0.RC3"));
        for (Artifact artifact : artifacts) {
            System.out.println(artifact);
        }
        artifacts = getPomReader().getImmediateDependencies(new Artifact("org.eclipse.jetty/jetty-servlet/7.0.0.RC3"));
        for (Artifact artifact : artifacts) {
            System.out.println(artifact);
        }
    }
    
    @Test
    public void testRequired() {
        assertTrue(PomReader.required(null, null));
        assertTrue(PomReader.required("compile", null));
        assertTrue(PomReader.required("runtime", null));
        assertTrue(PomReader.required(null, "false"));
        assertFalse(PomReader.required("test", null));
        assertFalse(PomReader.required(null, "true"));
    }
    
    @Test
    public void testOptional() {
        assertTrue(PomReader.optional("test", null));
        assertTrue(PomReader.optional("provided", null));
        assertTrue(PomReader.optional(null, "true"));
        assertFalse(PomReader.optional(null, null));
    }
}
