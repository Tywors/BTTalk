package com.tywors.bttalk.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.tywors.bttalk.domain.usecase.WalletUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(
    private val walletUseCase: WalletUseCase,
    context: Application,
): BaseViewModel(context) {

    private val _loadAddressFlow = MutableSharedFlow<Pair<Boolean, String>>()
    val loadAddressFlow = _loadAddressFlow.asSharedFlow()

    fun loadAddressWallet() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val address = walletUseCase.loadAddressWallet()
                address?.let {
                    _loadAddressFlow.emit(Pair(address != "", address))
                }
            }
        }
    }

}