package com.adservrs.adplayer.lite.example.examples

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.adservrs.adplayer.lite.AdPlayer
import com.adservrs.adplayer.lite.AdPlayerContentOverride
import com.adservrs.adplayer.lite.AdPlayerView
import com.adservrs.adplayer.lite.example.PUB_ID
import com.adservrs.adplayer.lite.example.TAG_ID
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import org.json.JSONArray
import org.json.JSONObject

private const val searchRequestUrl =
    "https://cms.manage.aniview.com/backend/api/videos/search?category={\"operator\": \"eq\", \"value\": \"cooking tutorial\"}"

@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
fun SearchResponseExample(modifier: Modifier = Modifier) {
    suspend fun fetch(): JSONArray {
        val client = HttpClient()
        val content = client.get(searchRequestUrl).bodyAsText()
        val json = JSONObject(content)
        json.optJSONArray("data")?.let { return it }
        json.optJSONArray("playlist")?.let { return it }
        error("No response found: ${json.toString(4)}")
    }

    val content = remember { mutableStateOf<Result<JSONArray>?>(null) }
    LaunchedEffect("request") {
        content.value = runCatching { fetch() }
    }

    val contentResult = content.value
    if (contentResult == null) {
        CircularProgressIndicator(
            modifier = modifier
                .fillMaxSize()
                .wrapContentSize()
        )
        return
    }

    val contentJson = contentResult.getOrNull()
    if (contentJson == null) {
        val message = contentResult.exceptionOrNull()?.message ?: "unknown error"
        Text(
            text = message,
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
        )
        return
    }

    AndroidView(
        factory = { context ->
            val tag = AdPlayer.getTag(context, pubId = PUB_ID, tagId = TAG_ID)
            val controller = tag.newInReadController {
                it.contentOverride = AdPlayerContentOverride.SearchContent(contentJson)
            }

            val view = AdPlayerView(context)
            view.attachController(controller)
            view
        },
        modifier = modifier.fillMaxSize(),
    )
}
