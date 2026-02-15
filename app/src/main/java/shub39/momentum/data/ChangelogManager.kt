package shub39.momentum.data

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import org.koin.core.annotation.Single
import shub39.momentum.app.Changelog

@Single
class ChangelogManager(
    private val context: Context,
) {
    private val _changelogs: MutableStateFlow<Changelog> = MutableStateFlow(emptyList())
    val changelogs = _changelogs.asStateFlow()
        .onStart { getChangelogs() }

    private fun getChangelogs() {
        val rawJson = context.assets
            .open("changelog.json")
            .bufferedReader()
            .use { it.readText() }

        val json = Json.decodeFromString<Changelog>(rawJson)

        _changelogs.update { json }
    }
}