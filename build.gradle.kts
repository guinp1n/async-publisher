plugins {
    id("java")
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
    implementation(platform("com.hivemq:hivemq-mqtt-client-websocket:1.2.2"))
    implementation("commons-cli:commons-cli:1.4")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}