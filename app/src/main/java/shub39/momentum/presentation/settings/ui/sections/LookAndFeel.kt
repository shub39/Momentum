package shub39.momentum.presentation.settings.ui.sections

import android.R.color.system_accent1_200
import android.os.Build
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.toShape
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
import shub39.momentum.presentation.settings.SettingsAction
import shub39.momentum.presentation.settings.SettingsState
import shub39.momentum.presentation.shared.ColorPickerDialog
import shub39.momentum.presentation.shared.MomentumTheme
import shub39.momentum.presentation.shared.endItemShape
import shub39.momentum.presentation.shared.leadingItemShape
import shub39.momentum.presentation.shared.listItemColors
import shub39.momentum.presentation.shared.middleItemShape
import shub39.momentum.presentation.toDisplayString
import shub39.momentum.presentation.toFontRes
import shub39.momentum.presentation.toMPaletteStyle
import shub39.momentum.presentation.toStringRes

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LookAndFeel(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onNavigateBack: () -> Unit,
    isPlusUser: Boolean,
    onNavigateToPaywall: () -> Unit,
    modifier: Modifier = Modifier
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
                        text = stringResource(R.string.look_and_feel)
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "Navigate Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding() + 16.dp,
                bottom = padding.calculateBottomPadding() + 60.dp,
                start = padding.calculateLeftPadding(LocalLayoutDirection.current) + 16.dp,
                end = padding.calculateRightPadding(LocalLayoutDirection.current) + 16.dp
            ),
        ) {
            item {
                // appTheme picker
                Column(
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Column(
                        modifier = Modifier.clip(leadingItemShape())
                    ) {
                        ListItem(
                            leadingContent = {
                                Icon(
                                    painter = painterResource(
                                        when (state.theme.appTheme) {
                                            AppTheme.SYSTEM -> {
                                                if (isSystemInDarkTheme()) R.drawable.dark_mode else R.drawable.light_mode
                                            }

                                            AppTheme.DARK -> R.drawable.dark_mode
                                            AppTheme.LIGHT -> R.drawable.light_mode
                                        }
                                    ),
                                    contentDescription = null
                                )
                            },
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.select_app_theme)
                                )
                            },
                            colors = listItemColors()
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween),
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .background(listItemColors().containerColor)
                                .padding(start = 52.dp, end = 16.dp, bottom = 8.dp)
                        ) {
                            AppTheme.entries.forEach { appTheme ->
                                ToggleButton(
                                    checked = appTheme == state.theme.appTheme,
                                    onCheckedChange = {
                                        onAction(SettingsAction.OnThemeSwitch(appTheme))
                                    },
                                    modifier = Modifier.weight(1f),
                                    colors = ToggleButtonDefaults.toggleButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                ) {
                                    Text(
                                        text = stringResource(appTheme.toStringRes()),
                                        modifier = Modifier.basicMarquee(),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.material_theme)
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = stringResource(R.string.material_theme_desc)
                                )
                            },
                            trailingContent = {
                                Switch(
                                    checked = state.theme.isMaterialYou,
                                    onCheckedChange = {
                                        onAction(SettingsAction.OnMaterialThemeToggle(it))
                                    }
                                )
                            },
                            colors = listItemColors(),
                            modifier = Modifier.clip(
                                if (isPlusUser) middleItemShape() else endItemShape()
                            )
                        )
                    }

                    // plus redirect
                    if (!isPlusUser) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .height(60.dp)
                        ) {
                            LinearWavyProgressIndicator(
                                progress = { 0.90f },
                                modifier = Modifier.fillParentMaxWidth()
                            )

                            Button(
                                onClick = onNavigateToPaywall
                            ) {
                                Text(
                                    text = stringResource(R.string.unlock_more_pro)
                                )
                            }
                        }
                    }

                    // font picker
                    Column(
                        modifier = Modifier.clip(
                            if (isPlusUser) middleItemShape() else leadingItemShape()
                        )
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.font)
                                )
                            },
                            leadingContent = {
                                Icon(
                                    painter = painterResource(R.drawable.font),
                                    contentDescription = null
                                )
                            },
                            colors = listItemColors()
                        )

                        FlowRow(
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .background(listItemColors().containerColor)
                                .padding(start = 52.dp, end = 16.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Fonts.entries.forEach { font ->
                                ToggleButton(
                                    checked = state.theme.font == font,
                                    onCheckedChange = {
                                        onAction(SettingsAction.OnFontChange(font))
                                    },
                                    enabled = isPlusUser,
                                    colors = ToggleButtonDefaults.toggleButtonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                                    )
                                ) {
                                    Text(
                                        text = font.toDisplayString(),
                                        fontFamily = font.toFontRes()?.let { FontFamily(Font(it)) }
                                            ?: FontFamily.Default
                                    )
                                }
                            }
                        }
                    }

                    ListItem(
                        headlineContent = {
                            Text(
                                text = stringResource(R.string.amoled)
                            )
                        },
                        supportingContent = {
                            Text(
                                text = stringResource(R.string.amoled_desc)
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = state.theme.isAmoled,
                                enabled = isPlusUser,
                                onCheckedChange = {
                                    onAction(SettingsAction.OnAmoledSwitch(it))
                                }
                            )
                        },
                        colors = listItemColors(),
                        modifier = Modifier.clip(middleItemShape())
                    )

                    AnimatedVisibility(
                        visible = !state.theme.isMaterialYou
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.seed_color)
                                )
                            },
                            supportingContent = {
                                Text(
                                    text = stringResource(R.string.seed_color_desc)
                                )
                            },
                            trailingContent = {
                                IconButton(
                                    onClick = { colorPickerDialog = true },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = state.theme.seedColor,
                                        contentColor = contentColorFor(state.theme.seedColor)
                                    ),
                                    enabled = isPlusUser
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.edit),
                                        contentDescription = "Select Color"
                                    )
                                }
                            },
                            colors = listItemColors(),
                            modifier = Modifier.clip(middleItemShape())
                        )
                    }

                    // palette style picker
                    Column(
                        modifier = Modifier.clip(endItemShape())
                    ) {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.palette_style)
                                )
                            },
                            colors = listItemColors(),
                            leadingContent = {
                                Icon(
                                    painter = painterResource(R.drawable.palette),
                                    contentDescription = null
                                )
                            }
                        )

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillParentMaxWidth()
                                .background(listItemColors().containerColor)
                                .padding(start = 52.dp, end = 16.dp, bottom = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PaletteStyle.entries.toList().forEach { style ->
                                val scheme = rememberDynamicColorScheme(
                                    primary = if (state.theme.isMaterialYou && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                        colorResource(system_accent1_200)
                                    } else state.theme.seedColor,
                                    isDark = when (state.theme.appTheme) {
                                        AppTheme.SYSTEM -> isSystemInDarkTheme()
                                        AppTheme.DARK -> true
                                        AppTheme.LIGHT -> false
                                    },
                                    isAmoled = state.theme.isAmoled,
                                    style = style.toMPaletteStyle()
                                )
                                val selected = state.theme.paletteStyle == style

                                Box(
                                    modifier = Modifier
                                        .size(50.dp)
                                        .background(
                                            color = scheme.tertiary,
                                            shape = if (selected) MaterialShapes.VerySunny.toShape() else CircleShape
                                        )
                                        .clickable(enabled = isPlusUser) {
                                            onAction(SettingsAction.OnPaletteChange(style))
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (selected) {
                                        Icon(
                                            painter = painterResource(R.drawable.check_circle),
                                            contentDescription = null,
                                            tint = scheme.onTertiary
                                        )
                                    }
                                }
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
            onDismiss = { colorPickerDialog = false }
        )
    }
}

@Composable
@Preview
private fun Preview() {
    MomentumTheme(
        theme = Theme(
            appTheme = AppTheme.DARK
        )
    ) {
        LookAndFeel(
            state = SettingsState(),
            onAction = { },
            onNavigateBack = { },
            isPlusUser = true,
            onNavigateToPaywall = { },
        )
    }
}