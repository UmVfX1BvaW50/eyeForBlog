package com.re.eyeforblog;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class delUrlActivity extends AppCompatActivity {

    private EditText urlEditText;
    private Button deleteButton;
    private DataDBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_del_url);
        urlEditText = findViewById(R.id.urlEditText);
        deleteButton = findViewById(R.id.deleteButton);
        dbHelper = new DataDBHelper(this);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = urlEditText.getText().toString();
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                int res = db.delete("data", "url=?", new String[]{url});
                if(res == 0) {
                    Toast.makeText(delUrlActivity.this, "数据库中没有该url", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(delUrlActivity.this, url + "已经删除", Toast.LENGTH_SHORT).show();
                }
                db.close();
            }
        });
    }
}