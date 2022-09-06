package com.clinkod.kabarak.fhir.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.clinkod.kabarak.MamasHubApplication
import com.clinkod.kabarak.fhir.helper.CodingObservation
import com.clinkod.kabarak.fhir.helper.FormatterClass
import com.clinkod.kabarak.fhir.helper.QuantityObservation
import com.clinkod.kabarak.fhir.helper.QuestionnaireHelper
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.datacapture.mapping.ResourceMapper
import com.google.android.fhir.datacapture.validation.QuestionnaireResponseValidator
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.*
import java.util.*
import kotlin.collections.ArrayList

class AddPatientDetailsViewModel(application: Application, private val state: SavedStateHandle) :AndroidViewModel(application){

    val questionnaire : String
        get() = getQuestionnaireJson()
    val isPatientSaved = MutableLiveData<Boolean>()

    private val questionnaireResource : Questionnaire
        get() =
            FhirContext.forCached(FhirVersionEnum.R4).newJsonParser().parseResource(questionnaire) as
                    Questionnaire
    private var fhirEngine: FhirEngine = MamasHubApplication.fhirEngine(application.applicationContext)
    private var questionnaireJson : String? = null

    private fun getQuestionnaireJson():String{
        questionnaireJson?.let { return it!! }

//        questionnaireJson = readFileFromAssets(state[MeasureFragment.QUESTIONNAIRE_FILE_PATH_KEY]!!)
        return questionnaire!!
    }

    private fun readFileFromAssets(fileName : String): String{
        return getApplication<Application>().assets.open(fileName).bufferedReader().use {
            it.readText()
        }

    }


    fun createEncounter(
        patientReference: Reference,
        encounterId: String,
        questionnaireResponse: QuestionnaireResponse,
        dataCodeList: ArrayList<CodingObservation>,
        dataQuantityList: ArrayList<QuantityObservation>,
        encounterReason: String
    ) {

        viewModelScope.launch {

            val bundle = ResourceMapper.extract(questionnaireResource, questionnaireResponse)

            val questionnaireHelper = QuestionnaireHelper()

            dataCodeList.forEach {
                bundle.addEntry()
                    .setResource(
                        questionnaireHelper.codingQuestionnaire(
                            it.code,
                            it.display,
                            it.value
                        )
                    )
                    .request.url = "Observation"
            }

            dataQuantityList.forEach {
                bundle.addEntry()
                    .setResource(
                        questionnaireHelper.quantityQuestionnaire(
                            it.code,
                            it.display,
                            it.display,
                            it.value,
                            it.unit,
                        )
                    )
                    .request.url = "Observation"
            }


            createCarePlan(patientReference, encounterId, encounterReason)
            saveResources(bundle, patientReference, encounterId, encounterReason)

        }

    }

    //Create CarePlan
    private suspend fun createCarePlan(
        patientReference: Reference,
        encounterId: String,
        encounterReason: String
    ) {

        val encounterReference = Reference("Encounter/$encounterId")

        val carePlan = CarePlan()
        carePlan.id = FormatterClass().generateUuid()
        carePlan.subject = patientReference
        carePlan.status = CarePlan.CarePlanStatus.ACTIVE
        carePlan.intent = CarePlan.CarePlanIntent.PLAN
        carePlan.encounter = encounterReference
        carePlan.title = encounterReason

        fhirEngine.create(carePlan)

    }

    private suspend fun saveResources(
        bundle: Bundle,
        subjectReference: Reference,
        encounterId: String,
        encounterReason: String,
    ) {

        val encounterReference = Reference("Encounter/$encounterId")

        bundle.entry.forEach {

            when (val resource = it.resource) {
                is Observation -> {
                    if (resource.hasCode()) {
                        resource.id = FormatterClass().generateUuid()
                        resource.subject = subjectReference
                        resource.encounter = encounterReference
                        resource.issued = Date()
                        saveResourceToDatabase(resource)
                    }

                }
                /**
                 * Add a location to the encounter; the location is the KMFL CODE
                 */
                is Encounter -> {
                    resource.subject = subjectReference
                    resource.id = encounterId
                    resource.reasonCodeFirstRep.text = encounterReason
                    resource.reasonCodeFirstRep.codingFirstRep.code = encounterReason
                    resource.status = Encounter.EncounterStatus.INPROGRESS
                    resource.period = Period().setStart(Date())
                    saveResourceToDatabase(resource)
                }



            }
        }
    }

    private suspend fun saveResourceToDatabase(resource: Resource) {
        fhirEngine.create(resource)
    }


}