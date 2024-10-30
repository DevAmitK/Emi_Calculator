import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.getlendingbuddha.emicalculator.data.Repo.Repo
import com.getlendingbuddha.emicalculator.data.check_network.ConnectivityObserver
import com.getlendingbuddha.emicalculator.data.common.ResultState
import com.getlendingbuddha.emicalculator.data.remoteRepo.RemoteRepo
import com.lendingbuddha.emicalculator.Utiles.EmiDetail
import com.lendingbuddha.emicalculator.Utiles.MonthlyDetail
import com.lendingbuddha.emicalculator.data.Utiles.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.pow

@HiltViewModel
class EMICalculatorViewModel @Inject constructor(
    private val repo: Repo,
    private val remoteRepo: RemoteRepo,
    connectivityObserver: ConnectivityObserver
) : ViewModel() {
    private val _emiDetails = MutableStateFlow<EmiDetail>(EmiDetail())
    val emiDetail: StateFlow<EmiDetail> = _emiDetails

    private val _monthlyDetails = MutableStateFlow<List<MonthlyDetail>>(emptyList())
    val monthlyDetails: StateFlow<List<MonthlyDetail>> = _monthlyDetails

    // EMI HOME SCREEN VARIABLE -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    var loanAmount: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(""))
        private set

    var interestRate: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(""))
        private set

    var tenure: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(""))
        private set

    var gstOnInterest: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue(""))
        private set

    var processingFee: MutableState<TextFieldValue> = mutableStateOf(TextFieldValue("2"))
        private set

    var isYears: MutableState<Boolean> = mutableStateOf(true)
        private set

    var isPercent: MutableState<Boolean> = mutableStateOf(true)
        private set

    var interestIsError: MutableState<Boolean> = mutableStateOf(false)
        private set

    var tenureError: MutableState<Boolean> = mutableStateOf(false)
        private set

    var detailIsEnable: MutableState<Boolean> = mutableStateOf(false)
        private set

    var loanAmountIsError: MutableState<Boolean> = mutableStateOf(false)
        private set

    // Functions to update the variables
    fun setLoanAmount(value: TextFieldValue) {
        loanAmount.value = value
    }

    fun setInterestRate(value: TextFieldValue) {
        interestRate.value = value
    }

    fun setTenure(value: TextFieldValue) {
        tenure.value = value
    }

    fun setGstOnInterest(value: TextFieldValue) {
        gstOnInterest.value = value
    }

    fun setProcessingFee(value: TextFieldValue) {
        processingFee.value = value
    }

    fun setIsYears(value: Boolean) {
        isYears.value = value
    }

    fun setIsPercent(value: Boolean) {
        isPercent.value = value
    }

    fun setInterestIsError(value: Boolean) {
        interestIsError.value = value
    }

    fun setTenureError(value: Boolean) {
        tenureError.value = value
    }

    fun setDetailIsEnable(value: Boolean) {
        detailIsEnable.value = value
    }

    fun setLoanAmountIsError(value: Boolean) {
        loanAmountIsError.value = value
    }

//-=-=-=-=-=-=-=-=-=-==--=-=-=-=-=-=-=-=-=-=-=-=-=-

    init {
        collectUserdata()
    }

    private val _isCollectUserData = MutableStateFlow(true)
    val isCollectUserData: StateFlow<Boolean> = _isCollectUserData

    private fun collectUserdata() {
        viewModelScope.launch {
            repo.isUserDetailSave().collect { userData ->
                _isCollectUserData.value = userData
            }
        }
    }


    // Function to calculate EMI and update states
    fun calculateEMI(
        loanAmount: Double,
        interestRate: Double,
        tenure: Double,
        isYears: Boolean,
        gstOnInterest: Double,
        processingFee: Double,
        isProcessingFeePercentage: Boolean,
    ) {
        val months = if (isYears) tenure * 12 else tenure
        _emiDetails.value = _emiDetails.value.copy(periodInMonths = months)

        val monthlyInterestRate = interestRate / (12 * 100)

        // EMI formula without GST
        val emi = if (interestRate == 0.0) {
            loanAmount / months
        } else {
            (loanAmount * monthlyInterestRate * (1 + monthlyInterestRate).pow(months)) /
                    ((1 + monthlyInterestRate).pow(months) - 1)
        }

        // Calculate the processing fee
        val processingFeeAmount =
            if (isProcessingFeePercentage) loanAmount * (processingFee / 100) else processingFee

        // Calculate the interest for the total loan tenure
        val totalInterestWithoutGST = emi * months - loanAmount

        // Apply GST on the interest portion
        val totalInterestWithGST = totalInterestWithoutGST * (1 + gstOnInterest / 100)

        // Update emiDetails state
        _emiDetails.value = _emiDetails.value.copy(
            emiResult = emi,
            loanAmount = loanAmount,
            processingFee = processingFeeAmount,
            totalInterest = totalInterestWithGST,
            totalPayment = loanAmount + totalInterestWithGST + processingFeeAmount,
            interestRate = interestRate
        )

        // Calculate monthly breakdown
        calculateMonthlyDetails(loanAmount, monthlyInterestRate, months, emi, gstOnInterest)
    }

    private fun calculateMonthlyDetails(
        loanAmount: Double,
        monthlyInterestRate: Double,
        months: Double,
        emi: Double,
        gstOnInterest: Double,
    ) {
        var balance = loanAmount
        val details = mutableListOf<MonthlyDetail>()

        for (month in 1..months.toInt()) {
            // Calculate interest for the current month
            val interest = balance * monthlyInterestRate
            // Calculate GST on the interest
            val gstOnMonthlyInterest = interest * (gstOnInterest / 100)
            // Calculate the principal portion of the EMI
            val principal = emi - interest - gstOnMonthlyInterest

            // Update the remaining balance after subtracting the principal paid
            balance -= principal

            // Ensure balance doesn't go negative
            balance = balance.coerceAtLeast(0.0)

            // Add monthly details to the list
            details.add(
                MonthlyDetail(
                    month = month,
                    emi = emi,
                    principal = principal,
                    interest = interest,
                    balance = balance
                )
            )
        }

        // Set the calculated details to the MutableLiveData
        _monthlyDetails.value = details
    }

    /**
     * Check Internet Is On Or Off
     */
    val connectionStatus: StateFlow<ConnectivityObserver.Status> =
        connectivityObserver.observe()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ConnectivityObserver.Status.Unavailable)
    /***   Add User Data In FireBase Data Base And Update in  Preference DB    */


    /* this function in Local Db */
    suspend fun isSaveUserData() {
        repo.saveUserDetailSave()
    }

    private val _isSuccesfullSave = MutableStateFlow(State())
    val isSuccesfullSave = _isSuccesfullSave


    /* this function in firebase  and update local DB */
    fun saveUserDataInDB(userData: UserData) {
        viewModelScope.launch(Dispatchers.IO) {
            remoteRepo.addUserDetails(userData = userData).collect {
                when (it) {
                    is ResultState.Error -> {
                        _isSuccesfullSave.value = State(error = it.error, isLoading = false)
                    }

                    ResultState.IsLoading -> {
                        _isSuccesfullSave.value = State(isLoading = true)
                    }

                    is ResultState.Success -> {
                        _isSuccesfullSave.value = State(data = true, isLoading = false)
                        isSaveUserData()
                    }
                }
            }
        }
    }
}

data class State(
    val data: Boolean = true,
    val error: String = "",
    val isLoading: Boolean = false,
)

