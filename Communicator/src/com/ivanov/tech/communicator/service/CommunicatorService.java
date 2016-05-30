package com.ivanov.tech.communicator.service;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.codebutler.android_websockets.WebSocketClient;
import com.codebutler.android_websockets.WebSocketClient.Listener;
import com.ivanov.tech.communicator.Communicator;
import com.ivanov.tech.session.Session;

public abstract class CommunicatorService extends Service implements Listener{

	private static final String TAG = CommunicatorService.class
            .getSimpleName();    
        
    private static final String JSON_LAST_TIMESTAMP="last_timestamp";
    
    protected WebSocketClient websocketclient=null;	
    
	protected int startId;
	protected int userid;//Потому что Session не доступен
	
	protected ArrayList<TransportBase> transports=createTransports();
	
	//You should create list of TransportBase objects here
	public abstract ArrayList<TransportBase> createTransports();
	
	public void onCreate() {
	    super.onCreate();
	    //Log.d(TAG, "onCreate");
	    
	    //If Internet connection change
	    IntentFilter filter = new IntentFilter();
	    filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");	    
	    registerReceiver(receiver_CONNECTIVITY_CHANGE, filter);
	    
	    //Initialize shared preferences
	    Communicator.Initialize(getApplicationContext(),getServerUrl(),getRestartServerUrl(),getCommunicatorServiceClass());
	    	    
	    for(TransportBase transport : transports){
	    	transport.onCommunicatorServiceCreate();
	    }
	}
	
	public void onDestroy() {
	    super.onDestroy();
	    //Log.d(TAG, "onDestroy");
	    unregisterReceiver(receiver_CONNECTIVITY_CHANGE);
	    
	    for(TransportBase transport : transports){
	    	transport.onCommunicatorServiceDestroy();
	    }
	}
		
    @Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    
    	Log.d(TAG, "onStartCommand");
    	
	    this.startId=startId;
	    
	    if(websocketclient==null){
	    	//Обязательный параметр. Required parameter. Без этого сервер не может нас идентифицировать	    	
	    	userid=intent.getIntExtra("userid", -1);//Так как Session недоступен в Connection вынуждены так делать
	    	
		    websocketclient=createAndSetupWebsocketСlient();	
	    }
	    //Log.d(TAG, "onStartCommand userid="+userid);
	    
	    websocketclient.connect();
	    	    
	    JSONObject json=null;
	    int transport=intent.getIntExtra("transport",0);
	    
	    if( (intent.hasExtra("json")) ){
		    try {
				json=new JSONObject(intent.getStringExtra("json"));
				
			} catch (JSONException e) {
				Log.d(TAG, "onStartCommand JSONException e="+e);
			}
	    }
	    
	    if(transport!=0){
	    	
	    	for(TransportBase _transport : transports){
	    		_transport.onOutgoingMessage(transport, json);
	    	}
	    }
	    
	    return START_NOT_STICKY;
	}
    
    @Override
	public IBinder onBind(Intent intent) {
		return null;
	}
    
    //Создает WebSocketClient, ставит параметры сервера, ставит listener
    public WebSocketClient createAndSetupWebsocketСlient(){
    	//Log.d(TAG, "createWebSocketClient");
    	
    	WebSocketClient websocketclient;

    	ArrayList<BasicNameValuePair> headers=new ArrayList<BasicNameValuePair>();    	
    	BasicNameValuePair header_userid=new BasicNameValuePair("userid",String.valueOf(userid));    
    	headers.add(header_userid);
    	
    	websocketclient = new WebSocketClient(URI.create(Communicator.getUrlServer()), this, headers);
    	
    	for(TransportBase transport : transports){
    		transport.websocketclient=websocketclient;
    	}
    	return websocketclient;
    }
    
    public abstract String getServerUrl(); // Returns protocol, url, port like  ws://igorpi25.ru:8001
    
    public abstract String getRestartServerUrl(); // Returns protocol, url, port like  ws://igorpi25.ru:8001
    
    public abstract String getCommunicatorServiceClass(); // Returns class of service that used to send messages through intent
    
//------------WebsocketClientListener------------------------
    
    @Override
    public void onCreate(WebSocketClient websocketclient) {
    	//Log.d(TAG, "onCreate(WebSocketClient)");
    	
    	for(TransportBase transport : transports){
    		transport.onCreate(websocketclient);
    	}
    }
    
    public void onConnect() {
    	//Log.d(TAG, "onConnect");
		
		ResetConnectAttempts();
		
		for(TransportBase transport : transports){
    		transport.onConnect();
    	}
    }

    @Override
    public void onMessage(String message) {
    	Log.d(TAG, "onMessage message="+message);  

        JSONObject json=null;
	    int transport=0;
        
        try {
        	json = new JSONObject(message);
        
	    	if(json.has(JSON_LAST_TIMESTAMP)){
	    		long last_timestamp=json.getLong(JSON_LAST_TIMESTAMP);
	    		//Сохраняем last_timestamp в секундах. Нужно для следующего подключения в header-е запроса
	    		Communicator.setLastTimestamp(last_timestamp);
	    	}
	    	
	    	if(json.has("transport")){
	    		transport=json.getInt("transport");
	    	}
	    	
        }catch(JSONException e){
        	Log.d(TAG, "onMessage JSONException e="+e);
        }
        
        for(TransportBase _transport : transports){
    		_transport.onMessage(message);
    	}
        
        if(transport!=0){
	        for(TransportBase _transport : transports){
	        	_transport.onIncomingMessage(transport, json);
	    	}
        }
        
    }

    @Override
    public void onMessage(byte[] data) {
    	//Log.d(TAG, "onMessage data");  
    	for(TransportBase transport : transports){
    		transport.onMessage(data);
    	}
    }

    @Override
    public void onDisconnect(int code, String reason) {
       // Log.d(TAG, String.format("onDisconnect code=%d Reason=%s", code, reason));
                
        Reconnect();
        
        for(TransportBase transport : transports){
    		transport.onDisconnect(code,reason);
    	}
    }

    @Override
    public void onError(Exception error) {
       // Log.e(TAG, "onError error="+error);
      
        for(TransportBase transport : transports){
    		transport.onError(error);
    	}

        if( (isOnline()) && (reconnect_attempts==2) ){
        	restartServerRequest();
        }else{
        	Reconnect();
        }
        
    }
    
//----------------Reconnecting---------------------------
    
    public int reconnect_attempts=0;
	public int max_reconnect_attempts=7;
	public boolean reconnect_attempt_waiting=false; 
	
    void Reconnect(){    
    	
    	if((reconnect_attempts>=max_reconnect_attempts)){
    		handler_reconnectevents.sendEmptyMessage(0);
    		
    		return;
    	}
    	
    	if(!reconnect_attempt_waiting){
    		
    		reconnect_attempt_waiting=true;
    		
    		reconnect_attempts++;
    		timer_reconnect.schedule(new ReconnectTask(), 6000);   	
    	}    	
    	
    }
    
    void ResetConnectAttempts(){
    	handler_reconnectevents.sendEmptyMessage(2);
    	reconnect_attempts=0;
    }
    
    private static Timer timer_reconnect = new Timer(); 
    
    private final Handler handler_reconnectevents = new Handler(){
    	
        @Override
        public void handleMessage(Message msg)
        {
        	switch(msg.what){
        		case 0:
        			onReconnectRefused();
            	return;
            	
        		case 1:
        			onReconnectAttempt(reconnect_attempts);
	        	return;
	        	
	        	case 2:
	        		onReconnected(reconnect_attempts);
	            return;	
        	}        	
        }
    };  
    
    private class ReconnectTask extends TimerTask { 
        
    	public void run() 
        {
        	reconnect_attempt_waiting=false;
        	handler_reconnectevents.sendEmptyMessage(1);
        	
        	websocketclient.connect();
        }
    }    
    
    private final BroadcastReceiver receiver_CONNECTIVITY_CHANGE = new BroadcastReceiver() {

    	   @Override
    	   public void onReceive(Context context, Intent intent) {

    		   String action = intent.getAction();
    	      
    		   if(action.equals("android.net.conn.CONNECTIVITY_CHANGE")){
    			   if(intent.getExtras()!=null) {
    	    	        NetworkInfo ni=(NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);
    	    	        if(ni!=null && ni.getState()==NetworkInfo.State.CONNECTED) {
    	    	        	if( (!reconnect_attempt_waiting)&&(reconnect_attempts>=max_reconnect_attempts) )
    	    	        		websocketclient.connect();
    	    	        }
    			   }    	        
    	      }
    	   }
    };
 
    //-------------------Server restart------------------------------
    
    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
    
    public void restartServerRequest() {

        StringRequest stringrequest = new StringRequest(Request.Method.GET, getRestartServerUrl(), new Response.Listener<String>() {

        	@Override
			public void onResponse(String response) {
            	Log.d(TAG, "restartServerRequest onResponse response="+response);
            	try {
					JSONObject json=new JSONObject(response);
					
					boolean error=json.getBoolean("error");
					
					if(!error){
						int status=json.getInt("status");
						Log.d(TAG, "restartServerRequest error=false status="+status);
						if(status==0){
							Log.d(TAG, "restartServerRequest server not running");
							Reconnect();
						}
						if(status==1){
							Log.d(TAG, "restartServerRequest server already running");
							Reconnect();
						}
						
					}else{
						Log.e(TAG, "restartServerRequest error=true");
					}
					
				} catch (JSONException e) {
					Log.e(TAG, "restartServerRequest JSONException e="+e);
				}            	
            }
			
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                
                if (error.getClass().equals(TimeoutError.class)) {
                	Log.d(TAG, "restartServerRequest timeout. Reconnecting");
                	
                }else{
                	Log.e(TAG, "restartServerRequest onErrorResponse error=" + error.toString());
                }
                Reconnect();
            }
        }){
        	@Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                 HashMap<String, String> headers = new HashMap<String, String>();
                 
                 headers.put("Content-Type", "application/x-www-form-urlencoded");
                 headers.put("Api-Key", Session.getApiKey());
                 
                 return headers;
            }
        	
        };
        
        String tag_stringrequest ="restartServerRequest";

        int socketTimeout = 3000;//3 seconds - wait for
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringrequest.setRetryPolicy(policy);
    	
    	stringrequest.setTag(tag_stringrequest);
    	Volley.newRequestQueue(getApplicationContext()).add(stringrequest);
    
    }
    
//-----------------Reconnecting Events---------------------------
    
    protected void onReconnectRefused(){
    	//Toast.makeText(getApplicationContext(), "Lets-Race: Connect refused", Toast.LENGTH_SHORT).show();
    }
    
    protected void onReconnectAttempt(int attempt){
    	
    	if(attempt==1){
    		
    	}
    	
    	//Toast.makeText(getApplicationContext(), "Lets-Race: connect attempt "+connectAttempts, Toast.LENGTH_SHORT).show();
    }
    
    protected void onReconnected(int attempt){
    	//Toast.makeText(getApplicationContext(), "Lets-Race: CONNECTED on "+connectAttempts, Toast.LENGTH_LONG).show();
    }

	
}
