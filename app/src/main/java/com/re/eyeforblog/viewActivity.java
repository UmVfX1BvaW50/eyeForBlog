package com.re.eyeforblog;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
        urlListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // 获取被长按的文本
                String text = ((TextView) view).getText().toString();

                // 执行复制文本的操作
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("label", text);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(viewActivity.this,"复制成功",Toast.LENGTH_SHORT).show();
                // 返回 true 表示该事件已被处理
                return true;
            }
        });
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