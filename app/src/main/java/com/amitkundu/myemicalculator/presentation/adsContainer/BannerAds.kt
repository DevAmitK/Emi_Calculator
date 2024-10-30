
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun BannerAds(modifier: Modifier = Modifier) {
    AndroidView(modifier = modifier, factory = {
        AdView(it).apply {
            setAdSize(AdSize.BANNER)
            adUnitId = ADMOB_BANNER_AD_UNIT_ID
            loadAd(AdRequest.Builder().build())
        }
    })

}