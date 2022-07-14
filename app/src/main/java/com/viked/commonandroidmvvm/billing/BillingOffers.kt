package com.viked.commonandroidmvvm.billing

import com.android.billingclient.api.ProductDetails

class BillingOffers {
    /**
     * Retrieves all eligible base plans and offers using tags from ProductDetails.
     *
     * @param productDetails use offerDetails from a ProductDetails returned by the library.
     *
     * @return the offer id token of the lowest priced offer.
     *
     */
    fun getOfferToken(productDetails: ProductDetails): String? {
        val offers = productDetails.subscriptionOfferDetails?.toList() ?: emptyList()
        return leastPricedOfferToken(offers)
    }

    /**
     * Calculates the lowest priced offer amongst all eligible offers.
     * In this implementation the lowest price of all offers' pricing phases is returned.
     * It's possible the logic can be implemented differently.
     * For example, the lowest average price in terms of month could be returned instead.
     *
     * @param offerDetails List of of eligible offers and base plans.
     *
     * @return the offer id token of the lowest priced offer.
     */
    private fun leastPricedOfferToken(
        offerDetails: List<ProductDetails.SubscriptionOfferDetails>
    ): String? {
        var offerToken: String? = null
        var leastPricedOffer: ProductDetails.SubscriptionOfferDetails
        var lowestPrice = Int.MAX_VALUE

        if (offerDetails.isNotEmpty()) {
            for (offer in offerDetails) {
                for (price in offer.pricingPhases.pricingPhaseList) {
                    if (price.priceAmountMicros < lowestPrice) {
                        lowestPrice = price.priceAmountMicros.toInt()
                        leastPricedOffer = offer
                        offerToken = leastPricedOffer.offerToken
                    }
                }
            }
        }
        return offerToken
    }
}