plugins {
    `java-library`
    `maven-publish`
    id("org.openrewrite.rewrite")
    id("com.diffplug.spotless")
}

group = "org.gdupontf"
description = "sandbox"
java.sourceCompatibility = JavaVersion.VERSION_17

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile>() {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
}

spotless {
    encoding("UTF-8")
    ratchetFrom("origin/main")
    java {
        toggleOffOn()
        removeUnusedImports()
        importOrder()
        cleanthat()
        googleJavaFormat("1.22.0")
        formatAnnotations()
        licenseHeaderFile("LICENSE.txt")
    }
    kotlinGradle {
        target("*.gradle.kts")
        ktlint()
    }
    yaml {
        target("/**/*.yaml")
        jackson()
    }
}

dependencies {
    implementation("com.google.auto.service:auto-service:1.1.1")
    rewrite("org.openrewrite.recipe:rewrite-migrate-java:2.11.0")
    rewrite("org.openrewrite.recipe:rewrite-logging-frameworks:2.5.0")
    rewrite("org.openrewrite.recipe:rewrite-spring:5.7.0")
    rewrite("org.openrewrite.recipe:rewrite-apache:1.0.0")
}
rewrite {
    setConfigFile(project.file("rewrite.yml"))
    rewrite.activeRecipe("com.gdupontf.Opinionated")
}
tasks.named("spotlessCheck") {
    dependsOn("rewriteDryRun")
}

tasks.rewriteDryRun {
    doLast {
        if(reportPath.exists()){
            throw GradleException("Rewrite was not run")
        }
    }
}
