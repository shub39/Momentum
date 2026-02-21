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
package shub39.momentum.presentation.project

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import shub39.momentum.core.data_classes.Day
import shub39.momentum.core.data_classes.MontageConfig
import shub39.momentum.core.data_classes.Project
import shub39.momentum.core.interfaces.MontageState

sealed interface ScanState {
    data object Idle : ScanState

    data class Processing(val progress: Float) : ScanState

    data object Done : ScanState
}

@Stable
@Immutable
data class ProjectState(
    val project: Project? = null,
    val days: List<Day> = emptyList(),
    val montage: MontageState = MontageState.ProcessingImages(),
    val montageConfig: MontageConfig = MontageConfig(),
    val scanState: ScanState = ScanState.Idle,
)
