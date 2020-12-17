import com.google.protobuf.gradle.*

val javaVersion = "1.8"
val protobufVersion = "3.14.0"
val grpcVersion = "1.34.1"
val grpcKotlinVersion = "1.0.0"

plugins {
    application
    java
    kotlin("jvm") version "1.4.21"
    id("com.palantir.graal") version "0.7.2"
    id("com.google.protobuf") version "0.8.14"
}


repositories {
    maven("https://dl.bintray.com/kotlin/kotlin-eap")
    google()
    jcenter()
    mavenCentral()
    mavenLocal()
}

sourceSets {
    main {
        java {
            setSrcDirs(listOf("build/generated/source/proto/main/grpc", "build/generated/source/proto/main/java"))
        }
        proto {
            setSrcDirs(listOf("src/main/proto"))
        }
        withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
            kotlin.srcDir("src/main/kotlin")
            kotlin.srcDir("build/generated/source/proto/main/grpckt")
        }
    }

}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("io.grpc:grpc-kotlin-stub-lite:${grpcKotlinVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.2")
    runtimeOnly("io.grpc:grpc-okhttp:${grpcVersion}")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    api("com.google.protobuf:protobuf-javalite:${protobufVersion}")
}

application {
    mainClass.set("io.grpc.examples.helloworld.HelloWorldClientKt")
}

// todo: add graalvm-config-create task
// JAVA_HOME=~/.gradle/caches/com.palantir.graal/20.2.0/8/graalvm-ce-java8-20.2.0 JAVA_OPTS=-agentlib:native-image-agent=config-output-dir=native-client/src/graal native-client/build/install/native-client/bin/native-client

graal {
    graalVersion("20.3.0")
    mainClass(application.mainClass.get())
    outputName("hello-world")
    option("--verbose")
    option("--no-server")
    option("--no-fallback")
    option("-H:+ReportExceptionStackTraces")
    option("-H:+PrintClassInitialization")
    option("-H:ReflectionConfigurationFiles=src/graal/reflect-config.json")
}


java {
    sourceCompatibility = JavaVersion.toVersion(javaVersion)
    targetCompatibility = JavaVersion.toVersion(javaVersion)
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = javaVersion
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = javaVersion
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.suppressWarnings = true
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:${protobufVersion}"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:${grpcVersion}"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:${grpcKotlinVersion}:jdk7@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.builtins {
                named("java") {
                    option("lite")
                }
            }
            it.plugins {
                id("grpc") {
                    option("lite")
                }
                id("grpckt") {
                    option("lite")
                }
            }
        }
    }
}

