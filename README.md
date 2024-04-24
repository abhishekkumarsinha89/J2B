# J2B
Utility class for converting Java files to XML using Sail4j (https://github.com/renliangfeng/sail4j-iiq-idn) and Apache Velocity

# Description
Sail4J library [GitHub - renliangfeng/sail4j-iiq-idn](https://github.com/renliangfeng/sail4j-iiq-idn) which provides a way to build Java Class in your IDE (I use VSCode) to beanshell code and generates rule XML. It is another way to generate your large rule libraries with Unit testing and No compilation errors.

I used the Sail4J libraries to create a Java Code that generates the rule XMLs from the Java Class(es) you create using the Sail4J annotations and use the apache velocity to create Rule XML.

This Utility provides you a way to build Sailpoint IIQ Rules in Eclipse and generate Rule XMLs ready to be deployed on your IIQ instance.

# Libraries required
commons-collections-3.2.jar
commons-lang-2.6.jar
commons-logging-1.3.1.jar
guava-33.1.0-jre.jar
identityiq.jar
javaparser-core-3.18.0.jar
log4j-jcl-2.23.1.jar
sail4j-api-1.2.jar
sail4j-transform-1.2.jar
slf4j-api-1.6.1.jar
velocity-1.6.2.jar
velocity-tools-2.0.jar
