package com.frogobox.appkeyboard.ui.autotext

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.viewModels
import com.frogobox.appkeyboard.common.base.BaseActivity
import com.frogobox.appkeyboard.databinding.ActivityAutotextBinding
import com.frogobox.appkeyboard.databinding.ItemAutotextBinding
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding
import com.frogobox.sdk.ext.gone
import com.frogobox.sdk.ext.visible
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */


@AndroidEntryPoint
class AutoTextActivity : BaseActivity<ActivityAutotextBinding>() {

    private val viewModel: AutoTextViewModel by viewModels()

    override fun setupViewBinding(): ActivityAutotextBinding {
        return ActivityAutotextBinding.inflate(layoutInflater)
    }

    override fun setupViewModel() {
        viewModel.apply {
            autoText.observe(this@AutoTextActivity) {
                if (it.isEmpty()) {
                    binding.emptyView.root.visible()
                    binding.rvAutotext.gone()
                } else {
                    binding.emptyView.root.gone()
                    binding.rvAutotext.visible()
                    setupRvAutoText(it)
                }
            }
        }
    }

    override fun onCreateExt(savedInstanceState: Bundle?) {
        super.onCreateExt(savedInstanceState)
        setupDetailActivity("Auto Text")
        setupUI()
        viewModel.getAutoText()
    }

    override fun setupActivityResultExt(result: ActivityResult) {
        super.setupActivityResultExt(result)
        if (result.resultCode == AutoTextDetailActivity.RESULT_CODE_DELETE) {
            viewModel.getAutoText()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getAutoText()
    }

    private fun setupUI() {
        binding.apply {
            btnAdd.setOnClickListener {
                startActivityResultExt(
                    Intent(
                        this@AutoTextActivity,
                        AutoTextEditorActivity::class.java
                    )
                )
            }
        }
    }

    private fun setupRvAutoText(data: List<AutoTextEntity>) {
        binding.rvAutotext.injectorBinding<AutoTextEntity, ItemAutotextBinding>()
            .addData(data)
            .addCallback(object : IFrogoBindingAdapter<AutoTextEntity, ItemAutotextBinding> {

                override fun setViewBinding(parent: ViewGroup): ItemAutotextBinding {
                    return ItemAutotextBinding.inflate(layoutInflater, parent, false)
                }

                override fun setupInitComponent(
                    binding: ItemAutotextBinding,
                    data: AutoTextEntity,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<AutoTextEntity>,
                ) {
                    binding.tvAutotextTitle.text = data.title
                    binding.tvAutotextContent.text = data.body
                }

                override fun onItemClicked(
                    binding: ItemAutotextBinding,
                    data: AutoTextEntity,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<AutoTextEntity>,
                ) {
                    val extra = Gson().toJson(data)
                    startActivityResultExt(
                        Intent(this@AutoTextActivity, AutoTextDetailActivity::class.java).apply {
                            putExtra(AutoTextDetailActivity.EXTRA_AUTO_TEXT, extra)
                        }
                    )
                }

                override fun onItemLongClicked(
                    binding: ItemAutotextBinding,
                    data: AutoTextEntity,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<AutoTextEntity>,
                ) {
                }

            })
            .createLayoutLinearVertical(false)
            .build()
    }

}