package shub39.momentum.presentation.settings.ui.sections


import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import shub39.momentum.R
import shub39.momentum.domain.data_classes.Theme
import shub39.momentum.domain.enums.AppTheme
import shub39.momentum.presentation.settings.SettingsAction
import shub39.momentum.presentation.settings.ui.component.AppInfo
import shub39.momentum.presentation.shared.MomentumTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Root(
    onAction: (SettingsAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLookAndFeel: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumFlexibleTopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text(text = stringResource(R.string.settings)) },
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
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 16.dp,
                bottom = paddingValues.calculateBottomPadding() + 16.dp
            ),
        ) {
            item {
                Card(
                    onClick = { onAction(SettingsAction.OnShowPaywall) },
                    modifier = Modifier.padding(16.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Momentum Plus",
                            modifier = Modifier.size(24.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = stringResource(R.string.pro),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Grit Plus"
                        )
                    }
                }
            }

            item { AppInfo() }

            item { Spacer(modifier = Modifier.height(12.dp)) }

            item {
                ListItem(
                    headlineContent = { Text(text = stringResource(R.string.look_and_feel)) },
                    supportingContent = { Text(text = stringResource(R.string.look_and_feel_info)) },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = "Navigate",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    trailingContent = {
                        IconButton(
                            onClick = onNavigateToLookAndFeel
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "Go"
                            )
                        }
                    }
                )
            }

            item {
                ListItem(
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Star,
                            contentDescription = "Onboarding"
                        )
                    },
                    headlineContent = {
                        Text(text = stringResource(R.string.onboarding))
                    },
                    supportingContent = {
                        Text(text = stringResource(R.string.onboarding_desc))
                    },
                    trailingContent = {
                        IconButton(
                            onClick = { onAction(SettingsAction.OnOnboardingToggle(false)) }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = "Go"
                            )
                        }
                    }
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MomentumTheme(
        theme = Theme(
            appTheme = AppTheme.DARK
        )
    ) {
        Root(
            onNavigateBack = {},
            onNavigateToLookAndFeel = {},
            onAction = {}
        )
    }
}