package com.ivanov.tech.communicator.demo;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

import com.ivanov.tech.communicator.R;

/**
 * Created by Igor on 09.05.15.
 */
public class FragmentDemo extends DialogFragment {


    public static final String TAG = FragmentDemo.class
            .getSimpleName();
    
	
    TextView textview_response;
    EditText edittext_server_url;
    Button button_request,button_check;
    View layout_dimming;

    public static FragmentDemo newInstance() {
    	FragmentDemo f = new FragmentDemo();
        return f;
    }
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Observable.just("one", "two", "three", "four", "five")
                .subscribeOn(Schedulers.newThread()).delay(10, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override public void onCompleted() {
                        Log.d(TAG, "onCompleted()");
                    }

                    @Override public void onError(Throwable e) {
                        Log.e(TAG, "onError()", e);
                    }

                    @Override public void onNext(String string) {
                        Log.d(TAG, "onNext(" + string + ")");
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        view = inflater.inflate(R.layout.fragment_demo, container, false);
       
        TextView textview=(TextView)view.findViewById(R.id.fragment_demo_textview);
        
        
        return view;
    }

}
