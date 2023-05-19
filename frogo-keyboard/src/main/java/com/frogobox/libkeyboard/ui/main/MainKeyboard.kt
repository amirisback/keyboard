package com.frogobox.libkeyboard.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.Paint.Align
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.frogobox.libkeyboard.R
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard.Companion.KEYCODE_DELETE
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard.Companion.KEYCODE_EMOJI
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard.Companion.KEYCODE_ENTER
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard.Companion.KEYCODE_MODE_CHANGE
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard.Companion.KEYCODE_SHIFT
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard.Companion.KEYCODE_SPACE
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard.Companion.MAX_KEYS_PER_MINI_ROW
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard.Companion.SHIFT_OFF
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard.Companion.SHIFT_ON_ONE_CHAR
import com.frogobox.libkeyboard.ui.main.ItemMainKeyboard.Companion.SHIFT_ON_PERMANENT
import com.frogobox.libkeyboard.common.ext.adjustAlpha
import com.frogobox.libkeyboard.common.ext.applyColorFilter
import com.frogobox.libkeyboard.common.ext.getContrastColor
import com.frogobox.libkeyboard.common.ext.getProperBackgroundColor
import com.frogobox.libkeyboard.common.ext.getProperPrimaryColor
import com.frogobox.libkeyboard.common.ext.getProperTextColor
import com.frogobox.libkeyboard.common.ext.getStrokeColor
import com.frogobox.libkeyboard.common.ext.isDarkThemeOn
import java.util.*
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min

@SuppressLint("UseCompatLoadingForDrawables", "ClickableViewAccessibility")
class MainKeyboard @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet?,
    defStyleRes: Int = 0,
) : View(context, attrs, defStyleRes) {

    private var mKeyboard: ItemMainKeyboard? = null
    private var mCurrentKeyIndex: Int = NOT_A_KEY

    private var mLabelTextSize = 0
    private var mKeyTextSize = 0

    private var mTextColor = 0
    private var mBackgroundColor = 0
    private var mPrimaryColor = 0

    private var mPreviewText: TextView? = null
    private val mPreviewPopup: PopupWindow
    private var mPreviewTextSizeLarge = 0
    private var mPreviewHeight = 0

    private val mCoordinates = IntArray(2)
    private val mPopupKeyboard: PopupWindow
    private var mMiniKeyboardContainer: View? = null
    private var mMiniKeyboard: MainKeyboard? = null
    private var mMiniKeyboardOnScreen = false
    private var mPopupParent: View
    private var mMiniKeyboardOffsetX = 0
    private var mMiniKeyboardOffsetY = 0
    private val mMiniKeyboardCache: MutableMap<ItemMainKeyboard.Key, View?>
    private var mKeys = ArrayList<ItemMainKeyboard.Key>()
    private var mMiniKeyboardSelectedKeyIndex = -1

    var mOnKeyboardActionListener: OnKeyboardActionListener? = null
    private var mVerticalCorrection = 0
    private var mProximityThreshold = 0
    private var mPopupPreviewX = 0
    private var mPopupPreviewY = 0
    private var mLastX = 0
    private var mLastY = 0

    private val mPaint: Paint
    private var mDownTime = 0L
    private var mLastMoveTime = 0L
    private var mLastKey = 0
    private var mLastCodeX = 0
    private var mLastCodeY = 0
    private var mCurrentKey: Int = NOT_A_KEY
    private var mLastKeyTime = 0L
    private var mCurrentKeyTime = 0L
    private val mKeyIndices = IntArray(12)
    private var mPopupX = 0
    private var mPopupY = 0
    private var mRepeatKeyIndex = NOT_A_KEY
    private var mPopupLayout = 0
    private var mAbortKey = false
    private var mIsLongPressingSpace = false
    private var mLastSpaceMoveX = 0
    private var mPopupMaxMoveDistance = 0f
    private var mTopSmallNumberSize = 0f
    private var mTopSmallNumberMarginWidth = 0f
    private var mTopSmallNumberMarginHeight = 0f
    private val mSpaceMoveThreshold: Int
    private var ignoreTouches = false

    private var mKeyBackground: Drawable? = null

    // For multi-tap
    private var mLastTapTime = 0L

    /** Whether the keyboard bitmap needs to be redrawn before it's blitted.  */
    private var mDrawPending = false

    /** The dirty region in the keyboard bitmap  */
    private val mDirtyRect = Rect()

    /** The keyboard bitmap for faster updates  */
    private var mBuffer: Bitmap? = null

    /** Notes if the keyboard just changed, so that we could possibly reallocate the mBuffer.  */
    private var mKeyboardChanged = false

    /** The canvas for the above mutable keyboard bitmap  */
    private var mCanvas: Canvas? = null

    /** The accessibility manager for accessibility support  */
    private val mAccessibilityManager: AccessibilityManager

    private var mHandler: Handler? = null

    companion object {
        private const val NOT_A_KEY = -1
        private val LONG_PRESSABLE_STATE_SET = intArrayOf(R.attr.state_long_pressable)
        private const val MSG_REMOVE_PREVIEW = 1
        private const val MSG_REPEAT = 2
        private const val MSG_LONGPRESS = 3
        private const val DELAY_AFTER_PREVIEW = 100
        private const val DEBOUNCE_TIME = 70
        private const val REPEAT_INTERVAL = 50 // ~20 keys per second
        private const val REPEAT_START_DELAY = 400
        private val LONGPRESS_TIMEOUT = ViewConfiguration.getLongPressTimeout()
    }

    // handle system default theme (Material You) specially as the color is taken from the system, not hardcoded by us

    init {
        val attributes = context.obtainStyledAttributes(attrs,
            R.styleable.KwKeyboardView, 0, defStyleRes)
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val keyTextSize = 0
        val indexCnt = attributes.indexCount

        try {
            for (i in 0 until indexCnt) {
                when (val attr = attributes.getIndex(i)) {
                    R.styleable.KwKeyboardView_keyTextSize -> mKeyTextSize =
                        attributes.getDimensionPixelSize(attr, 18)
                }
            }
        } finally {
            attributes.recycle()
        }

        mPopupLayout = R.layout.keyboard_main_mini
        mKeyBackground = resources.getDrawable(R.drawable.keyboard_bg_mini_selector, context.theme)
        mVerticalCorrection = resources.getDimension(R.dimen.vertical_correction).toInt()
        mLabelTextSize = resources.getDimension(R.dimen.label_text_size).toInt()
        mPreviewHeight = resources.getDimension(R.dimen.key_height).toInt()
        mSpaceMoveThreshold = resources.getDimension(com.frogobox.ui.R.dimen.frogo_dimen_8dp).toInt()
        mTextColor = context.getProperTextColor()
        mBackgroundColor = context.getProperBackgroundColor()
        mPrimaryColor = context.getProperPrimaryColor()

        mPreviewPopup = PopupWindow(context)
        mPreviewText = inflater.inflate(resources.getLayout(R.layout.item_keyboard_main), null) as TextView
        mPreviewTextSizeLarge = context.resources.getDimension(R.dimen.preview_text_size).toInt()
        mPreviewPopup.contentView = mPreviewText
        mPreviewPopup.setBackgroundDrawable(null)

        mPreviewPopup.isTouchable = false
        mPopupKeyboard = PopupWindow(context)
        mPopupKeyboard.setBackgroundDrawable(null)
        mPopupParent = this
        mPaint = Paint()
        mPaint.isAntiAlias = true
        mPaint.textSize = keyTextSize.toFloat()
        mPaint.textAlign = Align.CENTER
        mPaint.alpha = 255
        mMiniKeyboardCache = HashMap()
        mAccessibilityManager = (context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager)
        mPopupMaxMoveDistance = resources.getDimension(R.dimen.popup_max_move_distance)
        mTopSmallNumberSize = resources.getDimension(com.frogobox.ui.R.dimen.frogo_dimen_font_10sp)
        mTopSmallNumberMarginWidth = resources.getDimension(R.dimen.top_small_number_margin_width)
        mTopSmallNumberMarginHeight = resources.getDimension(R.dimen.top_small_number_margin_height)
    }

    @SuppressLint("HandlerLeak")
    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mHandler == null) {
            mHandler = object : Handler() {
                override fun handleMessage(msg: Message) {
                    when (msg.what) {
                        MSG_REMOVE_PREVIEW -> mPreviewText!!.visibility = INVISIBLE
                        MSG_REPEAT -> if (repeatKey(false)) {
                            val repeat = Message.obtain(this, MSG_REPEAT)
                            sendMessageDelayed(repeat, REPEAT_INTERVAL.toLong())
                        }
                        MSG_LONGPRESS -> openPopupIfRequired(msg.obj as MotionEvent)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onVisibilityChanged(changedView: View, visibility: Int) {
        super.onVisibilityChanged(changedView, visibility)

        if (visibility == VISIBLE) {
            mTextColor = context.getProperTextColor()
            mBackgroundColor = context.getProperBackgroundColor()
            mPrimaryColor = context.getProperPrimaryColor()
            val strokeColor = context.getStrokeColor()

            val darkerColor = if (context.isDarkThemeOn()) {
                ContextCompat.getColor(context, R.color.system_neutral1_900)
            } else {
                ContextCompat.getColor(context, R.color.system_neutral1_10)
            }

            val miniKeyboardBackgroundColor = ContextCompat.getColor(context,
                R.color.keyboard_bg_normal_color
            )

            if (changedView == findViewById(R.id.mini_keyboard_view)) {
                val previewBackground = background as LayerDrawable
                previewBackground.findDrawableByLayerId(R.id.button_background_shape)
                    .applyColorFilter(miniKeyboardBackgroundColor)
                previewBackground.findDrawableByLayerId(R.id.button_background_stroke)
                    .applyColorFilter(strokeColor)
                background = previewBackground
            } else {
                background.applyColorFilter(darkerColor)
            }

        }
    }

    /**
     * Attaches a keyboard to this view. The keyboard can be switched at any time and the view will re-layout itself to accommodate the keyboard.
     * @param keyboard the keyboard to display in this view
     */
    fun setKeyboard(keyboard: ItemMainKeyboard) {
        if (mKeyboard != null) {
            showPreview(NOT_A_KEY)
        }
        removeMessages()
        mKeyboard = keyboard
        val keys = mKeyboard!!.mKeys
        mKeys = keys!!.toMutableList() as ArrayList<ItemMainKeyboard.Key>
        requestLayout()
        mKeyboardChanged = true
        invalidateAllKeys()
        computeProximityThreshold(keyboard)
        mMiniKeyboardCache.clear()
        // Not really necessary to do every time, but will free up views
        // Switching to a different keyboard should abort any pending keys so that the key up
        // doesn't get delivered to the old or new keyboard
        mAbortKey = true // Until the next ACTION_DOWN
    }

    fun vibrateIfNeeded() {
        if (ItemMainKeyboard.VIBRATE_ON_KEYPRESS) {
            performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY, HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING)
        }
    }

    /**
     * Sets the state of the shift key of the keyboard, if any.
     * @param shifted whether or not to enable the state of the shift key
     * @return true if the shift key state changed, false if there was no change
     */
    private fun setShifted(shiftState: Int) {
        if (mKeyboard?.setShifted(shiftState) == true) {
            invalidateAllKeys()
        }
    }

    /**
     * Returns the state of the shift key of the keyboard, if any.
     * @return true if the shift is in a pressed state, false otherwise
     */
    private fun isShifted(): Boolean {
        return (mKeyboard?.mShiftState ?: SHIFT_OFF) > SHIFT_OFF
    }

    private fun setPopupOffset(x: Int, y: Int) {
        mMiniKeyboardOffsetX = x
        mMiniKeyboardOffsetY = y
        if (mPreviewPopup.isShowing) {
            mPreviewPopup.dismiss()
        }
    }

    private fun adjustCase(label: CharSequence): CharSequence? {
        var newLabel: CharSequence? = label
        if (!newLabel.isNullOrEmpty() && mKeyboard!!.mShiftState > SHIFT_OFF && newLabel.length < 3 && Character.isLowerCase(
                newLabel[0])
        ) {
            newLabel = newLabel.toString().uppercase(Locale.getDefault())
        }
        return newLabel
    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (mKeyboard == null) {
            setMeasuredDimension(0, 0)
        } else {
            var width = mKeyboard!!.mMinWidth
            if (MeasureSpec.getSize(widthMeasureSpec) < width + 10) {
                width = MeasureSpec.getSize(widthMeasureSpec)
            }
            setMeasuredDimension(width, mKeyboard!!.mHeight)
        }
    }

    /**
     * Compute the average distance between adjacent keys (horizontally and vertically) and square it to get the proximity threshold. We use a square here and
     * in computing the touch distance from a key's center to avoid taking a square root.
     * @param keyboard
     */
    private fun computeProximityThreshold(keyboard: ItemMainKeyboard?) {
        if (keyboard == null) {
            return
        }

        val keys = mKeys
        val length = keys.size
        var dimensionSum = 0
        for (i in 0 until length) {
            val key = keys[i]
            dimensionSum += min(key.width, key.height) + key.gap
        }

        if (dimensionSum < 0 || length == 0) {
            return
        }

        mProximityThreshold = (dimensionSum * 1.4f / length).toInt()
        mProximityThreshold *= mProximityThreshold // Square it
    }

    public override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (mDrawPending || mBuffer == null || mKeyboardChanged) {
            onBufferDraw()
        }
        canvas.drawBitmap(mBuffer!!, 0f, 0f, null)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun onBufferDraw() {
        if (mBuffer == null || mKeyboardChanged) {
            if (mBuffer == null || mKeyboardChanged && (mBuffer!!.width != width || mBuffer!!.height != height)) {
                // Make sure our bitmap is at least 1x1
                val width = max(1, width)
                val height = max(1, height)
                mBuffer = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                mCanvas = Canvas(mBuffer!!)
            }
            invalidateAllKeys()
            mKeyboardChanged = false
        }

        if (mKeyboard == null) {
            return
        }

        mCanvas!!.save()
        val canvas = mCanvas
        canvas!!.clipRect(mDirtyRect)
        val paint = mPaint
        val keys = mKeys
        paint.color = mTextColor
        val smallLetterPaint = Paint().apply {
            set(paint)
            color = paint.color.adjustAlpha(0.8f)
            textSize = mTopSmallNumberSize
            typeface = Typeface.DEFAULT
        }

        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)

        val keyCount = keys.size
        for (i in 0 until keyCount) {
            val key = keys[i]
            val code = key.code
            val labels = key.label
            var keyBackground = resources.getDrawable(R.drawable.keyboard_bg_normal, context.theme)
            when (code) {
                KEYCODE_SPACE -> {
                    keyBackground = resources.getDrawable(R.drawable.keyboard_bg_space, context.theme)
                }
                KEYCODE_ENTER -> {
                    keyBackground = resources.getDrawable(R.drawable.keyboard_bg_action, context.theme)
                }
                KEYCODE_MODE_CHANGE -> {
                    keyBackground = resources.getDrawable(R.drawable.keyboard_bg_change, context.theme)
                }
                KEYCODE_DELETE -> {
                    keyBackground = resources.getDrawable(R.drawable.keyboard_bg_attribute, context.theme)
                }
                KEYCODE_SHIFT -> {
                    keyBackground = resources.getDrawable(R.drawable.keyboard_bg_attribute, context.theme)
                }
                KEYCODE_EMOJI -> {
                    keyBackground = resources.getDrawable(R.drawable.keyboard_bg_attribute, context.theme)
                }

            }

            when (labels) {
                "," -> {
                    keyBackground = resources.getDrawable(R.drawable.keyboard_bg_attribute, context.theme)
                }
                "." -> {
                    keyBackground = resources.getDrawable(R.drawable.keyboard_bg_attribute, context.theme)
                }
            }

            // Switch the character to uppercase if shift is pressed
            val label = adjustCase(key.label)?.toString()
            val bounds = keyBackground!!.bounds
            if (key.width != bounds.right || key.height != bounds.bottom) {
                keyBackground.setBounds(0, 0, key.width, key.height)
            }

            keyBackground.state = when {
                key.pressed -> intArrayOf(android.R.attr.state_pressed)
                key.focused -> intArrayOf(android.R.attr.state_focused)
                else -> intArrayOf()
            }

            canvas.translate(key.x.toFloat(), key.y.toFloat())
            keyBackground.draw(canvas)
            if (label?.isNotEmpty() == true) {
                // For characters, use large font. For labels like "Done", use small font.
                if (label.length > 1) {
                    paint.textSize = mLabelTextSize.toFloat()
                    paint.typeface = Typeface.DEFAULT_BOLD
                } else {
                    paint.textSize = mKeyTextSize.toFloat()
                    paint.typeface = Typeface.DEFAULT
                }

                paint.color = if (key.focused) {
                    mPrimaryColor.getContrastColor()
                } else {
                    mTextColor
                }

                canvas.drawText(
                    label,
                    (key.width / 2).toFloat(),
                    key.height / 2 + (paint.textSize - paint.descent()) / 2,
                    paint
                )

                if (key.topSmallNumber.isNotEmpty()) {
                    canvas.drawText(key.topSmallNumber,
                        key.width - mTopSmallNumberMarginWidth,
                        mTopSmallNumberMarginHeight,
                        smallLetterPaint)
                }

                // Turn off drop shadow
                paint.setShadowLayer(0f, 0f, 0f, 0)
            } else if (key.icon != null && mKeyboard != null) {
                if (code == KEYCODE_SHIFT) {
                    val drawableId = when (mKeyboard!!.mShiftState) {
                        SHIFT_OFF -> R.drawable.ic_keyboard_caps_outline
                        SHIFT_ON_ONE_CHAR -> R.drawable.ic_keyboard_caps
                        else -> R.drawable.ic_keyboard_caps_underlined
                    }
                    key.icon = ResourcesCompat.getDrawable(resources, drawableId, null)
                }

                if (code == KEYCODE_DELETE || code == KEYCODE_SHIFT || code == KEYCODE_EMOJI || code == KEYCODE_ENTER) {
                    key.icon!!.applyColorFilter(mTextColor)
                }

                val drawableX = (key.width - key.icon!!.intrinsicWidth) / 2
                val drawableY = (key.height - key.icon!!.intrinsicHeight) / 2
                canvas.translate(drawableX.toFloat(), drawableY.toFloat())
                key.icon!!.setBounds(0, 0, key.icon!!.intrinsicWidth, key.icon!!.intrinsicHeight)
                key.icon!!.draw(canvas)
                canvas.translate(-drawableX.toFloat(), -drawableY.toFloat())
            }
            canvas.translate(-key.x.toFloat(), -key.y.toFloat())
        }

        // Overlay a dark rectangle to dim the keyboard
        if (mMiniKeyboardOnScreen) {
            paint.color = Color.BLACK.adjustAlpha(0.3f)
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        }

        mCanvas!!.restore()
        mDrawPending = false
        mDirtyRect.setEmpty()
    }

    private fun getPressedKeyIndex(x: Int, y: Int): Int {
        return mKeys.indexOfFirst {
            it.isInside(x, y)
        }
    }

    private fun detectAndSendKey(index: Int, x: Int, y: Int, eventTime: Long) {
        if (index != NOT_A_KEY && index < mKeys.size) {
            val key = mKeys[index]
            getPressedKeyIndex(x, y)
            mOnKeyboardActionListener!!.onKey(key.code)
            mLastTapTime = eventTime
        }
    }

    private fun showPreview(keyIndex: Int) {
        if (!ItemMainKeyboard.SHOW_POPUP_ON_KEYPRESS) {
            return
        }

        val oldKeyIndex = mCurrentKeyIndex
        val previewPopup = mPreviewPopup
        mCurrentKeyIndex = keyIndex
        // Release the old key and press the new key
        val keys = mKeys
        if (oldKeyIndex != mCurrentKeyIndex) {
            if (oldKeyIndex != NOT_A_KEY && keys.size > oldKeyIndex) {
                val oldKey = keys[oldKeyIndex]
                oldKey.pressed = false
                invalidateKey(oldKeyIndex)
                val keyCode = oldKey.code
                sendAccessibilityEventForUnicodeCharacter(AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUS_CLEARED,
                    keyCode)
            }

            if (mCurrentKeyIndex != NOT_A_KEY && keys.size > mCurrentKeyIndex) {
                val newKey = keys[mCurrentKeyIndex]

                val code = newKey.code
                if (code == KEYCODE_SHIFT || code == KEYCODE_MODE_CHANGE || code == KEYCODE_DELETE || code == KEYCODE_ENTER || code == KEYCODE_SPACE) {
                    newKey.pressed = true
                }

                invalidateKey(mCurrentKeyIndex)
                sendAccessibilityEventForUnicodeCharacter(AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED,
                    code)
            }
        }

        // If key changed and preview is on ...
        if (oldKeyIndex != mCurrentKeyIndex) {
            if (previewPopup.isShowing) {
                if (keyIndex == NOT_A_KEY) {
                    mHandler!!.sendMessageDelayed(
                        mHandler!!.obtainMessage(MSG_REMOVE_PREVIEW),
                        DELAY_AFTER_PREVIEW.toLong()
                    )
                }
            }

            if (keyIndex != NOT_A_KEY) {
                showKey(keyIndex)
            }
        }
    }

    private fun showKey(keyIndex: Int) {
        val previewPopup = mPreviewPopup
        val keys = mKeys
        if (keyIndex < 0 || keyIndex >= mKeys.size) {
            return
        }

        val key = keys[keyIndex]
        if (key.icon != null) {
            mPreviewText!!.setCompoundDrawables(null, null, null, key.icon)
        } else {
            if (key.label.length > 1) {
                mPreviewText!!.setTextSize(TypedValue.COMPLEX_UNIT_PX, mKeyTextSize.toFloat())
                mPreviewText!!.typeface = Typeface.DEFAULT_BOLD
            } else {
                mPreviewText!!.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    mPreviewTextSizeLarge.toFloat())
                mPreviewText!!.typeface = Typeface.DEFAULT
            }

            mPreviewText!!.setCompoundDrawables(null, null, null, null)
            try {
                mPreviewText!!.text = adjustCase(key.label)
            } catch (ignored: Exception) {
            }
        }


        val previewBackground = mPreviewText!!.background as LayerDrawable
        mPreviewText!!.background = previewBackground

        mPreviewText!!.setTextColor(mTextColor)
        mPreviewText!!.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
        val popupWidth = Math.max(mPreviewText!!.measuredWidth, key.width)
        val popupHeight = mPreviewHeight
        val lp = mPreviewText!!.layoutParams
        lp?.width = popupWidth
        lp?.height = popupHeight

        mPopupPreviewX = key.x
        mPopupPreviewY = key.y - popupHeight

        mHandler!!.removeMessages(MSG_REMOVE_PREVIEW)
        getLocationInWindow(mCoordinates)
        mCoordinates[0] += mMiniKeyboardOffsetX // Offset may be zero
        mCoordinates[1] += mMiniKeyboardOffsetY // Offset may be zero

        // Set the preview background state
        mPreviewText!!.background.state = if (key.popupResId != 0) {
            LONG_PRESSABLE_STATE_SET
        } else {
            EMPTY_STATE_SET
        }

        mPopupPreviewX += mCoordinates[0]
        mPopupPreviewY += mCoordinates[1]

        // If the popup cannot be shown above the key, put it on the side
        getLocationOnScreen(mCoordinates)
        if (mPopupPreviewY + mCoordinates[1] < 0) {
            // If the key you're pressing is on the left side of the keyboard, show the popup on
            // the right, offset by enough to see at least one key to the left/right.
            if (key.x + key.width <= width / 2) {
                mPopupPreviewX += (key.width * 2.5).toInt()
            } else {
                mPopupPreviewX -= (key.width * 2.5).toInt()
            }
            mPopupPreviewY += popupHeight
        }

        previewPopup.dismiss()

        if (key.label.isNotEmpty() && key.code != KEYCODE_MODE_CHANGE && key.code != KEYCODE_SHIFT) {
            previewPopup.width = popupWidth
            previewPopup.height = popupHeight
            previewPopup.showAtLocation(mPopupParent,
                Gravity.NO_GRAVITY,
                mPopupPreviewX,
                mPopupPreviewY)
            mPreviewText!!.visibility = VISIBLE
        }
    }

    private fun sendAccessibilityEventForUnicodeCharacter(eventType: Int, code: Int) {
        if (mAccessibilityManager.isEnabled) {
            val event = AccessibilityEvent.obtain(eventType)
            onInitializeAccessibilityEvent(event)
            val text: String = when (code) {
                KEYCODE_DELETE -> context.getString(R.string.keycode_delete)
                KEYCODE_ENTER -> context.getString(R.string.keycode_enter)
                KEYCODE_MODE_CHANGE -> context.getString(R.string.keycode_mode_change)
                KEYCODE_SHIFT -> context.getString(R.string.keycode_shift)
                else -> code.toChar().toString()
            }
            event.text.add(text)
            mAccessibilityManager.sendAccessibilityEvent(event)
        }
    }

    /**
     * Requests a redraw of the entire keyboard. Calling [.invalidate] is not sufficient because the keyboard renders the keys to an off-screen buffer and
     * an invalidate() only draws the cached buffer.
     */
    fun invalidateAllKeys() {
        mDirtyRect.union(0, 0, width, height)
        mDrawPending = true
        invalidate()
    }

    /**
     * Invalidates a key so that it will be redrawn on the next repaint. Use this method if only one key is changing it's content. Any changes that
     * affect the position or size of the key may not be honored.
     * @param keyIndex the index of the key in the attached [ItemMainKeyboard].
     */
    private fun invalidateKey(keyIndex: Int) {
        if (keyIndex < 0 || keyIndex >= mKeys.size) {
            return
        }

        val key = mKeys[keyIndex]
        mDirtyRect.union(
            key.x, key.y,
            key.x + key.width, key.y + key.height
        )
        onBufferDraw()
        invalidate(
            key.x, key.y,
            key.x + key.width, key.y + key.height
        )
    }

    private fun openPopupIfRequired(me: MotionEvent): Boolean {
        // Check if we have a popup layout specified first.
        if (mPopupLayout == 0) {
            return false
        }

        if (mCurrentKey < 0 || mCurrentKey >= mKeys.size) {
            return false
        }

        val popupKey = mKeys[mCurrentKey]
        val result = onLongPress(popupKey, me)
        if (result) {
            mAbortKey = true
            showPreview(NOT_A_KEY)
        }

        return result
    }

    /**
     * Called when a key is long pressed. By default this will open any popup keyboard associated with this key through the attributes
     * popupLayout and popupCharacters.
     * @param popupKey the key that was long pressed
     * @return true if the long press is handled, false otherwise. Subclasses should call the method on the base class if the subclass doesn't wish to
     * handle the call.
     */
    private fun onLongPress(popupKey: ItemMainKeyboard.Key, me: MotionEvent): Boolean {
        val popupKeyboardId = popupKey.popupResId
        if (popupKeyboardId != 0) {
            mMiniKeyboardContainer = mMiniKeyboardCache[popupKey]
            if (mMiniKeyboardContainer == null) {
                val inflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                mMiniKeyboardContainer = inflater.inflate(mPopupLayout, null)
                mMiniKeyboard = mMiniKeyboardContainer!!.findViewById<View>(R.id.mini_keyboard_view) as MainKeyboard

                mMiniKeyboard!!.mOnKeyboardActionListener = object : OnKeyboardActionListener {
                    override fun onKey(code: Int) {
                        mOnKeyboardActionListener!!.onKey(code)
                        dismissPopupKeyboard()
                    }

                    override fun onPress(primaryCode: Int) {
                        mOnKeyboardActionListener!!.onPress(primaryCode)
                    }

                    override fun onActionUp() {
                        mOnKeyboardActionListener!!.onActionUp()
                    }

                    override fun moveCursorLeft() {
                        mOnKeyboardActionListener!!.moveCursorLeft()
                    }

                    override fun moveCursorRight() {
                        mOnKeyboardActionListener!!.moveCursorRight()
                    }

                    override fun onText(text: String) {
                        mOnKeyboardActionListener!!.onText(text)
                    }
                }

                val keyboard = if (popupKey.popupCharacters != null) {
                    ItemMainKeyboard(context,
                        popupKeyboardId,
                        popupKey.popupCharacters!!,
                        popupKey.width)
                } else {
                    ItemMainKeyboard(context, popupKeyboardId, 0)
                }

                mMiniKeyboard!!.setKeyboard(keyboard)
                mPopupParent = this
                mMiniKeyboardContainer!!.measure(
                    MeasureSpec.makeMeasureSpec(width, MeasureSpec.AT_MOST),
                    MeasureSpec.makeMeasureSpec(height, MeasureSpec.AT_MOST)
                )
                mMiniKeyboardCache[popupKey] = mMiniKeyboardContainer
            } else {
                mMiniKeyboard =
                    mMiniKeyboardContainer!!.findViewById<View>(R.id.mini_keyboard_view) as MainKeyboard
            }

            getLocationInWindow(mCoordinates)
            mPopupX = popupKey.x
            mPopupY = popupKey.y

            val widthToUse =
                mMiniKeyboardContainer!!.measuredWidth - (popupKey.popupCharacters!!.length / 2) * popupKey.width
            mPopupX = mPopupX + popupKey.width - widthToUse
            mPopupY -= mMiniKeyboardContainer!!.measuredHeight
            val x = mPopupX + mCoordinates[0]
            val y = mPopupY + mCoordinates[1]
            val xOffset = max(0, x)
            mMiniKeyboard!!.setPopupOffset(xOffset, y)

            // make sure we highlight the proper key right after long pressing it, before any ACTION_MOVE event occurs
            val miniKeyboardX = if (xOffset + mMiniKeyboard!!.measuredWidth <= measuredWidth) {
                xOffset
            } else {
                measuredWidth - mMiniKeyboard!!.measuredWidth
            }

            val keysCnt = mMiniKeyboard!!.mKeys.size
            var selectedKeyIndex = floor((me.x - miniKeyboardX) / popupKey.width.toDouble()).toInt()
            if (keysCnt > MAX_KEYS_PER_MINI_ROW) {
                selectedKeyIndex += MAX_KEYS_PER_MINI_ROW
            }
            selectedKeyIndex = max(0, min(selectedKeyIndex, keysCnt - 1))

            for (i in 0 until keysCnt) {
                mMiniKeyboard!!.mKeys[i].focused = i == selectedKeyIndex
            }

            mMiniKeyboardSelectedKeyIndex = selectedKeyIndex
            mMiniKeyboard!!.invalidateAllKeys()

            val miniShiftStatus = if (isShifted()) SHIFT_ON_PERMANENT else SHIFT_OFF
            mMiniKeyboard!!.setShifted(miniShiftStatus)
            mPopupKeyboard.contentView = mMiniKeyboardContainer
            mPopupKeyboard.width = mMiniKeyboardContainer!!.measuredWidth
            mPopupKeyboard.height = mMiniKeyboardContainer!!.measuredHeight
            mPopupKeyboard.showAtLocation(this, Gravity.NO_GRAVITY, x, y)
            mMiniKeyboardOnScreen = true
            invalidateAllKeys()
            return true
        }
        return false
    }

    override fun onTouchEvent(me: MotionEvent): Boolean {
        val action = me.action

        if (ignoreTouches) {
            if (action == MotionEvent.ACTION_UP) {
                ignoreTouches = false

                // fix a glitch with long pressing backspace, then clicking some letter
                if (mRepeatKeyIndex != NOT_A_KEY) {
                    val key = mKeys[mRepeatKeyIndex]
                    if (key.code == KEYCODE_DELETE) {
                        mHandler?.removeMessages(MSG_REPEAT)
                        mRepeatKeyIndex = NOT_A_KEY
                    }
                }
            }
            return true
        }

        // handle moving between alternative popup characters by swiping
        if (mPopupKeyboard.isShowing) {
            when (action) {
                MotionEvent.ACTION_MOVE -> {
                    if (mMiniKeyboard != null) {
                        val coords = intArrayOf(0, 0)
                        mMiniKeyboard!!.getLocationOnScreen(coords)
                        val keysCnt = mMiniKeyboard!!.mKeys.size
                        val lastRowKeyCount = if (keysCnt > MAX_KEYS_PER_MINI_ROW) {
                            max(keysCnt % MAX_KEYS_PER_MINI_ROW, 1)
                        } else {
                            keysCnt
                        }

                        val widthPerKey = if (keysCnt > MAX_KEYS_PER_MINI_ROW) {
                            mMiniKeyboard!!.width / MAX_KEYS_PER_MINI_ROW
                        } else {
                            mMiniKeyboard!!.width / lastRowKeyCount
                        }

                        var selectedKeyIndex =
                            floor((me.x - coords[0]) / widthPerKey.toDouble()).toInt()
                        if (keysCnt > MAX_KEYS_PER_MINI_ROW) {
                            selectedKeyIndex = max(0, selectedKeyIndex)
                            selectedKeyIndex += MAX_KEYS_PER_MINI_ROW
                        }

                        selectedKeyIndex = max(0, min(selectedKeyIndex, keysCnt - 1))
                        if (selectedKeyIndex != mMiniKeyboardSelectedKeyIndex) {
                            for (i in 0 until keysCnt) {
                                mMiniKeyboard!!.mKeys[i].focused = i == selectedKeyIndex
                            }
                            mMiniKeyboardSelectedKeyIndex = selectedKeyIndex
                            mMiniKeyboard!!.invalidateAllKeys()
                        }

                        if (coords[0] > 0 || coords[1] > 0) {
                            if (coords[0] - me.x > mPopupMaxMoveDistance ||                                         // left
                                me.x - (coords[0] + mMiniKeyboard!!.measuredWidth) > mPopupMaxMoveDistance          // right
                            ) {
                                dismissPopupKeyboard()
                            }
                        }
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    mMiniKeyboard?.mKeys?.firstOrNull { it.focused }?.apply {
                        mOnKeyboardActionListener!!.onKey(code)
                    }
                    mMiniKeyboardSelectedKeyIndex = -1
                    dismissPopupKeyboard()
                }
            }
        }

        return onModifiedTouchEvent(me)
    }

    private fun onModifiedTouchEvent(me: MotionEvent): Boolean {
        var touchX = me.x.toInt()
        var touchY = me.y.toInt()
        if (touchY >= -mVerticalCorrection) {
            touchY += mVerticalCorrection
        }

        val action = me.actionMasked
        val eventTime = me.eventTime
        val keyIndex = getPressedKeyIndex(touchX, touchY)

        // Ignore all motion events until a DOWN.
        if (mAbortKey && action != MotionEvent.ACTION_DOWN && action != MotionEvent.ACTION_CANCEL) {
            return true
        }

        // Needs to be called after the gesture detector gets a turn, as it may have displayed the mini keyboard
        if (mMiniKeyboardOnScreen && action != MotionEvent.ACTION_CANCEL) {
            return true
        }

        when (action) {
            MotionEvent.ACTION_POINTER_DOWN -> {
                // if the user presses a key while still holding down the previous, type in both chars and ignore the later gestures
                // can happen at fast typing, easier to reproduce by increasing LONGPRESS_TIMEOUT
                ignoreTouches = true
                mHandler!!.removeMessages(MSG_LONGPRESS)
                dismissPopupKeyboard()
                detectAndSendKey(keyIndex, me.x.toInt(), me.y.toInt(), eventTime)

                val newPointerX = me.getX(1).toInt()
                val newPointerY = me.getY(1).toInt()
                val secondKeyIndex = getPressedKeyIndex(newPointerX, newPointerY)
                showPreview(secondKeyIndex)
                detectAndSendKey(secondKeyIndex, newPointerX, newPointerY, eventTime)

                val secondKeyCode = mKeys.getOrNull(secondKeyIndex)?.code
                if (secondKeyCode != null) {
                    mOnKeyboardActionListener!!.onPress(secondKeyCode)
                }

                showPreview(NOT_A_KEY)
                invalidateKey(mCurrentKey)
                return true
            }
            MotionEvent.ACTION_DOWN -> {
                mAbortKey = false
                mLastCodeX = touchX
                mLastCodeY = touchY
                mLastKeyTime = 0
                mCurrentKeyTime = 0
                mLastKey = NOT_A_KEY
                mCurrentKey = keyIndex
                mDownTime = me.eventTime
                mLastMoveTime = mDownTime

                val onPressKey = if (keyIndex != NOT_A_KEY) {
                    mKeys[keyIndex].code
                } else {
                    0
                }

                mOnKeyboardActionListener!!.onPress(onPressKey)

                var wasHandled = false
                if (mCurrentKey >= 0 && mKeys[mCurrentKey].repeatable) {
                    mRepeatKeyIndex = mCurrentKey

                    val msg = mHandler!!.obtainMessage(MSG_REPEAT)
                    mHandler!!.sendMessageDelayed(msg, REPEAT_START_DELAY.toLong())
                    // if the user long presses Space, move the cursor after swipine left/right
                    if (mKeys[mCurrentKey].code == KEYCODE_SPACE) {
                        mLastSpaceMoveX = -1
                    } else {
                        repeatKey(true)
                    }

                    // Delivering the key could have caused an abort
                    if (mAbortKey) {
                        mRepeatKeyIndex = NOT_A_KEY
                        wasHandled = true
                    }
                }

                if (!wasHandled && mCurrentKey != NOT_A_KEY) {
                    val msg = mHandler!!.obtainMessage(MSG_LONGPRESS, me)
                    mHandler!!.sendMessageDelayed(msg, LONGPRESS_TIMEOUT.toLong())
                }

                if (mPopupParent.id != R.id.mini_keyboard_view) {
                    showPreview(keyIndex)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                var continueLongPress = false
                if (keyIndex != NOT_A_KEY) {
                    if (mCurrentKey == NOT_A_KEY) {
                        mCurrentKey = keyIndex
                        mCurrentKeyTime = eventTime - mDownTime
                    } else {
                        if (keyIndex == mCurrentKey) {
                            mCurrentKeyTime += eventTime - mLastMoveTime
                            continueLongPress = true
                        } else if (mRepeatKeyIndex == NOT_A_KEY) {
                            mLastKey = mCurrentKey
                            mLastCodeX = mLastX
                            mLastCodeY = mLastY
                            mLastKeyTime = mCurrentKeyTime + eventTime - mLastMoveTime
                            mCurrentKey = keyIndex
                            mCurrentKeyTime = 0
                        }
                    }
                }

                if (mIsLongPressingSpace) {
                    if (mLastSpaceMoveX == -1) {
                        mLastSpaceMoveX = mLastX
                    }

                    val diff = mLastX - mLastSpaceMoveX
                    if (diff < -mSpaceMoveThreshold) {
                        for (i in diff / mSpaceMoveThreshold until 0) {
                            mOnKeyboardActionListener?.moveCursorLeft()
                        }
                        mLastSpaceMoveX = mLastX
                    } else if (diff > mSpaceMoveThreshold) {
                        for (i in 0 until diff / mSpaceMoveThreshold) {
                            mOnKeyboardActionListener?.moveCursorRight()
                        }
                        mLastSpaceMoveX = mLastX
                    }
                } else if (!continueLongPress) {
                    // Cancel old longpress
                    mHandler!!.removeMessages(MSG_LONGPRESS)
                    // Start new longpress if key has changed
                    if (keyIndex != NOT_A_KEY) {
                        val msg = mHandler!!.obtainMessage(MSG_LONGPRESS, me)
                        mHandler!!.sendMessageDelayed(msg, LONGPRESS_TIMEOUT.toLong())
                    }

                    if (mPopupParent.id != R.id.mini_keyboard_view) {
                        showPreview(mCurrentKey)
                    }
                    mLastMoveTime = eventTime
                }
            }
            MotionEvent.ACTION_UP -> {
                mLastSpaceMoveX = 0
                removeMessages()
                if (keyIndex == mCurrentKey) {
                    mCurrentKeyTime += eventTime - mLastMoveTime
                } else {
                    mLastKey = mCurrentKey
                    mLastKeyTime = mCurrentKeyTime + eventTime - mLastMoveTime
                    mCurrentKey = keyIndex
                    mCurrentKeyTime = 0
                }

                if (mCurrentKeyTime < mLastKeyTime && mCurrentKeyTime < DEBOUNCE_TIME && mLastKey != NOT_A_KEY) {
                    mCurrentKey = mLastKey
                    touchX = mLastCodeX
                    touchY = mLastCodeY
                }
                showPreview(NOT_A_KEY)
                Arrays.fill(mKeyIndices, NOT_A_KEY)
                // If we're not on a repeating key (which sends on a DOWN event)
                if (mRepeatKeyIndex == NOT_A_KEY && !mMiniKeyboardOnScreen && !mAbortKey) {
                    detectAndSendKey(mCurrentKey, touchX, touchY, eventTime)
                }

                if (mKeys.getOrNull(mCurrentKey)?.code == KEYCODE_SPACE && !mIsLongPressingSpace) {
                    detectAndSendKey(mCurrentKey, touchX, touchY, eventTime)
                }

                invalidateKey(keyIndex)
                mRepeatKeyIndex = NOT_A_KEY
                mOnKeyboardActionListener!!.onActionUp()
                mIsLongPressingSpace = false
            }
            MotionEvent.ACTION_CANCEL -> {
                mIsLongPressingSpace = false
                mLastSpaceMoveX = 0
                removeMessages()
                dismissPopupKeyboard()
                mAbortKey = true
                showPreview(NOT_A_KEY)
                invalidateKey(mCurrentKey)
            }
        }

        mLastX = touchX
        mLastY = touchY
        return true
    }

    private fun repeatKey(initialCall: Boolean): Boolean {
        val key = mKeys[mRepeatKeyIndex]
        if (!initialCall && key.code == KEYCODE_SPACE) {
            if (!mIsLongPressingSpace) {
                vibrateIfNeeded()
            }

            mIsLongPressingSpace = true
        } else {
            detectAndSendKey(mCurrentKey, key.x, key.y, mLastTapTime)
        }
        return true
    }

    private fun closing() {
        if (mPreviewPopup.isShowing) {
            mPreviewPopup.dismiss()
        }
        removeMessages()
        dismissPopupKeyboard()
        mBuffer = null
        mCanvas = null
        mMiniKeyboardCache.clear()
    }

    private fun removeMessages() {
        mHandler?.apply {
            removeMessages(MSG_REPEAT)
            removeMessages(MSG_LONGPRESS)
        }
    }

    public override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        closing()
    }

    private fun dismissPopupKeyboard() {
        if (mPopupKeyboard.isShowing) {
            mPopupKeyboard.dismiss()
            mMiniKeyboardOnScreen = false
            invalidateAllKeys()
        }
    }
}
