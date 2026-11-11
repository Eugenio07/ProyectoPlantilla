package org.example.proyecto_plantilla.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import org.example.proyecto_plantilla.ui.detail.DetailScreen
import org.example.proyecto_plantilla.ui.home.HomeScreen
import org.example.proyecto_plantilla.ui.splash.SplashScreen
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

/**
 * Splash (inicio) → Home → Detail(id). Al salir de la Splash se saca de la pila (`popUpTo` inclusivo): atrás desde Home
 * sale de la app. En iOS el back-swipe lo da el propio NavHost (sin transiciones propias, que lo desactivarían).
 */
@Composable
fun AppNavHost(startAtHome: Boolean) {
    val nav = rememberNavController()
    NavHost(navController = nav, startDestination = if (startAtHome) HomeRoute else SplashRoute) {
        composable<SplashRoute> {
            SplashScreen(
                viewModel = koinViewModel(),
                onReady = {
                    nav.navigate(HomeRoute) { popUpTo<SplashRoute> { inclusive = true } }
                },
            )
        }
        composable<HomeRoute> {
            HomeScreen(viewModel = koinViewModel(), onOpenDetail = { id -> nav.navigate(DetailRoute(id)) })
        }
        composable<DetailRoute> { entry ->
            val route = entry.toRoute<DetailRoute>()
            DetailScreen(viewModel = koinViewModel { parametersOf(route.id) })
        }
    }
}
