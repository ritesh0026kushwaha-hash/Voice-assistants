package com.example.voiceassistants

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder

class VoiceAssistantService : Service() {

    companion object {
        private const val CHANNEL_ID = "voice_assistants"
        private const val NOTIFICATION_ID = 100
    }

    private lateinit var speechManager: SpeechManager
    private lateinit var ttsManager: TtsManager

    override fun onCreate() {
        super.onCreate()

        createNotificationChannel()

        val notification = Notification.Builder(
            this,
            CHANNEL_ID
        )
            .setContentTitle("Voice Assistants")
            .setContentText("Voice assistant is active")
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .build()

        startForeground(
            NOTIFICATION_ID,
            notification
        )

        ttsManager = TtsManager(this)

        speechManager = SpeechManager(
            context = this,

            onResult = { command ->
                CommandProcessor(
                    this,
                    ttsManager
                ).process(command)
            },

            onError = { error ->
                ttsManager.speak(error)
            }
        )

        speechManager.start()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        speechManager.start()

        return START_STICKY
    }

    override fun onDestroy() {

        speechManager.destroy()
        ttsManager.shutdown()

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {

        val manager =
            getSystemService(NotificationManager::class.java)

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Voice Assistants",
            NotificationManager.IMPORTANCE_LOW
        )

        manager.createNotificationChannel(channel)
    }
}
