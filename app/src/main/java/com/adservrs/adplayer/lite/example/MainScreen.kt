package com.adservrs.adplayer.lite.example

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.adservrs.adplayer.lite.example.examples.ClickThroughInterceptorExample
import com.adservrs.adplayer.lite.example.examples.InstreamContentOverrideExample
import com.adservrs.adplayer.lite.example.examples.MasterHeadExample
import com.adservrs.adplayer.lite.example.examples.MergeContentConfigExample
import com.adservrs.adplayer.lite.example.examples.Preloading
import com.adservrs.adplayer.lite.example.examples.SearchResponseExample
import com.adservrs.adplayer.lite.example.examples.SimpleExample
import com.adservrs.adplayer.lite.example.examples.SwitchableExample

enum class Example {
    Simple {
        @Composable
        override fun Compose(modifier: Modifier) {
            SimpleExample(modifier)
        }
    },
    Switchable {
        @Composable
        override fun Compose(modifier: Modifier) {
            SwitchableExample(modifier)
        }
    },
    Preloading {
        @Composable
        override fun Compose(modifier: Modifier) {
            Preloading(modifier)
        }
    },
    MasterHead {
        @Composable
        override fun Compose(modifier: Modifier) {
            MasterHeadExample(modifier)
        }
    },
    ClickThroughInterceptor {
        @Composable
        override fun Compose(modifier: Modifier) {
            ClickThroughInterceptorExample(modifier)
        }
    },
    InstreamContentOverride {
        @Composable
        override fun Compose(modifier: Modifier) {
            InstreamContentOverrideExample(modifier)
        }
    },
    MergeContentConfig {
        @Composable
        override fun Compose(modifier: Modifier) {
            MergeContentConfigExample(modifier)
        }
    },
    SearchResponse {
        @Composable
        override fun Compose(modifier: Modifier) {
            SearchResponseExample(modifier)
        }
    };

    @Composable
    abstract fun Compose(modifier: Modifier)
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val exampleState = remember { mutableStateOf<Example?>(null) }

    val example = exampleState.value
    if (example != null) {
        return example.Compose(modifier)
    }

    LazyColumn(modifier) {
        items(Example.entries) {
            ListItem(
                headlineContent = { Text(it.name) },
                modifier = Modifier.clickable { exampleState.value = it }
            )
        }
    }
}
