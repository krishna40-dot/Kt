package com.taxiappclone.common.model

data class RiderAddress(
    val address_one: String,
    val address_two: String,
    val address_type: String,
    val city_id: String,
    val country_id: String,
    val id: String,
    val is_default: String,
    val latitude: String,
    val longitude: String,
    val name: String,
    val pincode: String,
    val state_id: String,
    val user_id: String
)