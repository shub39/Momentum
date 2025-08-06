package shub39.momentum.settings.component

import android.R.color.system_accent1_200
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.FontDownload
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.materialkolor.PaletteStyle
import com.materialkolor.ktx.from
import com.materialkolor.palettes.TonalPalette
import com.materialkolor.rememberDynamicColorScheme
import shub39.momentum.R
import shub39.momentum.core.domain.enums.AppTheme
import shub39.momentum.core.domain.enums.Fonts
import shub39.momentum.core.presentation.ColorPickerDialog
import shub39.momentum.core.presentation.MomentumDialog
import shub39.momentum.settings.SettingsAction
import shub39.momentum.settings.SettingsState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LookAndFeel(
    state: SettingsState,
    onAction: (SettingsAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    var colorPickerDialog by remember { mutableStateOf(false) }
    var fontPickerDialog by remember { mutableStateOf(false) }
    var themePickerDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.widthIn(max = 1000.dp),
        topBar = {
            TopAppBar(
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
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            // App theme switch
            item {
                ListItem(
                    headlineContent = {
                        Text(
                            text = stringResource(R.string.select_app_theme)
                        )
                    },
                    supportingContent = {
                        Text(
                            text = stringResource(state.theme.appTheme.stringRes)
                        )
                    },
                    trailingContent = {
                        FilledTonalIconButton(
                            onClick = { themePickerDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Create,
                                contentDescription = "Pick"
                            )
                        }
                    }
                )
            }

            if (state.isProUser) {// Font Picker
                item {
                    ListItem(
                        headlineContent = {
                            Text(text = stringResource(R.string.font))
                        },
                        supportingContent = {
                            Text(text = state.theme.font.displayName)
                        },
                        trailingContent = {
                            FilledTonalIconButton(
                                onClick = { fontPickerDialog = true }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FontDownload,
                                    contentDescription = "Pick Font"
                                )
                            }

                        }
                    )
                }

                // Amoled variant toggle
                item {
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
                                onCheckedChange = {
                                    onAction(SettingsAction.OnAmoledSwitch(it))
                                }
                            )
                        }
                    )
                }

                // Material you toggle
                item {
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
                        }
                    )
                }

                // Seed color picker
                item {
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
                                enabled = !state.theme.isMaterialYou
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Create,
                                    contentDescription = "Select Color"
                                )
                            }
                        }
                    )
                }

                // palette styles
                item {
                    Column {
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = stringResource(R.string.palette_style)
                                )
                            }
                        )

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            items(PaletteStyle.entries.toList(), key = { it.name }) { style ->
                                val scheme = rememberDynamicColorScheme(
                                    primary = if (state.theme.isMaterialYou) {
                                        colorResource(system_accent1_200)
                                    } else {
                                        state.theme.seedColor
                                    },
                                    isDark = when (state.theme.appTheme) {
                                        AppTheme.SYSTEM -> isSystemInDarkTheme()
                                        AppTheme.LIGHT -> false
                                        AppTheme.DARK -> true
                                    },
                                    isAmoled = state.theme.isAmoled,
                                    style = style
                                )

                                SelectableMiniPalette(
                                    selected = state.theme.paletteStyle == style,
                                    onClick = {
                                        onAction(SettingsAction.OnPaletteChange(style = style))
                                    },
                                    contentDescription = { style.name },
                                    accents = listOf(
                                        TonalPalette.from(scheme.primary),
                                        TonalPalette.from(scheme.tertiary),
                                        TonalPalette.from(scheme.secondary)
                                    )
                                )
                            }
                        }
                    }
                }
            } else {
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { onAction(SettingsAction.OnShowPaywall) }
                    ) {
                        Text(
                            text = stringResource(R.string.unlock_more_pro)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.padding(60.dp))
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

    // theme picker
    if (themePickerDialog) {
        MomentumDialog(
            onDismissRequest = { themePickerDialog = false }
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.WbSunny,
                    contentDescription = "Select App Theme",
                    modifier = Modifier.size(32.dp)
                )

                Text(
                    text = stringResource(R.string.select_app_theme),
                    style = MaterialTheme.typography.titleLarge
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    AppTheme.entries.forEach { appTheme ->
                        ToggleButton(
                            checked = state.theme.appTheme == appTheme,
                            onCheckedChange = {
                                onAction(SettingsAction.OnThemeSwitch(appTheme))
                                themePickerDialog = false
                            }
                        ) {
                            Text(text = stringResource(appTheme.stringRes))
                        }
                    }
                }
            }
        }
    }

    // Font picker
    if (fontPickerDialog) {
        MomentumDialog(
            onDismissRequest = { fontPickerDialog = false }
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FontDownload,
                    contentDescription = "Select App Theme",
                    modifier = Modifier.size(32.dp)
                )

                Text(
                    text = stringResource(R.string.font),
                    style = MaterialTheme.typography.titleLarge
                )

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Fonts.entries.forEach { font ->
                        ToggleButton(
                            checked = state.theme.font == font,
                            onCheckedChange = {
                                onAction(SettingsAction.OnFontChange(font))
                                fontPickerDialog = false
                            }
                        ) {
                            Text(
                                text = font.displayName,
                                fontFamily = FontFamily(Font(font.fontRes))
                            )
                        }
                    }
                }
            }
        }
    }
}