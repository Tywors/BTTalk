package com.tywors.bttalk.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import com.tywors.bttalk.ui.interfaces.IMainActivity
import com.tywors.bttalk.viewmodel.BaseViewModel
import kotlinx.coroutines.launch

abstract class BaseFragment<VBinding : ViewBinding, VModel : BaseViewModel>(
    layoutID: Int,
) : Fragment(layoutID) {

    protected lateinit var iMainActivity: IMainActivity
    private lateinit var context: Context

    lateinit var mViewModel: VModel
    protected abstract fun getViewModel(): VModel

    lateinit var vBinding: VBinding
    protected abstract fun getViewBinding(): VBinding

    abstract fun setupComponents()

    final override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vBinding = getViewBinding()
        mViewModel = getViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        vBinding = getViewBinding()
        return vBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOwnObservers()
        setupComponents()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
        iMainActivity = context as IMainActivity
    }


    private fun initOwnObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    mViewModel.navigationFlow.collect { id ->
                        iMainActivity.navigateTo(id)
                    }
                }
            }
        }
    }
}