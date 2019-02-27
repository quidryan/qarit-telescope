plugins {
    application
    kotlin("jvm") version "1.3.10"
}

application {
    mainClassName = "telescope.Download"
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("com.squareup.okhttp3:okhttp:3.12.1")
    implementation("org.slf4j:slf4j-api:1.7.25")
    implementation("org.slf4j:slf4j-simple:1.7.25")
    implementation("org.jsoup:jsoup:1.10.3")
}
