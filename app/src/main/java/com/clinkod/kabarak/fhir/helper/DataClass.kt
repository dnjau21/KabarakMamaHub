package com.clinkod.kabarak.fhir.helper

import com.clinkod.kabarak.R
import com.google.gson.annotations.SerializedName
import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Reference

enum class UrlData(var message: Int) {
    BASE_URL(R.string.base_url),
    FHIR_URL(R.string.fhir_url)
}
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
enum class DbResourceViews {
    CLIENT_WEARABLE_RECORDING,

    SYSTOLIC_BP,
    DIASTOLIC_BP,
    PULSE_RATE,
    TEMPARATURE,

    EDD,
    IPT_VISIT,
    HIV_NR_DATE,
    REFERRAL_PARTNER_HIV_DATE,
    CLINICAL_NOTES_NEXT_VISIT,
    NEXT_VISIT_DATE,
    LLITN_GIVEN_NEXT_DATE,
    IPTP_RESULT_NO,
    REPEAT_SEROLOGY_RESULTS_NO,
    NON_REACTIVE_SEROLOGY_APPOINTMENT
}
data class EncounterItem(
    val id: String,
    val code: String,
    val effective: String,
    val value: String
) {
    override fun toString(): String = code
}
data class DbEncounter(
    val reference: Reference,
    val description: String,
    val encounterId: String
)
data class ObservationItem(
    val id: String,
    val code: String?,
    val text: String,
    val value: String,
    val encounter: String
)
data class DbAppointments(
    val id: String,
    val title: String,
    val date: String,

)
data class DbLogin(
    val idNumber: String,
    val password: String
)
data class DbOtp(
    val idNumber: String,
    val password: String,
    val otp: String,
)
data class DbRegisterData(
    val idNumber: String,
    val phone: String,
)
data class AuthResponse(
    val message :String?,
    val status: String,
    val otp: String? = null
)
data class DbUserData(
    val data: DbData,
    val status: String
)
data class DbData(
    val idNumber: String,
    val names: String,
    val fhirPatientId: String,
)
data class DbAuthResponse(
    @SerializedName("status") val status: String,
    @SerializedName("token") val token: String,
    @SerializedName("issued") val issued: String,
    @SerializedName("expires") val expires: String,
    @SerializedName("newUser") val newUser: Boolean
)
