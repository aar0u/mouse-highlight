plugins {
    java
    application
}

group = "com.github.aar0u"
version = "1.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.github.kwhat:jnativehook:2.2.2")
}

application {
    mainClass.set("com.github.aar0u.mousehighlight.App")
}

tasks.jar {
    manifest {
        attributes("Main-Class" to "com.github.aar0u.mousehighlight.App")
    }
    
    // This configuration is equivalent to Maven's shade plugin, packaging all dependencies into one jar
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}