package com.example.downloader;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    int flag = 0;
    TextView percent;
    File src = new File(Environment.getExternalStorageDirectory(), "des");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button sign = findViewById(R.id.sign);
        Button begin = findViewById(R.id.begin);
        Button end = findViewById(R.id.end);
        percent = findViewById(R.id.percent);

        sign.setOnClickListener(this);
        begin.setOnClickListener(this);
        end.setOnClickListener(this);
    }


    private void beginDownload() {
        flag = 0;
        new Thread(new Runnable() {
            @Override
            public void run() {
                String url = "https://qd.myapp.com/myapp/qqteam/pcqq/PCQQ2019.exe";
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder().url(url).build();
                Response response = null;
                try {
                    response = client.newCall(request).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] read = new byte[1024 * 3];
                int i = 0;
                int temp = 0;
                final long length = response.body().contentLength();
                InputStream is = response.body().byteStream();
                try {
                    FileOutputStream os = new FileOutputStream(src);
                    if (src.exists()) src.delete();
                    src.createNewFile();
                    while ((i = is.read(read)) != -1) {
                        if (flag == 1) {
                            Thread.currentThread().stop();
                            break;
                        }
                        os.write(read, 0, i);
                        temp = temp + i;//每次度的量叠加等于临时文件总量
                        final long finalLength = temp;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long total = finalLength * 100 / length;
                                if (total==100) percent.setText("下载完成");
                                percent.setText("正在下载" + "\n" + "已完成" + total + "%");
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.begin:
                beginDownload();
                break;
            case R.id.sign:
                Toast.makeText(this, "这是一个下载器", Toast.LENGTH_SHORT).show();
                break;
            case R.id.end:
                try {
                    flag = 1;
                    if (src.exists()) src.delete();
                    percent.setText("已取消,已删除下载文件");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}