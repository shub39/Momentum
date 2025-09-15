package shub39.momentum.project.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import shub39.momentum.core.domain.data_classes.Day
import shub39.momentum.core.presentation.MomentumTheme
import shub39.momentum.core.presentation.getPlaceholder
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@Composable
fun FavDayCard(
    day: Day,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            CoilImage(
                imageModel = { day.image },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop
                ),
                failure = {
                    Icon(
                        imageVector = Icons.Rounded.Warning,
                        contentDescription = "Placeholder",
                        modifier = Modifier
                            .align(Alignment.Center)
                            .size(70.dp)
                    )
                },
                previewPlaceholder = getPlaceholder(),
                modifier = Modifier
                    .clip(RoundedCornerShape(32.dp))
                    .heightIn(max = 300.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
            ) {
                Text(
                    text = LocalDate.ofEpochDay(day.date).format(
                        DateTimeFormatter.ofLocalizedDate(
                            FormatStyle.LONG
                        )
                    ),
                    style = MaterialTheme.typography.titleLarge
                )

                if (!day.comment.isNullOrBlank()) {
                    Text(
                        text = day.comment
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MomentumTheme {
        FavDayCard(
            day = Day(
                id = 1,
                projectId = 1,
                image = "",
                comment = "A day",
                date = 1,
                isFavorite = false
            ),
            onClick = { },
        )
    }
}