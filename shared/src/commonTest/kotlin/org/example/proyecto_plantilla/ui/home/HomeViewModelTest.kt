package org.example.proyecto_plantilla.ui.home

import kotlin.test.Test
import kotlin.test.assertEquals

class HomeViewModelTest {
    @Test
    fun exposesTheSampleIdThatOpensDetail() = assertEquals("42", HomeViewModel().state.value.sampleDetailId)
}
