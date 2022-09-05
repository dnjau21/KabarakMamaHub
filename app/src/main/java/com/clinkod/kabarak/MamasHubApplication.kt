package com.clinkod.kabarak

import android.app.Application
import android.content.Context
import com.clinkod.kabarak.beans.ObjectBox
import com.clinkod.kabarak.fhir.data.FhirPeriodicSyncWorker
import com.google.android.fhir.*
import com.google.android.fhir.sync.Sync
import com.yucheng.ycbtsdk.YCBTClient

class MamasHubApplication : Application(){

    private val fhirEngine : FhirEngine by lazy { constructFhirEngine() }

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG){

        }
        FhirEngineProvider.init(
            FhirEngineConfiguration(
                enableEncryptionIfSupported = true,
                DatabaseErrorStrategy.RECREATE_AT_OPEN,
                ServerConfiguration("https://devhmis.intellisoftkenya.com/fhir/")
            )
        )
        Sync.oneTimeSync<FhirPeriodicSyncWorker>(applicationContext)

        ObjectBox.init(applicationContext)
        YCBTClient.initClient(applicationContext, true)


    }

    private fun constructFhirEngine() : FhirEngine{
        return FhirEngineProvider.getInstance(applicationContext)
    }

    companion object{
        fun fhirEngine(context: Context) = (context.applicationContext as MamasHubApplication).fhirEngine
    }

}