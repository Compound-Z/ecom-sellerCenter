package vn.ztech.software.ecomSeller.database.local.user

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.AnyThread
import vn.ztech.software.ecomSeller.api.response.Token
import vn.ztech.software.ecomSeller.model.UserData
import vn.ztech.software.ecomSeller.ui.splash.ISplashUseCase
import java.io.Serializable
import java.lang.ref.WeakReference
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.ReentrantLock

class UserManager (context: Context): Serializable {

    private val mPrefs: SharedPreferences
    private val mPrefsLock: ReentrantLock

    init {
        mPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        mPrefsLock = ReentrantLock()
    }
    @AnyThread
    fun saveLogs(userData: UserData? = null, accessToken: Token? = null, refreshToken: Token? = null) {
        mPrefsLock.lock()
        try {
            val editor = mPrefs.edit()
            /**Maybe we don't need to store userData, the userData will be fetched from server each time the user use the app, at the Splash Screen*/
            accessToken?.let {
                editor.putString(ACCESS_TOKEN, accessToken.token)
                editor.putString(ACCESS_TOKEN_EXPIRES, accessToken.exp)
            }
            refreshToken?.let {
                editor.putString(REFRESH_TOKEN, refreshToken.token)
                editor.putString(REFRESH_TOKEN_EXPIRES, refreshToken.exp)
            }

            if (!editor.commit()) {
                throw IllegalStateException("Failed to write login to shared prefs")
            }
        } finally {
            mPrefsLock.unlock()
        }
    }

    @AnyThread
    fun clearLogs() {
        mPrefsLock.lock()
        try {
            val editor = mPrefs.edit()
                editor.putString(ACCESS_TOKEN, "")
                editor.putString(ACCESS_TOKEN_EXPIRES, "")
                editor.putString(REFRESH_TOKEN, "")
                editor.putString(REFRESH_TOKEN_EXPIRES, "")
            if (!editor.commit()) {
                throw IllegalStateException("Failed to write login to shared prefs")
            }
        } finally {
            mPrefsLock.unlock()
        }
    }

    @AnyThread
    fun getAccessToken(): String {
        mPrefsLock.lock()
        try {

            return mPrefs.getString(ACCESS_TOKEN, null) ?: return ""
        } finally {
            mPrefsLock.unlock()
        }
    }
    @AnyThread
    fun getRefreshToken(): String {
        mPrefsLock.lock()
        try {

            return mPrefs.getString(REFRESH_TOKEN, null) ?: return ""
        } finally {
            mPrefsLock.unlock()
        }
    }
//
//    @AnyThread
//    fun saveCodeVerifier(authRequest: AuthorizationRequest?) {
//        mPrefsLock.lock()
//        try {
//            val editor = mPrefs.edit()
//            if (authRequest == null) {
//                return
//            } else {
//                editor.putString(CODE_VERIFIER, authRequest.codeVerifier)
//            }
//            if (!editor.commit()) {
//                throw IllegalStateException("Failed to write state to shared prefs")
//            }
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }
//
//    @AnyThread
//    fun saveLoginInfo(uriCallback: Uri) {
//        mPrefsLock.lock()
//        try {
//            val editor = mPrefs.edit()
//
//            val state = uriCallback.getQueryParameter("state")
//            val code = uriCallback.getQueryParameter("code")
//            val sessionId = uriCallback.getQueryParameter("session_id")
//
//            if (null != state && "" != state)
//                editor.putString(STATE, state)
//            if (null != code && "" != code)
//                editor.putString(CODE, code)
//            if (null != sessionId && "" != sessionId)
//                editor.putString(SESSION_ID, sessionId)
//
//            if (!editor.commit()) {
//                throw IllegalStateException("Failed to write login to shared prefs")
//            }
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }
//
//    @AnyThread
//    fun saveLogged(info: RequestTokenRespone?) {
//        mPrefsLock.lock()
//        try {
//            val editor = mPrefs.edit()
//
//            editor.putString(ACCESS_TOKEN, info?.access_token)
//            editor.putString(ACCESS_TOKEN_EXPIRES, info?.access_token_expires)
//            editor.putString(TOKEN_ID, info?.id_token)
//            editor.putString(REFRESH_TOKEN, info?.refresh_token)
//            if(info?.refresh_token_expires  != null && info?.refresh_token_expires != "") {
//                editor.putString(REFRESH_TOKEN_EXPIRES, info?.refresh_token_expires)
//            }
//
//            if (!editor.commit()) {
//                throw IllegalStateException("Failed to write login to shared prefs")
//            }
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }
//
//    @AnyThread
//    fun saveAccessToken(acc: String?, accTime: String?) {
//        mPrefsLock.lock()
//        try {
//            val editor = mPrefs.edit()
//
//            if(acc != null) {
//                editor.putString(ACCESS_TOKEN, acc)
//            }
//            if(accTime != null) {
//                editor.putString(ACCESS_TOKEN_EXPIRES, accTime)
//            }
//
//            if (!editor.commit()) {
//                throw IllegalStateException("Failed to write login to shared prefs")
//            }
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }
//
//    @AnyThread
//    fun savePlaceID(placeId: String) {
//        mPrefsLock.lock()
//        try {
//            val editor = mPrefs.edit()
//            editor.putString(ROOT_PLACE_ID, placeId ?: "")
//            if (!editor.commit()) {
//                throw IllegalStateException("Failed to write login to shared prefs")
//            }
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }
//
//    @AnyThread
//    fun clearMenuSession() {
//        mPrefsLock.lock()
//        try {
//            val editor = mPrefs.edit()
//            editor.putString(ROOT_PLACE_ID, "")
//            editor.putString(ITEM_GROUP_ID, "")
//            if (!editor.commit()) {
//                throw IllegalStateException("Failed to write login to shared prefs")
//            }
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }
//
//    @AnyThread
//    fun clearAll() {
//        mPrefsLock.lock()
//        try {
//            val editor = mPrefs.edit()
//            editor.clear()
//            if (!editor.commit()) {
//                throw IllegalStateException("Failed to write login to shared prefs")
//            }
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }
//
//    @AnyThread
//    fun saveGroupID(groupId: String) {
//        mPrefsLock.lock()
//        try {
//            val editor = mPrefs.edit()
//            editor.putString(ITEM_GROUP_ID, groupId ?: "")
//            if (!editor.commit()) {
//                throw IllegalStateException("Failed to write login to shared prefs")
//            }
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }
//
//    @AnyThread
//    fun saveLatestTimestamp(timestamp: Long) {
//        mPrefsLock.lock()
//        try {
//            val editor = mPrefs.edit()
//            editor.putLong(LATEST_TIMESTAMP_UPDATE_JWT, timestamp)
//            if (!editor.commit()) {
//                throw IllegalStateException("Failed to write login to shared prefs")
//            }
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }
//
//    @AnyThread
//    fun isLogin(): Boolean {
//        mPrefsLock.lock()
//        try {
//            val accessToken = mPrefs.getString(ACCESS_TOKEN, null)
//            return (accessToken != null && accessToken.isNotEmpty())
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }

    @AnyThread
    fun nextPage(): ISplashUseCase.PAGE {
        mPrefsLock.lock()
        try {
            val accessToken = mPrefs.getString(ACCESS_TOKEN, null)
            /**if doesn't has a token, it must be a new user*/
            if(accessToken == null)  return ISplashUseCase.PAGE.SIGNUP
            /**if the token if empty, the user has logged out before*/
            if(accessToken.isEmpty()) return ISplashUseCase.PAGE.LOGIN
            /**if there is a token exist, well, the user is allowed to continue to use the app*/
            return ISplashUseCase.PAGE.MAIN
        } finally {
            mPrefsLock.unlock()
        }
    }
//
//    @AnyThread
//    fun getCodeVerifier(): String {
//        mPrefsLock.lock()
//        try {
//            return mPrefs.getString(CODE_VERIFIER, null) ?: return ""
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }
//
//    @AnyThread
//    fun getCode(): String {
//        mPrefsLock.lock()
//        try {
//            return mPrefs.getString(CODE, null) ?: return ""
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }
//
//    @AnyThread
//    fun getTokenId(): String {
//        mPrefsLock.lock()
//        try {
//            return mPrefs.getString(TOKEN_ID, null) ?: return ""
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }
//
//    @AnyThread
//    fun getAccessToken(): String {
//        mPrefsLock.lock()
//        try {
//
//            return mPrefs.getString(ACCESS_TOKEN, null) ?: return ""
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }
//

    @AnyThread
    fun getRefreshTokenExpires(): String {
        mPrefsLock.lock()
        try {
            return mPrefs.getString(REFRESH_TOKEN_EXPIRES, null) ?: return ""
        } finally {
            mPrefsLock.unlock()
        }
    }
    @AnyThread
    fun getAccessTokenExpires(): String {
        mPrefsLock.lock()
        try {
            return mPrefs.getString(ACCESS_TOKEN_EXPIRES, null) ?: return ""
        } finally {
            mPrefsLock.unlock()
        }
    }
//
//    @AnyThread
//    fun getState(): String {
//        mPrefsLock.lock()
//        try {
//            return mPrefs.getString(STATE, null) ?: return ""
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }
//
//    @AnyThread
//    fun getSessionId(): String {
//        mPrefsLock.lock()
//        try {
//            return mPrefs.getString(SESSION_ID, null) ?: return ""
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }
//
//    @AnyThread
//    fun getPlaceID(): String {
//        mPrefsLock.lock()
//        try {
//            return mPrefs.getString(ROOT_PLACE_ID, null) ?: return ""
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }
//
//    @AnyThread
//    fun getGroupID(): String {
//        mPrefsLock.lock()
//        try {
//            return mPrefs.getString(ITEM_GROUP_ID, null) ?: return ""
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }
//
//    @AnyThread
//    fun getLatestTimestamp(): Long {
//        mPrefsLock.lock()
//        try {
//            return mPrefs.getLong(LATEST_TIMESTAMP_UPDATE_JWT, 0L)
//        } finally {
//            mPrefsLock.unlock()
//        }
//    }
//
    companion object {

        private val INSTANCE_REF = AtomicReference(WeakReference<UserManager>(null))

        private const val TAG = "UserManager"
        private const val PREF_NAME = "PREF_USER_LOGIN"
//        private const val CODE_VERIFIER = "CODE_VERIFIER"
//        private const val STATE = "STATE_KEY"
//        private const val CODE = "CODE_KEY"
//        private const val SESSION_ID = "SESSION_ID"
//        private const val TOKEN_ID = "TOKEN_ID"
        private const val REFRESH_TOKEN = "REFRESH_TOKEN"
        private const val REFRESH_TOKEN_EXPIRES = "REFRESH_TOKEN_EXPIRES"
        private const val ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val ACCESS_TOKEN_EXPIRES = "ACCESS_TOKEN_EXPIRES"
//        private const val ROOT_PLACE_ID = "ROOT_PLACE_ID"
//        private const val ITEM_GROUP_ID = "ITEM_GROUP_ID"
//        private const val LATEST_TIMESTAMP_UPDATE_JWT = "LATEST_TIMESTAMP_UPDATE_JWT"

        @AnyThread
        fun getInstance(context: Context): UserManager {
            var manager = INSTANCE_REF.get().get()
            if (manager == null) {
                manager =
                    UserManager(context.applicationContext)
                INSTANCE_REF.set(WeakReference(manager))
            }

            return manager
        }
    }
}