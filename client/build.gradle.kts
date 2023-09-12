import java.text.SimpleDateFormat
import java.util.*

plugins {
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

val lwjglVersion = "3.3.2"
val jomlVersion = "1.10.5"

val platforms = arrayOf(
    "linux",
    "linux-arm64",
    "linux-arm32",
    "macos",
    "macos-arm64",
    "windows",
    "windows-x86",
    "windows-arm64"
);

val lwjglModules = arrayOf(
    "glfw",
    "openal",
    "opengl",
    "stb"
)

var platform = providers.gradleProperty("voxelthing.client.platform").getOrElse(Pair(
    System.getProperty("os.name")!!,
    System.getProperty("os.arch")!!
).let { (name, arch) ->
    when {
        arrayOf("Linux", "FreeBSD", "SunOS", "Unit").any { name.startsWith(it) } ->
            if (arrayOf("arm", "aarch64").any { arch.startsWith(it) })
                "linux${if (arch.contains("64") || arch.startsWith("armv8")) "-arm64" else "-arm32"}"
            else
                "linux"

        arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) }                ->
            "macos${if (arch.startsWith("aarch64")) "-arm64" else ""}"

        arrayOf("Windows").any { name.startsWith(it) }                           ->
            if (arch.contains("64"))
                "windows${if (arch.startsWith("aarch64")) "-arm64" else ""}"
            else
                "windows-x86"

        else -> throw Error("Unrecognized or unsupported platform. Please set \"lwjglNatives\" manually")
    }
})

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl:lwjgl")
    for (m in lwjglModules) {
        implementation("org.lwjgl:lwjgl-$m")
    }
    implementation("org.joml:joml:${jomlVersion}")

    for (p in platforms.filter { platform == "all" || it == platform }) {
        runtimeOnly("org.lwjgl:lwjgl::natives-$p")
        for (m in lwjglModules) {
            runtimeOnly("org.lwjgl:lwjgl-$m::natives-$p")
        }
    }

    implementation(project(":shared"))
    implementation(project(":pds"))
}

application {
    mainClass.set("io.bluestaggo.voxelthing.Game")
}

tasks.shadowJar {
    archiveFileName.set("voxelthing-${platform}.jar")
}

tasks.compileJava {
    dependsOn("genMetadata")
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "io.bluestaggo.voxelthing.Game"
    }

    archiveBaseName.set("voxelthing")
}

task("genMetadata") {
    val resources = sourceSets.main.get().output.resourcesDir
    resources?.mkdirs()

    val versionFile = file("$resources/version.txt")
    versionFile.createNewFile()
    versionFile.writeText(getaVersion())
}

fun getaVersion(): String {
    var version = providers.gradleProperty("voxelthing.version").get();
    version = version.replace("dev", "dev ${SimpleDateFormat("yyyyMMdd").format(Date())}");
    return version;
}
