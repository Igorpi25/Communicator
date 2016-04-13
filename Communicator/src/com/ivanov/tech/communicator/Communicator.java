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

    private final static String TAG="Connection";
    
//---------------Server URL----------------------------------------------
    
    public final static String URL_PROTOCOL="ws://";
    public final static String URL_DOMEN="yourserver.com";  
    public final static String URL_PORT=":8001";//Websocket server port
    public final static String URL_SERVER=URL_PROTOCOL+URL_DOMEN+URL_PORT;
    
//--------------Service LastTime Preferences--------------------------------
	
    private static final String PREF = "Communicator";    
    public static final String PREF_LAST_TIMESTAMP="PREF_LAST_TIMESTAMP";
    public static final long PREF_LAST_TIMESTAMP_DEFAULT=0;
    
    static private SharedPreferences preferences=null;
    
    public static void Initialize(Context context){
    	if(preferences==null){
    		preferences=context.getApplicationContext().getSharedPreferences(PREF, 0);
    	}
    }
    
    public static long getLastTimestamp(){		
  		return preferences.getLong(Communicator.PREF_LAST_TIMESTAMP, Communicator.PREF_LAST_TIMESTAMP_DEFAULT);
  	}
    
    public static void setLastTimestamp(long timestamp){  		
  			preferences.edit().putLong(Communicator.PREF_LAST_TIMESTAMP, timestamp).commit();
  	}

}
