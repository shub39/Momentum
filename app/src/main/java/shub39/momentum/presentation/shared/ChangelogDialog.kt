package shub39.momentum.presentation.shared

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import shub39.momentum.R
import shub39.momentum.app.VersionEntry
import shub39.momentum.domain.data_classes.Theme
import shub39.momentum.domain.enums.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangelogDialog(
    modifier: Modifier = Modifier,
    currentLog: VersionEntry,
    onDismissRequest: () -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 500.dp),
            shape = MaterialTheme.shapes.extraLarge
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = stringResource(R.string.changelog),
                    style = MaterialTheme.typography.displaySmall
                )
                Text(
                    text = currentLog.version,
                    style = MaterialTheme.typography.titleLarge
                )
            }
            HorizontalDivider()

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                itemsIndexed(currentLog.changes) { index, change ->
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "${index.plus(1)}.",
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = change)
                    }
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(
                            onClick = onDismissRequest
                        ) {
                            Text(text = stringResource(R.string.done))
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MomentumTheme(theme = Theme(appTheme = AppTheme.DARK)) {
        ChangelogDialog(
            modifier = Modifier,
            currentLog = VersionEntry(
                version = "1.5.0",
                changes = (0..10).map {
                    "Change No : $it hjagdagsddkjhgajhshbhajbvdjshjagsbhjadgvjhasgvdjha"
                }
            ),
            onDismissRequest = { }
        )
    }
}