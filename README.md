# Credentials API

The Credentials API interfaces to the credential descriptions in `irma_configuration`. This is currently its main task. The classes responsible for this are contained in `org.irmacard.credentials.info`. These are also the primary entry points into this library. Furthermore, it provides some generalized functions for working with credential systems.

## Example of using the DescriptionStore

TODO: Create an example of setting up the description store and show how it integrates with `irma_configuration`.

## Dependencies

This library has the following dependencies.  All these dependencies will be automatically downloaded by gradle when building or installing the library.

 * [Apache Commons Codec](https://commons.apache.org/proper/commons-codec/)
 * [Google HTTP client](https://developers.google.com/api-client-library/java/google-http-java-client/)

For running the tests:

 * JUnit,  (>= 4.8), the Java unit-testing library

The build system depends on gradle version at least 1.12.

## Building using Gradle (recommended)

When you are using the Gradle build system, just run

    gradle install

to install the library to your local repository. This assumes you have downloaded the archive. Alternatively, you can run

    gradle build

to just build the library.

## Eclipse development files

You can run

    gradle eclipse

to create the required files for importing the project into Eclipse.

## Running tests

The tests are always run, just make sure that you do have a valid checkout of `irma_configuration` (see below). If you want to force the tests (for example because irma_configuration changed) run

    gradle cleanTest test

### irma_configuration

Download or link the `irma_configuration` project to a location within your tree. In particular the tests below assume that `irma_configuration` is placed in the root of this project.
