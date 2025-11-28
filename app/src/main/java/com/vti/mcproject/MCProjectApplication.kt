package com.vti.mcproject

import android.app.Application
import com.vti.mcproject.data.local.dao.AppContainer
import com.vti.mcproject.data.local.dao.AppDataContainer

class MCProjectApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(this)
    }
}
