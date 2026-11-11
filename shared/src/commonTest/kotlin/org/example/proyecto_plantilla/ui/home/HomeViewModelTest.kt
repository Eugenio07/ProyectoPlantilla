package org.example.proyecto_plantilla.ui.home

import org.example.proyecto_plantilla.core.platform.AppBuildInfo
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.assertEquals

class HomeViewModelTest {
    @Test
    fun exposesTheSampleIdThatOpensDetail() =
        assertEquals("42", HomeViewModel(AppBuildInfo(isDebug = false)).state.value.sampleDetailId)

    // CRASH-TEST-SCAFFOLD
    @Test
    fun crashTestButtonOnlyInDebug() {
        assertTrue(HomeViewModel(AppBuildInfo(isDebug = true)).state.value.showCrashTest, "depuración")
        assertFalse(HomeViewModel(AppBuildInfo(isDebug = false)).state.value.showCrashTest, "release")
    }
}
