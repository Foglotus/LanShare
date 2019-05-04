package com.foglotus.main.setting.fragment

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import com.foglotus.core.Const
import com.foglotus.core.util.SharedUtil
import com.foglotus.core.util.GlobalUtil
import com.foglotus.main.R
import com.foglotus.main.setting.activity.AboutActivity
import com.leon.lfilepickerlibrary.LFilePicker

class SettingFragment : PreferenceFragmentCompat() , SharedPreferences.OnSharedPreferenceChangeListener{
    private lateinit var mainActivity: Activity
    private lateinit var serverPath: Preference

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (activity == null) return
        mainActivity = activity as Activity

        val port = findPreference(getString(R.string.key_server_port_default))
        port.summary = SharedUtil.read(GlobalUtil.getString(R.string.key_server_port_default),Const.Server.PORT.toString())

        val timeout = findPreference(getString(R.string.key_server_timeout_default))
        timeout.summary = SharedUtil.read(GlobalUtil.getString(R.string.key_server_timeout_default),Const.Server.TIMEOUT.toString())

        serverPath = findPreference(getString(com.foglotus.main.R.string.key_server_path))
        serverPath.summary = SharedUtil.read(GlobalUtil.getString(R.string.key_server_upload_path_default),Const.Server.PATH)
        serverPath.setOnPreferenceClickListener {
            LFilePicker()
                .withActivity(activity)
                .withRequestCode(SETTING_UPLOAD_PATH)
                .withStartPath("/storage/emulated/0/Download")
                .withIsGreater(false)
                .withChooseMode(false)
                .start()
            serverPath.summary = SharedUtil.read(GlobalUtil.getString(R.string.key_server_upload_path_default),Const.Server.PATH)
            true
        }


        val appInfo = findPreference(getString(R.string.key_app_info))
        appInfo.setOnPreferenceClickListener {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.parse("package:" + GlobalUtil.appPackage)
            intent.data = uri
            startActivity(intent)
            true
        }

        val about = findPreference(getString(R.string.key_about))
        about.setOnPreferenceClickListener {
            AboutActivity.actionStart(mainActivity)
            true
        }
    }

    fun updatePath(path:String){
        SharedUtil.save(getString(R.string.key_server_upload_path_default),path)
        serverPath.summary = path
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }


    override fun onCreatePreferences(p0: Bundle?, p1: String?) {
        addPreferencesFromResource(R.xml.main_preference)
    }
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        val item =  findPreference(key)
        if(item is EditTextPreference){
            item.summary = sharedPreferences?.getString(key,item.summary.toString())
        }
    }

    companion object{
        const val SETTING_UPLOAD_PATH = 10000
    }

}
