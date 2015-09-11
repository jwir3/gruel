package com.glasstowerstudios.gruel

import com.glasstowerstudios.gruel.tasks.BumpVersionTask
import com.glasstowerstudios.gruel.extensions.HipChatExtension

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class GruelPlugin implements Plugin<Project> {
  void apply (Project aProject) {
    aProject.extensions.create("hipchat", HipChatExtension, aProject);

    aProject.task('bumpVersion', type: BumpVersionTask, description: 'Bumps the version number of the current release.', group: 'Management') << {
    }
  }
}
