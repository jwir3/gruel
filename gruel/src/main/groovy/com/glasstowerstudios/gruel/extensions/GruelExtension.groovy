package com.glasstowerstudios.gruel.extensions

import org.gradle.api.Project

import java.text.DateFormat
import java.text.SimpleDateFormat

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

    boolean willAdjustOutputSettings = false;

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

        public String build() {
          StringBuilder archiveName = new StringBuilder();
          for (String next : mPatterns) {
            archiveName.append(next);
          }

          return archiveName.toString();
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
     * Adjust the name of the output file for a given project.
     *
     * @param aProject The project for which to adjust the output file name.
     */
    public adjustOutputSettings(Project aProject) {
      project = aProject;

      if (project.hasProperty('jar')) {
        OutputNameBuilder bldr = new OutputNameBuilder(patternsToInclude);
        bldr.applyAppName(appName);
        bldr.applyVersion(version);
        bldr.applyCommitHash(commitHash);
        bldr.applyBuildType(buildType);
        bldr.applyTimestamp(timestamp);
        project.jar.archiveName = bldr.build() + "." + project.jar.extension
      } else if (project.hasProperty('android')) {
        project.android.applicationVariants.all { variant ->
          variant.outputs.each { output ->
            OutputNameBuilder bldr = new OutputNameBuilder(patternsToInclude);
            bldr.applyAppName(appName)
            bldr.applyVersion(version)
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

    /**
     * Retrieve a timestamp in ISO 8601 format
     * @return A timestamp for the current date/time
     */
    public String timestamp() {
      TimeZone tz = TimeZone.getTimeZone("UTC")
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ")
      df.setTimeZone(tz);
      return df.format(new Date())
    }
}
