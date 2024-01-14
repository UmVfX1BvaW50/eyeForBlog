package com.re.eyeforblog;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class viewActivity extends AppCompatActivity {

    private TextView titleTextView;
    private ListView urlListView;
    private DataDBHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view);

        titleTextView = findViewById(R.id.titleTextView);
        urlListView = findViewById(R.id.urlListView);
        databaseHelper = new DataDBHelper(this);
        displayUrls();
    }
    private void displayUrls() {
        ArrayList<String> urls = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT url FROM data", null);
        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String url = cursor.getString(cursor.getColumnIndex("url"));
                urls.add(url);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, urls);
        urlListView.setAdapter(adapter);
    }
}