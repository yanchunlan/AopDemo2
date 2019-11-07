package com.example.javassist

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class ClickListener implements Plugin<Project> {

    @Override
    void apply(Project project) {
        System.out.println "========== ClickListener start =========="
        def appExtension = project.extensions.findByType(AppExtension)
        appExtension.registerTransform(new ClickListenerTransform(project, appExtension))
        System.out.println "========== ClickListener end =========="
    }
}
