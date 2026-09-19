package com.frogobox.appkeyboard.ui.keyboard.autotext

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.frogobox.appkeyboard.databinding.ItemKeyboardAutotextBinding
import com.frogobox.appkeyboard.databinding.KeyboardAutotextBinding
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.ui.autotext.AutoTextActivity
import com.frogobox.libkeyboard.common.core.BaseKeyboard
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */

class AutoTextKeyboard(
    context: Context,
    attrs: AttributeSet?,
) : BaseKeyboard<KeyboardAutotextBinding>(context, attrs) {

    override fun setupViewBinding(inflater: LayoutInflater, parent: LinearLayout): KeyboardAutotextBinding {
        return KeyboardAutotextBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun initUI() {
        super.initUI()
        binding.apply {
            tvToolbarTitle.text = "Auto Text"
            toolbarManage.setOnClickListener {
                val intent = Intent(context, AutoTextActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                context.startActivity(intent)
            }
        }
    }

    override fun initData() {
        super.initData()
        val viewModel = AutoTextKeyboardViewModel(context)
        viewModel.getAutoText {
            setupRv(it)
        }
    }

    private fun setupRv(data: List<AutoTextEntity>) {
        binding.apply {
            if (data.isEmpty()) {
                llEmptyState.visibility = View.VISIBLE
                rvKeyboardMain.visibility = View.GONE
            } else {
                llEmptyState.visibility = View.GONE
                rvKeyboardMain.visibility = View.VISIBLE
            }

            val adapterCallback = object : IFrogoBindingAdapter<AutoTextEntity, ItemKeyboardAutotextBinding> {
                override fun onItemClicked(
                    binding: ItemKeyboardAutotextBinding,
                    data: AutoTextEntity,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<AutoTextEntity>,
                ) {
                    val output = data.body
                    currentInputConnection?.commitText(output, 1)
                }

                override fun onItemLongClicked(
                    binding: ItemKeyboardAutotextBinding,
                    data: AutoTextEntity,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<AutoTextEntity>,
                ) {
                }

                override fun areContentsTheSame(
                    oldItem: AutoTextEntity,
                    newItem: AutoTextEntity
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areItemsTheSame(
                    oldItem: AutoTextEntity,
                    newItem: AutoTextEntity
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun setViewBinding(parent: ViewGroup): ItemKeyboardAutotextBinding {
                    return ItemKeyboardAutotextBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false
                    )
                }

                override fun setupInitComponent(
                    binding: ItemKeyboardAutotextBinding,
                    data: AutoTextEntity,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<AutoTextEntity>,
                ) {
                    binding.apply {
                        tvItemKeyboardMain.text = data.title
                        tvItemKeyboardBody.text = data.body
                    }
                }
            }

            rvKeyboardMain.injectorBinding<AutoTextEntity, ItemKeyboardAutotextBinding>()
                .addData(data)
                .createLayoutLinearVertical(false)
                .addCallback(adapterCallback)
                .build()
        }
    }

}