import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "me.meng"
version = ""

repositories {
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
}

dependencies {

    // logger
    implementation(group = "io.github.microutils", name = "kotlin-logging-jvm", version = "3.0.5")
    // https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
    implementation("org.slf4j:slf4j-simple:2.0.7")


    // jni
    implementation(project(":jni"))

    // gson
    implementation(group = "com.google.code.gson", name = "gson", version = "2.10.1")

    // main
    implementation(group = "io.github.kotlin-telegram-bot.kotlin-telegram-bot", name = "telegram", version = "6.0.7")
    // webhook
    implementation(group = "io.ktor", name = "ktor-server-netty", version = "2.2.4")


    // api and net
    implementation(group = "com.github.kittinunf.fuel", name = "fuel", version = "2.3.1")

    // multi-task
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.4.2")

    // lunar about
    implementation(group = "cn.6tail", name = "lunar", version = "1.2.3")

    // json post
    implementation(group = "com.squareup.moshi", name = "moshi-kotlin", version = "1.12.0")

    // html parse
    implementation(group = "org.jsoup", name = "jsoup", version = "1.15.3")

    // shot
    implementation(group = "org.seleniumhq.selenium", name = "selenium-java", version = "4.8.3")

    // qrcode
    implementation(group = "com.google.zxing", name = "core", version = "3.4.1")
    implementation(group = "com.google.zxing", name = "javase", version = "3.4.1")


}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.jar {
    dependsOn(":jni:build")
    // enabled = true
    archiveBaseName.set("release-amd64")
    manifest {
        attributes(mapOf("Main-Class" to "MainKt"))
    }
    from(configurations.runtimeClasspath.get().map {
        if (it.isDirectory) it else zipTree(it)
    })
    val sourcesMain = sourceSets.main.get()
    sourcesMain.allSource.forEach { println("add from sources: ${it.name}") }
    from(sourcesMain.output)
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

application {
    mainClass.set("MainKt")
}
