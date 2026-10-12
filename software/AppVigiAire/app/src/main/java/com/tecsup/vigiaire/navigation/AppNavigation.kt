package com.tecsup.vigiaire.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tecsup.vigiaire.ui.screens.auth.LoginScreen
import com.tecsup.vigiaire.ui.screens.detalle.DetalleScreen
import com.tecsup.vigiaire.ui.screens.dispositivo.DispositivoScreen
import com.tecsup.vigiaire.ui.screens.home.HomeScreen
import com.tecsup.vigiaire.ui.screens.perfil.PerfilScreen
import com.tecsup.vigiaire.ui.screens.recomendacion.RecomendacionScreen
import com.tecsup.vigiaire.ui.screens.splash.SplashScreen
import com.tecsup.vigiaire.ui.screens.tendencia.TendenciaScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Pantalla.Splash.ruta,
        enterTransition = { fadeIn(animationSpec = tween(350)) },
        exitTransition = { fadeOut(animationSpec = tween(350)) },
        popEnterTransition = { fadeIn(animationSpec = tween(350)) },
        popExitTransition = { fadeOut(animationSpec = tween(350)) }
    ) {
        // 1. Splash / Bienvenida
        composable(Pantalla.Splash.ruta) {
            SplashScreen(
                onComenzarClick = {
                    navController.navigate(Pantalla.Login.ruta) {
                        popUpTo(Pantalla.Splash.ruta) { inclusive = true }
                    }
                }
            )
        }

        // 2. Login
        composable(Pantalla.Login.ruta) {
            LoginScreen(
                onLoginExitoso = {
                    navController.navigate(Pantalla.Home.ruta) {
                        popUpTo(Pantalla.Login.ruta) { inclusive = true }
                    }
                }
            )
        }

        // 3. Home / Dashboard
        composable(Pantalla.Home.ruta) {
            HomeScreen(
                currentRoute = Pantalla.Home.ruta,
                onNavigate = { ruta ->
                    if (ruta != Pantalla.Home.ruta) {
                        navController.navigate(ruta) {
                            popUpTo(Pantalla.Home.ruta)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        // 4. Detalle del Aire
        composable(Pantalla.Detalle.ruta) {
            DetalleScreen(
                currentRoute = Pantalla.Detalle.ruta,
                onNavigate = { ruta ->
                    navController.navigate(ruta) {
                        popUpTo(Pantalla.Home.ruta)
                        launchSingleTop = true
                    }
                }
            )
        }

        // 5. Recomendación / ¿Ventilo ahora?
        composable(Pantalla.Recomendacion.ruta) {
            RecomendacionScreen(
                currentRoute = Pantalla.Recomendacion.ruta,
                onNavigate = { ruta ->
                    navController.navigate(ruta) {
                        popUpTo(Pantalla.Home.ruta)
                        launchSingleTop = true
                    }
                }
            )
        }

        // 6. Tendencia de Hoy
        composable(Pantalla.Tendencia.ruta) {
            TendenciaScreen(
                currentRoute = Pantalla.Tendencia.ruta,
                onNavigate = { ruta ->
                    navController.navigate(ruta) {
                        popUpTo(Pantalla.Home.ruta)
                        launchSingleTop = true
                    }
                }
            )
        }

        // 7. Tu Dispositivo
        composable(Pantalla.Dispositivo.ruta) {
            DispositivoScreen(
                currentRoute = Pantalla.Dispositivo.ruta,
                onNavigate = { ruta ->
                    navController.navigate(ruta) {
                        popUpTo(Pantalla.Home.ruta)
                        launchSingleTop = true
                    }
                }
            )
        }

        // 8. Perfil de Usuario
        composable(Pantalla.Perfil.ruta) {
            PerfilScreen(
                onVolver = { navController.popBackStack() },
                onCerrarSesion = {
                    navController.navigate(Pantalla.Login.ruta) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
