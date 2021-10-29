package com.example.edu.hhu.wangzb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;


/**
 * @className: LoginActivity
 * @description: 登录界面
 * @author: ZiboWang
 * @date: 2021/10/25
 * @version:
 **/
public class LoginActivity extends Activity {
    //绑定控件
    private Button loginButton;
    private Button registerButton;
    private EditText accountText;
    private EditText passwordText;
    private Context context = this;

    private EditText accountEditText;
    private EditText passwordEditText;

    private CheckBox rememberAccountCheckbox;
    private CheckBox rememberPasswordCheckbox;
    private boolean rememberAccountFlag;
    private boolean rememberPasswordFlag;

    private String currentAccount;

    // 负责取数据
    private SharedPreferences sp;
    // 负责保存数据
    private SharedPreferences.Editor editor;
    // 数据的分隔符
    private final String FGF = ",_,";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        init();
        addAction();


    }
    //绑定控件
    private void init(){

        loginButton = (Button)findViewById(R.id.loginButton);
        registerButton = (Button)findViewById(R.id.registerButton);
        accountText = (EditText)findViewById(R.id.accountText);
        passwordText = (EditText)findViewById(R.id.passwordText);
        accountEditText = (EditText) findViewById(R.id.accountText);
        passwordEditText = (EditText) findViewById(R.id.passwordText);

        rememberAccountCheckbox = (CheckBox)findViewById(R.id.rememberAccountCheckbox);
        rememberPasswordCheckbox = (CheckBox)findViewById(R.id.rememberPasswordCheckbox);
        rememberAccountFlag = false;
        rememberPasswordFlag = false;

        sp = getSharedPreferences("baidumap", Context.MODE_PRIVATE);
        editor = sp.edit();

        currentAccount = sp.getString("CurrentAccount",null);

        if(currentAccount!=null){

            String infoStatus = sp.getString(currentAccount+"RememberStatus",null);
            Log.i("currentAccount","currentAccount: "+currentAccount);

            String[] infoStatusArray = infoStatus.split(",");

            Log.i("status","infoStatusOut "+infoStatusArray[0]+" "+infoStatusArray[1]);
            if(infoStatusArray[0].equals("1")){
                rememberAccountFlag = true;
                accountEditText.setText(currentAccount);
                rememberAccountCheckbox.setChecked(true);

            }
            if(infoStatusArray[1].equals("1")){
                rememberPasswordFlag = true;
                String info = sp.getString(currentAccount,null);
                String[] infoArray = info.split(FGF);
                String truePassword = infoArray[0];
                passwordEditText.setText(truePassword);
                rememberPasswordCheckbox.setChecked(true);
            }
        }


    }

    private void addAction(){

        rememberAccountCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean flag) {
                rememberAccountFlag = flag;
                if(!flag){
                    rememberPasswordCheckbox.setChecked(false);
                }
            }


        });

        rememberPasswordCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean flag) {
                rememberPasswordFlag = flag;
                if(flag){
                    rememberAccountCheckbox.setChecked(true);
                    //rememberAccountFlag = true;
                }
            }

        });

        loginButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View arg0){

                String account = accountText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();
                //String showContext = "登录失败";

                if(EmptyUtil.isNullorEmpty(account)){
                    Toast.makeText(context,"账号不能为空", Toast.LENGTH_LONG).show();
                    return;
                }
                String info = sp.getString(account,null);

                if(EmptyUtil.isNullorEmpty(info)){
                    Toast.makeText(context,"账号不存在", Toast.LENGTH_LONG).show();
                    return;
                }

                if(EmptyUtil.isNullorEmpty(password)){
                    Toast.makeText(context,"密码不能为空", Toast.LENGTH_LONG).show();
                    return;
                }

                String[] infoArray = info.split(FGF);
                String truePassword = infoArray[0];
                Log.i("truePassword ",truePassword+" password "+password);
                if(!password.equals(truePassword)){
                    Toast.makeText(context,"密码错误", Toast.LENGTH_LONG).show();
                    return;
                }
                else{
                    Toast.makeText(context,"登录成功", Toast.LENGTH_LONG).show();

                    //0 0
                    if(rememberAccountFlag==false && rememberPasswordFlag==false){

                        editor.putString(account+"RememberStatus", "0,0");
                    }
                    //1 0
                    if(rememberAccountFlag==true && rememberPasswordFlag==false){
                        editor.putString(account+"RememberStatus", "1,0");
                    }
                    //1 1
                    if(rememberAccountFlag==true && rememberPasswordFlag==true){
                        editor.putString(account+"RememberStatus", "1,1");
                    }

                    editor.putString("CurrentAccount",account);
                    editor.commit();

                    Intent intent = new Intent(LoginActivity.this,BaiduMapActivity.class);
                    startActivity(intent);
                    finish();
                }
                //进入地图页面
//                if(account.equals("zhangsan")&&password.equals("123")){
//                    Toast.makeText(context,"登录成功", Toast.LENGTH_LONG).show();
//
//                }
//                else{
//
//                    Toast.makeText(context,"登录失败", Toast.LENGTH_LONG).show();
//
//                }


            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }
}
