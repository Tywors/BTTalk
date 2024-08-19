package com.tywors.bttalk.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.viewModelScope
import com.tywors.bttalk.domain.usecase.WalletUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CreateWalletViewModel(
    private val walletUseCase: WalletUseCase,
    context: Application
): BaseViewModel(context) {

    private val _passwordFlow = MutableSharedFlow<Boolean>()
    val passwordFlow = _passwordFlow.asSharedFlow()

    var firstPattern: ArrayList<Int>? = null
    private var passwordCreated: Boolean = false

    fun correctNewPattern(ids: ArrayList<Int>): Boolean {
        if (ids.size < 6) {
            return false
        }

        if (firstPattern != null) {
            passwordCreated = firstPattern == ids

            viewModelScope.launch {
                withContext(Dispatchers.IO) {
                    _passwordFlow.emit(passwordCreated)
                }
            }

            return passwordCreated
        } else {
            firstPattern = ids
            return true
        }
    }

    fun saveNewWallet() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = walletUseCase.createWallet(firstPattern.toString())
                Toast.makeText(context, result.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }
}