package org.example.proyecto_plantilla.ui.detail

import kotlin.test.Test
import kotlin.test.assertEquals

class DetailViewModelTest {
    @Test
    fun exposesTheIdItWasOpenedWith() = assertEquals(DetailUiState("42"), DetailViewModel("42").state.value)
}
