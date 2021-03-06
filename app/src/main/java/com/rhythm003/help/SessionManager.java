package com.rhythm003.help;

/**
 * Created by Rhythm003 on 7/25/2016.
 * SessionManager stores information in private shared preferences, including the user name, email, apikey, Fitbit token...
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "TypeILogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(String name, String email, String apikey) {

        editor.putBoolean(KEY_IS_LOGGEDIN, true);
        // commit changes
        editor.putString("USER_NAME", name);
        editor.putString("USER_EMAIL", email);
        editor.putString("USER_APIKEY", apikey);
        editor.commit();

        Log.d(TAG, "User login session modified!(login)");
    }

    public void setLogoff() {
        editor.putBoolean(KEY_IS_LOGGEDIN, false);
        editor.commit();
        Log.d(TAG, "User login session modified!(logoff)");
    }
    public void clearPreff() {
        editor.putString("USER_NAME", "");
        editor.putString("USER_EMAIL", "");
        editor.putString("USER_APIKEY", "");
        editor.commit();
    }
    public void setToken(String token) {
        editor.putString("TOKEN", token);
        editor.commit();
    }

    public void setRToken(String r_token) {
        editor.putString("R_TOKEN", r_token);
        editor.commit();
    }

    public String getUSER_NAME() {
        return pref.getString("USER_NAME", "");
    }
    public String getUSER_EMAIL() {
        return pref.getString("USER_EMAIL", "");
    }
    public String getUSER_APIKEY() {
        return pref.getString("USER_APIKEY", "");
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
    public String getToken() { return pref.getString("TOKEN", ""); }
    public String getRToken() { return pref.getString("R_TOKEN", ""); }
}
