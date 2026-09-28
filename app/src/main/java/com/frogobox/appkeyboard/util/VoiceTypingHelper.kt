package com.frogobox.appkeyboard.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.core.content.ContextCompat
import java.util.Locale

/**
 * Native SpeechRecognizer wrapper for on-keyboard voice typing dictation.
 * Streams recognized words directly into the IME input connection with lifecycle safety and amplitude callbacks.
 */
class VoiceTypingHelper(
    private val context: Context
) {

    enum class State {
        IDLE,
        INITIALIZING,
        LISTENING,
        PROCESSING,
        ERROR
    }

    var currentState: State = State.IDLE
        private set

    val isListening: Boolean
        get() = currentState == State.LISTENING

    private var speechRecognizer: SpeechRecognizer? = null

    var onTextReceived: ((text: String, isFinal: Boolean) -> Unit)? = null
    var onRmsChanged: ((amplitude: Float) -> Unit)? = null
    var onStateChanged: ((State) -> Unit)? = null
    var onErrorOccurred: ((errorMessage: String) -> Unit)? = null

    /**
     * Checks if the device supports speech recognition.
     */
    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    /**
     * Checks if RECORD_AUDIO permission has been granted.
     */
    fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Starts voice recognition listening session.
     */
    fun startListening(): Boolean {
        if (!hasPermission()) {
            notifyError("Izin mikrofon (RECORD_AUDIO) belum diberikan.")
            return false
        }

        if (!isAvailable()) {
            notifyError("Layanan Pengenalan Suara tidak tersedia di perangkat ini.")
            return false
        }

        try {
            stopListening()
            ensureRecognizer()

            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toLanguageTag())
                putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            }

            speechRecognizer?.startListening(intent)
            updateState(State.INITIALIZING)
            return true
        } catch (e: Exception) {
            notifyError("Gagal memulai pengetikan suara: ${e.localizedMessage}")
            return false
        }
    }

    /**
     * Stops the active listening session.
     */
    fun stopListening() {
        try {
            speechRecognizer?.stopListening()
        } catch (_: Exception) {
        }
        updateState(State.IDLE)
        onRmsChanged?.invoke(0f)
    }

    /**
     * Releases the speech recognizer resources completely.
     * Must be called during onFinishInputView or onDestroy.
     */
    fun destroy() {
        stopListening()
        try {
            speechRecognizer?.destroy()
        } catch (_: Exception) {
        }
        speechRecognizer = null
        updateState(State.IDLE)
    }

    private fun ensureRecognizer() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                setRecognitionListener(createListener())
            }
        }
    }

    private fun updateState(newState: State) {
        currentState = newState
        onStateChanged?.invoke(newState)
    }

    private fun notifyError(message: String) {
        updateState(State.ERROR)
        onErrorOccurred?.invoke(message)
    }

    private fun createListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                updateState(State.LISTENING)
            }

            override fun onBeginningOfSpeech() {
                updateState(State.LISTENING)
            }

            override fun onRmsChanged(rmsdB: Float) {
                // Normalize rmsdB (-2dB to ~10dB) into 0.0f..1.0f range
                val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                onRmsChanged?.invoke(normalized)
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                updateState(State.PROCESSING)
                onRmsChanged?.invoke(0f)
            }

            override fun onError(error: Int) {
                val errorMsg = when (error) {
                    SpeechRecognizer.ERROR_AUDIO -> "Kesalahan perekaman audio."
                    SpeechRecognizer.ERROR_CLIENT -> "Kesalahan klien pengenalan suara."
                    SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Izin mikrofon diperlukan."
                    SpeechRecognizer.ERROR_NETWORK -> "Kesalahan jaringan pengenalan suara."
                    SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Koneksi jaringan suara batas waktu habis."
                    SpeechRecognizer.ERROR_NO_MATCH -> "Tidak ada suara yang terdeteksi."
                    SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Layanan suara sedang sibuk."
                    SpeechRecognizer.ERROR_SERVER -> "Kesalahan server suara."
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Waktu bicara telah habis."
                    else -> "Kesalahan pengenalan suara (kode: $error)."
                }
                notifyError(errorMsg)
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val recognizedText = matches?.firstOrNull().orEmpty()
                if (recognizedText.isNotBlank()) {
                    onTextReceived?.invoke(recognizedText, true)
                }
                updateState(State.IDLE)
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val partialText = matches?.firstOrNull().orEmpty()
                if (partialText.isNotBlank()) {
                    onTextReceived?.invoke(partialText, false)
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }
    }
}
