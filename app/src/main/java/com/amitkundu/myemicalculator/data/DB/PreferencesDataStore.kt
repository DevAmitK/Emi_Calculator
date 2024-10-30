
import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject



/**
 * This class manages saving and retrieving preferences from the Preferences DataStore.
 * It handles key-value pairs for Boolean login states.
 */
class PreferencesDataStore @Inject constructor(@ApplicationContext private val context: Context) {

    val Context.dataStore by preferencesDataStore(name = "LoginData")

    companion object {
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in") // Key for login state.
    }

    /**
     * Saves the login state (true or false) into Preferences DataStore.
     * @param isLoggedIn Boolean representing if the user is logged in.
     */
    suspend fun saveLoginState(isLoggedIn: Boolean ) {
        context.dataStore.edit { preferences ->
           preferences[IS_LOGGED_IN_KEY] = isLoggedIn
        }
    }
    /**
     * Retrieves the current login state from Preferences DataStore.
     * @return Flow<Boolean> that emits the login state.
     */
    fun getLoginState(): Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN_KEY] ?: false // Default to false if the key doesn't exist.
    }
}
