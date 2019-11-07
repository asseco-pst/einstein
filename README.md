## Einstein

[![CircleCI](https://circleci.com/gh/asseco-pst/einstein/tree/develop.svg?style=svg)](https://circleci.com/gh/asseco-pst/einstein/tree/develop)
[![CodeFactor](https://www.codefactor.io/repository/github/asseco-pst/einstein/badge)](https://www.codefactor.io/repository/github/asseco-pst/einstein)

### Getting Started

Import using Maven or Gradle:

```xml
<dependency>
    <groupId>io.github.asseco-pst</groupId>
    <artifactId>einstein</artifactId>
    <version>...</version>
</dependency>
```

```groovy
compile group: 'io.github.asseco-pst', name: 'einstein', version: '...'
```

### Build from source
1. Clone the project
```sh
git clone git@gitlab.dcs.exictos.com:devops/einstein.git
```

2. Run the following command on the root of the project:
```sh
gradlew build
```

## Usage

#### Environment setup
Einstein communicates with your repository management system (at the moment only supports GitLab). In order to authenticate
Einstein uses environment variables.

The following variables should be set on the environment where Einstein is running:

|Variable|Description|Example|
|--------|-----------|-------|
|`GITLAB_URL`|The URL to your GitLab instance|http://gitlab.mycompany.com/|
|`GITLAB_TOKEN`|A personal access token||


### As a CLI
#### Running the executable

```console
C:\> einstein.exe -p mycompany/server:2.3.0
```

### As a Groovy Lib

Calculates the runtime dependencies for project mycompany/server in version 2.3.0:
```groovy
 
Einstein.calcDependencies(new ProjectDAO("server", "mycompany", "2.3.0"))
// or Einstein.calcDependencies(ProjectDAO.fromFullName("mycompany/server:2.3.0"))

Map dependencies = Einstein.getDpManager().getFinalDependencies()
```

### Semver Ranges

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

### Requirements file (since 2.0.0)

The requirements file is a `yaml` file which contains all the runtime dependencies of the project.  
This file must respect the [YAML 1.2 specs](https://yaml.org/spec/1.2/spec.html) and should have the following structure:

```yaml
namespaceA:
 - projectA: =~1.2.3
 - projectB: =~2.3.0
namespaceB:
 - projectC: =~3.2.3
 - projectD: =~3.5.2
```

A real example could be:
```yaml
middleware:
 - server: =~3.4.6
 - irc_ws_bb: =~4.5.3
canais-n-presenciais:
 - backoffice: =~2.3.5
```

### More info

Know more about this Project [here](https://confluence.pst.asseco.com/display/CHAN/Einstein)
