package com.yz.dzq.ailuoku6;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView name;
    private EditText xuehao;
    private ProgressDialog progressDialog;

    String result = null;

    private void sendPost(final String uri, final String param, final String charset,final String real_name,final String xuehao) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PrintWriter out = null;
                InputStream in = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection urlcon = (HttpURLConnection) url.openConnection();
                    urlcon.setDoInput(true);
                    urlcon.setDoOutput(true);
                    urlcon.setUseCaches(false);
                    urlcon.setRequestMethod("POST");
                    urlcon.setConnectTimeout(5000);
                    urlcon.connect();// 获取连接
                    out = new PrintWriter(urlcon.getOutputStream());
                    out.print(param);
                    out.flush();
                    in = urlcon.getInputStream();
                    BufferedReader buffer = new BufferedReader(new InputStreamReader(
                            in, charset));
                    StringBuilder bs = new StringBuilder();
                    String line = null;
                    while ((line = buffer.readLine()) != null) {
                        bs.append(line);
                    }
                    result = bs.toString();

                    List<Student> sts = LitePal.where("name = ?",real_name).find(Student.class);

                    if(sts.isEmpty()){
                        Student student = new Student(real_name,xuehao);
                        student.save();
                    }

                    Intent intent = new Intent("com.yz.dzq.ailuoku6.excel");
                    intent.putExtra("data",result);
                    startActivity(intent);
                } catch (Exception e) {
                    System.out.println("[请求异常][地址：" + uri + "][参数：" + param + "][错误信息："
                            + e.getMessage() + "]");
                    showerror();
                } finally {
                    try {
                        if (null != in)
                            in.close();
                        if (null != out)
                            out.close();
                    } catch (Exception e2) {
                        System.out.println("[关闭流异常][错误信息：" + e2.getMessage() + "]");
                    }
                }

            }
        }).start();
    }

    private void showerror(){

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if ((progressDialog != null)&&(progressDialog.isShowing())){
                    progressDialog.dismiss();
                }
                Toast.makeText(MainActivity.this,"输入有误,请重新输入",Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LitePal.initialize(this);
        setContentView(R.layout.activity_main);
        Button chaxun = (Button) findViewById(R.id.chaxun);
        TextView questions = (TextView) findViewById(R.id.questions);
        name = (AutoCompleteTextView) findViewById(R.id.name);
        xuehao = (EditText) findViewById(R.id.xuehao);

        name.setText(load("name"));
        xuehao.setText(load("kaohao"));
        ArrayList<String> St_names = new ArrayList<>();

        List<Student> students = LitePal.findAll(Student.class);

        for (Student student:students){
            St_names.add(student.getName());
        }

        ArrayAdapter<String> mAdapter = new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,St_names);

        name.setAdapter(mAdapter);

        chaxun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isNetworkAvailable(MainActivity.this)){
                    progressDialog = new ProgressDialog(MainActivity.this);
                    progressDialog.setTitle("请稍候");
                    progressDialog.setMessage("查询中");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    String g_name = name.getText().toString();
                    String g_xuehao = xuehao.getText().toString();
                    String ggname = null;
                    String uri = "http://116.11.184.151:3288/cjcx%5E/list.asp";
                    try {
                        ggname = URLEncoder.encode(g_name, "gb2312");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String param = "name="+ggname+"&kaohao="+g_xuehao;
                    sendPost(uri,param, "gb2312",g_name,g_xuehao);
                }else {
                    noNetwork();
                }
            }
        });

        questions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,Questions.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isNetworkAvailable(MainActivity.this)){}else {
            noNetwork();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog != null&&progressDialog.isShowing()){
            progressDialog.dismiss();
        }
        String g_name = name.getText().toString();
        String g_xuehao = xuehao.getText().toString();
        save("name",g_name);
        save("kaohao",g_xuehao);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        name.setText(load("name"));
        xuehao.setText(load("kaohao"));
    }

    public void save(String name , String inputText){
        FileOutputStream out = null;
        BufferedWriter writer = null;
        try {
            out = openFileOutput(name, Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(inputText);
        }catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                if (writer != null){
                    writer.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public String load(String name){
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            in = openFileInput(name);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null){
                content.append(line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            if (reader != null){
                try {
                    reader.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }

    // check all network connect, WIFI or mobile
    public boolean isNetworkAvailable(final Context context) {
        boolean hasWifoCon = false;
        boolean hasMobileCon = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfos = cm.getAllNetworkInfo();
        for (NetworkInfo net : netInfos) {

            String type = net.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                if (net.isConnected()) {
                    hasWifoCon = true;
                }
            }

            if (type.equalsIgnoreCase("MOBILE")) {
                if (net.isConnected()) {
                    hasMobileCon = true;
                }
            }
        }
        return hasWifoCon || hasMobileCon;
    }

    public void noNetwork(){
        Snackbar.make(findViewById(R.id.mainLayout),"未连接网络!!!",Snackbar.LENGTH_LONG).setAction("打开设置", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Settings.ACTION_SETTINGS));
            }
        }).show();
    }

}
