gRPC Kotlin Graal demo
======================

# Attention

* Use `protobuf-javalite` and `grpc-kotlin-stub-lite` version
* Reflection for Protobuf messages in `src/graal/reflect-config.json` 

# How to test?

* Start gRPC Server from https://github.com/linux-china/grpc-kotlin-demo
* build native and test: 

```
$ ./gradlew -x test nativeImage
$ ./build/graal/hello-world
```

# References

* gRPC-Kotlin: https://github.com/grpc/grpc-kotlin
* gradle-graal: https://github.com/palantir/gradle-graal
* Protobuf Plugin for Gradle: https://github.com/google/protobuf-gradle-plugin
* Building Command Line Applications with GraalVM Enterprise Native Image: https://www.youtube.com/watch?v=7QCL7UgjTW4
* UPX: the Ultimate Packer for eXecutables: https://github.com/upx/upx
* Gradle plugin for GraalVM Native Image building: https://graalvm.github.io/native-build-tools/latest/gradle-plugin.html 
