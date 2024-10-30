
import com.getlendingbuddha.emicalculator.data.DB.PreferencesDataStore
import kotlinx.coroutines.flow.Flow

class Repo(
    private val dataStore: PreferencesDataStore,
) {
    suspend fun saveUserDetailSave() {
        dataStore.saveLoginState(isLoggedIn = true)

    }
    suspend fun isUserDetailSave(): Flow<Boolean> {
        return dataStore.getLoginState()
    }
}