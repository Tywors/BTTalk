package com.tywors.bttalk.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

open class BaseViewModel(
    context: Application,
) : ViewModel() {

    private val _navigationFlow = MutableSharedFlow<Int>()
    val navigationFlow = _navigationFlow.asSharedFlow()

    fun navigateTo(id: Int) {
        viewModelScope.launch {
            _navigationFlow.emit(id)
        }
    }
}