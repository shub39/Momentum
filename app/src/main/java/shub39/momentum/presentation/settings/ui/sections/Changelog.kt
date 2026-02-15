package shub39.momentum.presentation.settings.ui.sections

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import shub39.momentum.R
import shub39.momentum.app.Changelog
import shub39.momentum.app.VersionEntry
import shub39.momentum.presentation.shared.MomentumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Changelog(
    modifier: Modifier = Modifier,
    changelog: Changelog,
    onNavigateBack: () -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Text(
                        text = stringResource(R.string.changelog)
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
            changelog.forEach { versionEntry ->
                item {
                    Text(
                        text = "#${versionEntry.version}",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                items(versionEntry.changes) { change ->
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_forward),
                            contentDescription = null
                        )

                        Text(
                            text = change,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Preview
@Composable
private fun ChangelogPreview() {
    MomentumTheme {
        Changelog(
            changelog = listOf(
                VersionEntry(
                    version = "1.0.0",
                    changes = listOf("Initial release", "Added new feature")
                ),
                VersionEntry(
                    version = "1.1.0",
                    changes = listOf("Bug fixes", "Improved performance")
                )
            ),
            onNavigateBack = {}
        )
    }
}