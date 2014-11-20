# Credentials API

The Credentials API is the high-level interface to the credential system. The
API abstracts away from the low-level details involved in the communication
between the card, the terminal and the library. It offers a uniform interface
with the following two basic methods:

*Issue* The issuer provides the API with a specification of the credential to 
   be issued and the attribute values it should contain. The API then takes
   care of the protocol specific communication using the terminal application
   and the cryptographic library. The result of this action is either succes or
   failure.

*Verify* The relying party provides the API with a specification of the
   attributes it wants to verify and the credential they belong to. The API
   then uses the terminal application to request the attributes and a proof of
   validity from the card. Finally, the API will use the cryptographic library
   to verify the proof and it will either report failure, or it will return the
   revealed attributes to the relying party.

## Dependencies

This library has the following dependencies.  All these dependencies will be automatically downloaded by gradle when building or installing the library (except for cert-cvc, which is included).

External dependencies:

 * [Cert-CVC](http://www.ejbca.org/) (included)

Internal dependencies:

 * [Scuba](https://github.com/credentials/scuba), The smartcard abstraction layer, uses ` and `scuba_smartcard`

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
