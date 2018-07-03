package com.example.tictactoe.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    public static final String SERVICE_ID = "Service ID";
    public static final String NICKNAME = "nickname";
    private final String mName = "3A0E7C5314C194EF30DDF2E2654991A626DC9C7C2D246B76EAEF145F1BDC065C";
    private final String[] mModes = {SERVICE_ID, NICKNAME};
    private static SharedPreferencesManager mManager;
    private SharedPreferences preferences;

    private SharedPreferencesManager(Context context) {
        preferences = context.getSharedPreferences(mName, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesManager createInstance(Context context){
        if (mManager == null) {
            mManager = new SharedPreferencesManager(context);
        }
        return mManager;
    }

    public static SharedPreferencesManager getInstance(){
        if(mManager == null){
            throw new NullPointerException("getInstance method must be called after createInstance.");
        }
        return mManager;
    }

    public void savePreference(String mode, String preference) {
        if(validateMode(mode)){
            preferences.edit().putString(mode, preference).apply();
        }
        else {
            throw new IllegalArgumentException("Mode argument must be one of the class-provided constants.");
        }
    }

    public String getPreference(String mode){
        return preferences.getString(mode, null);
    }

    private boolean validateMode(String mode) {
        for (String mMode : mModes) {
            if (mode.equals(mMode)) {
                return true;
            }
        }
        return false;
    }
}
