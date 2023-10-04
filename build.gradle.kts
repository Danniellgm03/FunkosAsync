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
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")

    testCompileOnly("org.projectlombok:lombok:1.18.30")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.30")
    implementation("org.jetbrains:annotations:24.0.0")
    implementation("com.h2database:h2:2.2.224")
    implementation("org.mybatis:mybatis:3.5.13")
    implementation("io.projectreactor:reactor-core:3.5.10")
}

tasks.test {
    useJUnitPlatform()
}