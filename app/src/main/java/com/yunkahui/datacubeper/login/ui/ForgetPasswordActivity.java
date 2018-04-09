package com.yunkahui.datacubeper.login.ui;

import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.yunkahui.datacubeper.R;
import com.yunkahui.datacubeper.base.BaseActivity;
import com.yunkahui.datacubeper.base.IActivityStatusBar;

public class ForgetPasswordActivity extends AppCompatActivity implements IActivityStatusBar {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_forget_password);
        super.onCreate(savedInstanceState);
        setTitle("忘记密码");
    }

    @Override
    public void initView() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.anim_fragment_enter,R.anim.anim_fragment_exit)
                .replace(R.id.frame_layout,new ForgetPasswordFragment()).commit();
    }

    @Override
    public void initData() {

    }

    @Override
    public int getStatusBarColor() {
        return getResources().getColor(R.color.colorPrimary);
    }

    public void startToResetFragment(){
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new ResetPasswordFragment()).commitNowAllowingStateLoss();
    }

}
