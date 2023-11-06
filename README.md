## Einstein

[![CircleCI](https://circleci.com/gh/asseco-pst/einstein/tree/develop.svg?style=svg)](https://circleci.com/gh/asseco-pst/einstein/tree/develop)
[![CodeFactor](https://www.codefactor.io/repository/github/asseco-pst/einstein/badge)](https://www.codefactor.io/repository/github/asseco-pst/einstein)

**einstein** is a dependency management tool that aims to simplify and automate software projects dependencies calculation.

Software products are commonly composed of multiple projects, and those projects relate to each other at some kind of level. It's important
to guarantee that those relationships are based on the projects' versions so one can assure that they can evolve without compromising such
relationships.

This said, einstein allows us:
  1. To register project's dependencies on a single file that is placed within the project itself.
  2. To calculate the dependencies tree of one or more projects, based on the above file

### Requirements
During dependencies calculation, the einstein tool needs to fetch projects' extra information from a central repository.

At its current version, einstein is prepared to communicate to any Gitlab instance, through its api, so it assumes that declared dependencies represents projects that are all
stored in a single Gitlab instance.

In order to establish a successful connection to the Gitlab Api, it's necessary to create the following environment variables on the machine where einstein will be executed:

|Variable|Description|Example|
|--------|-----------|-------|
|`GITLAB_URL`|The URL to your GitLab instance|http://gitlab.mycompany.com/|
|`GITLAB_TOKEN`|A personal access token||


### Getting Started

#### einstein.yaml
In order to calculate dependencies between projects one must register them first.
To do so, create a file, named `einstein.yaml`, within the root folder of desired projects. This file must respect the [YAML 1.2 specs](https://yaml.org/spec/1.2/spec.html)

Dependencies registered on this file must uniquely identify its projects within the Gitlab instance.
This is achieved by identifying the dependency with:
- The Gitlab namespace of dependency's project
- The dependency's project Gitlab name

##### Example

```yaml
namespaceA:
 - projectA: =[version]
 - ...

otherNamespace:
 - projectB: =[version]
 - projectC: =[version]
 - ...
```

Declared versions must be specified according [semver specifications](https://semver.org/spec/v2.0.0.html#semantic-versioning-specification-semver)
and can be specified as ranges as well.

```yaml
namespaceA:
 - projectA: = ^1.0
 - ...

namespaceB:
 - projectB: = ~1.0
 - projectC: = 1.0.0
 - ...
```

The following table contains all version ranges accepted by Einstein. More information [here](https://devhints.io/semver)

|Expression|Description|Note|
|----------|-----------|----|
|~1.2.3|>= 1.2.3 < 1.3.0||
|^1.2.3|>= 1.2.3 < 2.0.0||
|~1.2.3|is >=1.2.3 <1.3.0||
|^1.2.3|is >=1.2.3 <2.0.0||
|^0.2.3|is >=0.2.3 <0.3.0|(0.x.x is special)|
|^0.0.1|is =0.0.1|(0.0.x is special)|
|^1.2|is >=1.2.0 <2.0.0|(like ^1.2.0)|
|~1.2|is >=1.2.0 <1.3.0|(like ~1.2.0)|
|^1|is >=1.0.0 <2.0.0||
|~1|same||
|1.x|same||
|1.*|same||
|1|same||
|*|any version||
|x|same||

#### Usage
##### As a CLI

1. Download the latest application binary from the [Releases section](https://github.com/asseco-pst/einstein/releases).
(zip/tar file)
2. Extract it and make it available within your system
3. Execute the `einstein -h` command to be sure that `einstein` is already available.

Now, imagine we're validating/calculating the dependencies for a project named 'server' with namespace 'middleware', for 2.0.0 version:

###### To validate server's einstein.yaml dependencies
The `validate` command allows you to locally validate the dependencies of a local project by running `einstein` within the
project's local working copy folder. `einstein` will start its execution by considering the `einstein.yaml` file located
at the project's root folder and then, for each of the dependencies found within this file, it will check, through the 
configured Gitlab instance's api, the dependencies of those identified projects.

This way, you can check (validate) the project's dependencies even before launching a new release.

```console
C:\> einstein validate -i [path]/server // if command is ran outside the project's root folder
C:\> einstein validate -i . // if command is ran inside the  project's root folder
```

###### To calculate server's dependencies
By using the `calculate` command, `einstein` assumes that it'll fetch all dependencies through the configured Gitlab
instance's api, so you don't have necessarily to run this command within any project's folder... you can run it anywhere
on your system, assuming tha `einstein` is already available on the system's path.

Since you're running `einstein` outside any project's scope, on this command you have to specify the project and version
from which you want to fetch dependencies. The project is identified by its Gitlab name and namespace.

```console
C:\> einstein calculate -p middleware/server:2.0.0
```

All the above commands supports the following options:
```
 -lt or --log-to: a path to where to log the output
 -o or --output: a path to where to save the dependency calculation
 -v or --verbose: control verbosity. Repeat as many as necessary (-vvv)
```

##### As a Groovy Lib

Import using Maven:

```xml
<dependency>
    <groupId>io.github.asseco-pst</groupId>
    <artifactId>einstein</artifactId>
    <version>...</version>
</dependency>
```

Import using Gradle:

```groovy
compile group: 'io.github.asseco-pst', name: 'einstein', version: '...'
```

Then use einstein as following:

```groovy

Einstein.calcDependencies(new ProjectDAO("server", "middleware", "2.0.0"))
// or Einstein.calcDependencies(ProjectDAO.fromFullName("middleware/server:2.0.0"))

Map dependencies = Einstein.getDpManager().getFinalDependencies()

//In order to provide a clean shutdown, when finishing your application, also call the following:
einstein.shutdown()
```

##### Build from source
1. Clone the project
```sh
git clone git@github.com:asseco-pst/einstein.git
```

2. Run the following command on the root of the project:
```sh
gradlew build
```
