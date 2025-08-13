package shub39.momentum.project.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import shub39.momentum.R
import shub39.momentum.core.domain.data_classes.Day
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
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            CoilImage(
                imageModel = { day.image },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop
                ),
                previewPlaceholder = painterResource(R.drawable.ic_launcher_foreground),
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .height(300.dp)
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

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