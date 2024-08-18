package com.tywors.bttalk.ui

import com.tywors.bttalk.R
import com.tywors.bttalk.databinding.FragmentCreateWalletBinding
import com.tywors.bttalk.viewmodel.CreateWalletViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateWalletFragment: BaseFragment<FragmentCreateWalletBinding, CreateWalletViewModel>(
    R.layout.fragment_create_wallet
) {

    private val vModel: CreateWalletViewModel by viewModel()

    override fun getViewModel(): CreateWalletViewModel {
        return vModel
    }

    override fun getViewBinding(): FragmentCreateWalletBinding {
        return FragmentCreateWalletBinding.inflate(layoutInflater)
    }

    override fun setupComponents() {
        vBinding.apply {
            tvCreatingWallet.text = "Funciona!"
        }
    }

}