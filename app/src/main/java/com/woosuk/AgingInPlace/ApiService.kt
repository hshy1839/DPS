import com.woosuk.AgingInPlace.LoginData
import com.woosuk.AgingInPlace.SignupData
import com.woosuk.AgingInPlace.UserData
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
    fun signup(@Body data: SignupData): Call<JsonObject>

    @POST("/api/android/updateUserInfo")
    fun updateUserInfo(@Body data: UserData): Call<JsonObject>

    @GET("/api/android/userinfo")
    fun getUserInfo(@Query("userId") userId: Int): Call<JsonObject>

    @POST("http://3.39.236.95:8080/wear/sleep")
    fun getSleepData(@Body jsonObject: JsonObject): Call<JsonObject>
}
