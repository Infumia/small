# Infumia Library
[![idea](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

![master](https://github.com/Infumia/InfumiaLib/workflows/build/badge.svg)
![Sonatype Nexus (Releases)](https://img.shields.io/nexus/r/tr.com.infumia/InfumiaLibShared?label=maven-central&server=https%3A%2F%2Foss.sonatype.org%2F)
![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/tr.com.infumia/InfumiaLibShared?label=maven-central&server=https%3A%2F%2Foss.sonatype.org)
## How to Use (Developers)
### Plugin.yml (Paper)
```yml
depend:
  - InfumiaLibraryPlugin
```
### velocity-plugin.json (Velocity)
```json
{
  "dependencies": [
    {
      "id": "infumialibraryplugin",
      "optional": false
    }
  ]
}
```
### Maven
```xml
<dependencies>
  <dependency>
    <groupId>tr.com.infumia</groupId>
    <artifactId>InfumiaLibProtobuf</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
  </dependency>
  <dependency>
    <groupId>tr.com.infumia</groupId>
    <artifactId>InfumiaLibKubernetes</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
  </dependency>
  <dependency>
    <groupId>tr.com.infumia</groupId>
    <artifactId>InfumiaLibShared</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
  </dependency>
  <dependency>
    <groupId>tr.com.infumia</groupId>
    <artifactId>InfumiaLibPaperApi</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
  </dependency>
  <dependency>
    <groupId>tr.com.infumia</groupId>
    <artifactId>InfumiaLibVelocityApi</artifactId>
    <version>VERSION</version>
    <scope>provided</scope>
  </dependency>
</dependencies>
```
### Gradle
```groovy
plugins {
  id "java"
}

dependencies {
  compileOnly "tr.com.infumia:InfumiaLibProtobuf:VERSION"
  compileOnly "tr.com.infumia:InfumiaLibKubernetes:VERSION"
  compileOnly "tr.com.infumia:InfumiaLibShared:VERSION"
  compileOnly "tr.com.infumia:InfumiaLibPaperApi:VERSION"
  compileOnly "tr.com.infumia:InfumiaLibVelocityApi:VERSION"
}
```
## How to Use (Server Owners)
Download the latest Jar files here https://github.com/Infumia/InfumiaLib/releases/latest

Put the Jar file into your mods/plugins directory.
