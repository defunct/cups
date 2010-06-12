---
layout: default
title: Cups Install
---

Its what people will expect anyway.

`cups install --recurse org.hibernate/hibernate-core`

## An Easier Notion of Install

I've got a handful of ways to install a dependency, through a GitHub downlaod,
throgh a Maven repository, and now building from source, using Remix.

Remix is inspired by [Homebrew](http://mxcl.github.com/homebrew/). Like
Homebrew, Remix build from source using Formulae. A `Formula` will be given a
sandbox where it can checkout or download the source, reorganized the source so
that is in the shape of a Mix project, then build and install the Mix project.

I'll be changing the discovery logic for `Formula` classes, but I began to think
about dependencies for `Formula`.

I'm not really sure how I got the idea, but it extends from the reality that
Cups will need a community that will submit `Forumla` classes, and come to a
consensus on how the artifacts should be built for each project. Imagining this
dicussion, the curation, I thought about an extension project that would curate
the entire Java corpus. When you need `ant/ant/1.6.5` then you should pull it from
the central Maven respoitory, but if you need `org.hibernate/hibernate-core/3.5.1-Final`
then you should build from source.

This could be implemented simply by creating a file that maps, for each fully
qualified artifact (fully qualified being group, name and version) a command
line that will install that artifact. The commnad line would be relative to
`java go.go cups` so it would append the actual installation method, which could
be `github`, `maven`, `remix` or some as of yet unknown artifact source.

The map will be a properties file, with property expansion, using
[Madlib](http://bigeasy.github.com/madlib/). Any property key that is in a three
part slash separated artifact, part of the map. Other properties can be used for
variables to eliminate some repetition in the file.

The command line would be the full command line, so this will get repetative,
but it is only a text file, not code. We'll be careful when we change it. I
would like to see it spelled out, rather than have a more complicated language.

One thing we're going to do to make it a little quieter in this file is pass the
version in, without having to type it twice on each line. This file will
probably, actually be quite legible, and really only a heartache for the
maintainers, not the innocents.

The idea has grown on me, so now I feel that this should be built into Cups
core, and it should be the default way of installing your dependencies. Now the
GitHub and Maven commands can forgo their recurse switch. They will only install
a single dependency. Recursive install will be done by an overarching command
`cups install` which is the command that everyone is expecting anyway.
