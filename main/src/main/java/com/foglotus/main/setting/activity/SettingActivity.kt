package com.foglotus.main.setting.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import com.foglotus.core.extention.logDebug
import com.foglotus.core.extention.logInfo
import com.foglotus.main.R
import com.foglotus.main.base.activity.BaseActivity
import com.foglotus.main.setting.fragment.SettingFragment
import kotlinx.android.synthetic.main.activity_main_settings.*

/**
 *
 * @author foglotus
 * @since 2019/3/19
 */
class SettingActivity:BaseActivity() {

    private var settingFragment: SettingFragment ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_settings)
        if(settingFragment == null){
            settingFragment = SettingFragment()
        }
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settingsFragmentLayout,settingFragment!!)
            .addToBackStack(null)
            .commit()
        toolbarTitle.text = "设置"
    }

    override fun setupViews() {
        setupToolbar()
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back)
    }

    override fun onBackPressed() {
        val fragments = supportFragmentManager.backStackEntryCount
        logDebug(TAG, "fragments is $fragments")
        if (fragments == 1) {
            finish()
        } else {
            if (fragments > 1) {
                supportFragmentManager.popBackStack()
            } else {
                finish()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        logInfo("RESULT","回来了")
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SettingFragment.SETTING_UPLOAD_PATH) {
                //If it is a folder selection mode, you need to get the folder path of your choice
                var path = data?.getStringExtra("path")
                if(path != null && !TextUtils.isEmpty(path)){
                    settingFragment?.updatePath(path)
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {

        private const val TAG = "SettingsActivity"

        const val MAIN_SETTINGS = 0

        const val SERVER_SETTINGS = 1

        private const val INTENT_SETTINGS_TYPE = "intent_settings_type"

        fun actionStart(activity: Activity) {
            val intent = Intent(activity, SettingActivity::class.java)
            activity.startActivity(intent)
        }

        fun actionStartServerSettings(activity: Activity) {
            val intent = Intent(activity, SettingActivity::class.java)
            intent.putExtra(INTENT_SETTINGS_TYPE, SERVER_SETTINGS)
            activity.startActivity(intent)
        }
    }
}