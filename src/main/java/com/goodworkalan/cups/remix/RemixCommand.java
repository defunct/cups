package com.goodworkalan.cups.remix;

import java.io.File;
import java.io.IOException;

import com.goodworkalan.comfort.io.Files;
import com.goodworkalan.cups.CupsCommand;
import com.goodworkalan.cups.CupsError;
import com.goodworkalan.go.go.Command;
import com.goodworkalan.go.go.Commandable;
import com.goodworkalan.go.go.Environment;
import com.goodworkalan.reflective.Reflective;

@Command(parent = CupsCommand.class)
public class RemixCommand implements Commandable {
     public void execute(Environment env) {
         for (String artifact : env.remaining) {
             StringBuilder className = new StringBuilder();
             String[] split = artifact.split("/");
             className.append("com.goodworkalan.cups.remix.formula.")
                      .append(split[0])
                      .append(".")
                      .append(recipeName(split[1]));
             execute(env, className.toString());
         }
     }
     
     private void execute(Environment env, String recipeClassName) {
         Class<?> recipeClass;
         try {
             recipeClass = Thread.currentThread().getContextClassLoader().loadClass(recipeClassName);
         } catch (ClassNotFoundException e1) {
             throw new CupsError(RemixCommand.class, "recipe.not.found", recipeClassName, Commandable.class);
         }
         Class<? extends Commandable> commandableClass;
         try {
             commandableClass = recipeClass.asSubclass(Commandable.class);
         } catch (ClassCastException e) {
             throw new CupsError(RemixCommand.class, "not.a.recipe", recipeClassName, Commandable.class);
         }
         Commandable commandable;
         try {
             commandable = commandableClass.newInstance();
         } catch (Throwable e) {
             // We call encode simply to assert that the exception is indeed a
             // reflection exception and not a runtime exception or error.
             Reflective.encode(e);
             throw new CupsError(RemixCommand.class, "cannot.create.recipe", e, recipeClassName, Commandable.class);
         }
         execute(env, commandable);
     }
     
     private void execute(Environment env, Commandable commandable) {
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
         sandbox.recipeClass = commandable.getClass();
         env.output(Sandbox.class, sandbox);
         commandable.execute(env);
         Files.unlink(directory);
     }
     
     private static CharSequence recipeName(String artifactName) {
         StringBuilder className = new StringBuilder();
         for (String segment : artifactName.split("-")) {
             className.append(segment.substring(0, 1).toUpperCase())
                      .append(segment.substring(1));
         }
         className.append("Formula");
         return className;
     }
}
