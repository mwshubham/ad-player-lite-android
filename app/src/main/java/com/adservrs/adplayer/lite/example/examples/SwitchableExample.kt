package com.adservrs.adplayer.lite.example.examples

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.adservrs.adplayer.lite.AdPlayerView
import com.adservrs.adplayer.lite.AdPlayerController
import com.adservrs.adplayer.lite.example.PUB_ID
import com.adservrs.adplayer.lite.example.TAG_ID
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import android.view.ViewGroup
import org.json.JSONObject

private val contentControlKeys = setOf(
    "primaryPlayButton",
    "primaryPauseButton",
    "primaryReplayButton",
    "primaryPrevButton",
    "primaryNextButton",
    "primaryFastForwardButton",
    "primaryFastBackwardButton",
    "secondaryPlayButton",
    "secondaryPauseButton",
    "secondaryReplayButton",
    "secondaryPrevButton",
    "secondaryNextButton",
    "secondaryFastForwardButton",
    "secondaryFastBackwardButton",
    "closeButton",
    "closeFullscreenButton",
    "playlistButton",
    "readMoreButton",
    "fullscreenButton",
    "volumeButton",
    "stayButton",
    "autoSkip",
    "nextPreview",
    "timeline",
    "duration",
)

@Composable
fun SwitchableExample(modifier: Modifier) {
    var isTopContainer by remember { mutableStateOf(true) }
    var controlsVisible by remember { mutableStateOf(true) }

    // Create a single AdPlayerView instance that will be reused
    val playerView = remember {
        mutableStateOf<AdPlayerView?>(null)
    }

    // Controller reference for managing content config
    val controller = remember { mutableStateOf<AdPlayerController?>(null) }

    // Function to hide all controls
    val hideAllControls: () -> Unit = {
        if (controlsVisible) {
            controlsVisible = false
            controller.value?.let { ctrl ->
                val config = JSONObject()
                for (key in contentControlKeys) {
                    config.put(key, JSONObject().put("enable", false))
                }
                ctrl.mergeContentConfig(config)
            }
        }
    }


    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Container
        PlayerContainer(
            modifier = Modifier.weight(4f),
            isActive = isTopContainer,
            activeLabel = "Active Player Container (Top)",
            inactiveLabel = "Top Container (Inactive)",
            playerView = playerView,
            controller = controller
        )

        // Buttons Row
        ButtonRow(
            onSwitchView = { isTopContainer = !isTopContainer },
            controlsVisible = controlsVisible,
            onHideControls = hideAllControls
        )

        // Bottom Container
        PlayerContainer(
            modifier = Modifier.weight(3f),
            isActive = !isTopContainer,
            activeLabel = "Active Player Container (Bottom)",
            inactiveLabel = "Bottom Container (Inactive)",
            playerView = playerView,
            controller = controller
        )
    }

    // Release the player when the entire SwitchableExample is disposed
    DisposableEffect(Unit) {
        onDispose {
            playerView.value?.release()
        }
    }
}

@Composable
fun PlayerContainer(
    modifier: Modifier,
    isActive: Boolean,
    activeLabel: String,
    inactiveLabel: String,
    playerView: MutableState<AdPlayerView?>,
    controller: MutableState<AdPlayerController?>
) {
    Box(
        modifier = modifier
            .background(
                if (isActive) Color.Green.copy(alpha = 0.3f)
                else Color.LightGray.copy(alpha = 0.5f)
            )
    ) {
        if (isActive) {
            VideoPlayerContainer(
                modifier = Modifier.fillMaxSize(),
                playerView = playerView,
                controller = controller
            )
            Text(
                text = activeLabel,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(Color.Black.copy(alpha = 0.7f))
                    .padding(4.dp),
                color = Color.White
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = inactiveLabel,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(16.dp),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun VideoPlayerContainer(
    modifier: Modifier,
    playerView: MutableState<AdPlayerView?>,
    controller: MutableState<AdPlayerController?>
) {
    AndroidView(
        factory = { context ->
            // Always create the player if it doesn't exist
            val view = playerView.value ?: run {
                val newView = AdPlayerView(context)
                playerView.value = newView
                controller.value = newView.load(pubId = PUB_ID, tagId = TAG_ID)
                newView
            }

            // Remove from current parent if it has one
            (view.parent as? ViewGroup)?.removeView(view)
            view
        },
        update = { view ->
            // Ensure the view is detached from any previous parent when switching
            (view.parent as? ViewGroup)?.removeView(view)
        },
        modifier = modifier,
    )
}

@Composable
fun ButtonRow(
    onSwitchView: () -> Unit,
    controlsVisible: Boolean,
    onHideControls: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Button(
            onClick = onSwitchView
        ) {
            Text("Switch View")
        }

        Button(
            onClick = onHideControls,
            enabled = controlsVisible
        ) {
            Text(if (controlsVisible) "🙈 Hide Controls" else "Controls Hidden")
        }
    }
}

