package com.example.voiceassistants

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

class VoiceAccessibilityService :
    AccessibilityService() {

    companion object {

        private var service:
            VoiceAccessibilityService? = null

        fun goHome(): Boolean {
            return service?.performGlobalAction(
                GLOBAL_ACTION_HOME
            ) == true
        }

        fun goBack(): Boolean {
            return service?.performGlobalAction(
                GLOBAL_ACTION_BACK
            ) == true
        }

        fun openRecents(): Boolean {
            return service?.performGlobalAction(
                GLOBAL_ACTION_RECENTS
            ) == true
        }

        fun tap(
            x: Float,
            y: Float
        ): Boolean {

            val current = service ?: return false

            val path = Path()

            path.moveTo(x, y)

            val gesture =
                GestureDescription.Builder()
                    .addStroke(
                        GestureDescription.StrokeDescription(
                            path,
                            0,
                            80
                        )
                    )
                    .build()

            return current.dispatchGesture(
                gesture,
                null,
                null
            )
        }

        fun swipeUp(): Boolean {

            val current = service ?: return false

            val path = Path()

            path.moveTo(500f, 1500f)
            path.lineTo(500f, 500f)

            val gesture =
                GestureDescription.Builder()
                    .addStroke(
                        GestureDescription.StrokeDescription(
                            path,
                            0,
                            500
                        )
                    )
                    .build()

            return current.dispatchGesture(
                gesture,
                null,
                null
            )
        }
    }

    override fun onServiceConnected() {

        super.onServiceConnected()

        service = this
    }

    override fun onAccessibilityEvent(
        event: AccessibilityEvent?
    ) {
        // Screen events are not automatically sent anywhere.
    }

    override fun onInterrupt() {
    }

    override fun onDestroy() {

        if (service === this) {
            service = null
        }

        super.onDestroy()
    }
    }
