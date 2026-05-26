package com.example.presentation.dto

data class CreateUserRequest(
    val fio: String,
    val phone: String,
    val email: String,
    val userTypeId: Int,
    val password: String
)