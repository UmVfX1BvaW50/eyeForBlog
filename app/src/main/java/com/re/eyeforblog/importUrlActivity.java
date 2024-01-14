package com.re.eyeforblog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class importUrlActivity extends AppCompatActivity {

    private static final int READ_REQUEST_CODE = 42; // 用于选择文件的请求码
    private Button selectFileButton;
    private TextView selectedFilePathTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_url);

        selectFileButton = findViewById(R.id.selectFileButton);
        selectedFilePathTextView = findViewById(R.id.selectedFilePathTextView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_REQUEST_CODE);
        }
        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFile();
            }
        });
    }

    private void selectFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");

        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == READ_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            String filePath = uri.toString();
            selectedFilePathTextView.setText(filePath);

            getContentResolver().takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);

            readUrlsFromFile(filePath);
        }
    }

    private void readUrlsFromFile(String filePath) {
        try {
            Uri uri = Uri.parse(filePath);
            DocumentFile documentFile = DocumentFile.fromSingleUri(this, uri);
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                processUrl(line);
            }
            reader.close();
            inputStream.close();

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(importUrlActivity.this, "文件读取完成", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processUrl(String url) {
        // 数据库添加URL
        new importUrlActivity.DownloadTask().execute(url);
        Log.d("URL", url);
    }

    private class DownloadTask extends AsyncTask<String, Void, data> {

        @Override
        protected data doInBackground(String... urls) {
            String result = "";

            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
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
            data res = new data(urls[0],result);
            return res;
        }

        @Override
        protected void onPostExecute(data result) {
            saveData(result.url, result.content);
        }
        private void saveData(String url, String content) {
            DataDBHelper dbHelper = new DataDBHelper(importUrlActivity.this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("url", url);
            values.put("content", content);
            long newRowId = db.insert("data", null, values);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(importUrlActivity.this,"已添加："+url,Toast.LENGTH_SHORT).show();
                }
            });

            db.close();
        }
    }
}
