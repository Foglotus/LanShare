package com.foglotus.server.annotation

/**
 *
 * @author foglotus
 * @since 2019/3/15
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class Filter(val path:String)