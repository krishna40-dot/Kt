package com.taxiappclone.common.model

data class VehicleDetails(
    val base_fare: String,
    val cancellation_charge: String,
    val capacity: String,
    val countries: String,
    val created_at: String,
    val discount: String,
    val id: String,
    val image: String,
    val modified_at: String,
    val name: String,
    val per_km_charge: String,
    val per_min_charge: String,
    val status: String,
    val tax: String,
    val type: String,
    val waiting_charge: String
){
    override fun toString(): String {
        return "VehicleDetails(base_fare='$base_fare', cancellation_charge='$cancellation_charge', capacity='$capacity', countries='$countries', created_at='$created_at', discount='$discount', id='$id', image='$image', modified_at='$modified_at', name='$name', per_km_charge='$per_km_charge', per_min_charge='$per_min_charge', status='$status', tax='$tax', type='$type', waiting_charge='$waiting_charge')"
    }
}