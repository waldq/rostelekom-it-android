package dev.waldq.rostelekom

import android.app.Application
import dev.waldq.rostelekom.di.AppContainer

class RostelekomApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppContainer.init(this)
    }
}