package com.goodworkalan.cups;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.goodworkalan.go.go.Argument;
import com.goodworkalan.go.go.ArgumentList;
import com.goodworkalan.go.go.Command;
import com.goodworkalan.go.go.Commandable;
import com.goodworkalan.go.go.Environment;
import com.goodworkalan.go.go.GoException;
import com.goodworkalan.go.go.library.Artifact;
import com.goodworkalan.go.go.library.Exclude;
import com.goodworkalan.go.go.library.Include;
import com.goodworkalan.ilk.Ilk;

/**
 * Installs an artifact.
 *
 * @author Alan Gutierrez
 */
@Command(parent = CupsCommand.class)
public class InstallCommand implements Commandable {
	/** Recursively install dependencies. */
	@Argument
	public boolean recursive;

	/** Force install. */
	@Argument
	public boolean force;
	
	/** Install to the specific library. */
	@Argument
	public String library;
	
	/**
	 * Install an artifact.
	 * 
	 * @param env
	 *            The environment.
	 */
	public void execute(Environment env) {
		ArtifactAssociation associations;
		try {
			associations = new ArtifactAssociation("META-INF/services/com.goodworkalan.cups.commands.properties");
		} catch (IOException e) {
			throw new CupsError(InstallCommand.class, "readCommands", e);
		}
		for (String remaining : env.remaining) {
			Artifact artifact = new Artifact(remaining);
			install(env, associations, new Include(artifact), new HashSet<Exclude>(), new HashSet<Exclude>());
		}
	}

    // TODO Document.
	private void exclude(Environment env, ArtifactAssociation associations, Include include, Set<Exclude> excludes, Set<Exclude> installed) {
		if (!excludes.contains(include.getArtifact().getUnversionedKey())) {
			Set<Exclude> subExcludes = new HashSet<Exclude>();
			subExcludes.addAll(excludes);
			subExcludes.addAll(include.getExcludes());
			install(env, associations, include, subExcludes, installed);
		}
	}

	/**
	 * Install the given artifact using the command in the given command map.
	 * 
	 * @param env
	 *            The environment.
	 * @param commands
	 *            The command map.
	 * @param include
	 *            The artifact to include.
	 */
	private void install(Environment env, ArtifactAssociation associations, Include include, Set<Exclude> excludes, Set<Exclude> installed) {
		Artifact artifact = include.getArtifact();
		String command = associations.get(artifact);
		if (command == null && artifact.getGroup().startsWith("com.github.")) {
			command = "github";
		}
		if (command == null) {
			env.io.out.println(new Result('!', artifact).toString());
			env.io.out.flush();
		} else if (!installed.contains(include.getArtifact().getUnversionedKey())) {
			install(env, associations, include, excludes, installed, asList(command.split("\\s+")));
		}
	}
	
    // TODO Document.
	private void install(Environment env, ArtifactAssociation associations, Include include, Set<Exclude> excludes, Set<Exclude> installed, List<String> command) {
		if (buildDependencies(env, associations, include, excludes, installed, command)) {
			ArgumentList arguments = new ArgumentList(command);
			if (force) {
				arguments.addArgument("force", "true");
			}
			if (library != null) {
				arguments.addArgument("library", library);
			}
			try {
				List<Include> includes = env.executor.run(new Ilk<List<Include>>() {}, env.io, env.commands.get(0), env.arguments.get(0), arguments, include.getArtifact());
				installed.add(new Exclude(include.getArtifact().getUnversionedKey()));
				if (recursive) {
					for (Include subInclude : includes) {
						exclude(env, associations, subInclude, excludes, installed);
					}
				}
			} catch (GoException e) {
				if (e.getCode() != GoException.COMMAND_CLASS_MISSING) {
					throw e;
				}
				List<String> full = new ArrayList<String>();
				full.add("cups");
				full.addAll(command);
				env.error("commandMissing", full);
			}
		}
	}
	
    // TODO Document.
	public boolean buildDependencies(Environment env, ArtifactAssociation associations, Include include, Set<Exclude> excludes, Set<Exclude> installed, List<String> command) {
		ArgumentList arguments = new ArgumentList(command);
		arguments.addArgument("build-dependencies", "true");
		try {
			List<Include> includes = env.executor.run(new Ilk<List<Include>>() {}, env.io, env.commands.get(0), env.arguments.get(0), arguments, include.getArtifact());
			for (Include buildDependency : includes) {
				install(env, associations, buildDependency, excludes, installed);
			}
		} catch (GoException e) {
			if (e.getCode() != GoException.COMMAND_CLASS_MISSING) {
				throw e;
			}
			List<String> full = new ArrayList<String>();
			full.add("cups");
			full.addAll(command);
			env.error("commandMissing", full);
			return false;
		}
		return true;
	}
}
