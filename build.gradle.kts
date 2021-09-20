import com.google.protobuf.gradle.*

val javaVersion = "1.8"
val protobufVersion = "3.18.0"
val grpcVersion = "1.40.1"
val grpcKotlinVersion = "1.1.0"

plugins {
    application
    java
    kotlin("jvm") version "1.5.31"
    id("com.google.protobuf") version "0.8.17"
}

repositories {
    google()
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
    runtimeOnly("io.grpc:grpc-okhttp:${grpcVersion}")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
    api("com.google.protobuf:protobuf-java-util:${protobufVersion}")
    api("io.grpc:grpc-protobuf:${grpcVersion}")
    api("io.grpc:grpc-stub:${grpcVersion}")
    api("io.grpc:grpc-kotlin-stub:${grpcKotlinVersion}")
}

application {
    mainClass.set("io.grpc.examples.helloworld.HelloWorldClientKt")
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
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}

