## Einstein

[![CircleCI](https://circleci.com/gh/asseco-pst/einstein/tree/develop.svg?style=svg)](https://circleci.com/gh/asseco-pst/einstein/tree/develop)

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

### As a CLI (since vx.x.x)

```console
C:\> java -jar einstein.jar -help (ATUALIZAR)
```

### As a Groovy Lib

ATUALIZAR

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
