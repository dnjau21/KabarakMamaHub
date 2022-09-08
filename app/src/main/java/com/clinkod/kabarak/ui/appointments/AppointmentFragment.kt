package com.clinkod.kabarak.ui.appointments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.clinkod.kabarak.AppointmentAdapter
import com.clinkod.kabarak.MamasHubApplication
import com.clinkod.kabarak.R
import com.clinkod.kabarak.fhir.helper.DbAppointments
import com.clinkod.kabarak.fhir.helper.DbResourceViews
import com.clinkod.kabarak.fhir.helper.FormatterClass
import com.clinkod.kabarak.fhir.viewmodel.MainActivityViewModel
import com.clinkod.kabarak.fhir.viewmodel.PatientDetailsViewModel
import com.google.android.fhir.FhirEngine
import kotlinx.android.synthetic.main.fragment_appointment.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppointmentFragment : Fragment() {

    private lateinit var rootView: View

    private lateinit var patientDetailsViewModel: PatientDetailsViewModel
    private lateinit var patientId: String
    private lateinit var fhirEngine: FhirEngine
    private val mainViewModel: MainActivityViewModel by viewModels()
    private val formatterClass = FormatterClass()

    private lateinit var layoutManager: RecyclerView.LayoutManager

    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        rootView = inflater.inflate(R.layout.fragment_appointment, container, false)

        mainViewModel.poll()

        patientId = formatterClass.retrieveSharedPreference(requireContext(), "patientId").toString()
        fhirEngine = MamasHubApplication.fhirEngine(requireContext())

        patientDetailsViewModel = ViewModelProvider(this,
            PatientDetailsViewModel.
            PatientDetailsViewModelFactory(requireActivity().application,fhirEngine, patientId)
        )[PatientDetailsViewModel::class.java]

        recyclerView = rootView.findViewById(R.id.recyclerView)

        layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)

        return rootView
    }

    override fun onStart() {
        super.onStart()

        getSavedData()
    }

    private fun getSavedData() {


        CoroutineScope(Dispatchers.IO).launch {

            val appointmentList = ArrayList<DbAppointments>()

            val dateList = ArrayList<String>()
            dateList.addAll(listOf(
                formatterClass.getCodes(DbResourceViews.EDD.name),
                formatterClass.getCodes(DbResourceViews.IPT_VISIT.name),
                formatterClass.getCodes(DbResourceViews.HIV_NR_DATE.name),
                formatterClass.getCodes(DbResourceViews.REFERRAL_PARTNER_HIV_DATE.name),
                formatterClass.getCodes(DbResourceViews.CLINICAL_NOTES_NEXT_VISIT.name),
                formatterClass.getCodes(DbResourceViews.NEXT_VISIT_DATE.name),
                formatterClass.getCodes(DbResourceViews.LLITN_GIVEN_NEXT_DATE.name),
                formatterClass.getCodes(DbResourceViews.IPTP_RESULT_NO.name),
                formatterClass.getCodes(DbResourceViews.REPEAT_SEROLOGY_RESULTS_NO.name),
                formatterClass.getCodes(DbResourceViews.NON_REACTIVE_SEROLOGY_APPOINTMENT.name),
            ))
            dateList.forEach {

                val appointmentObservationList = patientDetailsViewModel.getObservationFromCode(it)
                appointmentObservationList.forEach { observation ->

                    val value = observation.value.trim()
                    if (formatterClass.isDateInFuture(value)){

                        val appointmentDate = FormatterClass().convertDate(value)

                        var text = ""
                        val id = observation.id
                        val code = observation.code

                        text = when (code) {
                            formatterClass.getCodes(DbResourceViews.EDD.name) -> { "Expected Delivery Date" }
                            formatterClass.getCodes(DbResourceViews.IPT_VISIT.name) -> { "IPT Visit" }
                            formatterClass.getCodes(DbResourceViews.HIV_NR_DATE.name) -> { "HIV NR Date" }
                            formatterClass.getCodes(DbResourceViews.REFERRAL_PARTNER_HIV_DATE.name) -> { "Referral Partner HIV Date" }
                            formatterClass.getCodes(DbResourceViews.CLINICAL_NOTES_NEXT_VISIT.name) -> { "Clinical Notes Next Visit" }
                            formatterClass.getCodes(DbResourceViews.NEXT_VISIT_DATE.name) -> { "Next Visit Date" }
                            formatterClass.getCodes(DbResourceViews.LLITN_GIVEN_NEXT_DATE.name) -> { "LLITN Given Next Date" }
                            formatterClass.getCodes(DbResourceViews.IPTP_RESULT_NO.name) -> { "IPTP Result No" }
                            formatterClass.getCodes(DbResourceViews.REPEAT_SEROLOGY_RESULTS_NO.name) -> { "Repeat Serology Results No" }
                            formatterClass.getCodes(DbResourceViews.NON_REACTIVE_SEROLOGY_APPOINTMENT.name) -> { "Non Reactive Serology Appointment" }
                            else -> { "No Appointment" }
                        }

                        val dbAppointments = DbAppointments(id, text, appointmentDate)
                        appointmentList.add(dbAppointments)

                    }

                }
            }

            val appointment = "${appointmentList.size} upcoming appointments"
            tvAppointment.text = appointment

            appointmentList.sortBy { it.date }

            CoroutineScope(Dispatchers.Main).launch {

                if (appointmentList.isNotEmpty()){
                    no_record.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }else{
                    no_record.visibility = View.VISIBLE
                    recyclerView.visibility = View.GONE
                }

                val configurationListingAdapter = AppointmentAdapter(
                    appointmentList,requireContext())
                recyclerView.adapter = configurationListingAdapter

            }


        }



    }

}