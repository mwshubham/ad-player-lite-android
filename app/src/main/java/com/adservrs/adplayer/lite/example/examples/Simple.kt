package com.adservrs.adplayer.lite.example.examples

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.adservrs.adplayer.lite.AdPlayerController
import com.adservrs.adplayer.lite.AdPlayerView
import com.adservrs.adplayer.lite.AdPlayerState
import com.adservrs.adplayer.lite.AdPlayerStateListener
import com.adservrs.adplayer.lite.example.PUB_ID
import com.adservrs.adplayer.lite.example.TAG_ID


private var isNotPaused = true
private const val TAG = "SimpleExample"
@Composable
fun SimpleExample(modifier: Modifier) {
    var isPlaying by remember { mutableStateOf(false) }
    var controller by remember { mutableStateOf<AdPlayerController?>(null) }

    Column(modifier = modifier.fillMaxSize()) {
        // Play/Pause Toggle Button
        Button(
            onClick = {
                controller?.let { controller ->
                    if (isPlaying) {
                        Log.d(TAG, "Pausing player, controller: $controller")
                        controller.pause()
                        isPlaying = false
                    } else {
                        Log.d(TAG, "Resuming player")
                        controller.resume()
                        isPlaying = true
                    }
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        ) {
            Text(if (isPlaying) "⏸️ Pause" else "▶️ Play")
        }

        // Ad Player View
        Box(modifier = Modifier.weight(1f)) {
            AndroidView(
                factory = { context ->
                    val view = AdPlayerView(context)
                    controller = view.load(
                        pubId = PUB_ID,
                        tagId = TAG_ID
                    ).apply {
                        // Add state listener
                        addStateListener(object : AdPlayerStateListener {
                            override fun onAdPlayerStateChanged(newState: AdPlayerState) {
                                Log.d(TAG, "State changed: $newState")
                                isPlaying = newState == AdPlayerState.Playing.Content || newState == AdPlayerState.Playing.AdVideo

                                // This is working.
//                                val pauseNow = newState == AdPlayerState.Playing.AdVideo

                                // This is not working.
                                val pauseNow = newState == AdPlayerState.Ready ||
                                        newState == AdPlayerState.Preparing

                                if (isNotPaused && pauseNow) {
                                    isNotPaused = false
                                    Log.d(TAG, "Pausing player, controller: $controller")
                                    controller?.pause()
                                }
                            }
                        })

                        // Add events listener
                        addEventsListener { event ->
                            Log.d(TAG, "Event received: $event")
                        }
                    }
//                    Log.d(TAG, "Playing player after load")
//                    controller?.resume()
                    view
                },
                onRelease = {
                    it.release()
                },
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
