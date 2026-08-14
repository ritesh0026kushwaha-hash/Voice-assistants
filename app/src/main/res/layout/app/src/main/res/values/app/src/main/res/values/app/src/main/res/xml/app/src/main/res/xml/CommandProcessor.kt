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

        val text = command.trim().lowercase()

        when {

            // ---------- APPS ----------

            text.contains("youtube") ||
            text.contains("यूट्यूब") -> {
                confirm("YouTube खोल रहा हूँ")
                openApp("com.google.android.youtube")
            }

            text.contains("whatsapp") ||
            text.contains("व्हाट्सएप") -> {
                confirm("WhatsApp खोल रहा हूँ")
                openApp("com.whatsapp")
            }

            text.contains("chrome") ||
            text.contains("क्रोम") ||
            text.contains("ब्राउज़र") -> {
                confirm("Chrome खोल रहा हूँ")
                openApp("com.android.chrome")
            }

            text.contains("camera") ||
            text.contains("कैमरा") -> {
                confirm("Camera खोल रहा हूँ")
                openCamera()
            }

            // ---------- NAVIGATION ----------

            text == "home" ||
            text.contains("होम स्क्रीन") ||
            text.contains("होम जाओ") -> {

                confirm("Home screen पर जा रहा हूँ")

                if (!VoiceAccessibilityService.goHome()) {
                    permissionMessage()
                }
            }

            text == "back" ||
            text.contains("पीछे जाओ") ||
            text.contains("बैक जाओ") -> {

                confirm("पीछे जा रहा हूँ")

                if (!VoiceAccessibilityService.goBack()) {
                    permissionMessage()
                }
            }

            text.contains("recent") ||
            text.contains("रीसेंट") -> {

                confirm("Recent apps खोल रहा हूँ")

                if (!VoiceAccessibilityService.openRecents()) {
                    permissionMessage()
                }
            }

            // ---------- SETTINGS ----------

            text.contains("settings") ||
            text.contains("सेटिंग") -> {

                confirm("Settings खोल रहा हूँ")
                open(Settings.ACTION_SETTINGS)
            }

            text.contains("wifi") ||
            text.contains("वाईफाई") ||
            text.contains("वाई-फाई") -> {

                confirm("Wi-Fi settings खोल रहा हूँ")
                open(Settings.ACTION_WIFI_SETTINGS)
            }

            text.contains("bluetooth") ||
            text.contains("ब्लूटूथ") -> {

                confirm("Bluetooth settings खोल रहा हूँ")
                open(Settings.ACTION_BLUETOOTH_SETTINGS)
            }

            // ---------- VOLUME ----------

            text.contains("volume up") ||
            text.contains("वॉल्यूम बढ़ा") ||
            text.contains("आवाज़ बढ़ा") -> {

                confirm("Volume बढ़ा रहा हूँ")
                volumeUp()
            }

            text.contains("volume down") ||
            text.contains("वॉल्यूम कम") ||
            text.contains("आवाज़ कम") -> {

                confirm("Volume कम कर रहा हूँ")
                volumeDown()
            }

            text.contains("mute") ||
            text.contains("म्यूट") ||
            text.contains("आवाज़ बंद") -> {

                confirm("Phone को mute कर रहा हूँ")
                mute()
            }

            // ---------- CALL ----------

            text.startsWith("call ") ||
            text.startsWith("कॉल ") -> {

                val number = text
                    .substringAfter(" ")
                    .replace(" ", "")
                    .trim()

                if (number.isNotEmpty()) {

                    confirm("$number पर call कर रहा हूँ")
                    makeCall(number)

                } else {

                    tts.speak(
                        "Boss, किसे call करना है?"
                    )
                }
            }

            // ---------- SCREEN ACTIONS ----------

            text.contains("scroll down") ||
            text.contains("नीचे स्क्रोल") ||
            text.contains("नीचे करो") -> {

                confirm("नीचे scroll कर रहा हूँ")

                if (!VoiceAccessibilityService.swipeUp()) {
                    permissionMessage()
                }
            }

            // ---------- UNKNOWN ----------

            else -> {

                tts.speak(
                    "OK Boss, आपका command मिला। " +
                    "लेकिन इस काम के लिए अभी action उपलब्ध नहीं है।"
                )
            }
        }
    }

    // हर action के लिए common confirmation
    private fun confirm(action: String) {
        tts.speak("OK Boss, $action")
    }

    private fun permissionMessage() {
        tts.speak(
            "Boss, Phone Control permission चालू करनी होगी"
        )
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
                "Boss, ये काम नहीं कर पाया"
            )
        }
    }

    private fun openApp(packageName: String) {

        val intent =
            context.packageManager
                .getLaunchIntentForPackage(packageName)

        if (intent == null) {

            tts.speak(
                "Boss, ये app phone में नहीं मिला"
            )

            return
        }

        intent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK
        )

        context.startActivity(intent)
    }

    private fun openCamera() {

        try {

            val intent = Intent(
                android.provider.MediaStore.ACTION_IMAGE_CAPTURE
            )

            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )

            context.startActivity(intent)

        } catch (_: Exception) {

            tts.speak(
                "Boss, Camera नहीं खोल पाया"
            )
        }
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

    private fun mute() {

        val audio =
            context.getSystemService(
                Context.AUDIO_SERVICE
            ) as AudioManager

        audio.adjustVolume(
            AudioManager.ADJUST_MUTE,
            AudioManager.FLAG_SHOW_UI
        )
    }

    private fun makeCall(number: String) {

        val permission =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            )

        if (
            permission !=
            PackageManager.PERMISSION_GRANTED
        ) {

            tts.speak(
                "Boss, call permission नहीं मिली"
            )

            return
        }

        try {

            val intent = Intent(
                Intent.ACTION_CALL,
                Uri.parse("tel:$number")
            )

            intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
            )

            context.startActivity(intent)

        } catch (_: Exception) {

            tts.speak(
                "Boss, call नहीं कर पाया"
            )
        }
    }
}
