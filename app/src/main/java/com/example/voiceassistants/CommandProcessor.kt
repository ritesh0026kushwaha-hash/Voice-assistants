package com.example.voiceassistants

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat

class CommandProcessor(
    private val context: Context,
    private val tts: TtsManager
) {

    fun process(command: String) {

        val text = command
            .trim()
            .lowercase()

        when {

            text == "home" ||
            text.contains("होम") ||
            text.contains("होम स्क्रीन") -> {

                if (VoiceAccessibilityService.goHome()) {
                    tts.speak("होम स्क्रीन खोल रहा हूँ")
                } else {
                    tts.speak(
                        "Accessibility permission चालू करें"
                    )
                }
            }

            text == "back" ||
            text.contains("पीछे जाओ") ||
            text.contains("बैक जाओ") -> {

                if (VoiceAccessibilityService.goBack()) {
                    tts.speak("पीछे जा रहा हूँ")
                } else {
                    tts.speak(
                        "Accessibility permission चालू करें"
                    )
                }
            }

            text.contains("recent") ||
            text.contains("रीसेंट") -> {

                if (VoiceAccessibilityService.openRecents()) {
                    tts.speak("Recent apps खोल रहा हूँ")
                } else {
                    tts.speak(
                        "Accessibility permission चालू करें"
                    )
                }
            }

            text.contains("सेटिंग") ||
            text.contains("settings") -> {

                open(Settings.ACTION_SETTINGS)

                tts.speak("Settings खोल रहा हूँ")
            }

            text.contains("वाईफाई") ||
            text.contains("wi-fi") ||
            text.contains("wifi") -> {

                open(Settings.ACTION_WIFI_SETTINGS)

                tts.speak(
                    "Wi-Fi settings खोल रहा हूँ"
                )
            }

            text.contains("ब्लूटूथ") ||
            text.contains("bluetooth") -> {

                open(Settings.ACTION_BLUETOOTH_SETTINGS)

                tts.speak(
                    "Bluetooth settings खोल रहा हूँ"
                )
            }

            text.contains("वॉल्यूम बढ़ा") ||
            text.contains("volume up") -> {

                volumeUp()

                tts.speak("Volume बढ़ा दिया")
            }

            text.contains("वॉल्यूम कम") ||
            text.contains("volume down") -> {

                volumeDown()

                tts.speak("Volume कम कर दिया")
            }

            text.contains("youtube") ||
            text.contains("यूट्यूब") -> {

                openApp("com.google.android.youtube")

                tts.speak("YouTube खोल रहा हूँ")
            }

            text.contains("whatsapp") ||
            text.contains("व्हाट्सएप") -> {

                openApp("com.whatsapp")

                tts.speak("WhatsApp खोल रहा हूँ")
            }

            text.contains("chrome") ||
            text.contains("क्रोम") -> {

                openApp("com.android.chrome")

                tts.speak("Chrome खोल रहा हूँ")
            }

            text.startsWith("call ") ||
            text.startsWith("कॉल ") -> {

                val number =
                    text.substringAfter(" ")
                        .replace(" ", "")
                        .trim()

                if (number.isNotEmpty()) {
                    makeCall(number)
                } else {
                    tts.speak("नंबर बताइए")
                }
            }

            text.contains("tap") ||
            text.contains("टैप") -> {

                tts.speak(
                    "Tap action के लिए coordinates चाहिए"
                )
            }

            text.contains("swipe") ||
            text.contains("स्वाइप") -> {

                tts.speak(
                    "Swipe action के लिए direction चाहिए"
                )
            }

            else -> {

                tts.speak(
                    "मैंने सुना: $command"
                )
            }
        }
    }

    private fun open(action: String) {

        try {

            val intent = Intent(action)

            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )

            context.startActivity(intent)

        } catch (_: Exception) {

            tts.speak(
                "यह setting उपलब्ध नहीं है"
            )
        }
    }

    private fun openApp(packageName: String) {

        val intent =
            context.packageManager
                .getLaunchIntentForPackage(
                    packageName
                )

        if (intent == null) {

            tts.speak(
                "यह app फोन में नहीं मिला"
            )

            return
        }

        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
        )

        context.startActivity(intent)
    }

    private fun volumeUp() {

        val audio =
            context.getSystemService(
                Context.AUDIO_SERVICE
            ) as AudioManager

        audio.adjustVolume(
            AudioManager.ADJUST_RAISE,
            AudioManager.FLAG_SHOW_UI
        )
    }

    private fun volumeDown() {

        val audio =
            context.getSystemService(
                Context.AUDIO_SERVICE
            ) as AudioManager

        audio.adjustVolume(
            AudioManager.ADJUST_LOWER,
            AudioManager.FLAG_SHOW_UI
        )
    }

    private fun makeCall(number: String) {

        val granted =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED

        if (!granted) {

            tts.speak(
                "Call permission नहीं मिली"
            )

            return
        }

        val intent = Intent(
            Intent.ACTION_CALL,
            Uri.parse("tel:$number")
        )

        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
        )

        context.startActivity(intent)

        tts.speak("Call कर रहा हूँ")
    }
}
