import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.getlendingbuddha.emicalculator.presentation.Screen.viewmodel.EMICalculatorViewModel
import com.getlendingbuddha.emicalculator.ui.theme.buttonColour
import com.getlendingbuddha.emicalculator.ui.theme.customGreen
import com.getlendingbuddha.emicalculator.ui.theme.tableColour
import com.getlendingbuddha.emicalculator.ui.theme.tableColour2
import com.getlendingbuddha.emicalculator.ui.theme.tableHeadColour
import com.lendingbuddha.emicalculator.Utiles.MonthlyDetail

import kotlinx.coroutines.launch
@Composable
fun MonthlyEmiDetailScreen(viewModel: EMICalculatorViewModel = hiltViewModel()) {
    val monthlyDetails = viewModel.monthlyDetails.collectAsStateWithLifecycle().value
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Launcher to request permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission granted, proceed to save the PDF
            coroutineScope.launch {
                createPDF(monthlyDetails, context)
            }
        } else {
            Toast.makeText(context, "Go to Settings and enable permission", Toast.LENGTH_SHORT).show()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.90f)
                .padding(10.dp)
        ) {
            HeaderRow()
            if (monthlyDetails.isNotEmpty()) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(
                        items = monthlyDetails,
                        key = { _, item -> item.month }
                    ) { index, detail ->
                        DetailRow(index, detail)
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }
        }

        // Align the buttons at the bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = buttonColour,
                ),
                shape = RoundedCornerShape(0),
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        // Directly save PDF using MediaStore for Android 10 and above
                        createPDF(monthlyDetails, context)
                    } else {
                        // Check for WRITE_EXTERNAL_STORAGE permission for Android 9 and below
                        when {
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) == PackageManager.PERMISSION_GRANTED -> {
                                // Permission already granted, save PDF
                                createPDF(monthlyDetails, context)
                            }
                            else -> {
                                // Request permission
                                permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            }
                        }
                    }
                },
            ) {
                Text(text = "Download PDF")
            }

            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp),
                colors = ButtonDefaults.buttonColors(
                    contentColor = Color.White,
                    containerColor = customGreen,
                ),
                shape = RoundedCornerShape(0),
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

        }
    }
}




@Composable
fun HeaderRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(tableHeadColour)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Month",
            modifier = Modifier.weight(0.7f),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "EMI",
            modifier = Modifier.weight(0.6f),
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Principal",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Interest",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Balance",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun DetailRow(index: Int, detail: MonthlyDetail) {
    val backgroundColor = if (index % 2 == 0) tableColour2 else tableColour

    // Using derivedStateOf to reduce recomposition
    val formattedEMI by remember { derivedStateOf { formatCurrency(detail.emi) } }
    val formattedPrincipal by remember { derivedStateOf { formatCurrency(detail.principal) } }
    val formattedInterest by remember { derivedStateOf { formatCurrency(detail.interest) } }
    val formattedBalance by remember { derivedStateOf { formatCurrency(detail.balance) } }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "${detail.month}",
            modifier = Modifier.weight(0.6f),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = formattedEMI, // Display formatted EMI
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = formattedPrincipal,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = formattedInterest,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = formattedBalance,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodySmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

// Function to format currency
fun formatCurrency(amount: Double): String {
    return "₹${String.format("%.2f", amount)}"
}

// Function to check permissions
private fun checkPermissions(context: Context, permissions: Array<String>): Boolean {
    return permissions.all { permission ->
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}
