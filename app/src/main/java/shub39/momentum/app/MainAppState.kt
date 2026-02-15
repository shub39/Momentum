package shub39.momentum.app

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable
import shub39.momentum.domain.data_classes.Theme

@Serializable
data class VersionEntry(
    val version: String,
    val changes: List<String>
)

typealias Changelog = List<VersionEntry>

@Stable
@Immutable
data class MainAppState(
    val isOnboardingDone: Boolean = true,
    val theme: Theme = Theme(),
    val isPlusUser: Boolean = false,
    val currentChangelog: VersionEntry? = null, // if not null then shows changelog
)
