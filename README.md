# Scala Project Template

[![Build Status](https://travis-ci.org/mslinn/has-id.svg?branch=master)](https://travis-ci.org/mslinn/has-id)
[![GitHub version](https://badge.fury.io/gh/mslinn%2Fshas-id.svg)](https://badge.fury.io/gh/mslinn%2Fhas-id)

This project provides a stub for `model.persistence.Id`; 
both this project and [has-value](https://github.com/mslinn/has-value) are used by 
[html-form-scala](https://github.com/mslinn/html-form-scala).
Other projects that require a specific implementation (for persistence, for example) 
are free to provide their own implementation of `model.persistence.Id`.

## Installation
Add this to your project's `build.sbt`:

    resolvers += "micronautics/scala on bintray" at "http://dl.bintray.com/micronautics/scala"

    libraryDependencies += "com.micronautics" %% "has-id" % "1.0.1" withSources()

## Scaladoc
[Here](http://mslinn.github.io/has-id/latest/api/)
