package com.re.eyeforblog;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class resActivity extends AppCompatActivity {
    private DataDBHelper dbHelper;

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_res);

        listView = findViewById(R.id.listview_results);
        dbHelper = new DataDBHelper(this);
        new CheckDataTask().execute();

        Toast.makeText(resActivity.this,"正在查询",Toast.LENGTH_SHORT).show();

    }

    private class CheckDataTask extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {
            List<String> resultList = new ArrayList<>();
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT url, content FROM data", null);
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String url = cursor.getString(cursor.getColumnIndex("url"));
                    @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                    //打印结果，不相同更新数据库
                    String result = fetchDataFromUrl(url);
                    if(content.equals(result)){
                        String listItem = "URL：" + url + "  |  same";
                        resultList.add(listItem);
                    }else{
                        String listItem = "URL：" + url + "  |  diff";
                        resultList.add(listItem);
                        ContentValues values = new ContentValues();
                        values.put("content", result);
                        db.update("data", values, "url=?", new String[]{url});
                        db.close();
                    }
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();

            return resultList;
        }
        //访问url
        private String fetchDataFromUrl(String url) {

            String result = "";
            HttpURLConnection urlConnection = null;
            try {
                URL urll = new URL(url);
                urlConnection = (HttpURLConnection) urll.openConnection();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
            return result;
        }
        //异步结束执行
        @Override
        protected void onPostExecute(List<String> resultList) {
            Toast.makeText(resActivity.this,"查询结束",Toast.LENGTH_SHORT).show();
            ResultListAdapter adapter = new ResultListAdapter(resActivity.this, resultList);
            listView.setAdapter(adapter);
        }
    }
}