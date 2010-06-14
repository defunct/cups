---
layout: default
title: Cups Concerns and Decisions
---

# Cups Concerns and Decisions

## POM Readers

At some point in the past, I'd made a attempt to extract the POM reader into its
own little project, but then realized that there would be no community for it.
People wanting some means to read a POM would want to read all the details of
the POM, I'm sure, not just the dependency information. The POM reader in Cups
Maven is only used to extract the dependencies from the POM. None of the other
nonsense is considered.

I still have no use for the rest of the contents of a POM, but I have a new
place where I'd like to use the extracted dependencies; Cups Remix. Seems to
make more sense to obtain a list of the necessary includes form a POM if it
exists, rather than copy it all out into a Mix `ProjectModule`.

Now, however, we're going to need the full listing of dependencies, not just the
production dependencies, but the ones necessary for testing as well, so the POM
reader will have to return a map, with the dependencies as lists keyed by their
group. The `JavaProject` cookbook should now accept a list of `Include`
instances or `Artifact` instances in addition to the strings used to build
`Include` instances one at a time.

This is still something that is specific to Cups. I don't want another community
to try to form to turn it into a generalized POM parser. Only dependency
extraction. If something else is needed by a Cups Remix formula, it can be a
one off XPath querty against the POM using Comfort XML, which will be available
to make such things simple.

## An Easier Notion of Install

Wrote a blog post on what this has become.
