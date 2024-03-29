import com.google.protobuf.gradle.*
import org.junit.jupiter.api.Named.named

val javaVersion = "17"
val protobufVersion = "3.20.1"
val grpcVersion = "1.46.0"
val grpcKotlinVersion = "1.2.1"

plugins {
    kotlin("jvm") version "1.6.21"
    id("com.google.protobuf") version "0.8.18"
    java
    application
    id("org.graalvm.buildtools.native") version "0.9.11"
}

repositories {
    mavenLocal()
    mavenCentral()
    google()
}

sourceSets {
    main {
        java {
            setSrcDirs(
                listOf(
                    "build/generated/source/proto/main/grpc",
                    "build/generated/source/proto/main/java"
                )
            )
        }
        withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
            kotlin.srcDir("src/main/kotlin")
            kotlin.srcDir("build/generated/source/proto/main/grpckt")
            kotlin.srcDir("build/generated/source/proto/main/kotlin")
        }
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(platform("io.netty:netty-bom:4.1.76.Final"))
    implementation(platform("com.google.protobuf:protobuf-bom:${protobufVersion}"))
    implementation(platform("io.grpc:grpc-bom:${grpcVersion}"))
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("io.grpc:grpc-kotlin-stub:${grpcKotlinVersion}")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.1")
    implementation("com.google.protobuf:protobuf-java")
    implementation("com.google.protobuf:protobuf-kotlin:${protobufVersion}")
    implementation("com.google.protobuf:protobuf-java-util")
    implementation("io.grpc:grpc-netty")
    implementation("io.grpc:grpc-protobuf")
    implementation("io.grpc:grpc-stub")
    implementation("io.grpc:grpc-services")
    implementation("com.google.guava:guava:31.1-jre")
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
}

application {
    mainClass.value("io.grpc.examples.helloworld.HelloWorldClientKt")
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
        ofSourceSet("main").forEach {
            it.plugins {
                id("grpc")
                id("grpckt")
            }
            it.builtins {
                id("kotlin")
            }
        }
    }
}

graalvmNative {
    toolchainDetection.set(false)
    binaries {
        named("main") {
            imageName.set("grpc-kotlin-demo") // The name of the native image, defaults to the project name
            mainClass.set("io.grpc.examples.helloworld.HelloWorldClientKt")
        }
    }
}


