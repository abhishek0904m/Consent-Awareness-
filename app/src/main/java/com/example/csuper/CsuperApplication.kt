package com.example.csuper

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * C-SUPER Application Class
 * Entry point for Hilt dependency injection
 * 
 * Privacy Statement:
 * C-SUPER is designed for research and transparency purposes only.
 * It does not record personal data or send information to any external server.
 * All captured data remains local and fully under user control.
 */
@HiltAndroidApp
class CsuperApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Application initialization
    }
}
