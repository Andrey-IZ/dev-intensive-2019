package ru.skillbranch.devintensive.viewmodels

import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.skillbranch.devintensive.repositories.PreferencesRepository

class ProfileViewModel : ViewModel() {
    private val repository = PreferencesRepository
    private val profileData = MutableLiveData<Profile>()
    private val appTheme = MutableLiveData<Int>()

    private val isRepoError = MutableLiveData<Boolean>()
    private val repositoryError = MutableLiveData<Boolean>()

    init {
        Log.d("M_ProfileViewModel", "init view model")
        profileData.value = repository.getProfile()
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("M_ProfileViewModel", "view model onCleared")
    }

    fun getIsRepoError():LiveData<Boolean> = isRepoError
    fun getRepositoryError(): LiveData<Boolean> = repositoryError

    fun getProfileData(): LiveData<Profile> = profileData

    fun saveProfileData(profile: Profile) {
        repository.saveProfile(profile)
        profileData.value = profile
    }

    fun getTheme(): LiveData<Int> = appTheme

    fun switchTheme() {
        if (appTheme.value == AppCompatDelegate.MODE_NIGHT_YES)
            appTheme.value = AppCompatDelegate.MODE_NIGHT_NO
        else appTheme.value = AppCompatDelegate.MODE_NIGHT_YES

        repository.saveAppTheme(appTheme.value!!)
    }

    fun onRepositoryChanged(repository: String) {
        repositoryError.value = isValidateRepository(repository)
    }


    fun onRepoEditCompleted(isError: Boolean) {
        isRepoError.value = isError
    }

    private fun isValidateRepository(repoText: String): Boolean {
        val regexStr = "^(https:\\/\\/)?(www\\.)?(github\\.com\\/)(?!(${getRegexExceptions()})(?=\\/|\$))[a-zA-Z\\d](?:[a-zA-Z\\d]|-(?=[a-zA-Z\\d])){0,38}(\\/)?$"
        val regex = Regex(regexStr)

        return (repoText.isNotEmpty() && !regex.matches(repoText))
    }

    private fun getRegexExceptions(): String {
        val exceptions = arrayOf(
                "enterprise", "features", "topics", "collections", "trending", "events", "marketplace", "pricing",
                "nonprofit", "customer-stories", "security", "login", "join"
        )
        return exceptions.joinToString("|")
    }
}

data class Profile (
        val firstName: String,
        val lastName: String,
        val about: String,
        val repository: String,
        val rating: Int = 0,
        val respect: Int = 0
) {
    val nickName: String = ru.skillbranch.devintensive.utils.Utils.transliteration("$firstName $lastName", "_")
    val rank: String = "Junior Android Developer"

    fun toMap(): Map<String, Any> = mapOf(
            "nickName" to nickName,
            "rank" to rank,
            "firstName" to firstName,
            "lastName" to lastName,
            "about" to about,
            "repository" to repository,
            "rating" to rating,
            "respect" to respect
    )
}