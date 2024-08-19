package com.tywors.bttalk.ui.screen

import android.view.View
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.itsxtt.patternlock.PatternLockView
import com.tywors.bttalk.R
import com.tywors.bttalk.databinding.FragmentCreateWalletBinding
import com.tywors.bttalk.ui.BaseFragment
import com.tywors.bttalk.viewmodel.CreateWalletViewModel
import kotlinx.coroutines.launch
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
            patternLockView.setOnPatternListener(object : PatternLockView.OnPatternListener {
                override fun onComplete(ids: ArrayList<Int>): Boolean {
                    val isCorrect = vModel.correctNewPattern(ids)

                    if (!isCorrect) {
                        vModel.firstPattern = null
                    }

                    if (vModel.firstPattern != null) {
                        tvCreatePassword.text = "Repeat your password"
                    } else {
                        tvCreatePassword.text = "Create your password"
                    }

                    return isCorrect
                }
            })

            tvCreatingWallet.text = "Funciona!"
        }
    }

    override fun setupFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vModel.passwordFlow.collect {
                        if (it) {
                            vBinding.patternLockView.visibility = View.GONE
                            vBinding.tvCreatePassword.visibility = View.GONE
                            vBinding.imCreatePassword.visibility = View.GONE
                            vBinding.prCreatingWallet.visibility = View.VISIBLE
                            vBinding.tvCreatingWallet.visibility = View.VISIBLE

                            vModel.saveNewWallet()
                        }
                    }
                }

                launch {
                    vModel.walletCreated.collect {
                        if (it) {
                            iMainActivity.navigateTo(R.id.action_createWalletFragment_to_homeFragment)
                        }
                    }
                }
            }
        }
    }

    override fun initViewModel() {

    }

}