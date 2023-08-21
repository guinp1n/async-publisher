import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

sourceSets {
    main {
        resources.srcDirs("src/main/resources")
    }
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    implementation("com.hivemq:hivemq-mqtt-client:1.3.0")
    implementation("io.netty:netty-handler:4.1.77.Final")
    implementation("io.netty:netty-all:4.1.77.Final")
    //implementation(platform("com.hivemq:hivemq-mqtt-client-websocket:1.2.2"))
    implementation("info.picocli:picocli:4.6.1")
    annotationProcessor("info.picocli:picocli-codegen:4.6.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

tasks {
    named<Jar>("jar") {
        manifest {
            attributes["Main-Class"] = "org.example.Main" // Replace with your actual main class
        }
    }

    named<ShadowJar>("shadowJar") {
        archiveFileName.set("async-publisher.jar") // Rename the JAR file as needed

        dependencies {
            include(dependency("info.picocli:picocli:4.6.1"))
            include(dependency("com.hivemq:hivemq-mqtt-client:1.3.0"))
            include(dependency("io.netty:netty-handler:4.1.77.Final"))
            include(dependency("io.netty:netty-all:4.1.77.Final"))
        }
    }
}


