package com.tywors.bttalk.ui.screen

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.tywors.bttalk.R
import com.tywors.bttalk.databinding.FragmentAddContactBinding
import com.tywors.bttalk.databinding.FragmentHomeBinding
import com.tywors.bttalk.databinding.FragmentSplashBinding
import com.tywors.bttalk.ui.BaseFragment
import com.tywors.bttalk.viewmodel.AddContactViewModel
import com.tywors.bttalk.viewmodel.HomeViewModel
import com.tywors.bttalk.viewmodel.SplashViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddContactFragment: BaseFragment<FragmentAddContactBinding, AddContactViewModel>(
    R.layout.fragment_add_contact
) {

    private val vModel: AddContactViewModel by viewModel()

    override fun getViewModel(): AddContactViewModel {
        return vModel
    }

    override fun getViewBinding(): FragmentAddContactBinding {
        return FragmentAddContactBinding.inflate(layoutInflater)
    }

    override fun setupComponents() {

    }

    override fun setupFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {

                }
            }
        }
    }

    override fun initViewModel() {

    }
}