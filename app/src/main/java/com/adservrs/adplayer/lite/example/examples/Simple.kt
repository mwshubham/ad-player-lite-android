package com.adservrs.adplayer.lite.example.examples

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import com.adservrs.adplayer.lite.AdPlayerController
import com.adservrs.adplayer.lite.AdPlayerView
import com.adservrs.adplayer.lite.AdPlayerEvent
import com.adservrs.adplayer.lite.AdPlayerState

private const val TAG = "SimpleExample"

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SimpleExample(modifier: Modifier) {
    var controller by remember { mutableStateOf<AdPlayerController?>(null) }
    var isPlaying by remember { mutableStateOf(false) }
    var duration by remember { mutableStateOf<kotlin.time.Duration?>(null) }

    Column(modifier = modifier) {
        // Video Player
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            AndroidView(
                factory = {
                    val view = AdPlayerView(it)
                    val playerController = view.load(pubId = "693a712120421b6870002bc9", tagId = "696e3c4629f6cf0c2a02659c")
                    controller = playerController
                    playerController.addEventsListener { event ->
                        Log.d(TAG, "Event: $event")
                        // Check for video completion event
                        when (event) {
                            AdPlayerEvent.ContentPlaying -> {
                                isPlaying = true
                            }

                            AdPlayerEvent.ContentPaused -> {
                                isPlaying = false
                            }

                            is AdPlayerEvent.ContentVideoTimeChanged -> {
                                duration = event.duration
                            }

                            else -> {}
                        }
                    }
                    playerController.addStateListener { newState ->
                        Log.d(TAG, "New State: $newState")
                        when (newState) {
                            is AdPlayerState.Playing -> {
                                isPlaying = true
                                playerController.skipAd()
                            }

                            else -> {}
                        }
                    }
                    playerController.resume()
                    view
                },
                onRelease = {
                    it.release()
                },
                modifier = Modifier.fillMaxSize(),
            )
        }

        // Control Buttons Flow Layout
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 2
        ) {
            // Play/Pause Button
            Button(
                onClick = {
                    controller?.resume()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Play")
            }

            Button(
                onClick = {
                    controller?.pause()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Pause")
            }

            // Seek to End Button
            Button(
                onClick = {
                    Log.d(TAG, "Attempting to seek to end of video, duration: $duration")
                    controller?.setContentPosition(duration ?: return@Button)
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Seek to End")
            }

            // Seek to Start & Pause Button
            Button(
                onClick = {
                    Log.d(TAG, "Attempting to seek to start and pause video")
                    controller?.setContentPosition(kotlin.time.Duration.ZERO)
                    controller?.pause()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Seek to Start & Pause")
            }
        }
    }
}
