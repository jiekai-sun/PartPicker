plugins {
    // Apply the java plugin to add support for Java
    java

    // Apply the application plugin to add support for building a CLI application
    // You can run your app via task "run": ./gradlew run
    application

    /*
     * Adds tasks to export a runnable jar.
     * In order to create it, launch the "shadowJar" task.
     * The runnable jar will be found in build/libs/projectname-all.jar
     */
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories { // Where to search for dependencies
    mavenCentral()
}

dependencies {
    implementation("mysql:mysql-connector-java:8.0.29")
}

application {
    // Define the main class for the application.
    mainClass.set("it.unibo.application.LaunchApp")
}
