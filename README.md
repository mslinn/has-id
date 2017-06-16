# has-id

[![Build Status](https://travis-ci.org/mslinn/has-id.svg?branch=master)](https://travis-ci.org/mslinn/has-id)
[![GitHub version](https://badge.fury.io/gh/mslinn%2Fhas-id.svg)](https://badge.fury.io/gh/mslinn%2Fhas-id)

Using raw types for database ids invites errors.
Every Scala developer should always use the `Id` wrapper type provided by this project, because of the type safety it provides. 

`Id` can wrap `Long`, `UUID` and `String` values, and any of them can be optional.
For example: `Id[Long]`, `Id[UUID]`, `Id[String]`, `Id[Option[Long]]`, `Id[Option[UUID]]`, and `Id[Option[String]]`.

See the unit tests for examples and documentation.

## Installation
Add this to your project's `build.sbt`:

    resolvers += "micronautics/scala on bintray" at "http://dl.bintray.com/micronautics/scala"

    libraryDependencies += "com.micronautics" %% "has-id" % "1.2.0" withSources()

## Scaladoc
[Here](http://mslinn.github.io/has-id/latest/api/)
