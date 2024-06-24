package app.bangkit.ishara.ui.game

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.bangkit.ishara.data.preferences.UserPreference
import app.bangkit.ishara.data.requests.UpdateLevelRequest
import app.bangkit.ishara.data.responses.journey.level_star.UpdateLevelErrorResponse
import app.bangkit.ishara.data.responses.login.error.LoginErrorResponse
import app.bangkit.ishara.data.retrofit.ApiConfig
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ScoreViewModel(private val pref: UserPreference) : ViewModel() {

    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    fun postLevelStar(obtainedStar: Int, levelId: Int) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val token = pref.getJwtAccessToken().first()
                val updateLevelRequest = UpdateLevelRequest(obtainedStar)
                val response = ApiConfig.getApiService().postLevelStars(
                    token = "Bearer $token",
                    levelId = levelId,
                    updateLevelRequest = updateLevelRequest,
                )
                if (response.meta!!.success) {
                    _isSuccess.value = true
                }
                _isLoading.value = false
            } catch (e: Exception) {
                if (e is retrofit2.HttpException) {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorResponse =
                        Gson().fromJson(errorBody, UpdateLevelErrorResponse::class.java)
                    _errorMessage.value = "Update Star failed: ${errorResponse.meta!!.success}"
                    Log.d(TAG, "error: ${errorResponse.meta.success}")
                    Log.d(TAG, "levelid error: $levelId")

                } else {
                    _errorMessage.value = "Update Star failed: ${e.message}"
                    Log.d(TAG, "error: ${e.message}")
                }
                _isLoading.value = false
            }
        }
    }

    companion object {
        private const val TAG = "ScoreViewModel"
    }
}