package com.example.RssReader_ST2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.*;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MyActivity extends ActionBarActivity {
    public static final String log = "mylog";
    private static final String PREFS_NAME = "MySharedPreference";
    URL url = null;
    ArrayList<ElementRss> arrayList = null;
    MenuItem tabletLike;
    private static final int TABLET_LIKE_ITEM = 100;
    int globalPosition;
    int globalPosition1;
    Menu menu;
    private ShareActionProvider mShareActionProvider;
    final Uri CONTACT_URI = Uri
            .parse("content://com.example.RssReader_ST2/liked_entries");

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public ArrayList<ElementRss> getArrayList() {
        return arrayList;
    }

    public void setArrayList(ArrayList<ElementRss> arrayList) {
        this.arrayList = arrayList;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //Log.d(log, "INSIDE ACTIVITY onCreate() , try to print file dir: " + getApplicationContext().getFilesDir().toString());

        try {
            url = new URL(
                    "http://news.yandex.ru/mobile.rss"
                    // "http://itnews.com.ua/export/business.rss"
                    // "http://www.hyrax.ru/cgi-bin/mob_xml.cgi"
            );
        } catch (MalformedURLException e) {
            Log.d(log, e.toString());
            e.printStackTrace();
        }


        try {
            arrayList = new XmlObject().execute(url).get();
            Log.d(log, "INSIDE CLASS MyActivi, onCreat(), try to see on received arrayList size: " + arrayList.size());
            //Log.d(log, "INSIDE CLASS MyActivi, onCreat(), try to see on received arrayList " + arrayList.toString());

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        String titles[] = new String[arrayList.size()];

        for (int i = 0; i < arrayList.size(); i++) {
            titles[i] = arrayList.get(i).getTitle();
        }

        final ListView listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(adapter);
        if (getResources().getConfiguration().orientation == 1 & getResources().getBoolean(R.bool.istablet)) {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            listView.setLayoutParams(layoutParams);
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText(arrayList.get(0).toStringWithOutTitle());
            textView.setTextSize(30);
        }
        if (getResources().getConfiguration().orientation == 2 & getResources().getBoolean(R.bool.istablet)) {
            TextView textView = (TextView) findViewById(R.id.textView);
            textView.setText(arrayList.get(0).toStringWithOutTitle());
            textView.setTextSize(30);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(log, "globalPosition1 befor = " + globalPosition1 );
                globalPosition1 = position;
                Log.d(log, "globalPosition1 after = " + globalPosition1 );

                String showWhatWeGot = (String) listView.getItemAtPosition(position);
                Toast.makeText(getBaseContext(), showWhatWeGot, Toast.LENGTH_SHORT).show();

                if (getResources().getConfiguration().orientation == 2 & getResources().getBoolean(R.bool.istablet)) {

                    Log.d(log, "globalPosition befor = " + globalPosition );
                    globalPosition = position;
                    mShareActionProvider.setShareIntent(shareIntent());
                    Log.d(log, "globalPosition after = " + globalPosition );

                    MenuItem tabletLike = menu.findItem(TABLET_LIKE_ITEM);
                    if (checkElement(getApplicationContext(), arrayList.get(position).getTitle())) {
                        tabletLike.setIcon(R.drawable.ic_action_rating_bad);
                        tabletLike.setTitle(R.string.item22);
                    } else {
                        tabletLike.setIcon(R.drawable.ic_action_rating_good);
                        tabletLike.setTitle(R.string.item2);
                    }
                    TextView textView = (TextView) findViewById(R.id.textView);
                    textView.setText(arrayList.get(position).toStringWithOutTitle());
                } else {
                    Intent intent = new Intent(getApplicationContext(), DisplayActivity.class);
                    intent.putExtra("sendElement", getInfo(arrayList.get(position)));
                    startActivity(intent);
                }
            }
        });

//        Intent serviceIntent=new Intent(this, MyService.class);
//        serviceIntent.putExtra("sendStringToService",getArrayList().get(0).getPubDate());
//        serviceIntent.putExtra("sendUrlToService",getUrl());
//        startService(serviceIntent);
        // startService(new Intent(this, MyService.class));
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("lastPubDate", arrayList.get(0).getPubDate());
        editor.putString("url", getUrl().toString());
        editor.commit();
        Log.d(log, "INSIDE CLASS MyActivity, onCreate(), try to see on sending SharedPreferences: " + arrayList.get(0).getPubDate());

    }

    void deleteAllDatabase(final Context context) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FeedReaderContract mDbContract = new FeedReaderContract();
                FeedReaderContract.FeedReaderDbHelper mDbHelper = mDbContract.new FeedReaderDbHelper(context);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                Log.d(log, "--- Clear mytable: ---");
                // удаляем все записи
                int clearCount = db.delete(FeedReaderContract.FeedEntry.TABLE_NAME, null, null);
                Log.d(log, "deleted rows count = " + clearCount);
                db.close();
            }
        });

    }

    public String[] getInfo(ElementRss elementRss) {
        return new String[]{elementRss.getDescription(), elementRss.getLink(), elementRss.getPubDate(), elementRss.getTitle()};
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        item.setVisible(false);
        this.invalidateOptionsMenu();
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        if (getResources().getConfiguration().orientation == 2 & getResources().getBoolean(R.bool.istablet)) {
            item.setVisible(true);
            this.invalidateOptionsMenu();
            if (checkElement(getApplicationContext(), arrayList.get(globalPosition).getTitle())) {
                Log.d(log, "globalPosition onCreateOptionsMenu() checkElement() = " + globalPosition );
                menu.add(Menu.NONE, TABLET_LIKE_ITEM, Menu.FIRST, R.string.item22)
                        .setIcon(R.drawable.ic_action_rating_bad)
                        .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
            } else {
                menu.add(Menu.NONE, TABLET_LIKE_ITEM, Menu.FIRST, R.string.item2)
                        .setIcon(R.drawable.ic_action_rating_good)
                        .setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_ALWAYS);
            }
        }
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent());
        }
        this.menu=menu;
        return super.onCreateOptionsMenu(menu);
    }

    public Intent shareIntent () {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        Log.d(log, "globalPosition shareIntent () = " + globalPosition1 );
        shareIntent.putExtra(Intent.EXTRA_TEXT, arrayList.get(globalPosition1).getTitle()+" \n "+" \n "+arrayList.get(globalPosition1).getLink());
        Log.d(log, "globalPosition shareIntent () = " + globalPosition1 );
        return shareIntent;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                stopService(new Intent(this, MyService.class));
                Toast.makeText(this, "You click STOP SERVICE BUTTON in ActionBar", Toast.LENGTH_SHORT).show();
                break;

            case R.id.item3:
                startActivity(new Intent(this, FavoriteElementsActivity.class));
                break;
            case R.id.item4:
                deleteAllDatabase(getApplicationContext());
                break;
            case TABLET_LIKE_ITEM:
                if (item.getIcon().getConstantState().equals(getResources().getDrawable(R.drawable.ic_action_rating_good).getConstantState())) {
                    Toast.makeText(this, "You click LIKE THIS NEWS in TABLET ActionBar", Toast.LENGTH_SHORT).show();
                    addElement(getApplicationContext());
                    item.setIcon(R.drawable.ic_action_rating_bad);
                    item.setTitle(R.string.item22);
                    break;
                } else {
                    Toast.makeText(this, "You click DISLIKE THIS NEWS in TABLET ActionBar", Toast.LENGTH_SHORT).show();
                    deleteElement(getApplicationContext());
                    item.setIcon(R.drawable.ic_action_rating_good);
                    item.setTitle(R.string.item2);
                    break;
                }
        }
        return true;
    }


    void addElement(final Context context) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FeedReaderContract mDbContract = new FeedReaderContract();
                FeedReaderContract.FeedReaderDbHelper mDbHelper = mDbContract.new FeedReaderDbHelper(context);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                Log.d(log, "globalPosition addElement() = " + globalPosition );
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, arrayList.get(globalPosition).getTitle());
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION, arrayList.get(globalPosition).getDescription());
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LINK, arrayList.get(globalPosition).getLink());
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PUBDATE, arrayList.get(globalPosition).getPubDate());
                Log.d(log, "globalPosition addElement() = " + globalPosition );

                long newRowId;
                newRowId = db.insert(
                        FeedReaderContract.FeedEntry.TABLE_NAME,
                        null,
                        values);
                Log.d(log, "INSIDE CLASS MyActivity, addElement(), try to see DB result : " + newRowId);
                db.close();
            }
        });

    }

    void deleteElement(final Context context) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FeedReaderContract mDbContract = new FeedReaderContract();
                FeedReaderContract.FeedReaderDbHelper mDbHelper = mDbContract.new FeedReaderDbHelper(context);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                long newRowId;
                newRowId = db.delete(
                        FeedReaderContract.FeedEntry.TABLE_NAME,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + " = ?",
                        new String[]{arrayList.get(globalPosition).getTitle()});
                Log.d(log, "INSIDE CLASS DisplayActivity, deleteElement(), try to see DB result : " + newRowId);
                db.close();
            }
        });

    }

    boolean checkElement(final Context context, final String checkTitle) {
        Log.d(log, "INSIDE MyACTIVITY checkElement(), try to see checkTitle = " + checkTitle);
        final boolean[] exists = new boolean[1];
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                exists[0] = false;
                FeedReaderContract mDbContract = new FeedReaderContract();
                FeedReaderContract.FeedReaderDbHelper mDbHelper = mDbContract.new FeedReaderDbHelper(context);
                SQLiteDatabase db = mDbHelper.getReadableDatabase();
                String[] projection = {
                        FeedReaderContract.FeedEntry._ID,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_LINK,
                        FeedReaderContract.FeedEntry.COLUMN_NAME_PUBDATE
                };
                String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE + "=?";
                String selectionArgs[] = {arrayList.get(globalPosition).getTitle()};
                Log.d(log, "INSIDE MyACTIVITY checkElement(), try to see selectionArgs[]  in db = " + selectionArgs[0]);
                Cursor cursor = context.getContentResolver().query(CONTACT_URI, projection, selection, selectionArgs, null);
                Log.d(log, "INSIDE MyACTIVITY checkElement(), CONTENT PROVAIDER CURSOR" + cursor.getCount());
                //return (cursor.getCount() > 0);
//                Cursor cursor = db.query(
//                        FeedReaderContract.FeedEntry.TABLE_NAME, // The table to query
//                        projection, // The columns to return
//                        selection, //selection , // The columns for the WHERE clause
//                        selectionArgs, //selectionArgs , // The values for the WHERE clause
//                        null, // don't group the rows
//                        null, // don't filter by row groups
//                        null // The sort order
//                );
                if (cursor.moveToFirst()) {
                    do {
                        if (checkTitle.equals(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE)))) {
                            exists[0] = true;
                            Log.d(log, "INSIDE MyACTIVITY checkElement(), try to see boolean exists  in db = " + exists[0]);
                        }
                    } while (cursor.moveToNext());
                }
                cursor.close();
                db.close();
            }
        });
        Log.d(log, "INSIDE MyACTIVITY checkElement(), try to see boolean exists  in the END = " + exists[0]);
        return exists[0];
    }

}
