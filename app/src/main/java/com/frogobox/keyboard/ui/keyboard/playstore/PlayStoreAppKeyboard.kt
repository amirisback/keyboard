package com.frogobox.keyboard.ui.keyboard.playstore

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import com.frogobox.keyboard.common.base.BaseKeyboard
import com.frogobox.keyboard.databinding.ItemKeyboardNewsBinding
import com.frogobox.keyboard.databinding.KeyboardAutotextBinding
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.frogobox.recycler.ext.injectorBinding

class PlayStoreAppKeyboard(
    context: Context,
    attrs: AttributeSet?,
) : BaseKeyboard<KeyboardAutotextBinding>(context, attrs) {

    var typePlayStore : PlayStoreType? = null

    override fun setupViewBinding(): KeyboardAutotextBinding {
        return KeyboardAutotextBinding.inflate(LayoutInflater.from(context), this, true)
    }

    override fun onCreate() {
        initView()
        setupData()
    }

    private fun initView() {
        binding?.apply {
            tvToolbarTitle.text = when (typePlayStore) {
                PlayStoreType.GAME -> PlayStoreType.GAME.name
                PlayStoreType.APP -> PlayStoreType.APP.name
                else -> {
                    PlayStoreType.GAME.name
                }
            }
        }
    }

    fun setupTypePlayStore(typePlayStore: PlayStoreType) {
        this.typePlayStore = typePlayStore
        binding?.apply {
            tvToolbarTitle.text = when (typePlayStore) {
                PlayStoreType.GAME -> PlayStoreType.GAME.name
                PlayStoreType.APP -> PlayStoreType.APP.name
            }
        }
        setupRv(
            when (typePlayStore) {
                PlayStoreType.GAME -> PlayStoreUtils.getPlayStoreTextGame()
                PlayStoreType.APP -> PlayStoreUtils.getPlayStoreTextApp()
            }
        )
    }

    private fun setupData() {
        setupRv(
            when (typePlayStore) {
                PlayStoreType.GAME -> PlayStoreUtils.getPlayStoreTextGame()
                PlayStoreType.APP -> PlayStoreUtils.getPlayStoreTextApp()
                else -> {
                    PlayStoreUtils.getPlayStoreTextGame()}
            }
        )
    }

    private fun setupRv(data: List<PlayStoreTextModel>) {
        binding?.apply {

            val adapterCallback = object :
                IFrogoBindingAdapter<PlayStoreTextModel, ItemKeyboardNewsBinding> {
                override fun onItemClicked(
                    binding: ItemKeyboardNewsBinding,
                    data: PlayStoreTextModel,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<PlayStoreTextModel>,
                ) {
                    // Your Clicked
                    val output = data.text
                    currentInputConnection?.commitText(output, 1)
                }

                override fun onItemLongClicked(
                    binding: ItemKeyboardNewsBinding,
                    data: PlayStoreTextModel,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<PlayStoreTextModel>,
                ) {
                }

                override fun setViewBinding(parent: ViewGroup): ItemKeyboardNewsBinding {
                    return ItemKeyboardNewsBinding.inflate(
                        LayoutInflater.from(context),
                        parent,
                        false
                    )
                }

                override fun setupInitComponent(
                    binding: ItemKeyboardNewsBinding,
                    data: PlayStoreTextModel,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<PlayStoreTextModel>,
                ) {
                    binding.apply {
                        tvItemKeyboardMain.text = data.text
                    }
                }
            }

            rvKeyboardMain.injectorBinding<PlayStoreTextModel, ItemKeyboardNewsBinding>()
                .addData(data)
                .createLayoutLinearVertical(false)
                .addCallback(adapterCallback)
                .build()
        }
    }

}