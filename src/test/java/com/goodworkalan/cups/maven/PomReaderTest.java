package com.goodworkalan.cups.maven;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;
import static org.testng.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.testng.annotations.Test;

import com.goodworkalan.go.go.library.Artifact;

/**
 * Unit tests for the {@link PomReader} class.
 *
 * @author Alan Gutierrez
 */
public class PomReaderTest {
    /** Test parent. */
    @Test
    public void parent() throws FileNotFoundException {
        PomReader poms = getPomReader();
        Artifact parent = poms.getParent(new Artifact("org.slf4j/slf4j-api/1.4.2"));
        assertNotNull(parent);
        assertEquals(parent.getGroup(), "org.slf4j");
        assertEquals(parent.getName(), "slf4j-parent");
        assertEquals(parent.getVersion(), "1.4.2");
    }
    
    /** Test null parent. */
    @Test
    public void nullParent() throws FileNotFoundException {
        assertNull(getPomReader().getParent(new Artifact("com.broken/broken/0.1")));
    }

    /**
     * Get a new POM reader.
     * 
     * @return A new POM reader.
     */
    private PomReader getPomReader() {
        PomReader poms = new PomReader(new File("src/test/poms").getAbsoluteFile());
        return poms;
    }
    
    /** Test artifact file not found. */
    @Test
    public void artifactNotFound() {
        try {
            getPomReader().getParent(new Artifact("com.broken/broken/8.1"));
        } catch (FileNotFoundException e) {
            return;
        }
        fail("Expected exception not thrown.");
    }
    
    /** Test listing immediate dependencies. */
    @Test
    public void immediateDependenices() throws FileNotFoundException {
        List<Artifact> artifacts = getPomReader().getImmediateDependencies(new Artifact("org.eclipse.jetty/jetty-project/7.0.0.RC3"));
        for (Artifact artifact : artifacts) {
            System.out.println(artifact);
        }
        artifacts = getPomReader().getImmediateDependencies(new Artifact("org.eclipse.jetty/jetty-servlet/7.0.0.RC3"));
        for (Artifact artifact : artifacts) {
            System.out.println(artifact);
        }
    }
    
    /** Test the logic that determines if an artifact is required. */
    @Test
    public void testRequired() {
        assertTrue(PomReader.required(null, null));
        assertTrue(PomReader.required("compile", null));
        assertTrue(PomReader.required("runtime", null));
        assertTrue(PomReader.required(null, "false"));
        assertFalse(PomReader.required("test", null));
        assertFalse(PomReader.required(null, "true"));
    }
    
    /** Test the logic that determines if an artifact is optional. */
    @Test
    public void testOptional() {
        assertTrue(PomReader.optional("test", null));
        assertTrue(PomReader.optional("provided", null));
        assertTrue(PomReader.optional(null, "true"));
        assertFalse(PomReader.optional(null, null));
    }
}
