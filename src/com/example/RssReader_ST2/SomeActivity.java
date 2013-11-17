package com.example.RssReader_ST2;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created with IntelliJ IDEA.
 * User: Roman
 * Date: 16.11.13
 * Time: 18:42
 * To change this template use File | Settings | File Templates.
 */
public class SomeActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.for_some_activity);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
