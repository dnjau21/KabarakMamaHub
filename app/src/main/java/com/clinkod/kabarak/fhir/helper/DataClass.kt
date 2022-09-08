package com.clinkod.kabarak.fhir.helper

import org.hl7.fhir.r4.model.Encounter
import org.hl7.fhir.r4.model.Reference

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
