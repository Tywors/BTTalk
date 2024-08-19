package com.tywors.bttalk.ui.screen

import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tywors.bttalk.R
import com.tywors.bttalk.databinding.FragmentHomeBinding
import com.tywors.bttalk.ui.BaseFragment
import com.tywors.bttalk.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment: BaseFragment<FragmentHomeBinding, HomeViewModel>(
    R.layout.fragment_home
) {

    private val vModel: HomeViewModel by viewModel()

    override fun getViewModel(): HomeViewModel {
        return vModel
    }

    override fun getViewBinding(): FragmentHomeBinding {
        return FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun setupComponents() {

    }

    override fun setupFlows() {
        Log.d("XXX", "setupFlows()")

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vModel.loadAddressFlow.collect {
                        Log.d("XXX", "collect")

                        if (it.first) {
                            Log.d("XXX", it.second)
                            vBinding.tvWalletAddress.text = it.second
                        }
                    }
                }
            }
        }
    }

    override fun initViewModel() {
        Log.d("XXX", "loadAddressWallet()")
        vModel.loadAddressWallet()
    }
}