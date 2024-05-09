import com.example.dps.LoginData
import com.example.dps.UserData
import com.google.gson.JsonObject
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query


interface ApiService {

    @POST("/api/android/login")
    fun login(@Body data: LoginData): Call<JsonObject>
    @POST("/api/android/signup")
    fun signup(@Body data: UserData): Call<Void>

    @POST("/api/android/getUserName")
    fun getUserName(@Body data: LoginData, password: Any?): Call<UserData>

    @GET("/api/android/userinfo")
    fun getUserInfo(@Query("userId") userId: Int): Call<JsonObject>

    @POST("http://43.200.2.115:8080/chart/activityUsername")
    fun postUserData(@Body body: RequestBody?): Call<ResponseBody?>?
}
