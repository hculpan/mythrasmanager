# Mythras Manager
A combat management application for the Mythras RPG system

# To Build
This project was written for Java 8 and uses Gradle as the build tool.

It was developed to create an installable platform-specific artifact.
To do so, execute:
	./gradlew clean build shadow javapackage
	
Note that for Windows, you need to have the [WiX Toolset](http://wixtoolset.org/) installed.
The latest stable build, as of this writing, was 3.11.1, but this requires
.NET to be installed.  I used version 3.14 from the [Weekly build](http://wixtoolset.org/releases/weekly/)
and that seemed to work just fine, and did not have the .NET
dependency.  But I'm a Mac user, so I did very little testing on Windows.

