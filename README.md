# gruel
A suite of tools for making Gradle builds nice and smooth.

## Features
**Currently, `gruel` can add tasks to your gradle build to do the following:**

- Notify a HipChat channel with a specific message.
- Transition Jira issues matching a JQL filter to another status and update fields. This is useful in automating the transition to the appropriate QA status when an alpha build is ready for testing.
- Bump version numbers in a `gradle.properties` file to conform with semantic versioning.
- Specify a final archive pattern for Java (JAR) and Android (APK) archives.
- Upload an Android Package (APK) file to a distribution mechanism for release, with parameters dependent on product flavor.
  - Currently, Crashlytics is the only supported distribution mechanism. If you're interested in other distribution mechanisms, please <a href="http://www.github.com/jwir3/gruel/issues/new">file an issue</a>.

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

## Jira Integration
In order to use Jira integrations, you will need to specify the following closure
in your `build.gradle` file:
```
  jira {
    rootUrl = <YOUR JIRA ROOT URL, e.g. https://mycompany.atlassian.net>
    username = <YOUR JIRA USERNAME>
    password = <YOUR JIRA PASSWORD>
  }
```

All Jira tasks will be run using this configuration.

### Jira Issue Manipulations
You can manipulate Jira issues using the task `JiraUpdateTask`. Any assignable
field (custom or built-in) can be manipulated using the `fieldUpdates` variable.

The following task sets the `assignee` field to empty (i.e. "Unassigned") for
all issues that match the given `jql` filter:
```groovy
task jiraUpdate(type: com.glasstowerstudios.gruel.tasks.jira.JiraUpdateTask) {
  jql = 'project = "PROJECT_NAME" AND status = "CURRENT_STATUS"'
  fieldUpdates = ['assignee': '']
}
```

Now, to run this task, you can run:
```bash
$ gradle jiraUpdate
```

### Jira Issue Transitions
You can also transition Jira issues from one state to another using gruel. Since
the issue state isn't actually a field, but rather metadata than needs to be set
in a different way, the `JiraTransitionTask` allows you to indicate, in your
build script, that you want to transition an issue from one state to another.

The transition must be a legal transition, as specified in your Jira workflow.

You may also modify the issue with any of the field update parameters specified
by Jira Issue Manipulations (above).

Create a task with the following template:
```groovy
task jiraTransition(type: com.glasstowerstudios.gruel.tasks.jira.JiraTransitionTask) {
  jql = 'project = "PROJECT_NAME" AND status = "CURRENT_STATUS"'
  toStatus = 'NEW_STATUS'
  fieldUpdates = ['customfield_123': 'Updating a custom field', 'customfield_456': 'Updating another custom field']
}
```

Now, to run this task, you can run:
```bash
$ gradle jiraTransition
```

## Github Integration

In order to use Github integrations, you will need to specify the following closure in your `build.gradle` file:
```
  github {
    repositoryName = <github project name, e.g.
    username = <YOUR GITHUB USERNAME>
    password = <YOUR GITHUB PASSWORD>
    auth_token = <YOUR GITHUB PERSONAL OAUTH TOKEN>
  }
```
You only need to provide _either_ a username/password combination, or an authentication token. A personal access token, generated from http://github.com/settings/profile is fine.

All Github tasks will be run using this configuration.

Currently, there are two tasks you can use with the Github extension (you can add more - pull requests are accepted).

### Removing Labels
You can use the `RemoveLabelsTask` to remove labels from Github upon a certain action:

```
task removeDevelopingLabel (type: com.glasstowerstudios.gruel.tasks.github.RemoveLabelsTask) {
  issueNumber = 20
  labels = ['developing']
}
```

### Setting Assignee
The `ChangeAssigneeTask` allows you to change the assignee of a particular Github issue:

```
task removeAssignee (type: com.glasstowerstudios.gruel.tasks.github.ChangeAssigneeTask) {
  issueNumber = 20
  assignee = ''
}
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
  archiveName "%appName%", "%version%", "%codeName", "%buildType%", "%commitHash%"
  version android.defaultConfig.versionName
  commitHash gitSha()
}
```

The following variables can be defined:

| Variable Name | Description                        | Default Value |
| ------------- | ---------------------------------- | ------------- |
| `%appName%`   | The name of the application.       |  Default module (directory) name |
| `%version%`   | The human-readable version of the application. | `android.defaultConfig.versionName` |
| `%codeName`   | An optional code name for the release. | Empty |
| `%buildType%` | The type of the build (debug or release). | None |
| `%commitHash%`| The unique identifier of the current HEAD commit. | None |
| `%timestamp%` | A timestamp for the release, in ISO-8601 format. | Current date/time, in ISO-8601 format |

Any string that is not prefixed and suffixed with "%" will be treated as a literal, and not replaced by gruel.

Gruel provides a method `gitSha(String tagName)` for you to use to retrieve the hash of a named
`git` commit and a `timestamp()` method to output the current time in ISO 8601 format. You can use
these within the `gruel` configuration by simply calling the function by name, as in the above
example. (`gitSha()` is equivalent to running `gitSha('HEAD')`). If you wish to use these functions
outside of the gruel configuration block, you can invoke them using `gruel.gitSha(tagName)` once the
gruel plugin has been applied.

The output APK will be named to conform to this pattern, in the order of parameters specified.

Adjusting the name of the final output file is pretty easy in gradle when building Java archives (jar files).
It's provided by gruel simply for consistency between standard java and Android applications.

### Android Version Name Specification
In a similar way to setting the output file name using gruel, you can also set the `versionName` property dynamically using gruel:

```
android {
  productFlavors {
    ...
    production {
      ext.gruelVersionName = ["%version%", "%codeName%", "ALPHA"]
    }
  }
}
```

The above script will produce "1.0.0-Xavier-ALPHA" as a version name, if `version` is `1.0.0` and `codeName` is `Xavier`. You can use any variables in the above table to populate your version name.

### Setting up Upload Tasks (Android Only)
You can configure gruel to setup tasks for each of your `productFlavor`s for an Android project. This will allow you to distribute alpha, beta, production, or any other type of build to users using a task from the command line.

Currently, only Crashlytics Beta/Fabric is supported for distributing builds, so in order to configure these tasks, you must first [enable the fabric.io plugin for gradle](https://docs.fabric.io/android/beta/gradle.html). You may want to verify that `crashlyticsUploadDistributionXXXXX`, where `XXXX` is the name of one of your flavors, succeeds before attempting to configure gruel.

Once this is complete, for each of the flavors that you want to configure, simply call the method `gruel.uploadUsing` during the `productFlavor` configuration:
```
android {
  productFlavors {
    alpha {
      gruel.uploadUsing 'crashlytics', 'alpha-testers', 'release', delegate
    }
  }
}
```

After adding this to the `build.gradle` file, you'll be able to run `./gradlew uploadAlpha`, which will assemble the `alpha` flavor's `release` package and then upload it to Crashlytics. Once uploaded, it will distribute it to all members of the 'alpha-testers' group.

The parameters for this method are as follows:

| Type | Parameter Name | Description | Required? | Default Value |
| ---- | -------------- | ----------- | --------- | ------------- |
| String | uploadType | The type of distribution mechanism that should be used to upload this product flavor. Currently, 'crashlytics' is the only supported option, but we anticipate additional options in the future. | Yes | None |
| String | groupName | The name of the group to distribute releases of this type to. This must be setup using the Crashlytics Beta dashboard. You can omit this parameter if you don't want it to distribute to anyone initially. | No | None |
| String | buildType | The type of the build (must be one of the build types specified in the `buildTypes` closure to distribute.) Defaults to `debug`. | No | 'debug' |
| Object | flavor | This is the `productFlavor` instance you are configuring. Due to the semantics of the Gradle DSL, you typically want to specify `delegate` here. If you are configuring this outside of the `productFlavors` closure, then you will need to specify the full object (e.g. `project.android.productFlavors.alpha`). | Yes | None |

### The 'uninstall' Task
For java and android libraries for which a maven distribution mechanism is specified, gruel will add an 'uninstall' task.
This task will remove your library project from the _local_ maven repository (not versions pushed to a remote server).
This allows you to run `./gradlew install` on the library project to test the library locally, and then use
`./gradlew uninstall` to remove it (if you want to pull the library from local storage so it retrieves the dependency from,
  e.g. your snapshot repository).

## FAQ
- __Where did the project name come from?__
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
