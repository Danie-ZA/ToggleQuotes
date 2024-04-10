# ToggleQuotes Plugin

[![official JetBrains project](https://jb.gg/badges/official.svg)][jb:github]
[![Build](https://github.com/JetBrains/intellij-platform-plugin-template/workflows/Build/badge.svg)][gh:build]

<!-- Plugin description -->
**ToggleQuotes** plugin allows you to convert single quotes (') to double quotes ("), or vice versa, for highlighted text in the editor, using CMD + QUOTE/CTRL + QUOTE.

> **Note**
> 
> Click the <kbd>Watch</kbd> button on the top to be notified about releases containing new features and fixes.

### Table of contents

In this README, we will highlight the following elements of template-project creation:

- [Getting started](#getting-started)
- [Gradle configuration](#gradle-configuration)
- [Plugin template structure](#plugin-template-structure)
- [Plugin configuration file](#plugin-configuration-file)
- [Sample code](#sample-code):
  - listeners – project lifecycle listener
  - services – project and application-level services
- [Testing](#testing)
  - [Functional tests](#functional-tests)
  - [Code coverage](#code-coverage)
  - [UI tests](#ui-tests)
- [Qodana integration](#qodana-integration)
- [Predefined Run/Debug configurations](#predefined-rundebug-configurations)
- [Continuous integration](#continuous-integration) based on GitHub Actions
  - [Dependencies management](#dependencies-management) with Dependabot
  - [Changelog maintenance](#changelog-maintenance) with the Gradle Changelog Plugin
  - [Release flow](#release-flow) using GitHub Releases
  - [Plugin signing](#plugin-signing) with your private certificate
  - [Publishing the plugin](#publishing-the-plugin) with the Gradle IntelliJ Plugin
- [FAQ](#faq)
- [Useful links](#useful-links)


## Getting started

Before we dive into plugin development and everything related to it, it's worth mentioning the benefits of using GitHub Templates.
By creating a new project using the current template, you start with no history or reference to this repository.
This allows you to create a new repository easily without copying and pasting previous content, clone repositories, or clearing the history manually.

All you have to do is click the <kbd>Use this template</kbd> button (you must be logged in with your GitHub account).

![Use this template][file:use-this-template.png]

After using the template to create your blank project, the [Template Cleanup][file:template_cleanup.yml] workflow will be triggered to override or remove any template-specific configurations, such as the plugin name, current changelog, etc.
Once this is complete, the project is ready to be cloned to your local environment and opened with [IntelliJ IDEA][jb:download-ij].

The most convenient way for getting your new project from GitHub is the <kbd>Get from VCS</kbd> action available on the Welcome Screen, where you can filter your GitHub  repository by its name.

![Get from Version Control][file:get-from-version-control]

The next step, after opening your project in IntelliJ IDEA, is to set the proper <kbd>SDK</kbd> to Java in version `17` within the [Project Structure settings][docs:project-structure-settings].

![Project Structure — SDK][file:project-structure-sdk.png]

For the last step, you have to manually review the configuration variables described in the [`gradle.properties`][file:gradle.properties] file and *optionally* move sources from the *com.github.username.repository* package to the one that works best for you.
Then you can get to work implementing your ideas.

> **Note**
> 
> To use Java in your plugin, create the `/src/main/java` directory.


## Gradle configuration

The recommended method for plugin development involves using the [Gradle][gradle] setup with the [gradle-intellij-plugin][gh:gradle-intellij-plugin] installed.
The `gradle-intellij-plugin` makes it possible to run the IDE with your plugin and publish your plugin to JetBrains Marketplace.

> **Note**
> 
> Make sure to always upgrade to the latest version of `gradle-intellij-plugin`.

A project built using the IntelliJ Platform Plugin Template includes a Gradle configuration already set up.
Feel free to read through the [Using Gradle][docs:using-gradle] articles to understand your build better and learn how to customize it.

The most significant parts of the current configuration are:
- Integration with the [gradle-intellij-plugin][gh:gradle-intellij-plugin] for smoother development.
- Configuration written with [Gradle Kotlin DSL][gradle:kotlin-dsl].
- Support for Kotlin and Java implementation.
- Integration with the [gradle-changelog-plugin][gh:gradle-changelog-plugin], which automatically patches the change notes based on the `CHANGELOG.md` file.
- [Plugin publishing][docs:publishing] using the token.

For more details regarding Kotlin integration, please see [Kotlin for Plugin Developers][docs:kotlin] in the IntelliJ Platform Plugin SDK documentation.

### Gradle properties

The project-specific configuration file [`gradle.properties`][file:gradle.properties] contains:

| Property name         | Description                                                                                               |
|-----------------------|-----------------------------------------------------------------------------------------------------------|
| `pluginGroup`         | Package name - after *using* the template, this will be set to `com.github.username.repo`.                |
| `pluginName`          | Plugin name displayed in JetBrains Marketplace.                                                           |
| `pluginRepositoryUrl` | Repository URL used for generating URLs by the [Gradle Changelog Plugin][gh:gradle-changelog-plugin]      |
| `pluginVersion`       | The current version of the plugin in [SemVer][semver] format.                                             |
| `pluginSinceBuild`    | The `since-build` attribute of the `<idea-version>` tag.                                                  |
| `pluginUntilBuild`    | The `until-build` attribute of the `<idea-version>` tag.                                                  |
| `platformType`        | The type of IDE distribution.                                                                             |
| `platformVersion`     | The version of the IntelliJ Platform IDE will be used to build the plugin.                                |
| `platformPlugins`     | Comma-separated list of dependencies to the bundled IDE plugins and plugins from the Plugin Repositories. |
| `gradleVersion`       | Version of Gradle used for plugin development.                                                            |

The properties listed define the plugin itself or configure the [gradle-intellij-plugin][gh:gradle-intellij-plugin] – check its documentation for more details.

In addition, extra behaviors are configured through the [`gradle.properties`][file:gradle.properties] file, such as:

| Property name                                    | Value   | Description                                                                                    |
|--------------------------------------------------|---------|------------------------------------------------------------------------------------------------|
| `kotlin.stdlib.default.dependency`               | `false` | Opt-out flag for bundling [Kotlin standard library][docs:kotlin-stdlib]                        |
| `org.gradle.configuration-cache`                 | `true`  | Enable [Gradle Configuration Cache][gradle:configuration-cache]                                |
| `org.gradle.caching`                             | `true`  | Enable [Gradle Build Cache][gradle:build-cache]                                                |
| `systemProp.org.gradle.unsafe.kotlin.assignment` | `true`  | Enable [Gradle Kotlin DSL Lazy Property Assignment][gradle:kotlin-dsl-assignment]              |

### Environment variables

Some values used for the Gradle configuration shouldn't be stored in files to avoid publishing them to the Version Control System.

To avoid that, environment variables are introduced, which can be provided within the *Run/Debug Configuration* within the IDE, or on the CI – like for GitHub: `⚙️ Settings > Secrets`.

Environment variables used by the current project are related to the [plugin signing](#plugin-signing) and [publishing](#publishing-the-plugin).

| Environment variable name | Description                                                                                                  |
|---------------------------|--------------------------------------------------------------------------------------------------------------|
| `PRIVATE_KEY`             | Certificate private key, should contain: `-----BEGIN RSA PRIVATE KEY----- ... -----END RSA PRIVATE KEY-----` |
| `PRIVATE_KEY_PASSWORD`    | Password used for encrypting the certificate file.                                                           |
| `CERTIFICATE_CHAIN`       | Certificate chain, should contain: `-----BEGIN CERTIFICATE----- ... -----END CERTIFICATE----`                |
| `PUBLISH_TOKEN`           | Publishing token generated in your JetBrains Marketplace profile dashboard.                                  |

For more details on how to generate proper values, check the relevant sections mentioned above.

To configure GitHub secret environment variables, go to the `⚙️ Settings > Secrets` section of your project repository:

![Settings > Secrets][file:settings-secrets.png]

## Testing

[Testing plugins][docs:testing-plugins] is an essential part of the plugin development to make sure that everything works as expected between IDE releases and plugin refactorings.
The IntelliJ Platform Plugin Template project provides integration of two testing approaches – functional and UI tests.

### Functional tests

Most of the IntelliJ Platform codebase tests are model-level, run in a headless environment using an actual IDE instance.
The tests usually test a feature as a whole rather than individual functions that comprise its implementation, like in unit tests.

In `src/test/kotlin`, you'll find a basic `MyPluginTest` test that utilizes `BasePlatformTestCase` and runs a few checks against the XML files to indicate an example operation of creating files on the fly or reading them from `src/test/testData/rename` test resources.

> **Note**
> 
> Run your tests using predefined *Run Tests* configuration or by invoking the `./gradlew check` Gradle task.

### Code coverage

The [Kover][gh:kover] – a Gradle plugin for Kotlin code coverage agents: IntelliJ and JaCoCo – is integrated into the project to provide the code coverage feature.
Code coverage makes it possible to measure and track the degree of plugin sources testing.
The code coverage gets executed when running the `check` Gradle task.
The final test report is sent to [CodeCov][codecov] for better results visualization.

### UI tests

If your plugin provides complex user interfaces, you should consider covering them with tests and the functionality they utilize.

[IntelliJ UI Test Robot][gh:intellij-ui-test-robot] allows you to write and execute UI tests within the IntelliJ IDE running instance.
You can use the [XPath query language][xpath] to find components in the currently available IDE view.
Once IDE with `robot-server` has started, you can open the `http://localhost:8082` page that presents the currently available IDEA UI components hierarchy in HTML format and use a simple `XPath` generator, which can help test your plugin's interface.

> **Note**
> 
> Run IDE for UI tests using predefined *Run IDE for UI Tests* and then *Run Tests* configurations or by invoking the `./gradlew runIdeForUiTests` and `./gradlew check` Gradle tasks.

Check the UI Test Example project you can use as a reference for setting up UI testing in your plugin: [intellij-ui-test-robot/ui-test-example][gh:ui-test-example].

```kotlin
class MyUITest {

  @Test
  fun openAboutFromWelcomeScreen() {
    val robot = RemoteRobot("http://127.0.0.1:8082")
    robot.find<ComponentFixture>(byXpath("//div[@myactionlink = 'gearHover.svg']")).click()
    // ...
  }
}
```

![UI Testing][file:ui-testing.png]

A dedicated [Run UI Tests](.github/workflows/run-ui-tests.yml) workflow is available for manual triggering to run UI tests against three different operating systems: macOS, Windows, and Linux.
Due to its optional nature, this workflow isn't set as an automatic one, but this can be easily achieved by changing the `on` trigger event, like in the [Build](.github/workflows/build.yml) workflow file.

## Qodana integration

To increase the project value, the IntelliJ Platform Plugin Template got integrated with [Qodana][jb:qodana], a code quality monitoring platform that allows you to check the condition of your implementation and find any possible problems that may require enhancing.

Qodana brings into your CI/CD pipelines all the smart features you love in the JetBrains IDEs and generates an HTML report with the actual inspection status.

Qodana inspections are accessible within the project on two levels:

- using the [Qodana IntelliJ GitHub Action][jb:qodana-github-action], run automatically within the [Build](.github/workflows/build.yml) workflow,
- with the [Gradle Qodana Plugin][gh:gradle-qodana-plugin], so you can use it on the local environment or any CI other than GitHub Actions.

Qodana inspection is configured with the `qodana { ... }` section in the Gradle build file and [`qodana.yml`][file:qodana.yml] YAML configuration file.

> **Note**
> 
> Qodana requires Docker to be installed and available in your environment.

To run inspections, you can use a predefined *Run Qodana* configuration, which will provide a full report on `http://localhost:8080`, or invoke the Gradle task directly with the `./gradlew runInspections` command.

A final report is available in the `./build/reports/inspections/` directory.

![Qodana][file:qodana.png]


## Predefined Run/Debug configurations

Within the default project structure, there is a `.run` directory provided containing predefined *Run/Debug configurations* that expose corresponding Gradle tasks:

![Run/Debug configurations][file:run-debug-configurations.png]

| Configuration name   | Description                                                                                                                                                                   |
|----------------------|-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Run Plugin           | Runs [`:runIde`][gh:gradle-intellij-plugin-runIde] Gradle IntelliJ Plugin task. Use the *Debug* icon for plugin debugging.                                                    |
| Run Verifications    | Runs [`:runPluginVerifier`][gh:gradle-intellij-plugin-runPluginVerifier] Gradle IntelliJ Plugin task to check the plugin compatibility against the specified IntelliJ IDEs.   |
| Run Tests            | Runs [`:test`][gradle:lifecycle-tasks] Gradle task.                                                                                                                           |
| Run IDE for UI Tests | Runs [`:runIdeForUiTests`][gh:intellij-ui-test-robot] Gradle IntelliJ Plugin task to allow for running UI tests within the IntelliJ IDE running instance.                     |
| Run Qodana           | Runs [`:runInspections`][gh:gradle-qodana-plugin] Gradle Qodana Plugin task. Starts Qodana inspections in a Docker container and serves generated report on `localhost:8080`. |

> **Note**
> 
> You can find the logs from the running task in the `idea.log` tab.
>
> ![Run/Debug configuration logs][file:run-logs.png]


## Continuous integration

Continuous integration depends on [GitHub Actions][gh:actions], a set of workflows that make it possible to automate your testing and release process.
Thanks to such automation, you can delegate the testing and verification phases to the Continuous Integration (CI) and instead focus on development (and writing more tests).

In the `.github/workflows` directory, you can find definitions for the following GitHub Actions workflows:

- [Build](.github/workflows/build.yml)
  - Triggered on `push` and `pull_request` events.
  - Runs the *Gradle Wrapper Validation Action* to verify the wrapper's checksum.
  - Runs the `verifyPlugin` and `test` Gradle tasks.
  - Builds the plugin with the `buildPlugin` Gradle task and provides the artifact for the next jobs in the workflow.
  - Verifies the plugin using the *IntelliJ Plugin Verifier* tool.
  - Prepares a draft release of the GitHub Releases page for manual verification.
- [Release](.github/workflows/release.yml)
  - Triggered on `released` event.
  - Updates `CHANGELOG.md` file with the content provided with the release note.
  - Signs the plugin with a provided certificate before publishing.
  - Publishes the plugin to JetBrains Marketplace using the provided `PUBLISH_TOKEN`.
  - Sets publish channel depending on the plugin version, i.e. `1.0.0-beta` -> `beta` channel.
  - Patches the Changelog and commits.
- [Run UI Tests](.github/workflows/run-ui-tests.yml)
  - Triggered manually.
  - Runs for macOS, Windows, and Linux separately.
  - Runs `runIdeForUiTests` and `test` Gradle tasks.
- [Template Cleanup](.github/workflows/template-cleanup.yml)
  - Triggered once on the `push` event when a new template-based repository has been created.
  - Overrides the scaffold with files from the `.github/template-cleanup` directory.
  - Overrides JetBrains-specific sentences or package names with ones specific to the target repository.
  - Removes redundant files.

All the workflow files have accurate documentation, so it's a good idea to take a look through their sources.

### Dependencies management

This Template project depends on Gradle plugins and external libraries – and during the development, you will add more of them.

All plugins and dependencies used by Gradle are managed with [Gradle version catalog][gradle:version-catalog], which defines versions and coordinates of your dependencies in the [`gradle/libs.versions.toml`][file:libs.versions.toml] file.

> **Note**
>
> To add a new dependency to the project, in the `dependencies { ... }` block, add:
> 
> ```kotlin
> dependencies {
>   implementation(libs.annotations)
> }
> ```
> 
> and define the dependency in the [`gradle/libs.versions.toml`][file:libs.versions.toml] file as follows:
> ```toml
> [versions]
> annotations = "24.0.1"
> 
> [libraries]
> annotations = { group = "org.jetbrains", name = "annotations", version.ref = "annotations" }
> ```

Keeping the project in good shape and having all the dependencies up-to-date requires time and effort, but it is possible to automate that process using [Dependabot][gh:dependabot].

Dependabot is a bot provided by GitHub to check the build configuration files and review any outdated or insecure dependencies of yours – in case if any update is available, it creates a new pull request providing [the proper change][gh:dependabot-pr].

> **Note**
> 
> Dependabot doesn't yet support checking of the Gradle Wrapper.
> Check the [Gradle Releases][gradle:releases] page and update your `gradle.properties` file with:
> ```properties
> gradleVersion = ...
> ```
> and run
> ```bash
> ./gradlew wrapper
> ```

### Changelog maintenance

When releasing an update, it is essential to let your users know what the new version offers.
The best way to do this is to provide release notes.

The changelog is a curated list that contains information about any new features, fixes, and deprecations.
When they're provided, these lists are available in a few different places:
- the [CHANGELOG.md](./CHANGELOG.md) file,
- the [Releases page][gh:releases],
- the *What's new* section of JetBrains Marketplace Plugin page,
- and inside the Plugin Manager's item details.

There are many methods for handling the project's changelog.
The one used in the current template project is the [Keep a Changelog][keep-a-changelog] approach.

The [Gradle Changelog Plugin][gh:gradle-changelog-plugin] takes care of propagating information provided within the [CHANGELOG.md](./CHANGELOG.md) to the [Gradle IntelliJ Plugin][gh:gradle-intellij-plugin].
You only have to take care of writing down the actual changes in proper sections of the `[Unreleased]` section.

You start with an almost empty changelog:

```
# YourPlugin Changelog

## [Unreleased]
### Added
- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)
```

Now proceed with providing more entries to the `Added` group, or any other one that suits your change the most (see [How do I make a good changelog?][keep-a-changelog-how] for more details).

When releasing a plugin update, you don't have to care about bumping the `[Unreleased]` header to the upcoming version – it will be handled automatically on the Continuous Integration (CI) after you publish your plugin.
GitHub Actions will swap it and provide you an empty section for the next release so that you can proceed with your development:

```
# YourPlugin Changelog

## [Unreleased]

## [0.0.1]
### Added
- An awesome feature
- Initial scaffold created from [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template)

### Fixed
- One annoying bug
```

To configure how the Changelog plugin behaves, i.e., to create headers with the release date, see [Gradle Changelog Plugin][gh:gradle-changelog-plugin] README file.

### Release flow

The release process depends on the workflows already described above.
When your main branch receives a new pull request or a direct push, the [Build](.github/workflows/build.yml) workflow runs multiple tests on your plugin and prepares a draft release.

![Release draft][file:draft-release.png]

The draft release is a working copy of a release, which you can review before publishing.
It includes a predefined title and git tag, the current plugin version, for example, `v0.0.1`.
The changelog is provided automatically using the [gradle-changelog-plugin][gh:gradle-changelog-plugin].
An artifact file is also built with the plugin attached.
Every new Build overrides the previous draft to keep your *Releases* page clean.

When you edit the draft and use the <kbd>Publish release</kbd> button, GitHub will tag your repository with the given version and add a new entry to the Releases tab.
Next, it will notify users who are *watching* the repository, triggering the final [Release](.github/workflows/release.yml) workflow.

### Plugin signing

Plugin Signing is a mechanism introduced in the 2021.2 release cycle to increase security in [JetBrains Marketplace](https://plugins.jetbrains.com) and all of our IntelliJ-based IDEs.

JetBrains Marketplace signing is designed to ensure that plugins aren't modified over the course of the publishing and delivery pipeline.

The current project provides a predefined plugin signing configuration that lets you sign and publish your plugin from the Continuous Integration (CI) and local environments.
All the configuration related to the signing should be provided using [environment variables](#environment-variables).

To find out how to generate signing certificates, check the [Plugin Signing][docs:plugin-signing] section in the IntelliJ Platform Plugin SDK documentation.

> **Note**
>
> Remember to encode your secret environment variables using `base64` encoding to avoid issues with multi-line values.

### Publishing the plugin

Releasing a plugin to JetBrains Marketplace is a straightforward operation that uses the `publishPlugin` Gradle task provided by the [gradle-intellij-plugin][gh:gradle-intellij-plugin-docs].
In addition, the [Release](.github/workflows/release.yml) workflow automates this process by running the task when a new release appears in the GitHub Releases section.

> **Note**
> 
> Set a suffix to the plugin version to publish it in the custom repository channel, i.e. `v1.0.0-beta` will push your plugin to the `beta` [release channel][docs:release-channel].

The authorization process relies on the `PUBLISH_TOKEN` secret environment variable, specified in the _Secrets_ section of the repository _Settings_.

You can get that token in your JetBrains Marketplace profile dashboard in the [My Tokens][jb:my-tokens] tab.

> **Warning**
> 
> Before using the automated deployment process, it is necessary to manually create a new plugin in JetBrains Marketplace to specify options like the license, repository URL, etc.
> Please follow the [Publishing a Plugin][docs:publishing] instructions.

## FAQ

### How to disable *tests* or *build* job using the `[skip ci]` commit message?

Since February 2021, GitHub Actions [support the skip CI feature][github-actions-skip-ci].
If the message contains one of the following strings: `[skip ci]`, `[ci skip]`, `[no ci]`, `[skip actions]`, or `[actions skip]` – workflows will not be triggered.

### Why draft release no longer contains built plugin artifact?

All the binaries created with each workflow are still available, but as an output artifact of each run together with tests and Qodana results.
That approach gives more possibilities for testing and debugging pre-releases, for example, in your local environment.
