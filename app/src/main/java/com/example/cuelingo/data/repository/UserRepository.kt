package com.example.cuelingo.data.repository
import UserPreference
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.cuelingo.data.local.preferences.UserModel
import com.example.cuelingo.data.remote.response.ErrorResponse
import com.example.cuelingo.data.remote.response.LoginResponse
import com.example.cuelingo.data.remote.response.RegisterResponse
import com.example.cuelingo.data.remote.retrofit.ApiConfig
import com.example.cuelingo.data.remote.retrofit.ApiService
import com.example.cuelingo.data.result.Result
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException

class UserRepository (private val userPreference: UserPreference,
                      private val apiService: ApiService
)
{
    fun register(
        name: String,
        email: String,
        password: String
    ): LiveData<Result<RegisterResponse>> = liveData {
        emit(Result.Loading)
        try {
            val registerResponse = apiService.register(name, email, password)
            if (registerResponse.error == false) {
                emit(Result.Success(registerResponse))
            } else {
                emit(Result.Error(registerResponse.message ?: "error"))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody.message
            emit(Result.Error("Registration Failed : $errorMessage"))
        } catch (e: Exception) {
            emit(Result.Error("Signal Problem"))
        }
    }
    fun login(email: String, password: String): LiveData<Result<LoginResponse>> = liveData {
        emit(Result.Loading)
        try {
            val loginResponse = apiService.login(email, password)
            if (loginResponse.error == false) {
                val token = UserModel(
                    name = loginResponse.loginResult?.name ?: "",
                    email = email,
                    token = loginResponse.loginResult?.token ?: "",
                    isLogin = true
                )
                ApiConfig.token = loginResponse.message!!
                userPreference.saveSession(token)
                emit(Result.Success(loginResponse))
            } else {
                emit(Result.Error(loginResponse.message ?: "error"))
            }
        } catch (e: HttpException) {
            val jsonInString = e.response()?.errorBody()?.string()
            val errorBody = Gson().fromJson(jsonInString, ErrorResponse::class.java)
            val errorMessage = errorBody?.message
            emit(Result.Error("Login failed: $errorMessage"))
        } catch (e: Exception) {
            emit(Result.Error("Signal Problem"))
        }
    }
    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }
    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference,
            apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(
                    userPreference, apiService
                )
            }.also { instance = it }
    }

}