package io.github.pshevche.act.extensions

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class LocalOnly(val reason: String)
