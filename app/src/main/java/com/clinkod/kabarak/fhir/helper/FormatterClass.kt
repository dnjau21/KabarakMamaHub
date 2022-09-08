package com.clinkod.kabarak.fhir.helper

import android.content.Context
import android.os.Bundle
import com.clinkod.kabarak.R
import com.clinkod.kabarak.ui.measure.FragmentMeasure
import java.text.SimpleDateFormat
import java.util.*

class FormatterClass {

    fun saveSharedPreference(
        context: Context,
        sharedKey: String,
        sharedValue: String){

        val appName = context.getString(R.string.app_storage)
        val sharedPreferences = context.getSharedPreferences(appName, Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString(sharedKey, sharedValue)
        editor.apply()
    }

    fun retrieveSharedPreference(
        context: Context,
        sharedKey: String): String? {

        val appName = context.getString(R.string.app_storage)

        val sharedPreferences = context.getSharedPreferences(appName, Context.MODE_PRIVATE)
        return sharedPreferences.getString(sharedKey, null)

    }

    fun generateUuid(): String {
        return UUID.randomUUID().toString()
    }

    fun getCodes(value:String): String{
        return when(value){
            DbResourceViews.SYSTOLIC_BP.name -> { "271649006" }
            DbResourceViews.DIASTOLIC_BP.name -> { "271650006" }
            DbResourceViews.PULSE_RATE.name -> { "78564009" }
            DbResourceViews.TEMPARATURE.name -> { "703421000" }

            DbResourceViews.EDD.name -> { "161714006" }
            DbResourceViews.IPT_VISIT.name -> { "423337059" }
            DbResourceViews.HIV_NR_DATE.name -> { "31676001-NR" }
            DbResourceViews.REFERRAL_PARTNER_HIV_DATE.name -> { "31676001-RRD" }
            DbResourceViews.CLINICAL_NOTES_NEXT_VISIT.name -> { "390840007" }
            DbResourceViews.NEXT_VISIT_DATE.name -> { "390840006" }
            DbResourceViews.LLITN_GIVEN_NEXT_DATE.name -> { "784030374-N" }
            DbResourceViews.IPTP_RESULT_NO.name -> { "388435640-N" }
            DbResourceViews.REPEAT_SEROLOGY_RESULTS_NO.name -> { "412690006-N" }
            DbResourceViews.NON_REACTIVE_SEROLOGY_APPOINTMENT.name -> { "412690006-A" }

            else -> { "" }
        }
    }

    fun startFragmentConfirm(context: Context, encounterName: String): FragmentMeasure {

        val frag = FragmentMeasure()
        val bundle = Bundle()
        bundle.putString(FragmentMeasure.QUESTIONNAIRE_FILE_PATH_KEY, "client.json")
        frag.arguments = bundle
        return frag

    }

    //Check if string is a date in the future
    fun isDateInFuture(date: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd",
            Locale.getDefault())
        val currentDate = sdf.format(Date())
        return currentDate < date
    }

    //Check if string is date in this format yyyy-MM-dd
    fun isDateFormat(date: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd",
            Locale.getDefault())
        return try {
            sdf.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    //Check if string is date in this format dd-MMM-yyyy
    fun isDateFormat2(date: String): Boolean {
        val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
        return try {
            sdf.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    //Convert date to new format
    fun convertDate(dateValue: String): String {

        val ddMMyyyy = isDateFormat2(dateValue)
        return if (ddMMyyyy){
            //Convert to yyyy-MM-dd from dd-MMM-yyyy
            val sdf = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())
            val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = sdf.parse(dateValue)

            sdf2.format(date)
        }else{
            dateValue
        }

    }

    fun getDayOfWeek(dateValue: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateValue)
        val day = SimpleDateFormat("EEEE", Locale.getDefault()).format(date)
        return day.substring(0, 3)
    }

    //Get Number of days from today date
    fun getDaysFromToday(dateValue: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = sdf.parse(dateValue)
        val today = Date()
        val diff = date.time - today.time
        val days = (diff / (1000 * 60 * 60 * 24)).toInt()
        if (days > 1){

            return if (days == 2){
                "Tomorrow"
            }else if (days == 3) {
                "In 2 \ndays"
            }else if (days in 8..29){
                "In ${days / 7} \nweeks"
            }else if (days > 30) {
                "In ${days / 30} \nmonths"
            }else{
                "In $days \ndays"
            }

        }else{
            return "Today"
        }

    }


}