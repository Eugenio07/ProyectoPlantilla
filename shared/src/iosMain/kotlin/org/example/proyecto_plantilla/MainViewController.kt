package org.example.proyecto_plantilla

import androidx.compose.ui.window.ComposeUIViewController

// Swift la llama por este nombre (`MainViewControllerKt.MainViewController()`): es la fábrica del controlador de iOS.
@Suppress("ktlint:standard:function-naming", "FunctionNaming")
fun MainViewController() = ComposeUIViewController { App(startAtHome = false) }
