package com.frogobox.appkeyboard.ui.test

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import com.frogobox.appkeyboard.common.base.BaseComposeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestActivity : BaseComposeActivity() {

    companion object {
        private val TAG: String = TestActivity::class.java.simpleName
    }

    private val viewModel: TestViewModel by viewModels()

    private val dummyOptions = listOf(
        "Kuningan",
        "Menteng",
        "Menten213g",
        "Men123teng",
        "Mw23423",
        "Me123nteng",
        "Mente234234234ng",
        "Pegangsaan"
    )

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        setupDetailActivity("Test Area")
        if (savedInstanceState == null) {
            // Call View Model Here
            Log.d(TAG, "View Model : ${viewModel::class.java.simpleName}")
        }
    }

    @Composable
    override fun Content() {
        TestScreen(
            dummyOptions = dummyOptions
        )
    }

}