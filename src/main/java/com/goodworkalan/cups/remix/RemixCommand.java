package com.goodworkalan.cups.remix;

import static com.goodworkalan.comfort.io.Files.file;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.goodworkalan.comfort.io.Files;
import com.goodworkalan.cups.ArtifactAssociation;
import com.goodworkalan.cups.CupsCommand;
import com.goodworkalan.cups.CupsError;
import com.goodworkalan.go.go.Argument;
import com.goodworkalan.go.go.Command;
import com.goodworkalan.go.go.Commandable;
import com.goodworkalan.go.go.Environment;
import com.goodworkalan.go.go.library.Artifact;
import com.goodworkalan.go.go.library.Include;
import com.goodworkalan.ilk.Ilk;
import com.goodworkalan.reflective.Reflective;

// TODO Document.
@Command(parent = CupsCommand.class)
public class RemixCommand implements Commandable {
    // TODO Document.
	@Argument
	public boolean buildDependencies;
	
    // TODO Document.
	@Argument
	public boolean clean;
	
    // TODO Document.
	public File getSandbox(Artifact artifact) {
		File directory = new File(Files.file(System.getProperty("user.home"), ".cups", "remix"));
		return file(directory, artifact.getUnversionedDirectoryPath(), artifact.getVersion());
	}
	
    // TODO Document.
	public void execute(Environment env) {
		for (String argument : env.remaining) {
			Artifact artifact = new Artifact(argument);
			ArtifactAssociation association;
			try {
				association = new ArtifactAssociation("META-INF/services/com.goodworkalan.cups.remix.formulas.properties");
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			String formulaClassName = association.get(artifact);
			checkout(formulaClassName, env);
			if (buildDependencies) {
				Ilk<List<Include>> ilk = new Ilk<List<Include>>() {};
				List<Include> includes = env.executor.run(ilk, env.io, "mix", "--working-directory=" + file(getSandbox(artifact), "remixed").getAbsolutePath(), "--project-module=" + formulaClassName, "dependencies", "--immediate", "--development");
				env.output(ilk, includes);
			} else {
				File sandbox = getSandbox(artifact);
				env.executor.run(env.io, "mix", "--working-directory=" + file(sandbox, "remixed").getAbsolutePath(), "--project-module=" + formulaClassName, "install");
				if (sandbox.exists()) {
					Files.unlink(sandbox);
				}
			}
		}
	}
	
    // TODO Document.
	public void checkout(String formulaClassName, Environment env) {
		File workspace = new File(Files.file(System.getProperty("user.home"), ".cups", "remix"));
		for (String argument : env.remaining) {
			Artifact artifact = new Artifact(argument);
			File directory = file(workspace, artifact.getUnversionedDirectoryPath(), artifact.getVersion());
			if (!directory.isDirectory()) {
				if (!directory.mkdirs()) {
					throw new CupsError(RemixCommand.class, "createDirectory", directory);
				}
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
					// We call encode simply to assert that the exception is indeed
					// a reflection exception and not a runtime exception or error.
					Reflective.encode(e);
					throw new CupsError(RemixCommand.class, "cannot.create.recipe", e, formulaClassName, Commandable.class);
				}
				Sandbox sandbox = new Sandbox();
				sandbox.directory = directory;
				sandbox.formulaClass = commandable.getClass();
				env.output(Sandbox.class, sandbox);
				commandable.execute(env);
			}
		}
	}
}
