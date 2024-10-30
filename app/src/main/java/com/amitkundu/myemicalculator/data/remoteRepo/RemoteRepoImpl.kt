
import com.getlendingbuddha.emicalculator.data.common.ResultState
import com.google.firebase.firestore.FirebaseFirestore
import com.lendingbuddha.emicalculator.data.Utiles.UserData
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class RemoteRepoImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
) : RemoteRepo {


    override fun addUserDetails(
        userData: UserData,
    ): Flow<ResultState<Boolean>> = callbackFlow {
        trySend(ResultState.IsLoading)

        try {
            firestore.collection("USER_COLLECTION").document(userData.email).set(userData)
                .addOnSuccessListener {
                    trySend(ResultState.Success(data = true))
                }
                .addOnFailureListener {
                    trySend(ResultState.Error(error = it.localizedMessage))
                }
        }
        catch (e : Exception){
            trySend(ResultState.Error(error = e.localizedMessage))
        }
        awaitClose()
    }

}