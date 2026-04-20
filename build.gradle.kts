
plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
    idea
}
idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
group = "org.vd"
version = "0.0.1-SNAPSHOT"
description = "VlogDeputataRB"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.bucket4j:bucket4j-core:8.6.0") //Rate limiting удобнее чем обычнные коллекции
    implementation("com.warrenstrange:googleauth:1.5.0")// 2FA TOTP
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")//jackson
    implementation("org.apache.commons:commons-text:1.11.0")
    implementation("org.flywaydb:flyway-core")
    runtimeOnly("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.8")//быстрый кеш без Redis
    implementation("dev.samstevens.totp:totp:1.7.1") // 2FA TOTP
    implementation("com.google.zxing:core:3.5.3") //QR
    implementation("com.google.zxing:javase:3.5.3") //QR
    implementation("com.maxmind.geoip2:geoip2:4.2.0")//country loginEvent
    implementation("org.springframework.boot:spring-boot-starter-data-redis")//Redis
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jsoup:jsoup:1.16.2")//safe HTMl
    implementation("com.github.ua-parser:uap-java:1.6.1")//Разбор User-Agent
    implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")//sec authorize
    compileOnly("org.projectlombok:lombok")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
