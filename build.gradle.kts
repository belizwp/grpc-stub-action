import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.plugins
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.21"
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
    id("com.google.protobuf") version "0.8.18"
    `java-library`
    `maven-publish`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.2")

    implementation("com.google.protobuf:protobuf-java:3.21.1")
    implementation("com.google.protobuf:protobuf-kotlin:3.21.1")

    implementation("io.grpc:grpc-stub:1.47.0")
    implementation("io.grpc:grpc-protobuf:1.47.0")
    implementation("io.grpc:grpc-kotlin-stub:1.3.0")

    compileOnly("javax.annotation:javax.annotation-api:1.3.2")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
        jvmTarget = "1.8"
    }
}

configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
    android.set(false)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(false)
    disabledRules.set(setOf("no-wildcard-imports", "experimental:argument-list-wrapping"))
    enableExperimentalRules.set(true)
    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.21.1"
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.47.0"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }
    }
    generateProtoTasks {
        all().forEach {
            it.plugins {
                id("grpc") {
                    outputSubDir = "java"
                }
                id("grpckt") {
                    outputSubDir = "kotlin"
                }
                it.builtins {
                    id("kotlin")
                }
            }
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/${System.getenv("OWNER")}/${System.getenv("REPO_NAME")}")
            credentials {
                username = project.findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
                password = project.findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("gpr") {
            group = System.getenv("GROUP")
            artifactId = System.getenv("ARTIFACT_ID")
            version = System.getenv("VERSION")
            from(components["java"])
        }
    }
}
