package com.frogobox.appkeyboard.ui.theme

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import com.frogobox.appkeyboard.common.base.BaseActivity
import com.frogobox.appkeyboard.databinding.ActivityThemeBinding
import com.frogobox.appkeyboard.databinding.ItemThemeBinding
import com.frogobox.appkeyboard.model.KeyboardThemeModel
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding
import com.frogobox.sdk.ext.gone
import com.frogobox.sdk.ext.showToast
import com.frogobox.sdk.ext.visible
import com.frogobox.sdk.util.FrogoFunc
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by faisalamircs on 16/02/2024
 * -----------------------------------------
 * Name     : Muhammad Faisal Amir
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 */


@AndroidEntryPoint
class ThemeActivity : BaseActivity<ActivityThemeBinding>(),
    IFrogoBindingAdapter<KeyboardThemeModel, ItemThemeBinding> {

    companion object {
        private val TAG: String = ThemeActivity::class.java.simpleName
    }

    private val viewModel: ThemeViewModel by viewModels()

    override fun setupViewBinding(): ActivityThemeBinding {
        return ActivityThemeBinding.inflate(layoutInflater)
    }

    override fun setupViewModel() {
        super.setupViewModel()
        viewModel.keyboardThemeState.observe(this) {
            setupRV(it)
        }
    }


    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        setupDetailActivity("Setup Theme")
        if (savedInstanceState == null) {
            // Call View Model Here
            viewModel.getThemeData()
        }
        // TODO : Add your code here

    }

    override fun initView() {
        super.initView()
        binding.apply {

        }
    }

    override fun onItemClicked(
        binding: ItemThemeBinding,
        data: KeyboardThemeModel,
        position: Int,
        notifyListener: FrogoRecyclerNotifyListener<KeyboardThemeModel>
    ) {
        applyTheme(data)
    }

    override fun onItemLongClicked(
        binding: ItemThemeBinding,
        data: KeyboardThemeModel,
        position: Int,
        notifyListener: FrogoRecyclerNotifyListener<KeyboardThemeModel>
    ) {}

    override fun areContentsTheSame(
        oldItem: KeyboardThemeModel,
        newItem: KeyboardThemeModel
    ): Boolean {
        return oldItem == newItem
    }

    override fun areItemsTheSame(
        oldItem: KeyboardThemeModel,
        newItem: KeyboardThemeModel
    ): Boolean {
        return oldItem.name == newItem.name
    }

    override fun setViewBinding(parent: ViewGroup): ItemThemeBinding {
        return ItemThemeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    }

    override fun setupInitComponent(
        binding: ItemThemeBinding,
        data: KeyboardThemeModel,
        position: Int,
        notifyListener: FrogoRecyclerNotifyListener<KeyboardThemeModel>
    ) {
        binding.ivBackground.setImageResource(data.background)
        binding.tvTitle.text = data.name
        binding.tvDesc.text = data.description
        if (viewModel.getThemeColor() == data.background) {
            binding.ivIcon.visible()
        } else {
            binding.ivIcon.gone()
        }
    }

    private fun setupRV(data: List<KeyboardThemeModel>) {
        binding.root.injectorBinding<KeyboardThemeModel, ItemThemeBinding>()
            .addData(data)
            .addCallback(this)
            .createLayoutLinearVertical(false)
            .build()
    }

    private fun applyTheme(data: KeyboardThemeModel) {
        FrogoFunc.createDialogDefault(
            this@ThemeActivity,
            "Change Theme",
            "Are you sure you want to change keyboard theme?",
            listenerNo = {},
            listenerYes = {
                viewModel.setThemeColor(data) {
                    viewModel.getThemeData()
                    showToast("${data.name} Theme Applied")
                }
            }
        )
    }

}