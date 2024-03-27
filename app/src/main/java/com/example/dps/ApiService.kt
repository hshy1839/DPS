
import com.example.dps.LoginData
import com.example.dps.UserData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("/api/android/login")
    fun login(@Body data: LoginData): Call<Void>
    @POST("/api/android/signup")
    fun signup(@Body data: UserData): Call<Void>

    @POST("/api/android/getUserName")
    fun getUserName(@Body data: LoginData, password: Any?): Call<UserData>

    @GET("/api/android/userinfo")
    fun getUserInfo(): Call<List<UserData>>
}
