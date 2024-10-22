import android.content.Context
import android.util.Log
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

object UserInfoUtil {

    fun fetchUserInfo(apiService: ApiService, userId: Int, callback: (UserInfo?) -> Unit) {
        val call = apiService.getUserInfo(userId)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    val jsonObject = response.body()
                    if (jsonObject != null) {
                        val email = jsonObject.get("email").asString
                        val name = jsonObject.get("name").asString
                        val birthdate = jsonObject.get("birthdate").asString
                        val gender = jsonObject.get("gender").asString
                        val phoneNumber = jsonObject.get("phoneNumber").asString
                        val role = jsonObject.get("role").asString

                        // 필요한 형식으로 데이터 포맷
                        val originalFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val date = originalFormat.parse(birthdate)
                        val formattedBirthdate = originalFormat.format(date)

                        // 사용자 정보 객체 생성
                        val userInfo = UserInfo(email, name, formattedBirthdate, gender, phoneNumber, role)
                        callback(userInfo)
                    } else {
                        Log.e("EmptyData", "JsonObject is null")
                        callback(null)
                    }
                } else {
                    Log.e("API", "Request failed: ${response.code()}")
                    callback(null)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e("API", "Network error: ${t.message}")
                callback(null)
            }
        })
    }
}

// 사용자 정보를 담기 위한 데이터 클래스
data class UserInfo(
    val email: String,
    val name: String,
    val birthdate: String,
    val gender: String,
    val phoneNumber: String,
    val role: String
)
