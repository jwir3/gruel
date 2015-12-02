# gruel
A suite of tools for making Gradle builds nice and smooth.

## Features
**Currently, `gruel` adds tasks to your gradle build to do the following:**

- Notify a HipChat channel with a specific message.
- Bump version numbers in a `gradle.properties` file to conform with semantic versioning.
- Specify a final archive pattern for Java (JAR) and Android (APK) archives.

## Installation
Currently, `gruel` is available on the maven central staging repository. Keep in mind, it's in an alpha stage, meaning that it is somewhat unstable yet.

To enable the maven central staging repository, add the following to your `build.gradle` file:
```groovy
buildscript {
    repositories {
        maven { url "https://oss.sonatype.org/content/groups/staging" }
    }
}

```

Then, to use `gruel`, add the following to the same file:
```groovy
buildscript {
    dependencies {
        classpath 'com.glasstowerstudios.gruel:gruel:0.0.1-SNAPSHOT'
    }
}
```

## Usage

To enable `gruel` features, add the following to your `build.gradle` file:
```groovy
apply plugin: 'gruel'
```

### HipChat Notifications
You will need to enable integrations (and retrieve an integration auth token) for the HipChat room you wish to notify. To do this, within HipChat, click the three dots in the upper right hand corner of the channel you wish to use, then select 'Integrations'. This will take you to a web page, where, you'll have to log in, and then you'll be able to create a new auth key for the channel in question.

Once complete, add the following to your `build.gradle` file:
```groovy
hipchat {
  auth_token = 'THE AUTH TOKEN YOU JUST CREATED'
}
```

Finally, you can create a task that utilizes this with the following template:
```groovy
task notifyHipchat(type: com.glasstowerstudios.gruel.tasks.hipchat.HipChatNotificationTask) {
  // Can be one of: ['purple', 'red', 'green', 'yellow', 'gray', 'random']
  color = 'yellow'
  message = "Your message here. You can include simple html"
  channelName = "Name of channel to notify"
}
```

Now, to run this task, you can run:
```bash
$ gradle notifyHipchat
```

### Bumping Versions
The `bumpVersion` task is already added for you. It assumes that you have the following properties in your `gradle.properties` file:
```groovy
// Specifies an integer that is monotonically increasing with each
// new version.
PROJECTNAME_VERSION_CODE

// Specifies a human-readable string that is used to represent a
// version number to users (e.g. "3.0.0").
PROJECTNAME_VERSION_NAME
```

If these properties are not specified, `gruel` will create them on the first run of the `bumpVersion` task.

The `PROJECTNAME` prefix is an all-caps version of your project name. For example, if we were working with the project named `gruel`, the version code property would be: `GRUEL_VERSION_CODE`.

The version code property does not need to be used for non-Android projects. It's simply provided because it is required for Android builds.

The `bumpVersion` task will increase both the version name property, as well as the version code property. To explicitly specify a new version name you can run the task with the `--versionName` option:
```bash
$ cat gradle.properties
TESTAPP_VERSION_CODE=1
TESTAPP_VERSION_NAME=1.0.0

$ gradle bumpVersion --versionName "3.0.0"
$ cat gradle.properties
TESTAPP_VERSION_CODE=2
TESTAPP_VERSION_NAME=3.0.0
```

If you want to bump the major, minor, or patch versions incrementally, you can use these options, as well:
```bash
$ cat gradle.properties
TESTAPP_VERSION_CODE=1
TESTAPP_VERSION_NAME=1.0.0

$ gradle bumpVersion --major
$ cat gradle.properties
TESTAPP_VERSION_CODE=11
TESTAPP_VERSION_NAME=2.0.0

$ gradle bumpVersion --minor
$ cat gradle.properties
TESTAPP_VERSION_CODE=21
TESTAPP_VERSION_NAME=2.1.0

$ gradle bumpVersion --patch
$ cat gradle.properties
TESTAPP_VERSION_CODE=22
TESTAPP_VERSION_NAME=2.1.1
```

Notice that the `--major` and `--minor` version bumps also bump the version code by a factor of 10, allowing room for hotfix releases between these versions.

There are other useful options for the `bumpVersion` task. You can read about them with `gradle help --task bumpVersion`.

### Naming Build Outputs
Adjusting the name of the final output file for Android builds is somewhat complicated. Instead of adding
a bunch of boilerplate code to your `build.gradle` file for each Android project you have, you can
include gruel and specify a pattern:
```groovy
apply plugin: 'gruel'

gruel {
  archiveName "%appName%", "%version%", "%buildType%", "%commitHash%"
  version android.defaultConfig.versionName
  commitHash gitSha()
}
```

The following variables can be defined:

| Variable Name | Description                        | Default Value |
| ------------- | ---------------------------------- | ------------- |
| `%appName%`   | The name of the application.       |  Default module (directory) name |
| `%version%`   | The human-readable version of the application. | `android.defaultConfig.versionName` |
| `%buildType%` | The type of the build (debug or release). | None |
| `%commitHash%`| The unique identifier of the current HEAD commit. | None |

Gruel provides a method `gitSha(String tagName)` for you to use to retrieve the hash of a named
`git` commit. You can use it within the `gruel` configuration by simply calling the function by name,
as in the above example. (`gitSha()` is equivalent to running `gitSha('HEAD')`). If you wish to use this function outside of the gruel configuration block, you can invoke it using `gruel.gitSha(tagName)` once the gruel plugin has been applied.

The output APK will be named to conform to this pattern, in the order of parameters specified.

Adjusting the name of the final output file is pretty easy in gradle when building Java archives (jar files).
It's provided by gruel simply for consistency between standard java and Android applications.

## FAQ
- __Where did the name come from?__
  - The name actually had several iterations. Since it's a suite of tools that can be used immediately from the start of a project, it was called 'cradle', but this didn't seem incredibly descriptive, so it then was changed to 'gradlecradle'. This seemed too long, so the name was changed to 'grools', from "gradle" + "tools". From here, "gruel" seemed better, since it helps smooth out the process of starting a project, and because we didn't want to confuse it with "drools" (a business rules management system).

- __I'm getting an error similar to the following:__

  ```
  * What went wrong:
  A problem was found with the configuration of task ':app:zipalignDebug'.
  > File '/somepath/myproject/app/build/outputs/apk/rucksack-debug-unaligned.apk' specified for property 'inputFile' does not exist.

  * Try:
  Run with --stacktrace option to get the stack trace. Run with   --info or --debug option to get more log output.

  BUILD FAILED
  ```
  - This is a bug in the Android gradle plugin v1.3.0. Don't use this version. Instead, use version 1.3.1. Change the following line:

    ```
      classpath 'com.android.tools.build:gradle:1.3.0'

    ```
  to:

    ```
    classpath 'com.android.tools.build:gradle:1.3.1'
    ```
