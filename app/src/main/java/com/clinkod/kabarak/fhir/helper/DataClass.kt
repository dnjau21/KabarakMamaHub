package com.clinkod.kabarak.fhir.helper

data class CodingObservation(
    val code: String,
    val display: String,
    val value: String
)
data class QuantityObservation(
    val code: String,
    val display: String,
    val value: String,
    val unit: String,
)