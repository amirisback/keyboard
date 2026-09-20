package com.frogobox.appkeyboard.ui.productremote

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.frogobox.appkeyboard.common.base.BaseComposeActivity
import dagger.hilt.android.AndroidEntryPoint

/**
 * Dedicated In-App Activity for inspecting and browsing remote product catalog data.
 * Annotated with @AndroidEntryPoint and declared with android:exported="false" in AndroidManifest.xml.
 */
@AndroidEntryPoint
class ProductRemoteActivity : BaseComposeActivity() {

    private val viewModel: ProductRemoteViewModel by viewModels()

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        requestNotificationPermissionIfNeeded()
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }
    }

    @Composable
    override fun Content() {
        ProductRemoteScreen(
            viewModel = viewModel,
            onBackClick = { finish() }
        )
    }

}
