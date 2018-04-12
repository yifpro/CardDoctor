package com.yunkahui.datacubeper.share.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.yunkahui.datacubeper.R;
import com.yunkahui.datacubeper.base.BaseActivity;
import com.yunkahui.datacubeper.base.IActivityStatusBar;

/**
 * Created by YD1 on 2018/4/10
 */
public class ProfitActivity extends AppCompatActivity implements IActivityStatusBar {

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(1,1,1,"提现").setIcon(R.mipmap.ic_withdraw_text).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 1:
                startActivity(new Intent(this, ProfitWithdrawalsActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_profit);
        super.onCreate(savedInstanceState);
        setTitle("累计分润");
    }

    @Override
    public int getStatusBarColor() {
        return getResources().getColor(R.color.colorPrimary);
    }
}
