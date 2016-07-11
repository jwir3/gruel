package com.glasstowerstudios.gruel.extensions

import org.gradle.api.Project
import org.gradle.api.Task;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.UnknownTaskException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Plugin extension file. This allows specific parameters to be defined in the
 * plugin itself. (Think of it like mini-plugins within the Gruel plugin).
 *
 * This plugin extension is designed for main gruel functionality.
 */
class GruelExtension {
    Project project;

    List<String> patternsToInclude;
    String separator = OutputNameBuilder.DEFAULT_SEPARATOR;
    String appName;
    String version;
    String buildType;
    String commitHash;
    String timestamp;
    String codeName;

    boolean willAdjustOutputSettings = false;

    // The timestamp is cached from the first time it's used in a build so
    // it doesn't change throughout the build.
    String cachedTimestamp;

    /**
     * A builder for output name patterns.
     */
    class OutputNameBuilder {
        private static final String DEFAULT_SEPARATOR = "-";
        private static final String APP_NAME = "%appName%";
        private static final String VERSION = "%version%";
        private static final String BUILD_TYPE = "%buildType%";
        private static final String COMMIT_HASH = "%commitHash%";
        private static final String TIMESTAMP = "%timestamp%";
        private static final String CODE_NAME = "%codeName%";

        List<String> mPatterns;

        OutputNameBuilder(List<String> patterns) {
          mPatterns = new ArrayList<>();
          for (String nextPattern : patterns) {
            mPatterns.add(nextPattern);
          }
        }

        public OutputNameBuilder apply(String pattern, value) {
          def foundPattern = -1;
          for (int i = 0; i < mPatterns.size(); i++) {
            if (mPatterns[i].equals(pattern)) {
                foundPattern = i;
                break;
            }
          }

          if (foundPattern >= 0) {
            if (value == null) {
              // Then replace the pattern with an empty string
              mPatterns.remove(foundPattern);
              return;
            }

            String strVal = value.toString();
            if (!strVal.isEmpty()) {
              def effectiveSeparator = foundPattern == 0 ? "" : separator;
              mPatterns[foundPattern] = effectiveSeparator + strVal;
            }
          }

          return this;
        }

        public OutputNameBuilder applyCodeName(aCodeName) {
          if (aCodeName == null) {
            aCodeName = "";
          }

          return apply(CODE_NAME, aCodeName);
        }

        public OutputNameBuilder applyAppName(aAppName) {
          if (aAppName == null) {
            aAppName = project.getName();
          }

          return apply(APP_NAME, aAppName);
        }

        public OutputNameBuilder applyVersion(aVersion) {
          if (aVersion == null) {
            aVersion = project.getVersion();
          }

          return apply(VERSION, aVersion);
        }

        public OutputNameBuilder applyBuildType(aBuildType) {
          return apply(BUILD_TYPE, aBuildType);
        }

        public OutputNameBuilder applyCommitHash(aCommitHash) {
          return apply(COMMIT_HASH, aCommitHash);
        }

        public OutputNameBuilder applyTimestamp(aFormattedTimestamp) {
          if (timestamp == null) {
            timestamp = timestamp();
          }

          return apply(TIMESTAMP, aFormattedTimestamp);
        }

        public OutputNameBuilder applyNonVariableData() {
          for (String next : mPatterns) {
            if (!next.startsWith("%") && !next.endsWith("%")) {
              apply(next, next);
            }
          }

          return this;
        }

        public String build() {
          StringBuilder archiveName = new StringBuilder();
          for (String next : mPatterns) {
            if (!next.startsWith("%") && !next.endsWith("%")) {
              archiveName.append(next);
            }
          }

          String finalName = archiveName.toString();
          if (finalName.startsWith(separator)) {
            finalName = finalName.substring(1, finalName.size());
          }

          return finalName;
        }
    }

    /**
     * Set the archive name for the final output archive.
     *
     * The archive name will be set in the order that the parameters are given
     * to this method.
     *
     * @param aPattern An array of String objects, each of which conforming to
     *        a pattern that can be used to add an item to the final output
     *        file name.
     */
    public archiveName(String[] aPattern) {
      if (aPattern.length != 0) {
        this.willAdjustOutputSettings = true;
      }

      patternsToInclude = new ArrayList<String>();
      for(String next : aPattern) {
        patternsToInclude.add(next);
      }
    }

    /**
     * Determine if Gruel should adjust archive output settings.
     *
     * @return true, if the build configuration has specified a new archive
     *         naming pattern; false, otherwise.
     */
    public shouldAdjustOutputSettings() {
      return this.willAdjustOutputSettings;
    }

    /**
     * Adjust the version name of a given android project.
     *
     * @param aProject The project for which to adjust the version name.
     */
    public adjustVersionNameSettings(Project aProject) {
      if (aProject.hasProperty("android")) {
        aProject.android.applicationVariants.all { variant ->
          if (variant.productFlavors != null
              && variant.productFlavors[0] != null
              && variant.productFlavors[0].ext.has("gruelVersionName")) {
            // aProject.android.buildTypes.each { type ->
              List<String> versionNamePatterns = variant.productFlavors.get(0).ext.gruelVersionName;
              OutputNameBuilder bldr = new OutputNameBuilder(versionNamePatterns);
              bldr.applyNonVariableData();
              bldr.applyAppName(appName)
              bldr.applyVersion(version)
              bldr.applyCodeName(codeName);
              bldr.applyCommitHash(commitHash)
              bldr.applyBuildType(variant.getBaseName())
              bldr.applyTimestamp(timestamp);

              // println "Applying : " + bldr.build() + " as version name suffix for variant: " + variant

              variant.mergedFlavor.versionName = bldr.build();
            // }
          }
        }
      }
    }
    /**
     * Adjust the name of the output file for a given project.
     *
     * @param aProject The project for which to adjust the output file name.
     */
    public adjustOutputSettings(Project aProject) {
      project = aProject;

      if (project.hasProperty('jar')) {
        OutputNameBuilder bldr = new OutputNameBuilder(patternsToInclude);
        bldr.applyNonVariableData();
        bldr.applyAppName(appName);
        bldr.applyVersion(version);
        bldr.applyCodeName(codeName);
        bldr.applyCommitHash(commitHash);
        bldr.applyBuildType(buildType);
        bldr.applyTimestamp(timestamp);
        project.jar.archiveName = bldr.build() + "." + project.jar.extension
      } else if (project.hasProperty('android')) {
        project.android.applicationVariants.all { variant ->
          variant.outputs.each { output ->
            OutputNameBuilder bldr = new OutputNameBuilder(patternsToInclude);
            bldr.applyNonVariableData();
            bldr.applyAppName(appName)
            bldr.applyVersion(version)
            bldr.applyCodeName(codeName);
            bldr.applyCommitHash(commitHash)
            bldr.applyBuildType(variant.getBaseName())
            bldr.applyTimestamp(timestamp);

            File apk = output.outputFile
            String newName = bldr.build();
            output.outputFile = new File(apk.parentFile, newName + ".apk")

            if (variant.buildType.zipAlignEnabled) {
              output.packageApplication.outputFile = new File(output.packageApplication.outputFile.parentFile, newName + "-unaligned.apk");
            }
          }
        }
      }
    }

    /**
     * Retrieve the SHA hash for the current git HEAD.
     * Equivalent to gitSha('HEAD').
     *
     * @return The git commit hash for the the current git HEAD.
     */
    public String gitSha() {
      return gitSha('HEAD')
    }

    /**
     * Retrieve the SHA hash for a tagged git commit.
     *
     * @param aTag The named tag for which to retrieve the commit hash.
     *
     * @return The git commit hash for the tag.
     */
    public String gitSha(String aTag) {
      def command = "git rev-list --max-count=1 --abbrev-commit ${aTag}"
      return "${command}".execute().text.trim()
    }

    public void codeName(String codeName) {
      this.codeName = codeName;
    }

    public String getCodeName() {
      return this.codeName;
    }

    void setProject(Project aProject) {
      project = aProject;
    }

    /**
     * Retrieve a timestamp in ISO 8601 format
     * @return A timestamp for the current date/time
     */
    public String timestamp() {
      if (cachedTimestamp == null) {
        TimeZone tz = TimeZone.getTimeZone("UTC")
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ")
        df.setTimeZone(tz);
        cachedTimestamp = df.format(new Date())
      }

      return cachedTimestamp;
    }

    /**
     * Setup the task for uploading an assembled product.
     *
     * This will create a new task, uploadXXXXX, where XXXXX is the name of the
     * product flavor for which the upload task is being created. This version
     * will use the debug version of the package to upload, and it will not set
     * users to which the distribution will be sent.
     *
     * @param type The name of the distribution mechanism to use. Currently, only
     *        'crashlytics' is supported.
     * @param flavor The ProductFlavor for which this task is being created. This
     *        is an {@link Object} to prevent us from having to include the
     *        Android Gradle Plugin in this plugin.
     */
    public uploadUsing (String type, Object flavor) {
      uploadUsing(type, null, flavor)
    }

    /**
     * Setup the task for uploading an assembled product.
     *
     * This will create a new task, uploadXXXXX, where XXXXX is the name of the
     * product flavor for which the upload task is being created. This version
     * will use the debug version of the package to upload.
     *
     * @param type The name of the distribution mechanism to use. Currently, only
     *        'crashlytics' is supported.
     * @param groupName The name of the group of users to notify of a new
     *        distribution. This must be setup in Crashlytics before the task is
     *        executed.
     * @param flavor The ProductFlavor for which this task is being created. This
     *        is an {@link Object} to prevent us from having to include the
     *        Android Gradle Plugin in this plugin.
     */
    public uploadUsing(String type, String groupName, Object flavor) {
      uploadUsing(type, groupName, 'debug', flavor);
    }

    /**
     * Setup the task for uploading an assembled product.
     *
     * This will create a new task, uploadXXXXX, where XXXXX is the name of the
     * product flavor for which the upload task is being created.
     *
     * @param type The name of the distribution mechanism to use. Currently, only
     *        'crashlytics' is supported.
     * @param groupName The name of the group of users to notify of a new
     *        distribution. This must be setup in Crashlytics before the task is
     *        executed.
     * @param distributionType The type of distribution (e.g. 'release'). Must be
     *        one of the android.buildTypes, or 'debug' will be used. Defaults to
     *        'debug'.
     * @param flavor The ProductFlavor for which this task is being created. This
     *        is an {@link Object} to prevent us from having to include the
     *        Android Gradle Plugin in this plugin.
     */
    public uploadUsing(String type, String groupName, String distributionType,
                       Object flavor) {
      // Add a task for each of the flavors for uploading.
      if (project.hasProperty('android')) {
        String effectiveDistributionType = 'Debug';
        project.android.buildTypes.each { buildType ->
          if (buildType.name.equalsIgnoreCase(distributionType)) {
            effectiveDistributionType = distributionType.capitalize();
          }
        }

       if (type.toLowerCase().equals('crashlytics')) {
         setupCrashlyticsUploadTask(groupName, effectiveDistributionType,
                                    flavor)
       }
      }
    }

    /**
     * Setup the task for uploading an assembled product to Crashlytics Beta.
     *
     * This will create a new task, uploadXXXXX, where XXXXX is the name of the
     * product flavor for which the upload task is being created.
     *
     * @param groupName The name of the group of users to notify of a new
     *        distribution. This must be setup in Crashlytics before the task is
     *        executed.
     * @param distributionType The type of distribution (e.g. 'release'). Must be
     *        one of the android.buildTypes, or 'debug' will be used. Defaults to
     *        'debug'.
     * @param flavor The ProductFlavor for which this task is being created. This
     *        is an {@link Object} to prevent us from having to include the
     *        Android Gradle Plugin in this plugin.
     */
    private setupCrashlyticsUploadTask(String groupName, String distributionType,
                                       Object flavor) {
      String flavorName = flavor.name;
      TaskContainer taskContainer = project.getTasks();
      if (groupName != null) {
        flavor.ext.betaDistributionGroupAliases = groupName
      }

      project.afterEvaluate {
        try {
          String fullBuildType = flavorName.capitalize() + distributionType.capitalize()
          Task createdTask = taskContainer.create("upload"
            + flavorName.capitalize());
          String aAn = "aeiou".indexOf((Character.toLowerCase(flavorName.charAt(0)).toString())) >= 0 ? "an" : "a";
          createdTask.description = "Upload " + aAn + " " + flavorName + " release to Crashlytics Beta and notify group '${groupName}'"
          String crashlyticsTaskName = "crashlyticsUploadDistribution" + fullBuildType;
          Task crashlyticsTask = taskContainer.getByName(crashlyticsTaskName)
          Task assembleTask = taskContainer.getByName("assemble" + fullBuildType)
          createdTask.dependsOn = [assembleTask, crashlyticsTask]
        } catch (UnknownTaskException e) {
          throw new RuntimeException("Did you forget to apply the fabric plugin?", e);
        }
      }
    }
}
