plugins {
    kotlin("js") version "1.4.32"
}

group = "me.gcx11"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin.sourceSets {
    main {
        dependencies {
            implementation(kotlin("stdlib-js"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.4.3")
        }
    }
    test {
        dependencies {
            implementation(kotlin("test-js"))
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-js:1.4.3")
        }
    }
}

kotlin {
    js {
        browser {
            testTask {
                useKarma {
                    useFirefox()
                }
            }
        }
    }
}