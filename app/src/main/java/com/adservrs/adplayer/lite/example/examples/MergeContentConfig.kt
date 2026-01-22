package com.adservrs.adplayer.lite.example.examples

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.adservrs.adplayer.lite.AdPlayerController
import com.adservrs.adplayer.lite.AdPlayerView
import com.adservrs.adplayer.lite.example.PUB_ID
import com.adservrs.adplayer.lite.example.TAG_ID
import com.adservrs.adplayer.lite.example.utils.toToggleableState
import org.json.JSONArray
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
    "logo",
    "title",
    "sharing",
    "playbackRates",
    "textTracks",
    "videoTracks",
)

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
fun MergeContentConfigExample(modifier: Modifier) {
    val controller = remember { mutableStateOf<AdPlayerController?>(null) }
    val contentControls = remember { mutableStateMapOf<String, Boolean>() }

    Column(modifier = modifier) {
        Text(
            text = "Note: this will only work for [in-stream] tags",
            textAlign = TextAlign.Center,
            color = Color.Red,
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
        )

        AndroidView(
            factory = {
                val view = AdPlayerView(it)
                controller.value = view.load(pubId = PUB_ID, tagId = TAG_ID)
                controller.value?.hideControls()
                view
            },
            onRelease = {
                it.release()
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize(),
        ) {
            for (control in contentControlKeys) {
                item {
                    val state = contentControls[control] ?: false
                    ListItem(
                        headlineContent = { Text(control) },
                        trailingContent = {
                            TriStateCheckbox(
                                state = state.toToggleableState(),
                                onClick = {
                                    contentControls[control] = state == false

                                    val config1 = JSONObject()
                                    val config2 = JSONObject().put(
                                        "matches",
                                        JSONObject().put("device", JSONArray().put("mobile"))
                                    )
                                    for ((key, value) in contentControls) {
                                        config1.put(key, JSONObject().put("enable", value))
                                        config2.put(key, JSONObject().put("enable", value))
                                    }
                                    controller.value?.mergeContentConfig(config1)
                                    controller.value?.mergeContentConfig(config2)
                                },
                            )
                        },
                    )
                }
            }
        }
    }
}

fun AdPlayerController.hideControls() {
    // Use in case of Ads.
    showPlayButton.value = false
    showFullscreenButton.value = false

    // Used in case of content.
    val config1 = JSONObject()
    val config2 = JSONObject().put(
        "matches",
        JSONObject().put("device", JSONArray().put("mobile"))
    )
    for (key in contentControlKeys) {
        config1.put(key, JSONObject().put("enable", false))
        config2.put(key, JSONObject().put("enable", false))
    }
    mergeContentConfig(config1)
    mergeContentConfig(config2)
}
