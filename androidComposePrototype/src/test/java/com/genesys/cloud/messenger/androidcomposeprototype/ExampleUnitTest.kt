package com.genesys.cloud.messenger.androidcomposeprototype

import com.genesys.cloud.messenger.androidcomposeprototype.ui.testbed.TestBedViewModel
import androidx.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleUnitTest {
    @Test
    suspend fun testConnecting() {
        var controller = TestingController()
        controller.doConfigureSession()
        controller.doConnect()
    }
}

class TestingController: TestBedViewModel() {
    override suspend fun doConnect() {
        super.doConnect()
    }
}
