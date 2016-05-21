package com.ivanov.tech.communicator.demo;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.ivanov.tech.communicator.R;

/**
 * Created by Игорь on 15.01.15.
 */
public class ActivityDemo extends FragmentActivity {

	//---------------Server URL-------------
    public final static String URL_PROTOCOL="ws://";
    public final static String URL_DOMEN="space14.ru";  
    public final static String URL_PORT=":8001";//Websocket server port
    public final static String URL_SERVER=URL_PROTOCOL+URL_DOMEN+URL_PORT;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        
        showDemo();
    }

    private void showDemo() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_container, new FragmentDemo())
                .commit();
    }

    
}
