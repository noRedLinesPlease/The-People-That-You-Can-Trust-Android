package cornhole.beanbag.thepeopleyoucantrust.cookiesInterceptors

import android.content.Context
import androidx.preference.PreferenceManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


/**
 * This interceptor put all the Cookies in Preferences in the Request.
 * Your implementation on how to get the Preferences may ary, but this will work 99% of the time.
 */
class AddCookiesInterceptor( // We're storing our stuff in a database made just for cookies called PREF_COOKIES.
    // I reccomend you do this, and don't change this default value.
    private val context: Context
) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val preferences = PreferenceManager.getDefaultSharedPreferences(
            context
        ).getStringSet(PREF_COOKIES, HashSet()) as HashSet<String>?

        // Use the following if you need everything in one line.
        // Some APIs die if you do it differently.
        /*String cookiestring = "";
        for (String cookie : preferences) {
            String[] parser = cookie.split(";");
            cookiestring = cookiestring + parser[0] + "; ";
        }
        builder.addHeader("Cookie", cookiestring);
        */for (cookie in preferences!!) {
            builder.addHeader("Cookie", "SS_MID=42c81a3b-6610-4234-bd8e-7e10bb3bc3a8; SS_ANALYTICS_ID=b82a74d1-d962-4ac7-9504-d448c3b88de6; _gcl_au=1.1.1420435190.1698513542; _rdt_uuid=1698513542389.060bb988-f10a-4e49-a692-6e12c52dd3db; _pin_unauth=dWlkPU4ySTVOMkU0WVRZdFpXWmlOQzAwTlRZNExXRmlPRFl0T1RZNE5XRXlNVFZoTVdJMw; _fbp=fb.1.1698513542754.748272476; _tt_enable_cookie=1; _ttp=uYPQ9eJSgq8QGae7sx8bhurDMmW; _ga_F3G1ZTV1KN=GS1.1.1698629945.1.1.1698629983.22.0.0; notice_behavior=implied,us; SS_SESSION_ID=bf299c65-6707-423e-bd0c-861892202e9e; IR_gbd=squarespace.com; member-session=1|ErG4B1ccxiK2QAKUoCsDCAbRA5K1S6RxL3r/jXvxJra6|W8F64GNOrxxoe/MTqVMxmX3Gk8n6nlbwaei5zVGMZWE=; crumb=8WZARUfrN5vFQohbsOZ39dYhoD1ad9j+jSep9UwjATBc; __stripe_mid=ea571797-0d72-40c3-80c2-37d4903f9ea3226ab6; ss_lastid=eyJpZGVudGlmaWVyIjoiY29sb3JmdWwtdHJlZXMifQ%3D%3D; seven_frame_expanded=true; SiteUserSecureAuthToken=MXxjOTMwZGVhNS00ZDVmLTRmMDctOTIyYS00M2JhNzI2YTU3OTl8MzNjZnR3TEJLWnZLcnZ4RjJ0T3Vsak9FQkJlN3hrSzdYdlhsOF81aFJOYmVnSG1lUExzOWFLQzhQeVBBYlM1ZQ; SiteUserInfo=%7B%22authenticated%22%3Atrue%2C%22lastAuthenticatedOn%22%3A%222023-12-22T19%3A14%3A55.618Z%22%2C%22siteUserId%22%3A%226585da07de5a6618e6c1cee5%22%2C%22firstName%22%3A%22Brittany%22%7D; siteUserCrumb=QdDf3uc4U1nhm-xf02323cM0lDmkF6MRfWPtxZVAVH5kjABgEMRKWH-tcCwgc211aMXVUTQvJwZmwuzWa9Tt_eKDyqMsHe4H0GyJacwnhMoUO14me3toqXxXdNtw2n_P; CART=5YnSxlL0P8xyIQGBfIZaOxqFRYSxKRyTx51bWCyO; hasCart=true; IR_PI=09d8933f-75b6-11ee-90fb-09916d31f243%7C1703792549139; _gid=GA1.2.1946644336.1703706324; _ga_NFFDHXE9BF=GS1.1.1703706398.2.0.1703706398.60.0.0; _ga=GA1.1.1704667215.1699904562; IR_9084=1703706565323%7C0%7C1703706565323%7C%7C; _uetsid=f0c92cf0a4ec11ee843607f4887e0c5a; _uetvid=d508b6d0825c11ee8dee352d303ab376; __stripe_sid=cc8004c8-760b-42fe-a10f-63f479978c70e033f5; _ga_1L8CXRNJCG=GS1.1.1703708653.24.1.1703709287.53.0.0" )
        }
        return chain.proceed(builder.build())
    }

    companion object {
        const val PREF_COOKIES = "PREF_COOKIES"
    }
}