package com.rhythm003.help;

/**
 * Created by Rhythm003 on 7/25/2016.
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

    public void setLogin(String uid, String name, String email, String pass) {

        editor.putBoolean(KEY_IS_LOGGEDIN, true);
        // commit changes
        editor.putString("USER_ID", uid);
        editor.putString("USER_NAME", name);
        editor.putString("USER_EMAIL", email);
        editor.putString("USER_PASS", pass);
        editor.commit();

        Log.d(TAG, "User login session modified!(login)");
    }

    public void setLogoff() {
        editor.putBoolean(KEY_IS_LOGGEDIN, false);
        editor.commit();
        Log.d(TAG, "User login session modified!(logoff)");
    }

    public String getUSER_NAME() {
        return pref.getString("USER_NAME", "");
    }
    public String getUSER_EMAIL() {
        return pref.getString("USER_EMAIL", "");
    }
    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }
}
