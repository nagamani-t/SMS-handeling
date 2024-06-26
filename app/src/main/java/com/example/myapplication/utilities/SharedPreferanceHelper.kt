import android.content.Context
import android.content.SharedPreferences

object SharedPreferencesHelper {
    private const val PREFS_NAME = "MyAppPrefs"
    private const val KEY_EMAIL = "email"
    private const val KEY_PHONE = "phone"
    private const val KEY_NAME = "name"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveCredentials(context: Context, email: String, phone: String, name: String) {
        val editor = getSharedPreferences(context).edit()
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_PHONE, phone)
        editor.putString(KEY_NAME, name)
        editor.apply()
    }

    fun getSavedCredentials(context: Context): Triple<String?, String?, String?> {
        val prefs = getSharedPreferences(context)
        val email = prefs.getString(KEY_EMAIL, null)
        val phone = prefs.getString(KEY_PHONE, null)
        val name = prefs.getString(KEY_NAME, null)
        return Triple(email, phone, name)
    }
}