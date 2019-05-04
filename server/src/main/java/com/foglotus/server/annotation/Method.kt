package com.foglotus.server.annotation

/**
 *
 * @author foglotus
 * @since 2019/3/15
 */
@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class Method(val path:String)