import kotlinx.serialization.Serializable


sealed class Routs {
    @Serializable
    object EmiRout


    @Serializable
    object MonthlyEmiDetailRout

}