package com.taxiappclone.common.model

data class VehicleDetailsResponse(
        val `data`: List<VehicleDetails>,
        val status: Boolean
){
    override fun toString(): String {
        return "VehicleDetailsResponse(`data`=$`data`, status=$status)"
    }
}