package com.goodworkalan.cups.remix;

import static com.goodworkalan.comfort.io.Files.file;

import java.io.File;
import java.io.IOException;

import com.goodworkalan.comfort.io.Files;
import com.goodworkalan.cups.ArtifactAssociation;
import com.goodworkalan.cups.CupsCommand;
import com.goodworkalan.cups.CupsError;
import com.goodworkalan.go.go.Command;
import com.goodworkalan.go.go.Commandable;
import com.goodworkalan.go.go.Environment;
import com.goodworkalan.go.go.library.Artifact;
import com.goodworkalan.reflective.Reflective;

@Command(parent = CupsCommand.class)
public class RemixCommand implements Commandable {
     public void execute(Environment env) {
    	 ArtifactAssociation association;
    	 try {
    		 association = new ArtifactAssociation("META-INF/services/com.goodworkalan.cups.remix.formulas.properties");
    	 } catch (IOException e) {
    		 throw new RuntimeException(e);
    	 }
    	 for (String argument : env.remaining) {
    		 Artifact artifact = new Artifact(argument);
    		 String formulaClassName = association.get(artifact); 
    		 Class<?> formulaClass;
    		 try {
    			 formulaClass = Thread.currentThread().getContextClassLoader().loadClass(formulaClassName);
    		 } catch (ClassNotFoundException e1) {
    			 throw new CupsError(RemixCommand.class, "recipe.not.found", formulaClassName, Commandable.class);
    		 }
    		 Class<? extends Commandable> commandableClass;
    		 try {
    			 commandableClass = formulaClass.asSubclass(Commandable.class);
    		 } catch (ClassCastException e) {
    			 throw new CupsError(RemixCommand.class, "not.a.recipe", formulaClassName, Commandable.class);
    		 }
    		 Commandable commandable;
    		 try {
    			 commandable = commandableClass.newInstance();
    		 } catch (Throwable e) {
    			 // We call encode simply to assert that the exception is indeed a
    			 // reflection exception and not a runtime exception or error.
    			 Reflective.encode(e);
    			 throw new CupsError(RemixCommand.class, "cannot.create.recipe", e, formulaClassName, Commandable.class);
    		 }
    		 Sandbox sandbox = new Sandbox();
    		 File directory;
    		 do {
    			 try {
    				 directory = File.createTempFile("remix", ".sandbox");
    			 } catch (IOException e) {
    				 throw new CupsError(RemixCommand.class, "create.temp.directory");
    			 }
    			 directory.delete();
    		 } while (!directory.mkdirs());
    		 sandbox.directory = directory;
    		 sandbox.formulaClass = commandable.getClass();
    		 env.output(Sandbox.class, sandbox);
    		 commandable.execute(env);
    		 env.executor.run(env.io, "mix", "--working-directory=" + file(directory, "remixed").getAbsolutePath(), "--project-module=" + formulaClassName, "install");
    		 Files.unlink(directory);
         }
     }
}
