package com.frogobox.libkeyboard.common.sound

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.util.Log
import androidx.annotation.RawRes
import com.frogobox.libkeyboard.R
import java.util.concurrent.ConcurrentHashMap
import kotlin.random.Random

/**
 * Mechanical Keyboard Switch Profiles (Mechvibes compatible)
 */
enum class MechanicalSoundType(
    val id: String,
    val title: String,
    val description: String,
    @RawRes val rawResId: Int
) {
    OFF("off", "Off (Mute)", "Silent typing with no acoustic feedback", 0),
    SYSTEM_CLICK("system", "System Default Click", "Native Android system keyclick sound effect", -1),
    CHERRY_MX_BLUE("cherry_mx_blue", "Cherry MX Blue", "Crisp tactile click with high-frequency snap", R.raw.mechanical_cherry_blue),
    CHERRY_MX_BROWN("cherry_mx_brown", "Cherry MX Brown", "Balanced tactile bump with rounded acoustic thock", R.raw.mechanical_cherry_brown),
    CHERRY_MX_RED("cherry_mx_red", "Cherry MX Red", "Smooth linear travel with clean bottom-out thock", R.raw.mechanical_cherry_red),
    TYPEWRITER("typewriter", "Classic Typewriter", "Vintage mechanical typewriter strike & carriage clink", R.raw.mechanical_typewriter);

    companion object {
        fun fromId(id: String?): MechanicalSoundType {
            return entries.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: CHERRY_MX_BLUE
        }
    }
}

/**
 * Low-latency SoundPool Audio Engine for Mechanical Keyboard feedback.
 */
class MechanicalSoundManager private constructor(context: Context) {

    companion object {
        private const val TAG = "MechanicalSoundManager"
        const val PREF_KEYBOARD_SOUND_ENABLED = "KEYBOARD_SOUND_ENABLED"
        const val PREF_KEYBOARD_SOUND_TYPE = "KEYBOARD_SOUND_TYPE"
        const val PREF_KEYBOARD_SOUND_VOLUME = "KEYBOARD_SOUND_VOLUME"
        const val PREF_KEYBOARD_VIBRATE_ENABLED = "KEYBOARD_VIBRATE_ENABLED"

        @Volatile
        private var INSTANCE: MechanicalSoundManager? = null

        fun getInstance(context: Context): MechanicalSoundManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: MechanicalSoundManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    private val appContext = context.applicationContext
    private val audioManager = appContext.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
    private var soundPool: SoundPool? = null
    private val soundIdMap = ConcurrentHashMap<MechanicalSoundType, Int>()
    private val loadedSounds = ConcurrentHashMap<Int, Boolean>()

    init {
        initSoundPool()
    }

    private fun initSoundPool() {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(8)
            .setAudioAttributes(audioAttributes)
            .build().apply {
                setOnLoadCompleteListener { _, sampleId, status ->
                    if (status == 0) {
                        loadedSounds[sampleId] = true
                    }
                }
            }

        preloadSounds()
    }

    private fun preloadSounds() {
        val pool = soundPool ?: return
        for (type in MechanicalSoundType.entries) {
            if (type.rawResId > 0) {
                try {
                    val soundId = pool.load(appContext, type.rawResId, 1)
                    soundIdMap[type] = soundId
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to load sound buffer for $type", e)
                }
            }
        }
    }

    fun playKeySound(
        soundType: MechanicalSoundType = MechanicalSoundType.CHERRY_MX_BLUE,
        volume: Float = 0.8f
    ) {
        if (soundType == MechanicalSoundType.OFF) return

        if (soundType == MechanicalSoundType.SYSTEM_CLICK) {
            audioManager?.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD, volume)
            return
        }

        val pool = soundPool ?: return
        val soundId = soundIdMap[soundType] ?: return

        // Slight human acoustic jitter (+/- 2% pitch variation)
        val pitchVariation = 0.98f + Random.nextFloat() * 0.04f
        val clampedVol = volume.coerceIn(0.05f, 1.0f)

        pool.play(soundId, clampedVol, clampedVol, 1, 0, pitchVariation)
    }

    fun release() {
        soundPool?.release()
        soundPool = null
        soundIdMap.clear()
        loadedSounds.clear()
    }
}
