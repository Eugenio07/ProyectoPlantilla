package org.example.proyecto_plantilla.ui

import org.example.proyecto_plantilla.ui.detail.DetailViewModel
import org.example.proyecto_plantilla.ui.home.HomeViewModel
import org.example.proyecto_plantilla.ui.splash.SplashViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

/** ViewModels. Lo registran las raíces de composición de cada plataforma (nada de `core` depende de `ui`). */
val uiModule: Module = module {
    viewModelOf(::SplashViewModel)
    viewModelOf(::HomeViewModel)
    viewModelOf(::DetailViewModel)
}
