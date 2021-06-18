import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.10"
    application
}

group = "me.meng"
version = ""

repositories {
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
}

dependencies {
    //tgs -> gif
    implementation(project(":LottieToGif"))

    //main
    implementation( group = "io.github.kotlin-telegram-bot.kotlin-telegram-bot", name = "telegram", version = "6.0.4")

    //api and net
    implementation( group = "com.github.kittinunf.fuel", name = "fuel", version = "2.3.1")

    //sticker convert
    implementation( group = "org.sejda.imageio", name = "webp-imageio", version = "0.1.6")

    //multi-task
    implementation(group = "org.jetbrains.kotlinx", name = "kotlinx-coroutines-core", version = "1.4.2")

    //lunar about
    implementation(group = "cn.6tail", name = "lunar", version = "1.2.3")

    //json post
    implementation(group = "com.squareup.moshi", name = "moshi-kotlin", version = "1.12.0")

    //html parse
    implementation (group = "org.jsoup", name = "jsoup", version = "1.13.1")

    //shot
    implementation (group = "org.seleniumhq.selenium", name = "selenium-java", version = "4.0.0-beta-4")
    implementation( group = "org.apache.logging.log4j", name = "log4j-api", version = "2.1")
    implementation (group = "org.apache.logging.log4j", name = "log4j-core", version = "2.1")
    implementation (group = "org.apache.logging.log4j", name = "log4j-slf4j-impl", version = "2.1")


}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}

tasks.jar {
    dependsOn(":LottieToGif:build")
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
}

application {
    mainClass.set("MainKt")
}
