#!/usr/bin/env kotlin

@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-html-jvm:0.11.0")

import Profile_main.Group.*
import Profile_main.Type.*
import kotlinx.html.a
import kotlinx.html.b
import kotlinx.html.br
import kotlinx.html.consumers.measureTime
import kotlinx.html.i
import kotlinx.html.img
import kotlinx.html.small
import kotlinx.html.stream.createHTML
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import kotlin.io.path.Path
import kotlin.io.path.readText
import kotlin.io.path.writeText
import kotlin.text.RegexOption.DOT_MATCHES_ALL


data class Entry(
    val author: String = "SimonMarquis",
    val name: String,
    val label: String = name,
    val more: String? = null,
    val quote: String? = null,
    val packages: Map<Type, String?> = emptyMap(),
    val data: Map<String, Any> = emptyMap(),
)

enum class Group { AndroidApp, AndroidLibrary, GitHubAction, KotlinTool, Misc, AdventOfCode }
enum class Type { GooglePlay, FDroid, GitHubRelease, GitHubMarketplace, GitHubPages, Web, Maven }

// @formatter:off
val map = mapOf(
    AndroidApp to listOf(
        Entry(name = "nowinandroid", author = "android", label = "Now in Android", more = "(external contributor)", quote = "🤖 A fully functional Android app built entirely with Kotlin and Jetpack Compose", packages = mapOf(GooglePlay to "com.google.samples.apps.nowinandroid")),
        Entry(name = "InternalAppStore", label = "Internal App Store", quote = "📦 Manage your own internal Android App Store", packages = mapOf(Web to "https://public-app-store.web.app/", GitHubRelease to null)),
        Entry(name = "AR-Toolbox", label = "AR Toolbox", quote = "🧰 ARCore & Sceneform Playground", packages = mapOf(GooglePlay to "fr.smarquis.ar_toolbox", GitHubRelease to null)),
        Entry(name = "FCM-toolbox", label = "FCM toolbox", quote = "📲 Firebase Cloud Messaging toolbox", packages = mapOf(GooglePlay to "fr.smarquis.fcm", Web to "https://fcm-toolbox-public.web.app")),
        Entry(name = "QrCode", label = "QrCode", quote = "🏁 Scan and create QR Codes", packages = mapOf(GooglePlay to "fr.smarquis.qrcode", GitHubPages to null)),
        Entry(name = "SleepTimer", label = "Sleep Timer", quote = "💤 Simplest Sleep Timer", packages = listOf(GooglePlay, FDroid).associateWith { "fr.smarquis.sleeptimer" }),
        Entry(name = "Android-SoundQuickSettings", label = "Sound Quick Settings", quote = "🔊 A simple Quick Settings Tile to control the sound volume", packages = mapOf(GooglePlay to "fr.smarquis.soundquicksettings")),
        Entry(name = "Android-PreferencesManager", label = "Preferences Manager", quote = "⚙️ Seamlessly edit application's preferences", packages = listOf(GooglePlay, FDroid).associateWith { "fr.simon.marquis.preferencesmanager" }),
        Entry(name = "Android-SecretCodes", label = "Secret Codes", quote = "🪄 Browse through hidden codes of your Android phone", packages = listOf(GooglePlay, FDroid).associateWith { "fr.simon.marquis.secretcodes" }),
    ),
    AndroidLibrary to listOf(
        Entry(name = "Android-Playground", label = "Android Playground", quote = "🛝 Playground for Android projects"),
        Entry(name = "Lint-Playground", label = "Lint Playground", quote = "🛝 Playground for Lint projects", packages = mapOf(Web to "https://simonmarquis.github.io/Lint-Playground/")),
        Entry(name = "Android-App-Linking", label = "Android App Linking", quote = "🔗 The ultimate developer guide to Android app linking methods", packages = mapOf(GooglePlay to "fr.smarquis.applinks", GitHubPages to null)),
        Entry(name = "Preferences-filtering", label = "Preferences filtering", quote = "🔎 Search through Android's Preferences screens", packages = mapOf(GitHubRelease to null)),
        Entry(name = "Android-InstallReferrer", label = "Install Referrer", quote = "Test the referrer attribute on the Google Play Store", packages = mapOf(GooglePlay to "fr.simon.marquis.installreferrer", GitHubPages to null)),
        Entry(name = "Android-UrlSchemeInterceptor", label = "Url Scheme Interceptor", quote = "Intercept and debug url scheme on Android", packages = mapOf(GooglePlay to "fr.smarquis.usi.sample", GitHubPages to null)),
        Entry(name = "Android-Spans", label = "Android Spans", quote = "Kotlin and Java wrappers around SpannableStringBuilder"),
    ),
    KotlinTool to listOf(
        Entry(name = "SealedObjectInstances", label = "Sealed Object Instances", quote = "🗃️ A Kotlin Symbol Processor to list sealed object instances", packages = mapOf(Maven to "fr.smarquis.sealed/sealed-object-instances", GitHubRelease to null)),
        Entry(name = "JavaAgent", label = "Java Agent", quote = "👮 Detect suppressed exceptions in unit tests"),
        Entry(name = "Maven-Dependency-Tree", label = "Maven Dependency Tree", quote = "🌲 Kotlin script to list transitive dependencies of Maven artifacts"),
        Entry(name = "svg2avd", label = "svg2avd", quote = "⚙️ Kotlin script to convert SVG files to AVD"),
    ),
    GitHubAction to listOf(
        Entry(name = "android-accept-licenses", label = "Android accept licenses", quote = "🤖 Accept Android licenses with `sdkmanager`", packages = mapOf(GitHubMarketplace to "android-accept-licenses", GitHubRelease to null)),
        Entry(name = "ci-gradle-properties-action", label = "CI Gradle properties", quote = "🐘 Copy Gradle properties file to the CI's home directory", packages = mapOf(GitHubMarketplace to "setup-gradle-properties-file", GitHubRelease to null)),
    ),
    Misc to listOf(
        Entry(name = "TIL", label = "Today I Learned", quote = "🗓️ Today I Learned", packages = mapOf(GitHubPages to null)),
        Entry(name = "Firebase-Remote-Config-Changes-Notifier", label = "Firebase Remote Config changes notifier", quote = "🔥 Notify changes in real time through Slack Webhooks"),
        Entry(name = "Android-App-Reviews-Notifier", label = "Android App Reviews Notifier", quote = "🛎️ Get your Android app reviews directly in a Slack channel"),
        Entry(name = "GitHub-Actions-Playground", label = "GitHub Actions Playground", quote = "🛝 Playground for GitHub Actions"),
        Entry(name = "Android-Version-Distribution", label = "📊 Android Version Distribution"),
        Entry(name = "Firebase-Test-Lab-Devices", label = "📱 Firebase Test Lab Devices"),
    ),
    AdventOfCode to listOf(
        Entry(name = "advent-of-code-2025", label = "Advent of Code 2025", packages = mapOf(Web to "https://adventofcode.com/2025"), data = mapOf("⭐" to 14)),
        Entry(name = "advent-of-code-2024", label = "Advent of Code 2024", packages = mapOf(Web to "https://adventofcode.com/2024"), data = mapOf("⭐" to 18)),
        Entry(name = "advent-of-code-2023", label = "Advent of Code 2023", packages = mapOf(Web to "https://adventofcode.com/2023"), data = mapOf("⭐" to 18)),
        Entry(name = "advent-of-code-2022", label = "Advent of Code 2022", packages = mapOf(Web to "https://adventofcode.com/2022"), data = mapOf("⭐" to 31)),
        Entry(name = "advent-of-code-2021", label = "Advent of Code 2021", packages = mapOf(Web to "https://adventofcode.com/2021"), data = mapOf("⭐" to 27)),
        Entry(name = "advent-of-code-2020", label = "Advent of Code 2020", packages = mapOf(Web to "https://adventofcode.com/2020"), data = mapOf("⭐" to 12)),
        Entry(name = "advent-of-code-2019", label = "Advent of Code 2019", packages = mapOf(Web to "https://adventofcode.com/2019"), data = mapOf("⭐" to 26)),
    )
)
// @formatter:on

fun Entry.github() = "https://github.com/$author/$name"
fun shield(value: String) = "https://img.shields.io/$value"

createHTML().measureTime().run {
    table {
        map.forEach { (group, entries) ->
            thead {
                attributes["style"] = "font-size: larger; background-color: #FFFFFF11;"
                tr {
                    th {
                        attributes["style"] = "text-align: left"
                        b {
                            +when (group) {
                                AndroidApp -> "🤖 Android apps"
                                AndroidLibrary -> "🤖 Android libraries"
                                GitHubAction -> "🐙 Github Actions"
                                KotlinTool -> "🧰 Kotlin libraries & tools"
                                Misc -> "💎 Miscellaneous"
                                AdventOfCode -> "🎄 Advent of Code"
                            }
                        }
                    }
                    th { b { +"⭐" } }
                    th { b { +"🛎️" } }
                    th { b { +"🔗" } }
                }
            }
            tbody {
                entries.forEach {
                    tr {
                        /* Project */
                        td {
                            a(it.github()) {
                                b { +it.label }
                                it.more?.let { small { +" $it"} }
                                it.quote?.let { br; i { small { +it } } }
                            }
                        }
                        /* Stars & Forks */
                        td {
                            when (group) {
                                AdventOfCode -> {
                                    b { +it.data["⭐"].toString() }
                                }
                                else -> {
                                    a("${it.github()}/stargazers") { img(alt = "Stars", src = shield("github/stars/${it.author}/${it.name}")) }; br
                                    a("${it.github()}/forks") { img(alt = "Forks", src = shield("github/forks/${it.author}/${it.name}")) }
                                }
                            }
                        }
                        /* Issues & PRs */
                        td {
                            when (group) {
                                AdventOfCode -> Unit
                                else -> {
                                    a("${it.github()}/issues") { img(alt = "Issues", src = shield("github/issues/${it.author}/${it.name}?label=Issues")) }; br
                                    a("${it.github()}/pulls") { img(alt = "Pull Requests", src = shield("github/issues-pr/${it.author}/${it.name}?label=PRs%E2%A0%80%E2%A0%80")) }
                                }
                            }
                        }
                        /* Links */
                        td {
                            it.packages.entries.forEachIndexed { index, (type, name) ->
                                if (index != 0) br
                                a(
                                    href = when (type) {
                                        GooglePlay -> "https://play.google.com/store/apps/details?id=$name"
                                        FDroid -> "https://f-droid.org/en/packages/$name"
                                        Maven -> "https://central.sonatype.com/artifact/$name"
                                        GitHubMarketplace -> "https://github.com/marketplace/actions/$name"
                                        GitHubRelease -> "https://github.com/${it.author}/${it.name}/releases/latest"
                                        GitHubPages -> "https://${it.author.lowercase()}.github.io/${it.name}"
                                        Web -> name
                                    },
                                ) {
                                    img(
                                        alt = "Download",
                                        src = when (type) {
                                            GooglePlay -> shield("badge/Google%20Play-%20?logo=googleplay&color=grey")
                                            FDroid -> shield("badge/F--Droid-%20?logo=f-droid&color=grey")
                                            GitHubMarketplace -> shield("badge/Marketplace-%20?logo=github&logoColor=white&color=grey")
                                            GitHubRelease -> shield("github/v/release/${it.author}/${it.name}?logo=github&label=%20&color=grey")
                                            Web, GitHubPages -> shield("badge/Web-%20?logo=html5&logoColor=white&color=grey")
                                            Maven -> shield("maven-central/v/$name?label=Maven&color=grey")
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}.let {
    println("Generated in ${it.time} ms")
    with(Path("README.md")) {
        writeText(
            readText().replace("""(<!--region-->)(.*?)(<!--endregion-->)""".toRegex(DOT_MATCHES_ALL)) { match ->
                match.destructured.let { (start, _, end) -> "$start\n${it.result}$end" }
            },
        )
    }
}
