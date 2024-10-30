import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.getlendingbuddha.emicalculator.ui.theme.tableColour
import com.getlendingbuddha.emicalculator.ui.theme.tableHeadColour


@Composable
fun EMIDetailsTable(
    loanAmount: Double,
    interestRate: Double,
    periodInMonths: Int,
    monthlyEMI: Double,
    totalInterest: Double,
    processingFee: Double,
    totalPayment: Double
) {
    Column(modifier = Modifier.padding(16.dp).border(1.dp,Color.Black)) {
        // Table Headers
        Row(
            modifier = Modifier

                .fillMaxWidth()
                .background(tableHeadColour)
                .padding(5.dp),

            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TableCell(text = "Detail")
            TableCell(text = "Value")
        }

        Divider()

        // Table Data
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(tableColour).padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TableCell(text = "Loan Amount")
            TableCell(text = "₹${String.format("%.2f", loanAmount)}")
        }
        Box(modifier = Modifier.fillMaxWidth().background(Color.Black).height(1.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(tableColour).padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TableCell(text = "Interest Rate")
            TableCell(text = "${String.format("%.2f", interestRate)} %")
        }
        Box(modifier = Modifier.fillMaxWidth().background(Color.Black).height(1.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(tableColour)
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TableCell(text = "Period (Months)")
            TableCell(text = "$periodInMonths")
        }
        Box(modifier = Modifier.fillMaxWidth().background(Color.Black).height(1.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(tableColour)
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TableCell(text = "Monthly EMI")
            TableCell(text = "₹${String.format("%.2f", monthlyEMI)}")
        }
        Box(modifier = Modifier.fillMaxWidth().background(Color.Black).height(1.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(tableColour)
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TableCell(text = "Total Interest")
            TableCell(text = "₹${String.format("%.2f", totalInterest)}")
        }
        Box(modifier = Modifier.fillMaxWidth().background(Color.Black).height(1.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(tableColour)
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TableCell(text = "Processing Fee")
            TableCell(text = "₹${String.format("%.2f", processingFee)}")
        }
        Box(modifier = Modifier.fillMaxWidth().background(Color.Black).height(1.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(tableColour)
                .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TableCell(text = "Total Payment")
            TableCell(text = "₹${String.format("%.2f", totalPayment)}")
        }
    }
}

@Composable
fun TableCell(text: String) {
    Text(text = text)
}