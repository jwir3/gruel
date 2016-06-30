package com.glasstowerstudios.gruel

import com.glasstowerstudios.gruel.tasks.BumpVersionTask
import com.glasstowerstudios.gruel.extensions.HipChatExtension
import com.glasstowerstudios.gruel.extensions.GruelExtension

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GruelPlugin implements Plugin<Project> {
  void apply (Project aProject) {
    def gruelExtension = aProject.extensions.create("gruel", GruelExtension);
    aProject.extensions.create("hipchat", HipChatExtension);

    aProject.task('bumpVersion', type: BumpVersionTask, description: 'Bumps the version number of the current release.', group: 'Management') << {
    }

    aProject.afterEvaluate {
      if (gruelExtension.shouldAdjustOutputSettings()) {
        gruelExtension.adjustOutputSettings(aProject);
        gruelExtension.adjustVersionNameSettings(aProject);
      }
    }
  }
}
