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
package shub39.momentum.presentation.home.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil3.CoilImage
import java.time.LocalDate
import shub39.momentum.R
import shub39.momentum.core.data_classes.Day
import shub39.momentum.core.data_classes.Project
import shub39.momentum.core.data_classes.ProjectListData
import shub39.momentum.core.data_classes.Theme
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.presentation.shared.MomentumTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectListItem(
    projectListData: ProjectListData,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(32.dp),
        onClick = onClick,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            if (projectListData.last10Days.isNotEmpty()) {
                val carouselState = rememberCarouselState { projectListData.last10Days.size }

                HorizontalMultiBrowseCarousel(
                    state = carouselState,
                    modifier = Modifier.height(200.dp),
                    preferredItemWidth = 200.dp,
                    itemSpacing = 8.dp,
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp),
                ) {
                    val currentDay = projectListData.last10Days[it]

                    CoilImage(
                        imageModel = { currentDay.image },
                        failure = {
                            Box(
                                modifier = Modifier.matchParentSize(),
                                contentAlignment = Alignment.Center,
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.warning),
                                    contentDescription = "Warning",
                                )
                            }
                        },
                        imageOptions = ImageOptions(contentScale = ContentScale.Crop),
                        previewPlaceholder = painterResource(R.drawable.ic_launcher_foreground),
                        modifier = Modifier
                            .fillMaxSize()
                            .maskClip(RoundedCornerShape(16.dp)),
                    )
                }
            }

            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        text = projectListData.project.title,
                        style =
                            MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    )

                    if (projectListData.project.description.isNotBlank()) {
                        Text(
                            text = projectListData.project.description,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Icon(
                    painter = painterResource(R.drawable.arrow_forward),
                    contentDescription = "Navigate to project",
                )
            }
        }
    }
}

@Preview
@Composable
private fun Preview() {
    MomentumTheme(theme = Theme(appTheme = AppTheme.DARK)) {
        ProjectListItem(
            projectListData =
                ProjectListData(
                    project =
                        Project(
                            id = 1,
                            title = "Project 1",
                            description = "Description for project 1",
                        ),
                    last10Days =
                        (0..10).map { it1 ->
                            Day(
                                id = it1.toLong(),
                                projectId = it1.toLong(),
                                image = "",
                                comment = "",
                                date = LocalDate.now().toEpochDay(),
                                isFavorite = false,
                            )
                        },
                ),
            onClick = {},
        )
    }
}
