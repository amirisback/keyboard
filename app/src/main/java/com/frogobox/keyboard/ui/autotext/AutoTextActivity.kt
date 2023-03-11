package com.frogobox.keyboard.ui.autotext

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.viewModels
import com.frogobox.keyboard.common.base.BaseActivity
import com.frogobox.keyboard.data.local.autotext.AutoTextEntity
import com.frogobox.keyboard.databinding.ActivityAutotextBinding
import com.frogobox.keyboard.databinding.ActivityAutotextDetailBinding
import com.frogobox.keyboard.databinding.ItemAutotextBinding
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding
import com.frogobox.sdk.ext.startActivityExt
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
                setupRvAutoText(it)
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
        if (result.resultCode == AutoTextEditorActivity.RESULT_CODE_EDIT) {
            viewModel.getAutoText()
        }
    }

    private fun setupUI() {
        binding.apply {
            btnAdd.setOnClickListener {
                startActivityResult.launch(
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
                    startActivityExt<AutoTextDetailActivity, AutoTextEntity>(
                        AutoTextDetailActivity.EXTRA_AUTO_TEXT,
                        data
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