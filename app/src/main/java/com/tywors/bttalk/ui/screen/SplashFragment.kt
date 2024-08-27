package com.tywors.bttalk.ui.screen

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tywors.bttalk.R
import com.tywors.bttalk.databinding.FragmentHomeBinding
import com.tywors.bttalk.databinding.FragmentSplashBinding
import com.tywors.bttalk.ui.BaseFragment
import com.tywors.bttalk.viewmodel.HomeViewModel
import com.tywors.bttalk.viewmodel.SplashViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class SplashFragment: BaseFragment<FragmentSplashBinding, SplashViewModel>(
    R.layout.fragment_splash
) {

    private val vModel: SplashViewModel by viewModel()

    override fun getViewModel(): SplashViewModel {
        return vModel
    }

    override fun getViewBinding(): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(layoutInflater)
    }

    override fun setupComponents() {

    }

    override fun setupFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vModel.haveWalletFlow.collect {
                        if (it) {
                            iMainActivity.navigateTo(R.id.action_splashFragment_to_homeFragment)
                        } else {
                            iMainActivity.navigateTo(R.id.action_splashFragment_to_welcomeFragment)
                        }
                    }
                }
            }
        }
    }

    override fun initViewModel() {
        vModel.checkIfHaveWallet()
    }
}