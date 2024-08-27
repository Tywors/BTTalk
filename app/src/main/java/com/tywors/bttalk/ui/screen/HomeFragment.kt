package com.tywors.bttalk.ui.screen

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
        vBinding.apply {
            btAddContact.setOnClickListener {
                vModel.navigateTo(R.id.action_homeFragment_to_addContactFragment)
            }
        }
    }

    override fun setupFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    vModel.loadAddressFlow.collect {
                        if (it.first) {
                            vBinding.tvWalletAddress.text = it.second
                        }
                    }
                }
            }
        }
    }

    override fun initViewModel() {
        vModel.loadAddressWallet()
    }
}