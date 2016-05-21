package com.ivanov.tech.communicator;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class Communicator {

    private final static String TAG="Communicator";
    
//--------------Service LastTime Preferences--------------------------------
	
    private static final String PREF = "Communicator";    
    public static final String PREF_LAST_TIMESTAMP="PREF_LAST_TIMESTAMP";
    public static final long PREF_LAST_TIMESTAMP_DEFAULT=0;
    public static final String PREF_URL_SERVER="PREF_URL_SERVER";
    
    static private SharedPreferences preferences=null;
    
    public static void Initialize(Context context, String url_server){
    	if(preferences==null){
    		preferences=context.getApplicationContext().getSharedPreferences(PREF, 0);
    	}
    	preferences.edit().putString(PREF_URL_SERVER, url_server).commit();
    	
    }
    
    public static long getLastTimestamp(){		
    	Log.d(TAG, "getLastTimestamp timestamp="+preferences.getLong(Communicator.PREF_LAST_TIMESTAMP, Communicator.PREF_LAST_TIMESTAMP_DEFAULT));
  		return preferences.getLong(Communicator.PREF_LAST_TIMESTAMP, Communicator.PREF_LAST_TIMESTAMP_DEFAULT);
  	}
    
    public static void setLastTimestamp(long timestamp){  	
    		Log.d(TAG, "setLastTimestamp timestamp="+timestamp);
  			preferences.edit().putLong(Communicator.PREF_LAST_TIMESTAMP, timestamp).commit();
  	}

    public static String getUrlServer(){		
    	return preferences.getString(PREF_URL_SERVER, null);
  	}
}
