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
package shub39.momentum.navigation

import androidx.camera.core.CameraSelector
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import java.time.LocalDate
import org.koin.compose.viewmodel.koinViewModel
import shub39.momentum.app.MainAppState
import shub39.momentum.core.data_classes.CameraOptions
import shub39.momentum.core.data_classes.Day
import shub39.momentum.navigation.Routes.*
import shub39.momentum.presentation.home.Home
import shub39.momentum.presentation.onboarding.Onboarding
import shub39.momentum.presentation.project.ProjectAction
import shub39.momentum.presentation.project.ui.sections.Camera
import shub39.momentum.presentation.project.ui.sections.DayInfo
import shub39.momentum.presentation.project.ui.sections.ProjectCalendar
import shub39.momentum.presentation.project.ui.sections.ProjectDetails
import shub39.momentum.presentation.project.ui.sections.ProjectMontageView
import shub39.momentum.presentation.settings.ui.sections.About
import shub39.momentum.presentation.settings.ui.sections.Backup
import shub39.momentum.presentation.settings.ui.sections.Changelog
import shub39.momentum.presentation.settings.ui.sections.LookAndFeel
import shub39.momentum.presentation.settings.ui.sections.Root
import shub39.momentum.presentation.shared.MomentumTheme
import shub39.momentum.viewmodels.CameraViewModel
import shub39.momentum.viewmodels.HomeViewModel
import shub39.momentum.viewmodels.MainAppViewModel
import shub39.momentum.viewmodels.OnboardingViewModel
import shub39.momentum.viewmodels.ProjectViewModel
import shub39.momentum.viewmodels.SettingsViewModel

@Composable
fun MomentumNavDisplay(
    modifier: Modifier = Modifier,
    paywall: @Composable (Boolean, () -> Unit) -> Unit,
) {
    val mainVM = koinViewModel<MainAppViewModel>()
    val mainAppState by mainVM.state.collectAsStateWithLifecycle()

    val windowSizeClass = currentWindowAdaptiveInfoV2().windowSizeClass

    val stack = retain { TopLevelBackstack() }

    val twoPaneStrategy =
        remember(windowSizeClass) { TwoPaneSceneStrategy<Routes>(windowSizeClass) }
    val listDetailStrategy =
        remember(windowSizeClass) { ListDetailSceneStrategy<Routes>(windowSizeClass) }

    CompositionLocalProvider(LocalWindowSizeClass provides windowSizeClass) {
        MomentumTheme(theme = mainAppState.theme) {
            SharedTransitionLayout(modifier = modifier.background(colorScheme.background)) {
                NavDisplay(
                    backStack = stack.backStack,
                    sharedTransitionScope = this,
                    onBack = { stack.removeLast() },
                    sceneStrategies = listOf(listDetailStrategy, twoPaneStrategy),
                    entryProvider =
                        entryProvider {
                            entry<Home>(
                                metadata = ListDetailScene.listPane() + fadeTransitionMetadata()
                            ) {
                                val viewModel = koinViewModel<HomeViewModel>()
                                val state by viewModel.state.collectAsStateWithLifecycle()

                                Home(
                                    state = state,
                                    onAction = viewModel::onAction,
                                    onNavigateToSettings = { stack.add(Settings.SettingsRoot) },
                                    onNavigateToProject = { stack.add(Project.ProjectDetails) },
                                    isPlusUser = mainAppState.isPlusUser,
                                    onNavigateToPaywall = { stack.add(Paywall) },
                                )
                            }

                            entry<Onboarding>(metadata = verticalTransitionMetadata()) {
                                val viewModel = koinViewModel<OnboardingViewModel>()

                                Onboarding(
                                    onAction = viewModel::onAction,
                                    onNavigateBack = { stack.removeLast() },
                                )
                            }

                            entry<Paywall>(metadata = verticalTransitionMetadata()) {
                                paywall(mainAppState.isPlusUser) { stack.removeLast() }
                            }

                            settingsScreens(stack, mainAppState)

                            projectScreens(stack, mainAppState)
                        },
                )
            }
        }
    }
}

private fun EntryProviderScope<Routes>.projectScreens(
    stack: TopLevelBackstack,
    mainAppState: MainAppState,
) {
    entry<Project.ProjectDetails>(
        metadata =
            ListDetailScene.detailPane() + TwoPaneScene.twoPane() + horizontalTransitionMetadata()
    ) {
        val viewModel = koinViewModel<ProjectViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        ProjectDetails(
            state = state,
            onAction = viewModel::onAction,
            onNavigateBack = { stack.removeLast() },
            onNavigateToMontage = { stack.add(Project.ProjectMontageView) },
            onNavigateToCalendar = { stack.add(Project.ProjectCalendarView) },
            onNavigateToDayInfo = { stack.add(Project.DayInfoView(it)) },
        )
    }

    entry<Project.ProjectMontageView>(
        metadata = TwoPaneScene.twoPane() + verticalTransitionMetadata()
    ) {
        val viewModel = koinViewModel<ProjectViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()
        val exoPlayer by viewModel.exoPlayer.collectAsStateWithLifecycle()

        ProjectMontageView(
            state = state,
            onAction = viewModel::onAction,
            onNavigateBack = { stack.removeLast() },
            isPlusUser = mainAppState.isPlusUser,
            onNavigateToPaywall = { stack.add(Paywall) },
            exoPlayer = exoPlayer,
        )
    }

    entry<Project.ProjectCalendarView>(
        metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()
    ) {
        val viewModel = koinViewModel<ProjectViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        ProjectCalendar(
            state = state,
            onNavigateBack = { stack.removeLast() },
            onNavigateToDayInfo = { stack.add(Project.DayInfoView(it)) },
        )
    }

    entry<Project.DayInfoView>(metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()) {
        val viewModel = koinViewModel<ProjectViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        DayInfo(
            selectedDate = it.selectedDate,
            state = state,
            onAction = viewModel::onAction,
            onNavigateBack = { stack.removeLast() },
            onNavigateToCamera = { stack.add(Project.Camera(it.selectedDate)) },
        )
    }

    entry<Project.Camera>(metadata = verticalTransitionMetadata()) { entry ->
        val projectVM = koinViewModel<ProjectViewModel>()
        val state by projectVM.state.collectAsStateWithLifecycle()

        val cameraViewModel: CameraViewModel = koinViewModel()
        val surfaceRequest by cameraViewModel.surfaceRequest.collectAsStateWithLifecycle()
        val cameraSelector: CameraSelector by
            cameraViewModel.cameraSelector.collectAsStateWithLifecycle()

        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current

        val cameraOptions = state.project?.cameraOptions ?: CameraOptions()

        val lastImage =
            if (entry.selectedDate != LocalDate.now().toEpochDay()) null
            else if (state.days.getOrNull(0)?.date == entry.selectedDate) {
                state.days.getOrNull(1)?.image
            } else state.days.getOrNull(0)?.image

        LaunchedEffect(cameraOptions.isFrontCamera) {
            cameraViewModel.setCameraSelector(
                if (cameraOptions.isFrontCamera) CameraSelector.DEFAULT_FRONT_CAMERA
                else CameraSelector.DEFAULT_BACK_CAMERA
            )
        }

        LaunchedEffect(lifecycleOwner) {
            cameraViewModel.bindToCamera(context.applicationContext, lifecycleOwner)
        }

        Camera(
            surfaceRequest = surfaceRequest,
            cameraOptions = cameraOptions,
            cameraSelector = cameraSelector,
            lastImage = lastImage,
            onUpdateCameraOptions = { newOptions ->
                projectVM.onAction(ProjectAction.OnUpdateCameraOptions(newOptions))
            },
            onToggleCamera = {
                val isFront = cameraViewModel.toggleCamera()
                projectVM.onAction(
                    ProjectAction.OnUpdateCameraOptions(cameraOptions.copy(isFrontCamera = isFront))
                )
            },
            onTakePhoto = {
                cameraViewModel.takePhoto(
                    context = context,
                    onPhotoCaptured = { photoFile ->
                        val projectId = state.project?.id ?: return@takePhoto
                        val day = state.days.find { it.date == entry.selectedDate }
                        projectVM.onAction(
                            ProjectAction.OnUpsertDay(
                                day =
                                    day?.copy(image = photoFile.absolutePath)
                                        ?: Day(
                                            projectId = projectId,
                                            image = photoFile.absolutePath,
                                            comment = "",
                                            date = entry.selectedDate,
                                            isFavorite = false,
                                        ),
                                isNewImage = true,
                            )
                        )

                        stack.removeLast()
                    },
                )
            },
            onNavigateBack = { stack.removeLast() },
        )
    }
}

private fun EntryProviderScope<Routes>.settingsScreens(
    stack: TopLevelBackstack,
    mainAppState: MainAppState,
) {
    entry<Settings.SettingsRoot>(metadata = TwoPaneScene.twoPane() + verticalTransitionMetadata()) {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) { viewModel.onAction(OnOpenSettings) }

        Root(
            onNavigateBack = { stack.removeLast() },
            onNavigateToLookAndFeel = { stack.add(Settings.LookAndFeel) },
            onNavigateToOnboarding = { stack.add(Routes.Onboarding) },
            onNavigateToPaywall = { stack.add(Paywall) },
            onNavigateToChangelog = { stack.add(Settings.Changelog) },
            onNavigateToAppInfo = { stack.add(Settings.About) },
            onNavigateToBackup = { stack.add(Settings.Backup) },
            currentVersion = state.changelog.firstOrNull()?.version ?: "1.0.0",
        )
    }

    entry<Settings.LookAndFeel>(
        metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()
    ) {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        LookAndFeel(
            state = state,
            onAction = viewModel::onAction,
            onNavigateBack = { stack.removeLast() },
            isPlusUser = mainAppState.isPlusUser,
            onNavigateToPaywall = { stack.add(Paywall) },
        )
    }

    entry<Settings.Changelog>(metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()) {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) { viewModel.onAction(OnOpenChangelog) }

        Changelog(changelog = state.changelog, onNavigateBack = { stack.removeLast() })
    }

    entry<Settings.Backup>(metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()) {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        Backup(
            state = state,
            onAction = viewModel::onAction,
            onNavigateBack = { stack.removeLast() },
        )
    }

    entry<Settings.About>(metadata = TwoPaneScene.twoPane() + horizontalTransitionMetadata()) {
        val viewModel = koinViewModel<SettingsViewModel>()
        val state by viewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) { viewModel.onAction(OnOpenAbout) }

        About(
            versionName = state.changelog.firstOrNull()?.version ?: "1.0.0",
            onNavigateBack = { stack.removeLast() },
        )
    }
}
