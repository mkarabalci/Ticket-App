package com.example.data.repository

import com.example.core.domain.purchase.Purchase
import com.example.core.domain.purchase.PurchaseItem
import com.example.core.domain.purchase.PurchaseRepository
import com.example.core.domain.purchase.PurchaseStatus
import com.example.data.dto.purchase.CreatePurchaseRequestDto
import com.example.data.dto.purchase.PurchaseDto
import com.example.data.dto.purchase.PurchaseItemRequestDto
import com.example.data.remote.PurchaseApi
import com.example.data.util.runCatchingApi

class PurchaseRepositoryImpl(
    private val purchaseApi: PurchaseApi
) : PurchaseRepository {

    override suspend fun createPurchase(items: List<PurchaseItem>): Result<Purchase> =
        runCatchingApi {
            purchaseApi.createPurchase(
                CreatePurchaseRequestDto(
                    items = items.map { PurchaseItemRequestDto(it.ticketTypeId, it.quantity) }
                )
            )
        }.map { it.toDomain() }

    override suspend fun pay(purchaseId: String): Result<Purchase> =
        runCatchingApi { purchaseApi.pay(purchaseId) }.map { it.toDomain() }

    override suspend fun getPurchase(purchaseId: String): Result<Purchase> =
        runCatchingApi { purchaseApi.getPurchase(purchaseId) }.map { it.toDomain() }

    private fun PurchaseDto.toDomain(): Purchase = Purchase(
        id = id,
        status = when (status.uppercase()) {
            "PAID" -> PurchaseStatus.PAID
            else -> PurchaseStatus.PENDING
        },
        items = items.map { PurchaseItem(it.ticketTypeId, it.quantity) },
        totalCents = totalCents
    )
}