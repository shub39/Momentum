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

import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.materialkolor.rememberDynamicColorScheme
import shub39.momentum.R
import shub39.momentum.core.data_classes.Theme
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.core.enums.Fonts
import shub39.momentum.core.enums.PaletteStyle
import shub39.momentum.core.toFontRes
import shub39.momentum.presentation.settings.SettingsAction
import shub39.momentum.presentation.settings.SettingsState
import shub39.momentum.presentation.shared.ColorPickerDialog
import shub39.momentum.presentation.shared.ExpressiveSwitch
import shub39.momentum.presentation.shared.ListSelect
import shub39.momentum.presentation.shared.MomentumTheme
import shub39.momentum.presentation.shared.detachedItemShape
import shub39.momentum.presentation.shared.endItemShape
import shub39.momentum.presentation.shared.flexFontEmphasis
import shub39.momentum.presentation.shared.leadingItemShape
import shub39.momentum.presentation.shared.listItemColors
import shub39.momentum.presentation.shared.middleItemShape
import shub39.momentum.presentation.shared.rectListItemShapes
import shub39.momentum.presentation.toDisplayString
import shub39.momentum.presentation.toMPaletteStyle
import shub39.momentum.presentation.toStringRes

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LookAndFeel(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onNavigateBack: () -> Unit,
    isPlusUser: Boolean,
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var colorPickerDialog by remember { mutableStateOf(false) }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = stringResource(R.string.look_and_feel),
                        fontFamily = flexFontEmphasis(),
                    )
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
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding =
                PaddingValues(
                    top = padding.calculateTopPadding() + 16.dp,
                    bottom = padding.calculateBottomPadding() + 60.dp,
                    start = padding.calculateLeftPadding(LocalLayoutDirection.current) + 16.dp,
                    end = padding.calculateRightPadding(LocalLayoutDirection.current) + 16.dp,
                ),
        ) {
            item {
                // appTheme picker
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Column(modifier = Modifier.clip(leadingItemShape())) {
                        ListItem(
                            leadingContent = {
                                Icon(
                                    painter =
                                        painterResource(
                                            when (state.theme.appTheme) {
                                                AppTheme.SYSTEM -> {
                                                    if (isSystemInDarkTheme()) R.drawable.dark_mode
                                                    else R.drawable.light_mode
                                                }

                                                AppTheme.DARK -> R.drawable.dark_mode
                                                AppTheme.LIGHT -> R.drawable.light_mode
                                            }
                                        ),
                                    contentDescription = null,
                                )
                            },
                            shapes = rectListItemShapes(),
                            colors = listItemColors(),
                            content = { Text(text = stringResource(R.string.select_app_theme)) },
                        )

                        Row(
                            horizontalArrangement =
                                Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                            modifier =
                                Modifier.fillParentMaxWidth()
                                    .background(listItemColors().containerColor)
                                    .padding(start = 52.dp, end = 16.dp, bottom = 8.dp),
                        ) {
                            ListSelect(
                                title = null,
                                options = AppTheme.entries.toList(),
                                selected = state.theme.appTheme,
                                onSelectedChange = { onAction(SettingsAction.OnThemeSwitch(it)) },
                                labelProvider = {
                                    Text(
                                        text = stringResource(it.toStringRes()),
                                        modifier = Modifier.basicMarquee(),
                                        maxLines = 1,
                                    )
                                },
                            )
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ListItem(
                            content = { Text(text = stringResource(R.string.material_theme)) },
                            supportingContent = {
                                Text(text = stringResource(R.string.material_theme_desc))
                            },
                            trailingContent = {
                                ExpressiveSwitch(
                                    checked = state.theme.isMaterialYou,
                                    onCheckedChange = {
                                        onAction(SettingsAction.OnMaterialThemeToggle(it))
                                    },
                                )
                            },
                            colors = listItemColors(),
                            modifier =
                                Modifier.clip(if (isPlusUser) middleItemShape() else endItemShape()),
                        )
                    }

                    // plus redirect
                    if (!isPlusUser) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillParentMaxWidth().height(60.dp),
                        ) {
                            LinearWavyProgressIndicator(
                                progress = { 0.90f },
                                modifier = Modifier.fillParentMaxWidth(),
                            )

                            Button(onClick = onNavigateToPaywall) {
                                Text(text = stringResource(R.string.unlock_more_pro))
                            }
                        }
                    }

                    // font picker
                    Column(
                        modifier =
                            Modifier.clip(
                                when {
                                    state.theme.isMaterialYou && !isPlusUser -> detachedItemShape()

                                    state.theme.isMaterialYou -> endItemShape()
                                    isPlusUser -> middleItemShape()
                                    else -> leadingItemShape()
                                }
                            )
                    ) {
                        ListItem(
                            content = { Text(text = stringResource(R.string.font)) },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(R.drawable.font),
                                    contentDescription = null,
                                )
                            },
                            colors = listItemColors(),
                            shapes = rectListItemShapes(),
                        )

                        FlowRow(
                            modifier =
                                Modifier.fillParentMaxWidth()
                                    .background(listItemColors().containerColor)
                                    .padding(start = 52.dp, end = 16.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                        ) {
                            ListSelect(
                                title = null,
                                options = Fonts.entries.toList(),
                                selected = state.theme.font,
                                onSelectedChange = { onAction(SettingsAction.OnFontChange(it)) },
                                labelProvider = { font ->
                                    Text(
                                        text = font.toDisplayString(),
                                        fontFamily =
                                            font.toFontRes()?.let { FontFamily(Font(it)) }
                                                ?: FontFamily.Default,
                                    )
                                },
                            )
                        }
                    }

                    if (!state.theme.isMaterialYou) {
                        ListItem(
                            content = { Text(text = stringResource(R.string.amoled)) },
                            supportingContent = {
                                Text(text = stringResource(R.string.amoled_desc))
                            },
                            trailingContent = {
                                ExpressiveSwitch(
                                    checked = state.theme.isAmoled,
                                    enabled = isPlusUser,
                                    onCheckedChange = {
                                        onAction(SettingsAction.OnAmoledSwitch(it))
                                    },
                                )
                            },
                            colors = listItemColors(),
                            modifier = Modifier.clip(middleItemShape()),
                        )

                        AnimatedVisibility(visible = !state.theme.isMaterialYou) {
                            ListItem(
                                content = { Text(text = stringResource(R.string.seed_color)) },
                                supportingContent = {
                                    Text(text = stringResource(R.string.seed_color_desc))
                                },
                                trailingContent = {
                                    IconButton(
                                        onClick = { colorPickerDialog = true },
                                        colors =
                                            IconButtonDefaults.iconButtonColors(
                                                containerColor = state.theme.seedColor,
                                                contentColor =
                                                    contentColorFor(state.theme.seedColor),
                                            ),
                                        enabled = isPlusUser,
                                    ) {
                                        Icon(
                                            painter = painterResource(R.drawable.edit),
                                            contentDescription = "Select Color",
                                        )
                                    }
                                },
                                colors = listItemColors(),
                                modifier = Modifier.clip(middleItemShape()),
                            )
                        }

                        // palette style picker
                        Column(modifier = Modifier.clip(endItemShape())) {
                            ListItem(
                                content = { Text(text = stringResource(R.string.palette_style)) },
                                colors = listItemColors(),
                                shapes = rectListItemShapes(),
                                leadingContent = {
                                    Icon(
                                        painter = painterResource(R.drawable.palette),
                                        contentDescription = null,
                                    )
                                },
                            )

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier =
                                    Modifier.fillMaxWidth()
                                        .background(listItemColors().containerColor)
                                        .padding(start = 52.dp, end = 16.dp, bottom = 16.dp),
                            ) {
                                ListSelect(
                                    title = null,
                                    options = PaletteStyle.entries.toList(),
                                    selected = state.theme.paletteStyle,
                                    onSelectedChange = {
                                        onAction(SettingsAction.OnPaletteChange(it))
                                    },
                                    enabled = isPlusUser,
                                    labelProvider = { style: PaletteStyle ->
                                        val scheme =
                                            rememberDynamicColorScheme(
                                                primary =
                                                    if (
                                                        state.theme.isMaterialYou &&
                                                            Build.VERSION.SDK_INT >=
                                                                Build.VERSION_CODES.S
                                                    ) {
                                                        colorResource(
                                                            android.R.color.system_accent1_900
                                                        )
                                                    } else state.theme.seedColor,
                                                isDark =
                                                    when (state.theme.appTheme) {
                                                        AppTheme.SYSTEM -> isSystemInDarkTheme()
                                                        AppTheme.DARK -> true
                                                        AppTheme.LIGHT -> false
                                                    },
                                                isAmoled = state.theme.isAmoled,
                                                style = style.toMPaletteStyle(),
                                            )

                                        Row(
                                            modifier =
                                                Modifier.size(width = 64.dp, height = 20.dp)
                                                    .clip(MaterialTheme.shapes.extraSmall)
                                        ) {
                                            listOf(
                                                    scheme.primary,
                                                    scheme.primaryContainer,
                                                    scheme.onPrimary,
                                                    scheme.secondary,
                                                    scheme.secondaryContainer,
                                                    scheme.onSecondary,
                                                    scheme.tertiary,
                                                    scheme.tertiaryContainer,
                                                    scheme.onTertiary,
                                                    scheme.surface,
                                                    scheme.onSurface,
                                                )
                                                .forEach { color ->
                                                    Box(
                                                        modifier =
                                                            Modifier.weight(1f)
                                                                .fillMaxHeight()
                                                                .background(color)
                                                    )
                                                }
                                        }
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Seed color picker
    if (colorPickerDialog) {
        ColorPickerDialog(
            initialColor = state.theme.seedColor,
            onSelect = { onAction(SettingsAction.OnSeedColorChange(it)) },
            onDismiss = { colorPickerDialog = false },
        )
    }
}

@Composable
@Preview
private fun Preview() {
    MomentumTheme(theme = Theme(appTheme = AppTheme.DARK)) {
        LookAndFeel(
            state = SettingsState(),
            onAction = {},
            onNavigateBack = {},
            isPlusUser = true,
            onNavigateToPaywall = {},
        )
    }
}
