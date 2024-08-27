package com.tywors.bttalk.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.tywors.bttalk.domain.usecase.WalletUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashViewModel(
    private val walletUseCase: WalletUseCase,
    context: Application,
): BaseViewModel(context) {

    private val _haveWalletFlow = MutableSharedFlow<Boolean>()
    val haveWalletFlow = _haveWalletFlow.asSharedFlow()

    fun checkIfHaveWallet() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val loadedAddress = walletUseCase.loadAddressWallet()

                _haveWalletFlow.emit(
                    loadedAddress != null && loadedAddress != ""
                )
            }
        }
    }
}