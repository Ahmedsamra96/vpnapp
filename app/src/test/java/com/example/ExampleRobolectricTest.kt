package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import android.app.Application
import com.example.ui.VpnViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Ruvon VPN", appName)
  }

  @Test
  fun `verify vpn consent acceptance`() {
    val app = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = VpnViewModel(app)
    viewModel.acceptConsent()
    assertTrue(viewModel.hasAcceptedConsent.value)
  }
}
