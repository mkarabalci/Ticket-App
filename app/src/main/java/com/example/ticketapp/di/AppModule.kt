package com.example.ticketapp.di

import com.example.ticketapp.viewmodel.LoginViewModel
import com.example.ticketapp.viewmodel.RegisterViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appModule = module {
    // viewModel
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
}