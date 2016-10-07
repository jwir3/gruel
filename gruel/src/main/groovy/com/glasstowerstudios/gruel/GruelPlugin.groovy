package com.glasstowerstudios.gruel

import com.glasstowerstudios.gruel.tasks.BumpVersionTask
import com.glasstowerstudios.gruel.extensions.HipChatExtension
import com.glasstowerstudios.gruel.extensions.GruelExtension
import com.glasstowerstudios.gruel.tasks.UninstallTask

import org.gradle.api.Task;
import org.gradle.api.tasks.TaskContainer;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GruelPlugin implements Plugin<Project> {
  void apply (Project aProject) {
    def gruelExtension = aProject.extensions.create("gruel", GruelExtension);
    gruelExtension.setProject(aProject);

    aProject.extensions.create("hipchat", HipChatExtension);

    aProject.task('bumpVersion', type: BumpVersionTask, description: 'Bumps the version number of the current release.', group: 'Management') << {
    }

    // We only want to add this task if the project is a library (android or JAR)
    // Perhaps we can detect this if it has a maven publishing task?
    aProject.task('uninstall', type: UninstallTask, description: "Uninstall any currently installed versions of '${aProject.name}' from the local maven repository") << {
    }

    aProject.afterEvaluate {
      if (gruelExtension.shouldAdjustOutputSettings()) {
        gruelExtension.adjustOutputSettings(aProject);
        gruelExtension.adjustVersionNameSettings(aProject);
      }
    }
  }
}
