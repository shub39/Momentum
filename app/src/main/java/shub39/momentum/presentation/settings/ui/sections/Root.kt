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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import shub39.momentum.R
import shub39.momentum.core.data_classes.Theme
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.presentation.shared.MomentumTheme
import shub39.momentum.presentation.shared.detachedItemShape
import shub39.momentum.presentation.shared.endItemShape
import shub39.momentum.presentation.shared.flexFontEmphasis
import shub39.momentum.presentation.shared.leadingItemShape
import shub39.momentum.presentation.shared.listItemColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Root(
    onNavigateBack: () -> Unit,
    onNavigateToLookAndFeel: () -> Unit,
    onNavigateToOnboarding: () -> Unit,
    onNavigateToPaywall: () -> Unit,
    onNavigateToChangelog: () -> Unit,
    onNavigateToAppInfo: () -> Unit,
    onNavigateToBackup: () -> Unit,
    currentVersion: String,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumFlexibleTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(text = stringResource(R.string.settings), fontFamily = flexFontEmphasis())
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(R.drawable.nav_arrow_back),
                            contentDescription = "Navigate Back",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    bottom = paddingValues.calculateBottomPadding() + 60.dp,
                    start =
                        paddingValues.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
                    end = paddingValues.calculateEndPadding(LocalLayoutDirection.current) + 16.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // momentum pro
            item {
                ListItem(
                    content = { Text(text = stringResource(R.string.pro)) },
                    colors = listItemColors(),
                    modifier =
                        Modifier.clip(detachedItemShape()).clickable { onNavigateToPaywall() },
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.arrow_forward),
                            contentDescription = "Grit Plus",
                        )
                    },
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.app_icon),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    },
                )
            }

            // look and feel
            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    ListItem(
                        colors = listItemColors(),
                        content = { Text(text = stringResource(R.string.backup)) },
                        supportingContent = { Text(text = stringResource(R.string.backup_info)) },
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.backup),
                                contentDescription = "Navigate",
                                modifier = Modifier.size(24.dp),
                            )
                        },
                        trailingContent = {
                            Icon(
                                painter = painterResource(R.drawable.arrow_forward),
                                contentDescription = "Go",
                            )
                        },
                        modifier =
                            Modifier.clip(leadingItemShape()).clickable { onNavigateToBackup() },
                    )

                    ListItem(
                        colors = listItemColors(),
                        content = { Text(text = stringResource(R.string.look_and_feel)) },
                        supportingContent = {
                            Text(text = stringResource(R.string.look_and_feel_info))
                        },
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.palette),
                                contentDescription = "Navigate",
                                modifier = Modifier.size(24.dp),
                            )
                        },
                        trailingContent = {
                            Icon(
                                painter = painterResource(R.drawable.arrow_forward),
                                contentDescription = "Go",
                            )
                        },
                        modifier =
                            Modifier.clip(endItemShape()).clickable { onNavigateToLookAndFeel() },
                    )
                }
            }

            // onboarding
            item {
                ListItem(
                    colors = listItemColors(),
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.star),
                            contentDescription = "Onboarding",
                        )
                    },
                    content = { Text(text = stringResource(R.string.onboarding)) },
                    supportingContent = { Text(text = stringResource(R.string.onboarding_desc)) },
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.arrow_forward),
                            contentDescription = "Go",
                        )
                    },
                    modifier =
                        Modifier.clip(detachedItemShape()).clickable { onNavigateToOnboarding() },
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    // about
                    ListItem(
                        colors = listItemColors(),
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.info),
                                contentDescription = null,
                            )
                        },
                        supportingContent = { Text(text = "Momentum $currentVersion") },
                        trailingContent = {
                            Icon(
                                painter = painterResource(R.drawable.arrow_forward),
                                contentDescription = "Navigate",
                            )
                        },
                        content = { Text(text = stringResource(R.string.about)) },
                        modifier =
                            Modifier.clip(leadingItemShape()).clickable { onNavigateToAppInfo() },
                    )

                    // changelog
                    ListItem(
                        colors = listItemColors(),
                        content = { Text(text = stringResource(R.string.changelog)) },
                        leadingContent = {
                            Icon(
                                painter = painterResource(R.drawable.logs),
                                contentDescription = "Changelog",
                            )
                        },
                        trailingContent = {
                            Icon(
                                painter = painterResource(R.drawable.arrow_forward),
                                contentDescription = "Go",
                            )
                        },
                        modifier =
                            Modifier.clip(endItemShape()).clickable { onNavigateToChangelog() },
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MomentumTheme(theme = Theme(appTheme = AppTheme.DARK)) {
        Root(
            onNavigateBack = {},
            onNavigateToLookAndFeel = {},
            onNavigateToPaywall = {},
            onNavigateToChangelog = {},
            onNavigateToOnboarding = {},
            onNavigateToAppInfo = {},
            onNavigateToBackup = {},
            currentVersion = "1.0.0",
        )
    }
}
