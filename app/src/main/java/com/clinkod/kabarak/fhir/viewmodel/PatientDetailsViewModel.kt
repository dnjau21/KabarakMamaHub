package com.clinkod.kabarak.fhir.viewmodel

import android.app.Application
import android.content.res.Resources
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.clinkod.kabarak.R
import com.clinkod.kabarak.fhir.helper.DbResourceViews
import com.clinkod.kabarak.fhir.helper.EncounterItem
import com.clinkod.kabarak.fhir.helper.FormatterClass
import com.clinkod.kabarak.fhir.helper.ObservationItem
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.get
import com.google.android.fhir.logicalId
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.search
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.hl7.fhir.r4.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.EnumSet.of
import kotlin.collections.ArrayList

@RequiresApi(Build.VERSION_CODES.O)
class PatientDetailsViewModel(
    application: Application,
    private val fhirEngine: FhirEngine,
    private val patientId: String): AndroidViewModel(application) {

    init {
        getObservationFromEncounter(DbResourceViews.CLIENT_WEARABLE_RECORDING.name)
    }

    fun getObservationFromEncounter(encounterName: String) = runBlocking {
        observationFromEncounter(encounterName)
    }

    private suspend fun observationFromEncounter(encounterName: String) : List<EncounterItem>{

        val encounter = mutableListOf<EncounterItem>()

        /**
         * Filter encounters by Location which will be the KMFL Code from the logged in person
         */

        fhirEngine.search<Encounter>{
            filter(Encounter.REASON_CODE, {value = of(Coding().apply { code = encounterName })})
            filter(Encounter.SUBJECT, {value = "Patient/$patientId"})
            sort(Encounter.DATE, Order.ASCENDING)
        }.take(Int.MAX_VALUE)
            .map { createEncounterItem(it, getApplication<Application>().resources) }
            .let { encounter.addAll(it) }

        return encounter
    }

    //Get observations from code
    fun getObservationFromCode(code: String) = runBlocking {
        observationFromCode(code)
    }

    private suspend fun observationFromCode(codeValue: String): List<ObservationItem>{

        val observations = mutableListOf<ObservationItem>()
        fhirEngine
            .search<Observation> {
                filter(Observation.CODE, {value = of(Coding().apply {
                    system = "http://snomed.info/sct"; code = codeValue
                })})
                filter(Observation.SUBJECT, {value = "Patient/$patientId"})
            }
            .take(1)
            .map { createObservationItem(it, getApplication<Application>().resources) }
            .let { observations.addAll(it) }


        return observations

    }





    companion object{

        private fun createObservationItem(observation: Observation, resources: Resources): ObservationItem{

            val id = observation.logicalId
            val text = observation.code.text ?: observation.code.codingFirstRep.display
            val code = observation.code.coding[0].code
            val value =
                if (observation.hasValueQuantity()) {
                    observation.valueQuantity.value.toString()
                } else if (observation.hasValueCodeableConcept()) {
                    observation.valueCodeableConcept.coding.firstOrNull()?.display ?: ""
                }else if (observation.hasValueStringType()) {
                    observation.valueStringType.asStringValue().toString() ?: ""
                }else {
                    ""
                }
            val valueUnit =
                if (observation.hasValueQuantity()) {
                    observation.valueQuantity.unit ?: observation.valueQuantity.code
                } else {
                    ""
                }
            val encounterId = if (observation.hasEncounter()){
                val encounterReference = observation.encounter
                if (encounterReference.hasReference()){
                    encounterReference.reference
                }else{
                    ""
                }
            }else{
                ""
            }
            val valueString = "$value $valueUnit"

            return ObservationItem(
                id,
                code,
                text,
                valueString,
                encounterId
            )
        }

        private fun createEncounterItem(encounter: Encounter, resources: Resources): EncounterItem{

            val encounterDate =
                if (encounter.hasPeriod()) {
                    if (encounter.period.hasStart()) {
                        encounter.period.start
                    } else {
                        ""
                    }
                } else {
                    ""
                }

            var lastUpdatedValue = ""



            if (encounter.hasMeta()){
                if (encounter.meta.hasLastUpdated()){
                    lastUpdatedValue = encounter.meta.lastUpdated.toString()
                }
            }

            val reasonCode = encounter.reasonCode.firstOrNull()?.text ?: ""

            var textValue = ""

            if(encounter.reasonCode.size > 0){

                val text = encounter.reasonCode[0].text
                val textString = encounter.reasonCode[0].text?.toString() ?: ""
                val textStringValue = encounter.reasonCode[0].coding[0].code ?: ""

                textValue = if (textString != "") {
                    textString
                }else if (textStringValue != ""){
                    textStringValue
                }else text ?: ""

            }

            val encounterDateStr = if (encounterDate != "") {
                encounterDate.toString()
            } else {
                lastUpdatedValue
            }

            return EncounterItem(
                encounter.logicalId,
                textValue,
                encounterDateStr,
                reasonCode
            )
        }
    }


    class PatientDetailsViewModelFactory(
        private val application: Application,
        private val fhirEngine: FhirEngine,
        private val patientId: String
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PatientDetailsViewModel(application, fhirEngine, patientId) as T
        }
    }




}

