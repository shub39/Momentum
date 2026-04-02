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
package shub39.momentum.presentation.settings.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import shub39.momentum.BuildConfig
import shub39.momentum.R
import shub39.momentum.presentation.shared.endItemShape
import shub39.momentum.presentation.shared.flexFontRounded
import shub39.momentum.presentation.shared.leadingItemShape

@Composable
fun AboutApp(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = modifier.fillMaxWidth(),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            ),
        shape = MaterialTheme.shapes.extraLarge,
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Column {
                Text(
                    text = "Momentum",
                    style =
                        MaterialTheme.typography.headlineMedium.copy(fontFamily = flexFontRounded()),
                )
                Text(
                    text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row {
                IconButton(onClick = { uriHandler.openUri("https://discord.gg/nxA2hgtEKf") }) {
                    Icon(
                        painter = painterResource(R.drawable.discord),
                        contentDescription = "Discord",
                        modifier = Modifier.size(24.dp),
                    )
                }

                IconButton(onClick = { uriHandler.openUri("https://github.com/shub39/Momentum") }) {
                    Icon(
                        painter = painterResource(R.drawable.github),
                        contentDescription = "Github",
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
        }

        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Row(
                modifier =
                    Modifier.background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = leadingItemShape(),
                        )
                        .clip(leadingItemShape())
                        .clickable { uriHandler.openUri("https://buymeacoffee.com/shub39") }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.buymeacoffee),
                        contentDescription = "Buy me a coffee",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.bmc),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontFamily = flexFontRounded(),
                    )
                }
            }

            Row(
                modifier =
                    Modifier.background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = endItemShape(),
                        )
                        .clip(endItemShape())
                        .clickable {
                            uriHandler.openUri(
                                "https://play.google.com/store/apps/details?id=shub39.momentum.play"
                            )
                        }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.play_store),
                        contentDescription = "Rate On Google Play",
                        modifier = Modifier.size(24.dp),
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = stringResource(R.string.rate_on_play),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontFamily = flexFontRounded(),
                    )
                }
            }
        }
    }
}
