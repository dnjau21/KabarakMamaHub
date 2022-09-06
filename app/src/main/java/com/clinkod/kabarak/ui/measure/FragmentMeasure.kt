package com.clinkod.kabarak.ui.measure

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.clinkod.kabarak.R
import com.clinkod.kabarak.fhir.helper.FormatterClass
import com.clinkod.kabarak.models.PropertyUtils
import com.clinkod.kabarak.services.DataReadService
import com.clinkod.kabarak.ui.devicescan.DeviceScanActivity

class FragmentMeasure : Fragment(){

    private lateinit var rootView: View

    private lateinit var btnRead : Button
    private lateinit var status : TextView
    private lateinit var hrResults : TextView
    private lateinit var sysResults : TextView
    private lateinit var diaResults : TextView
    private lateinit var statuslabel : LinearLayout

    private var reading = false
    private var mDeviceAddress: String? = null
    private lateinit var dataReadService: DataReadService

    private val formatterClass = FormatterClass()

    private var latestData = IntArray(3)
    private lateinit var mDeviceName: String
    private lateinit var handler: Handler
    private lateinit var controller: NavController

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        rootView = inflater.inflate(R.layout.fragment_measure, container, false)

        Toast.makeText(context, "FragmentMeasure", Toast.LENGTH_SHORT).show()

        //final TextView textView = root.findViewById(R.id.text_dashboard);
        handler = Handler()
        controller = NavHostFragment.findNavController(this)

        btnRead = rootView.findViewById(R.id.startReading)
//        result = root.findViewById(R.id.result);
        //        result = root.findViewById(R.id.result);
        status = rootView.findViewById(R.id.status)
        sysResults = rootView.findViewById(R.id.sysResults)
        diaResults = rootView.findViewById(R.id.diaResults)
        hrResults = rootView.findViewById(R.id.hrResults)
        statuslabel = rootView.findViewById(R.id.linearStatus)

        //status.setText("Initializing...");

//        heartBeatView = (HeartBeatView) root.findViewById(R.id.heartBeat);
//        heartBeatView.setDurationBasedOnBPM(50);
        btnRead.setOnClickListener { v: View? ->
            if (dataReadService != null && !reading) {
                dataReadService.sendReadCommands()
                //dataReadService.sendGetTempCommands();
            } else if (dataReadService != null) {
                dataReadService.stopReading()
            }
        }

        mDeviceName = PropertyUtils.getDeviceName()
        mDeviceAddress = PropertyUtils.getDeviceAddress()

        if (mDeviceAddress == null) {
            startActivity(Intent(activity, DeviceScanActivity::class.java))
            //connect(mDeviceAddress);
        }

        activity!!.registerReceiver(
            bleServiceReceiver,
            makeGattUpdateIntentFilter()
        )


        /*if(!Utils.isServiceRunning(getActivity(), DataReadService.class))
            DataReadService.startActionDataReadService(getActivity());*/

        if (DataReadService.getInstance() == null) {
            DataReadService.startActionDataReadService(activity)
            status.text = "Initializing..."
        } else {
            dataReadService = DataReadService.getInstance()
            if (dataReadService.state == DataReadService.STATE_DISCONNECTED) {
                DataReadService.startActionDataReadService(activity)
                status.text = "Initializing..."
            }
            updateUiState()
        }

        return rootView
    }

    private val bleServiceReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            dataReadService = DataReadService.getInstance()
            if (DataReadService.ACTION_STATE_CHANGED == action) {
                updateUiState()
            } else if (DataReadService.ACTION_GENERAL_FAILURE == action) {
                val message = intent.getStringExtra(DataReadService.EXTRA_DATA)

                // Log.d(TAG, "Failure:" + message);
            } else if (DataReadService.ACTION_DATA_AVAILABLE == action) {
                val reading = dataReadService.data
                setCurrentReading(reading[0], reading[1], reading[2])
            }
        }
    }

    private fun updateUiState() {
        var state = DataReadService.STATE_DISCONNECTED
        if (dataReadService != null) {
            state = dataReadService.state
        }
        when (state) {
            DataReadService.STATE_CONNECTING -> {
                status.text = "Connecting..."
                btnRead.isEnabled = false
                btnRead.text = "Start"
                statuslabel.setBackgroundResource(R.drawable.status_connecting)
                status.setBackgroundResource(R.drawable.status_connecting)
                reading = false
            }
            DataReadService.STATE_CONNECTED -> {
                status.text = "Initializing device..."
                btnRead.isEnabled = true
                statuslabel.setBackgroundResource(R.drawable.status_connecting)
                status.setBackgroundResource(R.drawable.status_connecting)
                reading = false
            }
            DataReadService.STATE_CONNECTION_FAILED -> {
                status.text = "Connection Failed"
                btnRead.isEnabled = false
                btnRead.text = "Start"
                statuslabel.setBackgroundResource(R.drawable.status_failed)
                status.setBackgroundResource(R.drawable.status_failed)
                reading = false
            }
            DataReadService.STATE_DISCONNECTED -> {
                status.text = "Disconnected"
                btnRead.isEnabled = false
                btnRead.text = "Start"
                statuslabel.setBackgroundResource(R.drawable.status_failed)
                status.setBackgroundResource(R.drawable.status_failed)
                status.setTextColor(Color.WHITE)
                reading = false
            }
            DataReadService.STATE_READY -> {
                status.text = "Ready"
                btnRead.isEnabled = true
                btnRead.text = "Start"
                statuslabel.setBackgroundResource(R.drawable.ic_statusframe)
                status.setBackgroundResource(R.drawable.ic_statusframe)
                status.setTextColor(Color.BLACK)
                reading = false
            }
            DataReadService.STATE_STARTING_MEASUREMENT -> {
                status.text = "Starting measurement..."
                btnRead.isEnabled = true
                btnRead.text = "Stop"
                statuslabel.setBackgroundResource(R.drawable.status_measuring)
                status.setBackgroundResource(R.drawable.status_measuring)
                status.setTextColor(Color.WHITE)
                reading = true
            }
            DataReadService.STATE_READING_DATA -> {
                status.text = "Measuring..."
                //getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//                TODO - Animate heart reading state
//                if(!heartBeatView.isHeartBeating()){
//                    heartBeatView.start();
//                }
                statuslabel.setBackgroundResource(R.drawable.status_measuring)
                status.setBackgroundResource(R.drawable.status_measuring)
                btnRead.isEnabled = true
                btnRead.text = "Stop"
                reading = true
            }
            DataReadService.STATE_DONE -> {
                status.text = "Done"
                btnRead.isEnabled = true
                btnRead.text = "Start"
                reading = false
                statuslabel.setBackgroundResource(R.drawable.status_ready)
                status.setBackgroundResource(R.drawable.status_ready)
            }
            DataReadService.STATE_NOT_SUPPORTED -> {
                status.text = "Device not supported"
                btnRead.isEnabled = false
                btnRead.text = "Start"
                reading = false
            }
            else -> {
                status.text = "An error occurred!"
                //                if(heartBeatView.isHeartBeating()){
//                    heartBeatView.stop();
//                }
                btnRead.isEnabled = true
                btnRead.text = "Start"
                reading = false
            }
        }
    }

    fun makeGattUpdateIntentFilter(): IntentFilter {
        val intentFilter = IntentFilter()
        intentFilter.addAction(DataReadService.ACTION_GENERAL_FAILURE)
        intentFilter.addAction(DataReadService.ACTION_STATE_CHANGED)
        intentFilter.addAction(DataReadService.ACTION_DATA_AVAILABLE)
        return intentFilter
    }

    private fun setCurrentReading(systole: Int, diastole: Int, heartrate: Int) {
        latestData = intArrayOf(systole, diastole, heartrate)

//        result.setText(String.format("Reading: %d/%d %d", systole, diastole, heartrate) );
        sysResults.text = systole.toString()
        diaResults.text = diastole.toString()
        //        bpResults.setText(String.format("Blood Pressure: %d/ %d", systole, diastole) );
        hrResults.text = heartrate.toString()
        /**
         * TODO - Send these data to the fhir server
         * Get the patient id from the shared preferences
         */
        val patientId = formatterClass.retrieveSharedPreference(requireContext(), "patientId")
        if (patientId != null) {

        }
    }


    override fun onDetach() {
        super.onDetach()
        requireActivity().unregisterReceiver(bleServiceReceiver)
        // Log.i(TAG, "onDetach()");
    }


    companion object {
        const val QUESTIONNAIRE_FILE_PATH_KEY = "questionnaire-file-path-key"
        const val QUESTIONNAIRE_FRAGMENT_TAG = "questionnaire-fragment-tag"
    }
}