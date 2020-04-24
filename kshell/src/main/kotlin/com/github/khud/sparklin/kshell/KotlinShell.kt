package com.github.khud.sparklin.kshell

import com.github.khud.sparklin.kshell.configuration.CachedInstance
import com.github.khud.sparklin.kshell.configuration.ReplConfiguration
import com.github.khud.sparklin.kshell.configuration.ReplConfigurationImpl
import kotlin.script.experimental.api.ScriptCompilationConfiguration
import kotlin.script.experimental.api.ScriptEvaluationConfiguration
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvm.defaultJvmScriptingHostConfiguration
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvm.util.scriptCompilationClasspathFromContext

object KotlinShell {
    @JvmStatic
    fun main(args: Array<String>) {
        val repl =
                KShell(
                        configuration(),
                        defaultJvmScriptingHostConfiguration,
                        ScriptCompilationConfiguration {
                            jvm {
                                // TODO: replJars here
                                scriptCompilationClasspathFromContext(wholeClasspath = true)
                            }
                        },
                        ScriptEvaluationConfiguration {
                            jvm {
                                baseClassLoader(KShell::class.java.classLoader)
                            }
                        }
                )

//        repl.addClasspathRoots(replJars())
        Runtime.getRuntime().addShutdownHook(Thread {
            println("\nBye!")
            repl.cleanUp()
        })

        repl.doRun()
    }

    fun configuration(): ReplConfiguration {
        val instance = CachedInstance<ReplConfiguration>()
        val klassName: String? = System.getProperty("config.class")

        return if (klassName != null) {
            instance.load(klassName, ReplConfiguration::class)
        } else {
            instance.get { ReplConfigurationImpl() }
        }
    }
}