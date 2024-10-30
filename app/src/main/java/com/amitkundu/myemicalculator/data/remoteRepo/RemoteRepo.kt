import com.getlendingbuddha.emicalculator.data.common.ResultState
import com.lendingbuddha.emicalculator.data.Utiles.UserData
import kotlinx.coroutines.flow.Flow


interface RemoteRepo {
    fun addUserDetails(userData: UserData): Flow<ResultState<Boolean>>
}