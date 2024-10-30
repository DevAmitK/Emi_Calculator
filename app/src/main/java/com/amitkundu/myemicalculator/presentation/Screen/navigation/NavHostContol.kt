import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.getlendingbuddha.emicalculator.presentation.Screen.emi_Screen.EMICalculatorScreen
import com.getlendingbuddha.emicalculator.presentation.Screen.emi_Screen.MonthlyEmiDetailScreen
import com.getlendingbuddha.emicalculator.presentation.Screen.viewmodel.EMICalculatorViewModel


@Composable
fun NavHostCont(navHostController: NavHostController, viewmodel: EMICalculatorViewModel) {

    NavHost(navController = navHostController, startDestination = Routs.EmiRout) {

        composable<Routs.MonthlyEmiDetailRout> {

            MonthlyEmiDetailScreen(viewmodel)
        }
        composable<Routs.EmiRout> {
            EMICalculatorScreen(navHostController = navHostController, viewModel = viewmodel)
        }

    }

}