package shub39.momentum.presentation.project.ui.component

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes.Companion.VerySunny
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import shub39.momentum.R
import shub39.momentum.presentation.shared.MomentumTheme

@Composable
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
fun CreateMontageButton(
    daysSize: Int,
    onNavigateToMontage: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dayProgress by animateFloatAsState(
        targetValue = daysSize.toFloat() / 5f
    )

    val canCreateMontage = daysSize >= 5

    val roundness by animateDpAsState(
        targetValue = if (canCreateMontage) 16.dp else 100.dp
    )

    val infiniteTransition =
        rememberInfiniteTransition(label = "rotation")

    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 2000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Restart
        ),
        label = "infinite rotation"
    )

    Card(
        onClick = {
            if (canCreateMontage) onNavigateToMontage()
        },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(roundness),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.8f),
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
        )
    ) {
        Box {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .drawWithContent {
                        val width = size.width * dayProgress

                        clipRect(
                            right = width
                        ) {
                            this@drawWithContent.drawContent()
                        }
                    }
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(16.dp)
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.montage),
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )

                    val daysLeft = 5 - daysSize
                    Text(
                        text = if (!canCreateMontage) {
                            pluralStringResource(
                                id = R.plurals.add_more_days,
                                count = daysLeft,
                                formatArgs = arrayOf(daysLeft)
                            )
                        } else {
                            "$daysSize ${stringResource(R.string.days)}"
                        },
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier.wrapContentSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .then(
                                if (canCreateMontage) {
                                    Modifier.graphicsLayer {
                                        rotationZ = rotation
                                    }
                                } else Modifier
                            )
                            .background(
                                color = MaterialTheme.colorScheme.secondary,
                                shape = VerySunny.toShape()
                            )
                    )

                    Icon(
                        painter = painterResource(R.drawable.arrow_forward),
                        contentDescription = "Create Montage",
                        tint = MaterialTheme.colorScheme.onSecondary
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
        CreateMontageButton(
            daysSize = 7,
            onNavigateToMontage = {}
        )
    }
}