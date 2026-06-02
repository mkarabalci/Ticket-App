package com.example.core.util

import com.example.core.network.ApiException
import com.example.core.network.NetworkException

fun Throwable.toUserMessage(): String = when (this) {
    is ApiException -> when {
        code == 401 -> "Email veya şifre hatalı"
        code == 409 && errorMessage?.contains("capacity_exceeded") == true -> "Stok yetersiz, lütfen yenileyin"
        code == 409 && errorMessage?.contains("already_paid") == true -> "Bu sipariş zaten ödendi"
        code == 403 && errorMessage?.contains("not_purchase_owner") == true -> "Bu siparişe erişim yetkiniz yok"
        code == 409 -> "Çakışma hatası oluştu"
        code in 500..599 -> "Sunucu şu anda cevap veremiyor"
        else -> "Beklenmeyen bir hata oluştu"
    }
    is NetworkException -> "İnternet bağlantısı yok"
    else -> message ?: "Bilinmeyen bir hata oluştu"
}