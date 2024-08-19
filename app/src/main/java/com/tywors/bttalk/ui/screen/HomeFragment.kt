package com.tywors.bttalk.ui.screen

import com.tywors.bttalk.R
import com.tywors.bttalk.databinding.FragmentWelcomeBinding
import com.tywors.bttalk.ui.BaseFragment
import com.tywors.bttalk.viewmodel.WelcomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class WelcomeFragment: BaseFragment<FragmentWelcomeBinding, WelcomeViewModel>(
    R.layout.fragment_welcome
) {

    private val vModel: WelcomeViewModel by viewModel()

    override fun getViewModel(): WelcomeViewModel {
        return vModel
    }

    override fun getViewBinding(): FragmentWelcomeBinding {
        return FragmentWelcomeBinding.inflate(layoutInflater)
    }

    override fun setupComponents() {
        vModel.navigateTo(R.id.action_welcomeFragment_to_createWalletFragment)

        vBinding.btCreateWallet.setOnClickListener {
            vModel.navigateTo(R.id.action_welcomeFragment_to_createWalletFragment)
        }
    }

    override fun setupFlows() {

    }
}