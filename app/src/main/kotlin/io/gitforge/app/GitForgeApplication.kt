package io.gitforge.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.gitforge.app.work.WorkManagerHelper
import javax.inject.Inject

@HiltAndroidApp
class GitForgeApplication : Application() {

    @Inject
    lateinit var workManagerHelper: WorkManagerHelper

    override fun onCreate() {
        super.onCreate()
        workManagerHelper.schedulePeriodicRepoSync()
    }
}
