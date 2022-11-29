# JPackage

A copy of the JPackage source code branched of from
[openjdk/jdk](https://github.com/openjdk/jdk) at
[5795c760db5](https://github.com/openjdk/jdk/commit/5795c760db5bbfd7ff3b56a0c6236827526df70a).

The JDK is a huge repository with 1.8Gb, >80K files and > 70K commits.
Checking it out from the remote in order to inspect things in a local IDE
is not a quick operation with a repository of this size.

This clone has relocated the packages and renamed everything from
`jdk.jpackage` to `mobanisto.jpackage` so that conflicts with the
packages from the JDK this is compiled with are avoided.

Also, Gradle configurations have been added to that the project can be
built in the IDE.
