
import android.content.Context
import com.getlendingbuddha.emicalculator.data.DB.PreferencesDataStore
import com.getlendingbuddha.emicalculator.data.Repo.Repo
import com.getlendingbuddha.emicalculator.data.check_network.ConnectivityObserver
import com.getlendingbuddha.emicalculator.data.check_network.NetworkConnectivityObserver
import com.getlendingbuddha.emicalculator.data.remoteRepo.RemoteRepo
import com.getlendingbuddha.emicalculator.data.remoteRepo.RemoteRepoImpl
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module that provides dependencies for repository and other layers.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideRepo(
        dataStore: PreferencesDataStore,
    ): Repo {
        return Repo(dataStore)

    }

    @Singleton
    @Provides
    fun provideRemoteRepo (firestore: FirebaseFirestore): RemoteRepo {
        return RemoteRepoImpl(firestore)
    }

    @Provides
    @Singleton
    fun provideConnectivityObserver(@ApplicationContext context: Context): ConnectivityObserver {
        return NetworkConnectivityObserver(context)
    }

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }


}