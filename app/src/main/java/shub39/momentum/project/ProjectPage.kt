package shub39.momentum.project

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProjectPage(
    state: ProjectState,
    onAction: (ProjectAction) -> Unit,
    onNavigateBack: () -> Unit
) {
    if (state.project == null) {
        LoadingIndicator()
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = state.project.title) },
                    subtitle = { Text(text = state.project.description) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
                            )
                        }
                    }
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.padding(padding)
            ) {
                items(state.days, key = { it.id }) { day ->
                    Text(day.toString())
                }
            }
        }
    }
}