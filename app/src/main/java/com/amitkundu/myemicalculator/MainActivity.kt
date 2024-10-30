

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.getlendingbuddha.emicalculator.presentation.Screen.navigation.NavHostCont
import com.getlendingbuddha.emicalculator.presentation.Screen.viewmodel.EMICalculatorViewModel
import com.getlendingbuddha.emicalculator.ui.theme.backgroundColour
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //installSplashScreen()
        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {}
        }
        setContent {
            val navHostController = rememberNavController()
            val viewmodel by viewModels<EMICalculatorViewModel>()

            val view = LocalView.current
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(
                    window,
                    view
                ).isAppearanceLightStatusBars = true

                Box(
                    modifier = Modifier
                        .background(backgroundColour)
                        .padding(innerPadding)
                ) {
                    NavHostCont(navHostController = navHostController, viewmodel)
                }
            }
        }
    }
}


