package com.viked.commonandroidmvvm

import com.viked.commonandroidmvvm.preference.PreferenceHelper
import com.viked.commonandroidmvvm.preference.get
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.lang.Boolean
import java.lang.Integer
import kotlin.Double
import kotlin.Float
import kotlin.Int
import kotlin.assert
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PreferencesTest {

    lateinit var preferenceHelper: PreferenceHelper

    @BeforeAll
    fun setUp() {
        preferenceHelper = mockk {}
    }

    @Test
    fun integer() {
        every { preferenceHelper.getValue(any(), Integer::class.java) } returns Integer(11)
        val i: Int = preferenceHelper[0]!!
        assertEquals(i, 11)
        verify { preferenceHelper.getValue(any(), Integer::class.java) }
    }

    @Test
    fun double() {
        every { preferenceHelper.getValue(any(), java.lang.Double::class.java) } returns java.lang.Double(11.0)
        val i: Double = preferenceHelper[0]!!
        assertEquals(i, 11.0)
        verify { preferenceHelper.getValue(any(), java.lang.Double::class.java) }
    }

    @Test
    fun float() {
        every { preferenceHelper.getValue(any(), java.lang.Float::class.java) } returns java.lang.Float(11.0)
        val i: Float = preferenceHelper[0]!!
        assertEquals(i, 11.0f)
        verify { preferenceHelper.getValue(any(), java.lang.Float::class.java) }
    }

    @Test
    fun bool() {
        every { preferenceHelper.getValue(any(), Boolean::class.java) } returns Boolean(true)
        val i: kotlin.Boolean = preferenceHelper[0]!!
        assert(i)
        verify { preferenceHelper.getValue(any(), Boolean::class.java) }
    }


}