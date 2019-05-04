package com.foglotus.main.setting.view

import android.content.Context
import android.support.v7.preference.EditTextPreference
import android.util.AttributeSet

/**
 *
 * @author foglotus
 * @since 2019/3/19
 */
class ServerPathPreference : EditTextPreference {
    constructor(context:Context, attrs: AttributeSet,defStyleAttr:Int,defStyleRes:Int):super(context,attrs,defStyleAttr,defStyleRes)

    constructor(context:Context, attrs: AttributeSet,defStyleAttr:Int):super(context,attrs,defStyleAttr)

    constructor(context:Context, attrs: AttributeSet):super(context,attrs)

    constructor(context:Context):super(context)

    override fun onClick() {

    }
}