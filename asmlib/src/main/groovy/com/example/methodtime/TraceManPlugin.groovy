package com.example.methodtime

import com.android.build.gradle.*
import org.gradle.api.Plugin
import org.gradle.api.Project

class TraceManPlugin implements Plugin <Project>{

    @Override
    void apply(Project project) {
        def hasApp = project.plugins.hasPlugin(AppPlugin)
        def hasLib = project.plugins.hasPlugin(LibraryPlugin)
        if (!hasApp && !hasLib) {
            throw new IllegalStateException("'android' or 'android-library' plugin required.")
        }
        System.out.println("---- TraceManPlugin ${hasApp?"app":"lib"} start ----")
        project.extensions.create("traceMan",TraceManConfig)
        if (hasApp) {
            def android = project.extensions.getByType(AppExtension)
            android.registerTransform(new TraceManTransform(project, TraceManTransform.ScopesType.TYPE_APP))
        } else if (hasLib) {
            def android = project.extensions.getByType(LibraryExtension)
            android.registerTransform(new TraceManTransform(project, TraceManTransform.ScopesType.TYPE_LIB))
        }
        System.out.println("---- TraceManPlugin ${hasApp?"app":"lib"} end ----")
    }
}