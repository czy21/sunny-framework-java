## Publish to maven
```shell
./gradlew clean
./gradlew publishToMavenLocal
```
## Graaljs
- OpenJDK11
```groovy
implementation 'org.graalvm.sdk:graal-sdk:22.3.5'
implementation 'org.graalvm.js:js:22.3.5'
```
## Mybatis generate
```shell
./gradlew :sunny-generator-mybatis:clean :sunny-generator-mybatis:build
./gradlew :sunny-generator-mybatis:mybatisGenerate
```