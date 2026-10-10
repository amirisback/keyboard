package com.frogobox.appkeyboard.util

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.inputmethod.InputMethodManager

/**
 * Helper for step-by-step keyboard activation and onboarding wizard.
 * Checks whether the IME service is enabled in system settings and chosen as default.
 */
object KeyboardSetupWizardHelper {

    enum class SetupStep(val stepNumber: Int, val title: String, val description: String) {
        STEP_ENABLE(
            1,
            "Aktifkan Frogo Keyboard",
            "Buka Pengaturan Sistem Android dan centang Frogo Keyboard pada daftar keyboard yang tersedia."
        ),
        STEP_SELECT_DEFAULT(
            2,
            "Pilih Keyboard Utama",
            "Jadikan Frogo Keyboard sebagai metode input default untuk seluruh aplikasi."
        ),
        STEP_READY(
            3,
            "Keyboard Siap Digunakan",
            "Frogo Keyboard aktif dan siap digunakan. Anda dapat mencoba mengetik di kotak uji coba."
        )
    }

    fun isImeEnabled(context: Context, packageName: String): Boolean {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager ?: return false
        val enabledKeyboards = imm.enabledInputMethodList
        return enabledKeyboards.any { it.serviceInfo.packageName == packageName }
    }

    fun isImeDefault(context: Context, packageName: String): Boolean {
        val defaultIme = Settings.Secure.getString(context.contentResolver, Settings.Secure.DEFAULT_INPUT_METHOD) ?: return false
        return defaultIme.contains(packageName)
    }

    fun getSetupStep(isImeEnabled: Boolean, isImeDefault: Boolean): SetupStep {
        return when {
            !isImeEnabled -> SetupStep.STEP_ENABLE
            !isImeDefault -> SetupStep.STEP_SELECT_DEFAULT
            else -> SetupStep.STEP_READY
        }
    }

    fun createEnableSettingsIntent(): Intent {
        return Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
    }
}
