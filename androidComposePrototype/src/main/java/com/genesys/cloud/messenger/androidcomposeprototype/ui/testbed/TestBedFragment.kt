package com.genesys.cloud.messenger.androidcomposeprototype.ui.testbed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import kotlinx.coroutines.runBlocking

class TestBedFragment : Fragment() {

    private val viewModel: TestBedViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewContent(this)
        runBlocking {
            viewModel.init(requireContext())
        }
    }

    private fun setViewContent(composeView: ComposeView) {
        composeView.setContent {
            TestBedScreen(viewModel)
        }
    }
}
