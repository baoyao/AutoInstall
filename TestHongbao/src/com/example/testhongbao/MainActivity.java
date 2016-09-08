package com.example.testhongbao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;

public class MainActivity extends Activity {

    private LinearLayout mStepLayout1,mStepLayout2,mStepLayout3;
    private CheckBox mCheckBox;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStepLayout1=(LinearLayout)findViewById(R.id.step_layout1);
        mStepLayout2=(LinearLayout)findViewById(R.id.step_layout2);
        mStepLayout3=(LinearLayout)findViewById(R.id.step_layout3);
        mCheckBox=(CheckBox) findViewById(R.id.command_support);
        
        mCheckBox.setChecked(PublicUtils.getConfiguration(this));
        mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // TODO Auto-generated method stub
                PublicUtils.saveConfiguration(MainActivity.this,isChecked);
            }
        });
    }

    
    public void onAllowButtonClick(View view){
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivity(intent);
    }
    
    public void onStepButtonClick(View view){
        switch(view.getId()){
        case R.id.step_btn1:
            if(mStepLayout1.getVisibility()==View.GONE){
                mStepLayout1.setVisibility(View.VISIBLE);
            }else{
                mStepLayout1.setVisibility(View.GONE);
            }
            break;
        case R.id.step_btn2:
            if(mStepLayout2.getVisibility()==View.GONE){
                mStepLayout2.setVisibility(View.VISIBLE);
            }else{
                mStepLayout2.setVisibility(View.GONE);
            }
            break;
        case R.id.step_btn3:
            if(mStepLayout3.getVisibility()==View.GONE){
                mStepLayout3.setVisibility(View.VISIBLE);
            }else{
                mStepLayout3.setVisibility(View.GONE);
            }
            break;
            default:
                break;
        }
    }
}
