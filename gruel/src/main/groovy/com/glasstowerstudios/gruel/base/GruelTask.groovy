package com.glasstowerstudios.gruel.base;

import org.gradle.api.DefaultTask

class GruelTask extends DefaultTask {
  // The prefix we're going to use for this repository/project. Stored for the
  // first time when getPropertyPrefix() is called.
  private static String mPropertyPrefix;

  // The properties object we'll be working on.
  private Properties mProps;

  // The file from which the properties object will be read initially.
  private File mPropsFile;

  /**
   * Retrieve the {@link Properties} object for the gradle.properties file for
   * this build configuration.
   *
   * @return The {@link Properties} for the current project, or null if one does
   *         not exist.
   */
  protected Properties getProperties() {
    if (mProps == null) {
      def propsFile = getPropertiesFile();
      mProps = new Properties();
      mProps.load(propsFile.newDataInputStream());
    }

    return mProps;
  }

  /**
   * Retrieve the properties file 'gradle.properties', if it exists, or create
   * it if it does not exist.
   *
   * @return A new {@link File} object pointing to the 'gradle.properties' file
   *         of the current project.
   */
  protected File getPropertiesFile() {
    if (mPropsFile == null) {
      mPropsFile = new File("gradle.properties");
    }

    if (!mPropsFile.exists()) {
      mPropsFile.createNewFile();
    }

    return mPropsFile;
  }

  /**
   * Retrieve the property prefix for the current project.
   *
   * @return A string containing the prefix for properties in the current
   *         project.
   */
  protected String getPropertyPrefix() {
    if (mPropertyPrefix == null) {
      Properties props = getProperties();
      mPropertyPrefix = props.getProperty("PROPERTY_PREFIX") as String;
    }

    // If it's still null, then the user didn't specify it in the
    // gradle.properties file, so let's use the project name in all caps.
    if (mPropertyPrefix == null) {
      mPropertyPrefix = project.name.toUpperCase();
    }

    return mPropertyPrefix;
  }
}
