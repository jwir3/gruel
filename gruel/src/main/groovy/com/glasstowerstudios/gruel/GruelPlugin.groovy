package com.glasstowerstudios.gruel;

import com.glasstowerstudios.gruel.BumpVersionTask;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GruelPlugin implements Plugin<Project> {
  void apply (Project aProject) {
    aProject.task('bumpVersion', type: BumpVersionTask, description: 'Bumps the version number of the current release.', group: 'Management') << {
    }
  }
}
