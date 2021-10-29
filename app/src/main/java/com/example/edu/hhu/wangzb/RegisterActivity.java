package com.example.edu.hhu.wangzb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import com.example.edu.hhu.wangzb.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @className: RegisterActivity
 * @description: 注册页面
 * @author: ZiboWang
 * @date: 2021/10/26
 * @version:
 **/
public class RegisterActivity extends Activity {

    private Context context;

    private EditText nameEditText;
    private EditText accountEditText;
    private EditText passwordEditText;
    private EditText repasswordEditText;
    private RadioGroup genderRadioGroup;
    private RadioButton maleRadioButton;
    private RadioButton femaleRadioButton;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText birthdayEditText;
    private Spinner provinceSpinner;
    private TextView middleLineTextView;
    private Spinner citySpinner;
    private EditText hobbyEditText;
    private EditText introductionEditText;
    private Button registerButton;
    private Button cancelButton;
    private boolean re;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private int provinceIndex = 0;
    private int cityIndex = -1;
    private String[] hobbyArray = new String[]{"唱歌","跳舞","篮球","足球","旅游","美食","电影","游戏"};
    private boolean[] initHobbyChooseArray = new boolean[8];
    // 负责取数据
    private SharedPreferences sp;
    // 负责保存数据
    private SharedPreferences.Editor editor;
    // 数据的分隔符
    private final String FGF = ",_,";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        addAction();
    }

    // 提示信息输出
    private void showMessage(String msg){
        Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void init(){
        context = this;
        nameEditText = (EditText) findViewById(R.id.registerNameText);
        accountEditText = (EditText) findViewById(R.id.registerAccountText);
        passwordEditText = (EditText) findViewById(R.id.registerPasswordText);
        repasswordEditText = (EditText) findViewById(R.id.registerRepasswordText);
        genderRadioGroup = (RadioGroup) findViewById(R.id.registerGender);
        maleRadioButton = (RadioButton) findViewById(R.id.maleButton);
        femaleRadioButton = (RadioButton) findViewById(R.id.femaleButton);
        phoneEditText = (EditText) findViewById(R.id.registerPhoneNumber);
        emailEditText = (EditText) findViewById(R.id.registerEmail);
        birthdayEditText = (EditText) findViewById(R.id.registerBirthday);
        hobbyEditText = (EditText) findViewById(R.id.registerHobby);
        introductionEditText = (EditText) findViewById(R.id.selfIntroduction);
        registerButton = (Button) findViewById(R.id.registerregisterButton);
        cancelButton = (Button) findViewById(R.id.cancelButton);
        re = true;

        provinceSpinner = (Spinner) findViewById(R.id.provinceSpinner);
        middleLineTextView = (TextView) findViewById(R.id.middleLineTextView);
        citySpinner = (Spinner) findViewById(R.id.citySpinner);


        sp = getSharedPreferences("baidumap", Context.MODE_PRIVATE);
        editor = sp.edit();

        //为省下拉控件填充数据

        ArrayAdapter<String> provinceAdapter =
                new ArrayAdapter<String>(context,
                        android.R.layout.simple_spinner_item,
                        ProvinceCityUtil.PROVINCE_ARRAY);
        //设置下拉的效果
        provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinceSpinner.setAdapter(provinceAdapter);

        middleLineTextView.setVisibility(View.INVISIBLE);
        citySpinner.setVisibility(View.INVISIBLE);

    }

    private void addAction(){

        //弹出日期选择器对话框

        birthdayEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                int currentYear = calendar.get(Calendar.YEAR);
                int currentMonth = calendar.get(Calendar.MONTH);
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        birthdayEditText.setText(i+"-"+((i1+1)<10?"0"+(i1+1):(i1+1))+"-"+(i2<10?"0"+i2:i2));
                    }
                }, currentYear-18, currentMonth, currentDay);
                datePickerDialog.show();
            }
        });

        //省的下拉选择

        provinceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                provinceIndex = i;
                if((i>=0&&i<=3)||i==31||i==32){
                    middleLineTextView.setVisibility(View.INVISIBLE);
                    citySpinner.setVisibility(View.INVISIBLE);
                    cityIndex = -1;
                }
                else{
                    middleLineTextView.setVisibility(View.VISIBLE);
                    citySpinner.setVisibility(View.VISIBLE);

                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>(context,
                            android.R.layout.simple_spinner_item,
                            ProvinceCityUtil.CITY_ARRAY[i]);
                    //设置下拉的效果
                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(cityAdapter);
                    cityIndex = 0;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //市的下拉选择
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cityIndex = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        //弹出兴趣对话框

        hobbyEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setIcon(R.drawable.bg);
                builder.setTitle("请选择你的兴趣： ");
                builder.setMultiChoiceItems(hobbyArray, initHobbyChooseArray, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        initHobbyChooseArray[i]=b;
                    }
                });
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        StringBuilder stringBuilder = new StringBuilder();
                        for(int index=0;index<hobbyArray.length;index++){
                            if(initHobbyChooseArray[index]){
                                stringBuilder.append(hobbyArray[index]).append(",");
                            }

                        }
                        String hobby = stringBuilder.toString();
                        hobby = hobby.substring(0, hobby.length()-1);
                        hobbyEditText.setText(hobby);

                    }
                });
                builder.setNegativeButton("取消",null);
                builder.create();
                builder.show();

            }
        });

        //注册业务逻辑
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                re = true;
                //1.取得用户所填写或者选择的信息
                String name = nameEditText.getText().toString().trim();
                //用户名非空验证
                if(EmptyUtil.isNullorEmpty(name)){
                    Toast.makeText(context,"用户名不能为空", Toast.LENGTH_LONG).show();
                    re = false;
                }

                String account = accountEditText.getText().toString().trim();
                //账号非空验证
                if(EmptyUtil.isNullorEmpty(account)){
                    Toast.makeText(context,"账号不能为空", Toast.LENGTH_LONG).show();
                    re = false;
                }
                else{

                    // *这里需要做账号是否重复的验证
                    String info = sp.getString(account,null);
                    if(!EmptyUtil.isNullorEmpty(info)){
                        re = false;
                        showMessage("账号已存在");
                    }
                }

                String password = passwordEditText.getText().toString().trim();
                // 做密码非空验证
                String repassword = repasswordEditText.getText().toString().trim();
                if(EmptyUtil.isNullorEmpty(password)){
                    Toast.makeText(context,"密码不能为空", Toast.LENGTH_LONG).show();
                    re = false;
                }
                else{
                    // 这里需要做两次密码输入是否一致的验证
                    if(!password.equals(repassword)){
                        Toast.makeText(context,"两次输入密码不一致", Toast.LENGTH_LONG).show();
                        re = false;
                    }
                }

                String gender = "男";
                if(femaleRadioButton.isChecked()){
                    gender = "女";
                }
                String phone = phoneEditText.getText().toString().trim();
                // 这里需要做手机号码格式的验证
                if(!EmptyUtil.isNullorEmpty(phone)&&!RegexpUtil.isMobileNum(phone)){
                    re = false;
                    Toast.makeText(context,"手机号码格式无效", Toast.LENGTH_LONG).show();
                }

                String email = emailEditText.getText().toString().trim();
                // 这里需要做Email格式的验证
                if(!EmptyUtil.isNullorEmpty(email)&&!RegexpUtil.isValidEmail(email)){
                    re = false;
                    Toast.makeText(context,"邮箱号码格式无效", Toast.LENGTH_LONG).show();
                }

                String birthday = birthdayEditText.getText().toString().trim();
                Date today = new Date();
                Log.i("birthday","birthday: "+birthday);
                // 这里需要做日期的验证
                if(!EmptyUtil.isNullorEmpty(birthday)){
                    String[] birthdayArray = birthday.split("-");

                    Date date = new Date(Integer.parseInt(birthdayArray[0])-1900,
                            Integer.parseInt(birthdayArray[1])-1,
                            Integer.parseInt(birthdayArray[2]));
                    if(date.after(today)){
                        re = false;
                        Toast.makeText(context,"日期无效", Toast.LENGTH_LONG).show();
                    }

                    Log.i("date","date: "+birthdayArray[0]+"-"+birthdayArray[1]+"-"+birthdayArray[2]);

                }
                else{
                    birthday = (today.getYear()+1900)+"-"+(today.getMonth()+1)+"-"+today.getDate();
                    //birthday = today.toString();
                    Log.i("birthday","birthday: "+birthday);

                }

                //Log.i("省市下标","provinceIndex: "+provinceIndex+" cityIndex: "+cityIndex);
                String birthPlace = ProvinceCityUtil.PROVINCE_ARRAY[provinceIndex];
                if(cityIndex!=-1){
                    birthPlace += "-"+ProvinceCityUtil.CITY_ARRAY[provinceIndex][cityIndex];
                }
                String hobby = hobbyEditText.getText().toString().trim();
                String introduction = introductionEditText.getText().toString().trim();
                //2.显示一下信息
                String msg = "姓名："+name+"\n"+
                        "密码："+password+"\n"+
                        "重复密码："+repassword+"\n"+
                        "性别："+gender+"\n"+
                        "手机："+phone+"\n"+
                        "邮箱："+email+"\n"+
                        "生日："+birthday+"\n"+
                        "籍贯："+birthPlace+"\n"+
                        "兴趣："+hobby+"\n"+
                        "自我介绍："+introduction;
                Log.i("用户的信息",msg);
				/*
				showMessage("姓名："+name+"\n"+
						"密码："+password+"\n"+
						"重复密码："+repassword+"\n"+
						"性别："+gender+"\n"+
						"手机："+phone+"\n"+
						"邮箱："+email+"\n"+
						"生日："+birthday+"\n"+
						"籍贯："+birthPlace+"\n"+
						"兴趣："+hobby+"\n"+
						"自我介绍："+introduction);
				*/
                //3.将输入保存起来
                if(re){
                    String key = account;
                    String value = password+FGF+name+FGF+gender+FGF+phone+FGF+email+FGF+birthday+FGF+birthPlace+FGF+hobby+FGF+introduction;
                    editor.putString(key, value);
                    editor.putString(key+"RememberStatus", "0,0");
                    editor.commit();
                    showMessage("注册成功!");
                    //4.将页面跳转到登录页面
                    Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
