---
layout: default
title: "Cups Remix: Building Java From Source"
---

Here are the notes on my build form source proof of concept where I've built two
projects that are not available on any of the library repositories.

One of the sad facts of Maven was that you'd have to wait for the projects you
depend upon to reach a Maven respository. This would happen quickly for some
projects, slowly for others, and never for some of the key projects I came to
depend upon. 

There were those projects that had classes, but no Javadoc and no source code,
so that when I generated my Eclispe project, I wasn't able to trace into the
code to see what was going on.\[1] Defeat the purpose of open source, which has
the freedom to explore the workings of software at its core.

One of the things that drives the other communities in which I program, is that
the source is naturally open, because those communities have interpreted
langauges. With Perl and Ruby you install the source. It is always there and you
can always read it, the stack traces give you the file and line number to
inspect.

The initial goal with Cups Remix is to build from source those projects that
cannot be obtained from a Maven repository. It seemed like such a task would be
rather fiddly, so that the preferred way to obtain artifacts would be through a
Maven repository or better still, from artifacts made available through GitHub
downloads. Yet, I got so far so fast, I can see my personal prefernces might
change. 

To put a point on the goals, build from source producing the classes, source and
javadoc jar files, and generate a dependencies file for use with Jav-a-Go-Go,
Cups and Mix. The project is built in a temporary directory and disposed of
after the build is complete.

Cups Remix will establish some well-documented cross-platform external
dependencies.

For starters I'll say that in order to use Cups Remix, you will have to have
Subversion and git intalled and in your executable path. Chances are, these two
dependencies will cover almost all tasks. Ideally, that will be the depenency
trifecta, Subversion, git and Java, but the canon is not yet closed.

From the command line, we'll be able to install a project from a collection of
recipes using the following command.

`java go.go cups remix com.habitsoft/html-dtd-cache`

That will install a version of the artifact that is the lastest stable version
according to Cups Remix.

For each project to install from source there is a recipe. This is inspired by
Homebrew on OS X, which installed from GitHub with a bunch of recipes for UNIX
programs that are simply little Ruby programs that build a component called a
Formula, so I even went so far as to use the name Formula, because I like to be
inspired.

You create a Formula by implementing a Jav-a-Go-Go Commandable with the name of
the artifact in camel case.

<pre><code>package com.goodworkalan.cups.remix.formula.com.habitsoft;

import com.goodworkalan.go.go.Commandable;
import com.goodworkalan.go.go.Environment;

public class HtmlDtdCacheRecipe extends Commandable {
    public void execute(Environment env) {
    }
}</code></pre>

The artifact name `com.habitsoft/html-dtd-cache` maps to the above commandable.

The longish package names may antagonize some developers, but so many of use
IDEs that display the directories as a package, we don't really have to drill
down seven directories to find the file.

The first formula I completed was for the javax.inject package.

<code><pre>public class InjectFormula implements Commandable, ProjectModule {
    /**
     * Create the project directory and execute mix.
     */
    public void execute(Environment env) {
        Sandbox sandbox = env.get(Sandbox.class, 1);
        Spawn spawn = new Spawn();
        spawn.setWorkingDirectory(sandbox.directory);
        spawn.$$("svn", "checkout", "http://atinject.googlecode.com/svn/trunk/", "atinject");
        file(sandbox.directory, "remixed", "src", "main").mkdirs();
        file(sandbox.directory, "atinject", "src").renameTo(file(sandbox.directory, "remixed", "src", "main", "java"));
        sandbox.mix(env);
    }

    /**
     * Build the project definition for Java Inject.
     *
     * @param builder
     *          The project builder.
     */
    public void build(Builder builder) {
        builder
            .cookbook(JavaProject.class)
                .produces("javax.inject/inject/1.0")
                .end()
            .end();
    }
}</pre></code>

The `Sandbox` is provided by Cups Remix and contains a working directory where
the project can be built. The formula uses Spawn to run Subversion and fetch the
source code. It rearranges the source code into a Mix flavored project. Then it
calls Mix install using the forumla class itself as the Mix `ProjectModule`. The
`build` method uses the `JavaProject` cookbook to build a project definition
that has a great many default build targets, including one that will build a
distribution. That way, when we call mix install, we simply build a distribution
and install it to our library.

In order for Cups Remix to work, you have to have your path in order. Cups Remix
will use the `svn` or `git` found in your path.


 some of the
tools common to builds. There is no desire for a 100% Java approach to this
problem, since building Java is already platform specific. We've gone form build
once, run everywhere, to build everywhere. I don't want to provide a utility
that provides a univieras 

[1] To the strawman that says that you shouldn't depend upon seeing the source,
I say that you are confusing ecapsulation with obfuscation. If you live in a
pure world where software is flawless and software boundaries are taken
personally, then you're probably not looking to encourage the contributions that
come when people can easily review your work.
[2] Programmers are lonely creatures that get strange thoughts in their heads
and will give you so many caveats and whatnot, but intead of writing them all
out above and ruining my prose, I'm going to put them out down here.
[2] To the strawman that says; But, but, but... There is a 100% Pure Java
version of [Subversion](http://svnkit.com/) and
[git](http://www.eclipse.org/jgit/) git. Why don't you use them and avoid the
ugly prospect of software miscegenation? To you I say, I'm afraid of what
becomes of the project if I'm catering to a community that cannot install,
configure and operate command line programs as common, well-documented and
well-designed as Subversion or git. You're asking for purity, but can you give
me any reason why an incomplete Java implementation of `git` is better at
checkout out a `git` repository than `git`? I'd much rather "force" users to
install git or Subversion, than burden formula writers to work with 
The fetish for 100% Pure Java is one to reduce dependencies on other systems,
but here we're not talking about write once, run everywhere. We're talking about
build everywhere, so we're going to demand a reasonable enviornment for commad
line builds, if we're going to perform command line builds. It is not a goal of
the project to have a click 
Cups Remix makes no attempt to provide a 100% Pure Java solution, because the
stated goal of Jav-a-Go-Go is to modify Java's behavior on the command line so
that it can interact with its environmental betters. Part of this is to encouage
Java programmers to learn how to call other programs, instead of rewriting that
program in Java, because that is somehow more correct.

One of the stated goals of Jav-a-Go-Go is to help modify Java's behavior on the
command line so that it can interact with its environmental betters. Cups is
supposed to be an easy to use dependency management tool, that is used from the
command line, so that you can easily manage dependencies on your Windows laptop,
or on your Linux servers, using a familiar UI.

Plus, there are other source control programs
out there, that do not have their sickly, plasticine 100% Pure Java facimile and
for those the programs, we will need to fork anyway. Plus, we're already on the
command line if we're using Cups Remix, so why not do it the UNIX way, and call
the small, purposeful tool that is meant to address the problem?
As noted above, the approach for Cups Remix is to play nice with its
environment, and to sneer at the 100% Java fetishists. Sadly, most of Java is on
Windows, which is a place I fear to tread, a whiney and petulant environment
that people need to work against. There are few tools that we can count on to be
present, but we can assume that if you're doing 
