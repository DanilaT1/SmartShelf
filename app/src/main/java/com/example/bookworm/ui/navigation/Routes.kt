package com.example.bookworm.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomDestination(val route: String, val label: String, val icon: ImageVector) {
    data object Home : BottomDestination("home", "Home", Icons.Default.Home)
    data object Dictionary : BottomDestination("dictionary", "Dictionary", Icons.AutoMirrored.Filled.MenuBook)
    data object Archive : BottomDestination("archive", "Archive", Icons.Default.Archive)
    data object Profile : BottomDestination("profile", "Profile", Icons.Default.Person)
}

val bottomDestinations = listOf(
    BottomDestination.Home,
    BottomDestination.Dictionary,
    BottomDestination.Archive,
    BottomDestination.Profile,
)
