
sealed class ResultState<out T>() {
    data class Success<out T>(val data : T) : ResultState<T>()
    data class Error <T>(val error : String) : ResultState<T>()
    object IsLoading : ResultState<Nothing>()

}