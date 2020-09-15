package com.mypet.www.ui.launcher


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mypet.www.R
import com.mypet.www.ui.main.MainActivity

class LauncherActivity: AppCompatActivity(){
    // https://firebase.google.com/docs/auth/android/password-auth
    private lateinit var firebaseAuth: FirebaseAuth
    private val navController by lazy{
        (supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
                as NavHostFragment).navController
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launcher);
        firebaseAuth = FirebaseAuth.getInstance()
        setupActionBarWithNavController(navController = navController)
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        updateUI(currentUser)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    private fun updateUI(user: FirebaseUser?){
        user?.let{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}