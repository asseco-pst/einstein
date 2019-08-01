## Einstein


### Getting Started

Import using Maven or Gradle:

```xml
<dependency>
    <groupId>com.pst.asseco.channels.devops</groupId>
    <artifactId>einstein</artifactId>
    <version>...</version>
</dependency>
```

```json
compile group: 'com.pst.asseco.channels.devops', name: 'einstein', version: '...'
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

### More info

Know more about this Project [here](https://confluence.pst.asseco.com/display/CHAN/Einstein)