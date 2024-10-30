
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.getlendingbuddha.emicalculator.R
import com.getlendingbuddha.emicalculator.data.check_network.ConnectivityObserver
import com.getlendingbuddha.emicalculator.presentation.Screen.navigation.Routs
import com.getlendingbuddha.emicalculator.presentation.Screen.viewmodel.EMICalculatorViewModel
import com.getlendingbuddha.emicalculator.presentation.adsContainer.BannerAds
import com.getlendingbuddha.emicalculator.ui.theme.backgroundColour
import com.getlendingbuddha.emicalculator.ui.theme.buttonColour
import com.getlendingbuddha.emicalculator.ui.theme.customGreen
import com.google.android.gms.ads.MobileAds
import com.lendingbuddha.emicalculator.data.Utiles.UserData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@Composable
fun EMICalculatorScreen(
    viewModel: EMICalculatorViewModel,
    navHostController: NavHostController,
) {

    var loanAmount by viewModel.loanAmount
    var interestRate by viewModel.interestRate
    var tenure by viewModel.tenure
    var gstOnInterest by viewModel.gstOnInterest
    var processingFee by viewModel.processingFee
    var isYears by viewModel.isYears
    var ispercent by viewModel.isPercent
    var interestIsError by viewModel.interestIsError
    var tenureError by viewModel.tenureError
    var detailIsEnable by viewModel.detailIsEnable
    var loanAmountIsError by viewModel.loanAmountIsError


    val context = LocalContext.current


    val emiDetail by viewModel.emiDetail.collectAsState()
    val isSaveData by viewModel.isCollectUserData.collectAsState()
    val connectionStatus = viewModel.connectionStatus.collectAsState().value


    LaunchedEffect(connectionStatus) {
        if (connectionStatus == ConnectivityObserver.Status.Available) {
            MobileAds.initialize(context) {}

        }

    }

    if (!isSaveData) {
        UserInputPopup(viewModel)
    }

    Box (
     modifier = Modifier
         .fillMaxSize()
         .background(backgroundColour),

    ) {

        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text("Loan Calculator", fontSize = 24.sp)

            Spacer(modifier = Modifier.height(26.dp))


            // Get Loan Amount Text Box----------------------------------------


            CustomEditText(
                value = loanAmount,
                onValueChange = {
                    loanAmount = it
                    loanAmountIsError = if (it.text.isBlank()) true else false
                },
                label = "Enter Loan Amount",
                isError = loanAmountIsError,
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.rupee_24),
                        contentDescription = ""
                    )
                },

                )

            // Get Interest Rate Text Box----------------------------------------
            CustomEditText(value = interestRate,
                onValueChange = {
                    interestRate = it
                    interestIsError = if (it.text.isBlank()) true else false
                },
                label = "Interest Rate",
                isError = interestIsError,
                trailingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_percent_24),
                        contentDescription = ""
                    )

                })

            CustomEditText(
                value = tenure,
                onValueChange = {
                    tenure = it
                    tenureError = if (it.text.isBlank()) true else false
                },
                label = "Period",
                isError = tenureError,
                trailingIcon = {
                    Row {
                        Text(fontSize = TextUnit.Unspecified,
                            color = if (isYears) Color.Black else Color.LightGray,
                            fontWeight = if (isYears) FontWeight.Bold else FontWeight.SemiBold,

                            text = "Year",
                            modifier = Modifier.clickable { isYears = true }
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(color = if (!isYears) Color.Black else Color.LightGray,
                            fontWeight = if (!isYears) FontWeight.Bold else FontWeight.SemiBold,
                            text = "Month",
                            modifier = Modifier.clickable { isYears = false }
                        )
                        Spacer(modifier = Modifier.width(5.dp))

                    }
                },
            )
            /*
        com.getlendingbuddha.emicalculator.presentation.Screen.emi_Screen.CustomEditText(
            value = gstOnInterest, onValueChange = {
                gstOnInterest = it
            },
            label = "Gst In Interest"
        )
        */

            CustomEditText(
                value = processingFee,
                onValueChange = {
                    processingFee = it
                },
                label = "Processing Fee",
                trailingIcon = {
                    Row {
                        Icon(
                            modifier = Modifier.clickable {
                                ispercent = true
                            },
                            painter = painterResource(id = R.drawable.baseline_percent_24),
                            tint = if (ispercent) Color.Black else Color.LightGray,
                            contentDescription = ""
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Icon(
                            modifier = Modifier.clickable { ispercent = false },
                            tint = if (ispercent) Color.LightGray else Color.Black,
                            painter = painterResource(id = R.drawable.rupee_24),
                            contentDescription = ""
                        )

                        Spacer(modifier = Modifier.width(15.dp))

                    }
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Calculate EMI button

            Row(
                modifier = Modifier.fillMaxWidth(),

                horizontalArrangement = Arrangement.SpaceAround
            ) {

                ElevatedButton(
                    shape = RoundedCornerShape(7.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = buttonColour,
                        contentColor = Color.White
                    ),
                    onClick = {
                        val loan = loanAmount.text.replace(",", "").toDoubleOrNull() ?: 0.0
                        val interest = interestRate.text.replace(",", "").toDoubleOrNull() ?: 0.0
                        val tenurePeriod = tenure.text.replace(",", "").toDoubleOrNull() ?: 0.0
                        val gst = gstOnInterest.text.replace(",", "").toDoubleOrNull() ?: 0.0
                        val procFee = processingFee.text.replace(",", "").toDoubleOrNull() ?: 0.0

                        //  errors if text box is empty
                        loanAmountIsError = loanAmount.text.isBlank() || loan <= 0.0
                        interestIsError = interestRate.text.isBlank()
                        tenureError = tenure.text.isBlank() || tenurePeriod <= 0.0

                        //  calculate EMI if there are no errors
                        if (!loanAmountIsError && !interestIsError && !tenureError) {
                            viewModel.calculateEMI(
                                loan,
                                interest,
                                tenurePeriod,
                                isYears,
                                gst,
                                procFee,
                                ispercent
                            )
                        }
                    }
                ) {
                    Text("Calculate EMI")
                }


                ElevatedButton(
                    onClick = {
                        navHostController.navigate(Routs.MonthlyEmiDetailRout)
                    },
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = buttonColour,
                        contentColor = Color.White
                    ),
                    modifier = Modifier,
                    shape = RoundedCornerShape(7.dp),
                    enabled = detailIsEnable
                ) {
                    Text(text = "Detail")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = customGreen,
                ),
                shape = RoundedCornerShape(7.dp),
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("https://wa.me/$WHATSAPP_NUMBER")
                    try {
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        // Handle the case where WhatsApp is not installed
                        Toast.makeText(context, "WhatsApp is not installed on your device", Toast.LENGTH_SHORT).show()
                    }
                },
            ) {
                Text(text = "Connect Loan Adviser")
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Display results
            if (emiDetail.emiResult != 0.0) {
                EMIDetailsTable(
                    loanAmount = emiDetail.loanAmount,
                    interestRate = emiDetail.interestRate,
                    periodInMonths = emiDetail.periodInMonths.toInt(),
                    monthlyEMI = emiDetail.emiResult,
                    totalInterest = emiDetail.totalInterest,
                    processingFee = emiDetail.processingFee,
                    totalPayment = emiDetail.totalPayment
                )
                detailIsEnable = true
            }

            Spacer(modifier = Modifier.height(36.dp))


        }


        /*** adds Column in the bottom */

        Column (
            modifier = Modifier
                .align(Alignment.BottomCenter)
        ){
            BannerAds()
        }


    }
}


fun formatNumber(value: Double): String {
    val numberFormat = NumberFormat.getNumberInstance(Locale.US)
    return numberFormat.format(value)
}


@Composable
fun UserInputPopup(viewModel: EMICalculatorViewModel) {
    // Variables for user input

    var name by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var phError by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    val context = LocalContext.current

    var internetConnection by remember { mutableStateOf(false) }

    val res = viewModel.isSuccesfullSave.collectAsStateWithLifecycle()
    var showDialog by remember { mutableStateOf(res.value.data) }
    val connectionStatus = viewModel.connectionStatus.collectAsState().value

    when (connectionStatus) {
        ConnectivityObserver.Status.Available -> {
            internetConnection = true
        }

        ConnectivityObserver.Status.Losing -> {
            internetConnection = false
        }

        ConnectivityObserver.Status.Lost -> {
            internetConnection = false
        }

        ConnectivityObserver.Status.Unavailable -> {
            internetConnection = false
        }
    }


    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = MaterialTheme.shapes.medium,
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {


                    if (res.value.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    } else {


                        Text(
                            text = "Enter Details",
                            fontSize = 28.sp
                        )

                        /** Check Internet In on Or Off*/
                        if (!internetConnection) {
                            Text(
                                text = "Please Tern On Internet",
                                color = Color.Red,
                                fontSize = 28.sp
                            )
                        }
                        if (!res.value.error.isNullOrEmpty()) {
                            Text(
                                text = "${res.value.error}",
                                color = Color.Red,
                                fontSize = 28.sp
                            )
                        }

                        // TextField for Name
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(text = "Name") },
                            modifier = Modifier.padding(top = 8.dp),
                            isError = nameError,
                            supportingText = {
                                if (nameError) Text(text = "Required!") else null
                            }
                        )

                        // TextField for Email
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(text = "Email") },
                            modifier = Modifier.padding(top = 8.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),

                            isError = emailError,
                            supportingText = {
                                if (emailError) Text(text = "Required!") else null
                            }
                        )

                        // TextField for Phone Number
                        OutlinedTextField(
                            value = phoneNumber,
                            onValueChange = { phoneNumber = it },
                            label = { Text(text = "Phone Number") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.padding(top = 8.dp),
                            isError = nameError,
                            supportingText = {
                                if (nameError) Text(text = "Required!") else null
                            }
                        )

                        // Row with Save and Cancel buttons
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.White,
                                    containerColor = Color.Green
                                ),
                                shape = RectangleShape,
                                /***
                                 * Save User data in firebase
                                 */
                                onClick = {
                                    nameError = name.isBlank()
                                    phError = phoneNumber.isBlank()
                                    emailError = email.isBlank()
                                    if (!nameError && !emailError && !phError) {

                                        if (internetConnection) {
                                            viewModel.saveUserDataInDB(
                                                UserData(
                                                    name = name,
                                                    email = email,
                                                    number = phoneNumber
                                                )
                                            )
                                        }
                                    }

                                }) {
                                Text(text = "Save")
                            }

                            Button(
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.White,
                                    containerColor = Color.Red
                                ),
                                shape = RectangleShape,
                                onClick = {
                                    // Close the dialog when cancel is clicked
                                    showDialog = false
                                }) {
                                Text(text = "Cancel")
                            }
                        }
                    }
                }
            }
        }
    }
}
