package com.tywors.bttalk.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import com.tywors.bttalk.R
import com.tywors.bttalk.databinding.ActivityMainBinding
import com.tywors.bttalk.ui.interfaces.IMainActivity
import com.tywors.bttalk.viewmodel.MainActivityViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity(), IMainActivity {

    private lateinit var vBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(vBinding.root)
    }

    override suspend fun navigateTo(id: Int) {
        findNavController(R.id.navHostFragment).navigate(id)
    }
}