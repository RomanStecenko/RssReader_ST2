package com.example.RssReader_ST2;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created with IntelliJ IDEA.
 * User: Roman
 * Date: 10.11.13
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */
public class DisplayActivity extends ActionBarActivity {
    final String log = "mylog";
    public static CharSequence mainTitle = "";
    private MenuItem StatusItem;
    ElementRss elementRss;
    private ShareActionProvider mShareActionProvider;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_activity_layout);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String[] info = intent.getStringArrayExtra("sendElement");
        elementRss = new ElementRss(info[3], info[1], info[0], info[2]);

        TextView textView = (TextView) findViewById(R.id.textView);
        textView.setText(formatInfo(info));
        if (getResources().getBoolean(R.bool.istablet)) {
            textView.setTextSize(30);
        }
        setTitle(mainTitle);
    }

    void addElement(final Context context) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                FeedReaderContract mDbContract = new FeedReaderContract();
                FeedReaderContract.FeedReaderDbHelper mDbHelper = mDbContract.new FeedReaderDbHelper(context);
                SQLiteDatabase db = mDbHelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE, elementRss.getTitle());
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_DESCRIPTION, elementRss.getDescription());
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_LINK, elementRss.getLink());
                values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_PUBDATE, elementRss.getPubDate());


                long newRowId;
                newRowId = db.insert(
                        FeedReaderContract.FeedEntry.TABLE_NAME,
                        null,
                        values);
                Log.d(log, "INSIDE CLASS DisplayActivity, addElement(), try to see DB result : " + newRowId);
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
                        new String[]{elementRss.getTitle()});
                Log.d(log, "INSIDE CLASS DisplayActivity, deleteElement(), try to see DB result : " + newRowId);
                db.close();
            }
        });

    }

    boolean checkElement(final Context context, final String checkTitle){
        final boolean[] exists = new boolean[1];
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                   exists[0] =false;
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
                String selection=FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE+"=?";
                String selectionArgs[] = {elementRss.getTitle()};
                Log.d(log,"INSIDE DISPLAY ACTIVITY checkElement(), try to see selectionArgs[]  in db = "+selectionArgs[0]);
                Cursor cursor = db.query(
                        FeedReaderContract.FeedEntry.TABLE_NAME, // The table to query
                        projection, // The columns to return
                        selection, //selection , // The columns for the WHERE clause
                        selectionArgs, //selectionArgs , // The values for the WHERE clause
                        null, // don't group the rows
                        null, // don't filter by row groups
                        null // The sort order
                );
                if (cursor.moveToFirst ()){
                    do{
                        if(checkTitle.equals(cursor.getString(cursor.getColumnIndex(FeedReaderContract.FeedEntry.COLUMN_NAME_TITLE)))){
                            exists[0] =true;
                        }
                    }   while (cursor.moveToNext());
                }
                cursor.close();
                db.close();
            }
        });
        return exists[0];
    }

    public String formatInfo(String[] strArray) {
        String result = "";
        for (int i = 0; i < strArray.length - 1; i++) {
            result = result + strArray[i] + "\n";
        }
        mainTitle = strArray[strArray.length - 1];
        return result;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.display_activity_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        StatusItem = menu.findItem(R.id.item2);
        if(checkElement(getApplicationContext(),elementRss.getTitle())){
            StatusItem.setIcon(R.drawable.ic_action_rating_bad);
            StatusItem.setTitle(R.string.item22);
        }  else {
            StatusItem.setIcon(R.drawable.ic_action_rating_good);
            StatusItem.setTitle(R.string.item2);
        }
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent());
        }
        return super.onCreateOptionsMenu(menu);
    }


    public Intent shareIntent () {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, elementRss.getTitle()+" \n "+" \n "+elementRss.getLink());
        return shareIntent;
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.item2:
                if (StatusItem.getIcon().getConstantState().equals(getResources().getDrawable(R.drawable.ic_action_rating_good).getConstantState())) {
                    Toast.makeText(this, "You click LIKE THIS NEWS in ActionBar", Toast.LENGTH_SHORT).show();
                    addElement(getApplicationContext());
                    StatusItem.setIcon(R.drawable.ic_action_rating_bad);
                    StatusItem.setTitle(R.string.item22);
                    break;
                } else {
                    Toast.makeText(this, "You click DISLIKE THIS NEWS in ActionBar", Toast.LENGTH_SHORT).show();
                    deleteElement(getApplicationContext());
                    StatusItem.setIcon(R.drawable.ic_action_rating_good);
                    StatusItem.setTitle(R.string.item2);
                    break;
                }
            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }
}
