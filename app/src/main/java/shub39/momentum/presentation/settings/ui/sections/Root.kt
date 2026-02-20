package shub39.momentum.presentation.settings.ui.sections


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import shub39.momentum.R
import shub39.momentum.core.data_classes.Theme
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.presentation.settings.SettingsAction
import shub39.momentum.presentation.settings.ui.component.AboutApp
import shub39.momentum.presentation.shared.MomentumTheme
import shub39.momentum.presentation.shared.detachedItemShape
import shub39.momentum.presentation.shared.listItemColors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Root(
    onAction: (SettingsAction) -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToLookAndFeel: () -> Unit,
    onNavigateToPaywall: () -> Unit,
    onNavigateToChangelog: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            MediumFlexibleTopAppBar(
                scrollBehavior = scrollBehavior,
                title = { Text(text = stringResource(R.string.settings)) },
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
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = paddingValues.calculateTopPadding() + 16.dp,
                bottom = paddingValues.calculateBottomPadding() + 60.dp,
                start = paddingValues.calculateStartPadding(LocalLayoutDirection.current) + 16.dp,
                end = paddingValues.calculateEndPadding(LocalLayoutDirection.current) + 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // about app
            item { AboutApp() }

            item {
                Card(
                    onClick = onNavigateToPaywall,
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
                            painter = painterResource(R.drawable.add),
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
                            painter = painterResource(R.drawable.arrow_forward),
                            contentDescription = null
                        )
                    }
                }
            }

            item {
                ListItem(
                    colors = listItemColors(),
                    headlineContent = { Text(text = stringResource(R.string.look_and_feel)) },
                    supportingContent = { Text(text = stringResource(R.string.look_and_feel_info)) },
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.palette),
                            contentDescription = "Navigate",
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.arrow_forward),
                            contentDescription = "Go"
                        )
                    },
                    modifier = Modifier
                        .clip(detachedItemShape())
                        .clickable { onNavigateToLookAndFeel() }
                )
            }

            item {
                ListItem(
                    colors = listItemColors(),
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.star),
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
                        Icon(
                            painter = painterResource(R.drawable.arrow_forward),
                            contentDescription = "Go"
                        )
                    },
                    modifier = Modifier
                        .clip(detachedItemShape())
                        .clickable {
                            onAction(SettingsAction.OnOnboardingToggle(false))
                        }
                )
            }

            item {
                ListItem(
                    colors = listItemColors(),
                    headlineContent = {
                        Text(text = stringResource(R.string.changelog))
                    },
                    leadingContent = {
                        Icon(
                            painter = painterResource(R.drawable.logs),
                            contentDescription = "Changelog"
                        )
                    },
                    trailingContent = {
                        Icon(
                            painter = painterResource(R.drawable.arrow_forward),
                            contentDescription = "Go"
                        )
                    },
                    modifier = Modifier
                        .clip(detachedItemShape())
                        .clickable {
                            onNavigateToChangelog()
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
            onAction = {},
            onNavigateToPaywall = {},
            onNavigateToChangelog = {}
        )
    }
}