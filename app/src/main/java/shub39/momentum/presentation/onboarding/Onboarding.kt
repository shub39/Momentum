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
package shub39.momentum.presentation.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import shub39.momentum.core.data_classes.Theme
import shub39.momentum.core.enums.AppTheme
import shub39.momentum.presentation.onboarding.ui.components.AutoAnimation
import shub39.momentum.presentation.onboarding.ui.components.CameraAnimation
import shub39.momentum.presentation.onboarding.ui.components.PrivateAnimation
import shub39.momentum.presentation.shared.MomentumTheme

@Composable
fun Onboarding(
    state: OnboardingState,
    onAction: (OnboardingAction) -> Unit,
    onNavigateToHome: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState { 3 }

    Scaffold { paddingValues ->
        HorizontalPager(
            state = pagerState,
            pageSpacing = 32.dp,
            contentPadding =
                PaddingValues(
                    top = paddingValues.calculateTopPadding() + 16.dp,
                    bottom = paddingValues.calculateBottomPadding() + 16.dp,
                    start = paddingValues.calculateLeftPadding(LayoutDirection.Ltr) + 32.dp,
                    end = paddingValues.calculateRightPadding(LayoutDirection.Ltr) + 32.dp,
                ),
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            when (page) {
                0 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CameraAnimation()

                            Spacer(modifier = Modifier.height(32.dp))

                            Text(
                                text = "Create Montages Easily",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                            )

                            Text(
                                text = "Get reminded to take a picture everyday.",
                                textAlign = TextAlign.Center,
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = { scope.launch { pagerState.animateScrollToPage(1) } }
                            ) {
                                Text(text = "Next")
                            }
                        }
                    }
                }

                1 -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            AutoAnimation()

                            Spacer(modifier = Modifier.height(32.dp))

                            Text(
                                text = "Convenient and Automatic",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                            )

                            Text(
                                text = "Don't even think about storage or editing.",
                                textAlign = TextAlign.Center,
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = { scope.launch { pagerState.animateScrollToPage(2) } }
                            ) {
                                Text(text = "Next")
                            }
                        }
                    }
                }

                else -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            PrivateAnimation()

                            Spacer(modifier = Modifier.height(32.dp))

                            Text(
                                text = "Permissionless and Private",
                                style = MaterialTheme.typography.titleLarge,
                                textAlign = TextAlign.Center,
                            )

                            Text(
                                text =
                                    "No invasive permissions, Everything is processed on device!",
                                textAlign = TextAlign.Center,
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = {
                                    onAction(OnboardingAction.OnOnboardingDone)
                                    onNavigateToHome()
                                }
                            ) {
                                Text(text = "Done")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun Preview() {
    MomentumTheme(theme = Theme(appTheme = AppTheme.DARK)) {
        Onboarding(state = OnboardingState(), onAction = {}, onNavigateToHome = {})
    }
}
