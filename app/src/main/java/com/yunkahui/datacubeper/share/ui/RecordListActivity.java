package com.yunkahui.datacubeper.share.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.yunkahui.datacubeper.R;
import com.yunkahui.datacubeper.base.IActivityStatusBar;
import com.yunkahui.datacubeper.common.utils.LogUtils;
import com.yunkahui.datacubeper.common.utils.TimeUtils;
import com.yunkahui.datacubeper.common.view.PlanSpinner;
import com.yunkahui.datacubeper.share.logic.RecordType;

import java.util.ArrayList;
import java.util.List;

/**
 * 明细列表  （新）
 */
public class RecordListActivity extends AppCompatActivity implements IActivityStatusBar {

    private PlanSpinner mPlanSpinnerRecordType;
    private PlanSpinner mPlanSpinnerRecordTime;
    private RecordListNewFragment mRecordListNewFragment;
    private RecordListNew2Fragment mRecordListNew2Fragment;

    private RecordType mRecordType;

    private List<RecordType> mTypes;

    private long mStartTime;
    private long mEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_record_list);
        super.onCreate(savedInstanceState);
        String title = getIntent().getStringExtra("title");
        setTitle(TextUtils.isEmpty(title) ? "明细" : title);
    }

    @Override
    public void initData() {
        mRecordType = (RecordType) getIntent().getSerializableExtra("type");
        initTypes();

        List<String> types = new ArrayList<>();
        if (mRecordType == RecordType.balance_all) {
            types.add("全部");
            types.add("充值");
            types.add("提现");
        } else {
            types.add("收入");
            types.add("提现");
        }

        List<String> times = new ArrayList<>();
        times.add("近3个月");
        times.add("近半年");
        times.add("今年");
        times.add("更早");

        mPlanSpinnerRecordType.setList(types);
        mPlanSpinnerRecordTime.setList(times);
        mRecordListNewFragment = new RecordListNewFragment();
        mRecordListNew2Fragment = new RecordListNew2Fragment();

        mStartTime = TimeUtils.getCalendarCompluteForMonth(-3);
        mEndTime = System.currentTimeMillis();

        selectPage();

        mPlanSpinnerRecordTime.setOnItemSelectListener(new PlanSpinner.OnItemSelectListener() {
            @Override
            public void onItemSelect(int position, String text) {
                switch (position) {
                    case 0:
                        mStartTime = TimeUtils.getCalendarCompluteForMonth(-3);
                        mEndTime = System.currentTimeMillis();
                        break;
                    case 1:
                        mStartTime = TimeUtils.getCalendarCompluteForMonth(-6);
                        mEndTime = System.currentTimeMillis();
                        break;
                    case 2:
                        mStartTime = TimeUtils.getThisYear();
                        mEndTime = System.currentTimeMillis();
                        break;
                    case 3:
                        mStartTime = 0;
                        mEndTime = 0;
                        break;
                }
                if (mRecordListNewFragment.isVisible()) {
                    mRecordListNewFragment.update();
                }
                if (mRecordListNew2Fragment.isVisible()) {
                    mRecordListNew2Fragment.update();
                }

            }
        });
        mPlanSpinnerRecordType.setOnItemSelectListener(new PlanSpinner.OnItemSelectListener() {
            @Override
            public void onItemSelect(int position, String text) {
                mRecordType = mTypes.get(position);
                selectPage();
                if (mRecordListNewFragment.isVisible()) {
                    mRecordListNewFragment.update();
                }
                if (mRecordListNew2Fragment.isVisible()) {
                    mRecordListNew2Fragment.update();
                }
            }
        });

    }

    public void initTypes() {
        mTypes = new ArrayList<>();
        switch (mRecordType) {
            case MyWallet_come:
                mTypes.add(RecordType.MyWallet_come);
                mTypes.add(RecordType.MyWallet_withdraw);
                break;
            case online_come:
                mTypes.add(RecordType.online_come);
                mTypes.add(RecordType.online_withdraw);
                break;
            case pos_come:
                mTypes.add(RecordType.pos_come);
                mTypes.add(RecordType.pos_withdraw);
                break;
            case balance_all:
                mTypes.add(RecordType.balance_all);
                mTypes.add(RecordType.balance_come);
                mTypes.add(RecordType.balance_withdraw);
                break;
        }
    }


    @Override
    public void initView() {
        mPlanSpinnerRecordType = findViewById(R.id.plan_spinner_record_type);
        mPlanSpinnerRecordTime = findViewById(R.id.plan_spinner_record_time);
    }

    //选择列表页面(分组或不分组)
    public void selectPage() {
        switch (mRecordType) {
            case MyWallet_come:
            case online_come:
            case pos_come:
            case balance_all:
                LogUtils.e("替换页面1");
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mRecordListNewFragment).commitNowAllowingStateLoss();
                break;
            case MyWallet_withdraw:
            case online_withdraw:
            case pos_withdraw:
            case balance_come:
            case balance_withdraw:
                LogUtils.e("替换页面2");
                getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, mRecordListNew2Fragment).commitNowAllowingStateLoss();
                break;
        }
    }

    public long getStartTime() {
        return mStartTime;
    }

    public long getEndTime() {
        return mEndTime;
    }


    public RecordType getRecordType() {
        return mRecordType;
    }

    @Override
    public int getStatusBarColor() {
        return getResources().getColor(R.color.colorPrimary);
    }
}
