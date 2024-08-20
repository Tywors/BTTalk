package com.tywors.bttalk.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.tywors.bttalk.domain.usecase.ContactUseCase
import com.tywors.bttalk.domain.usecase.SecurityUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddContactViewModel(
    private val securityUseCase: SecurityUseCase,
    private val contactUseCase: ContactUseCase,
    context: Application
): BaseViewModel(context) {

    private val TAG = "AddContactViewModel"

    private val _generatedTemporalContactFlow = MutableSharedFlow<Boolean>()
    val generatedTemporalContactFlow = _generatedTemporalContactFlow.asSharedFlow()

    fun generateTemporalContact() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val id = securityUseCase.generateRandomKey(8)
                val ownKey =  securityUseCase.generateRandomKey(32)

                val result = contactUseCase.createTemporalContact(id, ownKey)

                _generatedTemporalContactFlow.emit(result > 0)
            }
        }
    }

}