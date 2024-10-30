
import androidx.compose.ui.text.input.TextFieldValue
import kotlinx.serialization.Serializable

@Serializable
data class MonthlyDetail(
    val month: Int,
    val principal: Double,
    val emi : Double,
    val interest: Double,
    val balance: Double,
) {
    constructor() : this(0, 0.0, 0.0, 0.0,0.0)
}

data class EmiDetail(
    var emiResult: Double,
    val processingFee: Double,
    var totalPayment: Double,
    var totalInterest: Double,
    var periodInMonths: Double,
    var loanAmount: Double,
    var interestRate: Double,
) {
    constructor() : this(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)

}

data class LoanData(
    var loanAmount: TextFieldValue,
    var interestRate: TextFieldValue,
    var tenure: TextFieldValue,
    var gstOnInterest: TextFieldValue,
    var processingFee: TextFieldValue,
    var isYears: Boolean,
    var isPercent: Boolean,
    var interestIsError: Boolean,
    var tenureError: Boolean,
    var detailIsEnable: Boolean,
    var loanAmountIsError: Boolean,
) {
    constructor() : this(
        TextFieldValue(""),
        TextFieldValue(""),
        TextFieldValue(""),
        TextFieldValue(""),
        TextFieldValue(""),
        true,
        true,
        false,
        false,
        false,
        false
    )
}

