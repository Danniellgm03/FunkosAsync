plugins {
    id("java")
}

group = "org.pokemonAsync"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("com.google.code.gson:gson:2.10.1")

    implementation("ch.qos.logback:logback-classic:1.4.11")
    implementation("org.jetbrains:annotations:24.0.0")
    implementation("com.h2database:h2:2.1.210")
    implementation("org.mybatis:mybatis:3.5.13")
    implementation("com.zaxxer:HikariCP:5.0.1")

    testImplementation("org.mockito:mockito-junit-jupiter:5.5.0")
    testImplementation("org.mockito:mockito-core:5.5.0")

}

tasks.jar{
    manifest {
        attributes["Main-Class"] = "org.funkoAsync.Main"
    }
}

tasks.test {
    useJUnitPlatform()
}