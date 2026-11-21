/*
 * Copyright (C) 2026  Shubham Gorai
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package shub39.momentum.presentation.settings.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import shub39.momentum.R
import shub39.momentum.app.Changelog
import shub39.momentum.app.VersionEntry
import shub39.momentum.core.data_classes.Theme
import shub39.momentum.presentation.shared.ExpressiveTopBar
import shub39.momentum.presentation.shared.LazierColumn
import shub39.momentum.presentation.shared.LazierDefaults
import shub39.momentum.presentation.shared.MomentumTheme
import shub39.momentum.presentation.shared.detachedItemShape
import shub39.momentum.presentation.shared.endItemShape
import shub39.momentum.presentation.shared.flexFontEmphasis
import shub39.momentum.presentation.shared.flexFontRounded
import shub39.momentum.presentation.shared.leadingItemShape
import shub39.momentum.presentation.shared.listItemColors
import shub39.momentum.presentation.shared.middleItemShape

@Composable
fun Changelog(modifier: Modifier = Modifier, changelog: Changelog, onNavigateBack: () -> Unit) {
    Scaffold(
        modifier = modifier,
        topBar = {
            ExpressiveTopBar(
                title = {
                    Text(text = stringResource(R.string.changelog), fontFamily = flexFontEmphasis())
                },
                navigationIcon = {
                    FilledTonalIconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.nav_arrow_back),
                            contentDescription = "Navigate Back",
                        )
                    }
                },
            )
        },
    ) { padding ->
        LazierColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            contentPadding = padding,
            extraPadding = LazierDefaults.pagePadding(extraBottom = 44.dp),
        ) {
            changelog.forEach { versionEntry ->
                item {
                    Text(
                        text = versionEntry.version,
                        style =
                            MaterialTheme.typography.headlineSmall.copy(
                                fontFamily = flexFontRounded()
                            ),
                    )
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                itemsIndexed(versionEntry.changes) { index, change ->
                    val shape =
                        when {
                            versionEntry.changes.size == 1 -> detachedItemShape()
                            index == 0 -> leadingItemShape()
                            index == versionEntry.changes.size - 1 -> endItemShape()
                            else -> middleItemShape()
                        }

                    ListItem(
                        colors = listItemColors(),
                        modifier = Modifier.clip(shape),
                        content = { Text(text = change) },
                    )
                }

                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@Preview
@Composable
private fun ChangelogPreview() {
    MomentumTheme(theme = Theme()) {
        Changelog(
            changelog =
                listOf(
                    VersionEntry(
                        version = "1.0.0",
                        changes = listOf("Initial release", "Added new feature"),
                    ),
                    VersionEntry(
                        version = "1.1.0",
                        changes = listOf("Bug fixes", "Improved performance"),
                    ),
                ),
            onNavigateBack = {},
        )
    }
}
