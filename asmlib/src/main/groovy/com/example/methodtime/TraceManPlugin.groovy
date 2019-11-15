package com.example.methodtime

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class TraceManPlugin implements Plugin <Project>{

    @Override
    void apply(Project project) {

        System.out.println("---- TraceManPlugin start ----")
        project.extensions.create("traceMan",TraceManConfig)
        def android = project.extensions.getByType(AppExtension)
        android.registerTransform(new TraceManTransform(project))
        System.out.println("---- TraceManPlugin end ----")
    }
}