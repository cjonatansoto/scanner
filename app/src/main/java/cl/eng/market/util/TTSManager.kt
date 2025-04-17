package cl.eng.market.util

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.Locale
import android.util.Log // Import Log for debugging

class TTSManager(context: Context) {
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("es", "CL"))
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTSManager", "Language not supported or data missing")
                } else {
                    Log.d("TTSManager", "TTS initialized successfully for Spanish (Chile)")
                }
            } else {
                Log.e("TTSManager", "TTS initialization failed with status: $status")
            }
        }
    }

    fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        Log.d("TTSManager", "Speaking: $text") // Debug log
    }

    fun shutdown() {
        tts?.shutdown()
        Log.d("TTSManager", "TTS shutdown") // Debug log
    }
}
