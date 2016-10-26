package com.glasstowerstudios.gruel

import com.glasstowerstudios.gruel.tasks.BumpVersionTask
import com.glasstowerstudios.gruel.extensions.JiraExtension
import com.glasstowerstudios.gruel.extensions.HipChatExtension
import com.glasstowerstudios.gruel.extensions.GithubExtension
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
    def jiraExtension = aProject.extensions.create("jira", JiraExtension);
    def githubExtension = aProject.extensions.create("github", GithubExtension);

    aProject.task('bumpVersion', type: BumpVersionTask, description: 'Bumps the version number of the current release.', group: 'Management') << {
    }

    aProject.afterEvaluate {
      try {
        def publishingClosure = aProject.publishing.publications.maven
        if (publishingClosure) {
          aProject.task('uninstall', type: UninstallTask,
                        description: "Uninstall any currently installed versions of '${aProject.name}' from the local maven repository") << {
          }
        }
      } catch (MissingPropertyException e) {
        // We didn't find a maven publishing closure, so we're not going to add
        // the uninstall task. We don't actually need to do anything here, so
        // just catch the exception and move on.
      }

      if (gruelExtension.shouldAdjustOutputSettings()) {
        gruelExtension.adjustOutputSettings(aProject);
        gruelExtension.adjustVersionNameSettings(aProject);
      }

      if (aProject.hasProperty('github')) {
        if (!githubExtension.isValid()) {
          throw new Exception("When using the github extension, you must provide a repository, along with either an authentication token, or a username/password combination");
        }
      }
    }
  }
}
