package com.example.bookworm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.bookworm.ui.navigation.BottomDestination
import com.example.bookworm.ui.navigation.bottomDestinations
import com.example.bookworm.ui.screens.ArchiveScreen
import com.example.bookworm.ui.screens.DictionaryScreen
import com.example.bookworm.ui.screens.HomeScreen
import com.example.bookworm.ui.screens.ProfileScreen
import com.example.bookworm.ui.theme.BookWormTheme
import com.example.bookworm.data.repository.BookWormRepository
import com.example.bookworm.model.AppAppearance
import com.example.bookworm.viewmodel.ArchiveViewModel
import com.example.bookworm.viewmodel.BookWormViewModelFactory
import com.example.bookworm.viewmodel.DictionaryViewModel
import com.example.bookworm.viewmodel.HomeViewModel
import com.example.bookworm.viewmodel.ProfileViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = (application as BookWormApplication).appContainer.repository
        val factory = BookWormViewModelFactory(repository)
        setContent { BookWormApp(factory, repository) }
    }
}

@Composable
private fun BookWormApp(factory: BookWormViewModelFactory, repository: BookWormRepository) {
    val appearance = repository.appearance.collectAsStateWithLifecycle(initialValue = AppAppearance()).value
    BookWormTheme(appearance = appearance) {
        val navController = rememberNavController()
        val backStackEntry = navController.currentBackStackEntryAsState().value
        val currentRoute = backStackEntry?.destination?.route ?: BottomDestination.Home.route
        Scaffold(
            bottomBar = {
                NavigationBar {
                    bottomDestinations.forEach { destination ->
                        NavigationBarItem(
                            selected = currentRoute == destination.route,
                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(destination.icon, contentDescription = destination.label) },
                            label = { Text(destination.label) },
                        )
                    }
                }
            },
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = BottomDestination.Home.route,
                modifier = Modifier.padding(padding),
            ) {
                composable(BottomDestination.Home.route) { HomeScreen(viewModel<HomeViewModel>(factory = factory)) }
                composable(BottomDestination.Dictionary.route) { DictionaryScreen(viewModel<DictionaryViewModel>(factory = factory)) }
                composable(BottomDestination.Archive.route) { ArchiveScreen(viewModel<ArchiveViewModel>(factory = factory)) }
                composable(BottomDestination.Profile.route) { ProfileScreen(viewModel<ProfileViewModel>(factory = factory)) }
            }
        }
    }
}
