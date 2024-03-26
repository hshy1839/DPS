
import com.example.dps.FindPwData_login
import com.example.dps.FindPwResponse_login
import com.example.dps.LoginData
import com.example.dps.UserData
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("/api/signup")
    fun registerUser(
        @Body data: UserData
    ): Call<String>

    @POST("/api/login")
    fun PWFind(@Body data: FindPwData_login): Call<UserData>

    @POST("/checkConnection")
    fun checkConnection(): Call<String>

    @POST("/api/android/signup")
    fun signup(@Body data: UserData): Call<Void>
}
