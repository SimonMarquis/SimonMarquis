#!/usr/bin/env kotlin

@file:Repository("https://repo1.maven.org/maven2/")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-html:0.10.1")
@file:DependsOn("org.jetbrains.kotlinx:kotlinx-html-jvm:0.10.1")

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
    val author: String,
    val name: String,
    val label: String = name,
    val quote: String? = null,
    val packages: Map<Type, String?> = emptyMap(),
)

enum class Group { AndroidApp, AndroidLibrary, GitHubAction, KotlinTool, Misc }
enum class Type { GooglePlay, FDroid, GitHubRelease, GitHubMarketplace, GitHubPages, Web, Maven }

// @formatter:off
val map = mapOf(
    AndroidApp to listOf(
        Entry(author = "SimonMarquis", name = "InternalAppStore", label = "Internal App Store", quote = "📦 Manage your own internal Android App Store", mapOf(Web to "https://public-app-store.web.app/", GitHubRelease to null)),
        Entry(author = "SimonMarquis", name = "AR-Toolbox", label = "AR Toolbox", quote = "🧰 ARCore & Sceneform Playground", packages = mapOf(GooglePlay to "fr.smarquis.ar_toolbox", GitHubRelease to null)),
        Entry(author = "SimonMarquis", name = "FCM-toolbox", label = "FCM toolbox", quote = "📲 Firebase Cloud Messaging toolbox", packages = mapOf(GooglePlay to "fr.smarquis.fcm", Web to "https://fcm-toolbox-public.web.app")),
        Entry(author = "SimonMarquis", name = "QrCode", label = "QrCode", quote = "🏁 Scan and create QR Codes", packages = mapOf(GooglePlay to "fr.smarquis.qrcode", GitHubPages to null)),
        Entry(author = "SimonMarquis", name = "SleepTimer", label = "Sleep Timer", quote = "💤 Simplest Sleep Timer", packages = listOf(GooglePlay, FDroid).associateWith { "fr.smarquis.sleeptimer" }),
        Entry(author = "SimonMarquis", name = "Android-SoundQuickSettings", label = "Sound Quick Settings", quote = "🔊 A simple Quick Settings Tile to control the sound volume", packages = mapOf(GooglePlay to "fr.smarquis.soundquicksettings")),
        Entry(author = "SimonMarquis", name = "Android-PreferencesManager", label = "Preferences Manager", quote = "⚙️ Seamlessly edit application's preferences", packages = listOf(GooglePlay, FDroid).associateWith { "fr.simon.marquis.preferencesmanager" }),
        Entry(author = "SimonMarquis", name = "Android-SecretCodes", label = "Secret Codes", quote = "🪄 Browse through hidden codes of your Android phone", packages = listOf(GooglePlay, FDroid).associateWith { "fr.simon.marquis.secretcodes" }),
    ),
    AndroidLibrary to listOf(
        Entry(author = "SimonMarquis", name = "Android-App-Linking", label = "Android App Linking", quote = "🔗 The ultimate developer guide to Android app linking methods", packages = mapOf(GooglePlay to "fr.smarquis.applinks", GitHubPages to null)),
        Entry(author = "SimonMarquis", name = "Preferences-filtering", label = "Preferences filtering", quote = "🔎 Search through Android's Preferences screens", packages = mapOf(GitHubRelease to null)),
        Entry(author = "SimonMarquis", name = "Android-InstallReferrer", label = "Install Referrer", quote = "Test the referrer attribute on the Google Play Store", mapOf(GooglePlay to "fr.simon.marquis.installreferrer", GitHubPages to null)),
        Entry(author = "SimonMarquis", name = "Android-UrlSchemeInterceptor", label = "Url Scheme Interceptor", quote = "Intercept and debug url scheme on Android", mapOf(GooglePlay to "fr.smarquis.usi.sample", GitHubPages to null)),
        Entry(author = "SimonMarquis", name = "Android-Spans", label = "Android Spans", quote = "Kotlin and Java wrappers around SpannableStringBuilder"),
    ),
    KotlinTool to listOf(
        Entry(author = "SimonMarquis", name = "SealedObjectInstances", label = "Sealed Object Instances", quote = "🗃️ A Kotlin Symbol Processor to list sealed object instances", mapOf(Maven to "fr.smarquis.sealed/sealed-object-instances", GitHubRelease to null)),
        Entry(author = "SimonMarquis", name = "JavaAgent", label = "Java Agent", quote = "👮 Detect suppressed exceptions in unit tests"),
        Entry(author = "SimonMarquis", name = "Maven-Dependency-Tree", label = "Maven Dependency Tree", quote = "🌲 Kotlin script to list transitive dependencies of Maven artifacts"),
        Entry(author = "SimonMarquis", name = "svg2avd", label = "svg2avd", quote = "⚙️ Kotlin script to convert SVG files to AVD"),
    ),
    GitHubAction to listOf(
        Entry(author = "SimonMarquis", name = "android-accept-licenses", label = "Android accept licenses", quote = "🤖 Accept Android licenses with `sdkmanager`", packages = mapOf(GitHubMarketplace to "android-accept-licenses", GitHubRelease to null)),
        Entry(author = "SimonMarquis", name = "ci-gradle-properties-action", label = "CI Gradle properties", quote = "🐘 Copy Gradle properties file to the CI's home directory", packages = mapOf(GitHubMarketplace to "setup-gradle-properties-file", GitHubRelease to null)),
    ),
    Misc to listOf(
        Entry(author = "SimonMarquis", name = "TIL", label = "Today I Learned", quote = "🗓️ Today I Learned", mapOf(GitHubPages to null)),
        Entry(author = "SimonMarquis", name = "Firebase-Remote-Config-Changes-Notifier", label = "Firebase Remote Config changes notifier", quote = "🔥 Notify changes in real time through Slack Webhooks"),
        Entry(author = "SimonMarquis", name = "GitHub-Actions-Playground", label = "GitHub Actions Playground", quote = "🛝 Playground for GitHub Actions"),
        Entry(author = "SimonMarquis", name = "Android-Version-Distribution", label = "📊 Android Version Distribution"),
        Entry(author = "SimonMarquis", name = "Firebase-Test-Lab-Devices", label = "📱 Firebase Test Lab Devices"),
    ),
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
                                it.quote?.let { br; i { small { +it } } }
                            }
                        }
                        /* Stars & Forks */
                        td {
                            a("${it.github()}/stargazers") { img(alt = "Stars", src = shield("github/stars/SimonMarquis/${it.name}")) }; br
                            a("${it.github()}/forks") { img(alt = "Forks", src = shield("github/forks/SimonMarquis/${it.name}")) }
                        }
                        /* Issues & PRs */
                        td {
                            a("${it.github()}/issues") { img(alt = "Issues", src = shield("github/issues/SimonMarquis/${it.name}")) }; br
                            a("${it.github()}/pulls") { img(alt = "Pull Requests", src = shield("github/issues-pr/SimonMarquis/${it.name}")) }
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
