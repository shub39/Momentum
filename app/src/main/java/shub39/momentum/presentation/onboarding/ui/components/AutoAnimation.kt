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
package shub39.momentum.presentation.onboarding.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import shub39.momentum.R

@Composable
fun AutoAnimation() {
    val iconSize = 100.dp
    val notificationSize = 38.dp

    val iconOffsetY = remember { Animatable(-200f) }
    val notifScale = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        iconOffsetY.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        )
        delay(200)
        notifScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        )
    }

    Box(
        modifier = Modifier
            .size(iconSize)
            .offset { IntOffset(0, iconOffsetY.value.roundToInt()) },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.image),
            contentDescription = "Camera",
            modifier = Modifier.fillMaxSize(),
            tint = MaterialTheme.colorScheme.primary,
        )

        Icon(
            painter = painterResource(R.drawable.check_circle),
            contentDescription = "Notification",
            modifier =
                Modifier
                    .size(notificationSize)
                    .align(Alignment.TopEnd)
                    .scale(notifScale.value),
            tint = MaterialTheme.colorScheme.error,
        )
    }
}
