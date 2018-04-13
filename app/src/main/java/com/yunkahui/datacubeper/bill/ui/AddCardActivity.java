package com.yunkahui.datacubeper.bill.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.JsonObject;
import com.hellokiki.rrorequest.SimpleCallBack;
import com.yunkahui.datacubeper.R;
import com.yunkahui.datacubeper.base.IActivityStatusBar;
import com.yunkahui.datacubeper.bill.adapter.SelectDateAdapter;
import com.yunkahui.datacubeper.bill.logic.AddCardLogic;
import com.yunkahui.datacubeper.common.utils.CustomTextChangeListener;
import com.yunkahui.datacubeper.common.utils.DataUtils;
import com.yunkahui.datacubeper.common.view.InfoFillView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AddCardActivity extends AppCompatActivity implements IActivityStatusBar {

    private static final String TAG = "AddCardActivity";
    private InfoFillView mInfoFillName;
    private InfoFillView mInfoFillCardNum;
    private InfoFillView mInfoFillBankName;
    private InfoFillView mInfoFillBill;
    private InfoFillView mInfoFillRepay;
    private AddCardLogic mLogic;
    private Button mBtnCommit;
    private String mBankNameEn;
    private int mBillDay;
    private int mRepayDay;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_add_card);
        super.onCreate(savedInstanceState);
        setTitle("添加卡片");
    }

    @Override
    public void initData() {
        mLogic = new AddCardLogic();
        setOnClickListener();
        mInfoFillName.setName(DataUtils.getInfo().getParent_name());
        mInfoFillCardNum.setCursorVisible(false);
        mInfoFillCardNum.setOnEditTextTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (MotionEvent.ACTION_DOWN == event.getAction()) {
                    mInfoFillCardNum.setCursorVisible(true);// 再次点击显示光标
                }
                return false;
            }
        });
        mInfoFillCardNum.addTextChangeListener(new CustomTextChangeListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 10) {
                    mLogic.queryBankByCardId(AddCardActivity.this, s.toString(), new SimpleCallBack<JsonObject>() {
                        @Override
                        public void onSuccess(JsonObject jsonObject) {
                            try {
                                JSONObject object = new JSONObject(jsonObject.toString());
                                String respCode = object.optString("respCode");
                                if ("0000".equals(respCode)) {
                                    JSONObject respData = object.optJSONObject("respData");
                                    mInfoFillBankName.setDest(respData.optString("bankName"));
                                    mBankNameEn = respData.optString("bankNameEn");
                                    Log.e(TAG, "onSuccess: " + mBankNameEn + ", " + jsonObject.toString());
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(Throwable throwable) {
                            Log.e(TAG, "onFailure: " + throwable.getMessage());
                        }
                    });
                }
            }
        });
    }

    @Override
    public void initView() {
        mInfoFillName = findViewById(R.id.info_fill_name);
        mInfoFillCardNum = findViewById(R.id.info_fill_card_id);
        mInfoFillBankName = findViewById(R.id.info_fill_bank);
        mInfoFillBill = findViewById(R.id.info_fill_bill);
        mInfoFillRepay = findViewById(R.id.info_fill_repay);
        mBtnCommit = findViewById(R.id.btn_commit);
    }

    private void setOnClickListener() {
        mInfoFillBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateSelector(true);
            }
        });
        mInfoFillRepay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateSelector(false);
            }
        });
        mBtnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(mBankNameEn)) {
                    mLogic.addBankCard(AddCardActivity.this, mInfoFillCardNum.getCardNum(), mInfoFillBankName.getDest(), mBankNameEn,
                            mInfoFillName.getName(), mBillDay, mRepayDay, new SimpleCallBack<JsonObject>() {
                                @Override
                                public void onSuccess(JsonObject jsonObject) {
                                    Log.e(TAG, "onSuccess: " + jsonObject.toString());
                                    try {
                                        JSONObject object = new JSONObject(jsonObject.toString());
                                        String respCode = object.optString("respCode");
                                        if ("0023".equals(respCode)) {
                                            Toast.makeText(AddCardActivity.this, object.optString("respDesc"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }

                                @Override
                                public void onFailure(Throwable throwable) {
                                    Log.e(TAG, "onFailure: " + throwable.getMessage());
                                }
                            });
                } else {
                    Toast.makeText(AddCardActivity.this, "未能获取到对应银行卡信息", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getStatusBarColor() {
        return getResources().getColor(R.color.colorPrimary);
    }

    private void showDateSelector(final boolean isBill) {
        RecyclerView recyclerView = new RecyclerView(this);
        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i < 31; i++) {
            list.add(String.valueOf(i));
        }
        SelectDateAdapter selectDateAdapter = new SelectDateAdapter(R.layout.layout_list_item_select_date, list);
        selectDateAdapter.bindToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 7));
        recyclerView.setAdapter(selectDateAdapter);
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_logo)
                .setTitle(String.format("请选择%s日", isBill ? "账单" : "还款"))
                .setView(recyclerView)
                .show();
        selectDateAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                if (isBill) {
                    mBillDay = position + 1;
                    mInfoFillBill.setDest(String.format(getString(R.string.day_sel_format), position + 1));
                } else {
                    mRepayDay = position + 1;
                    mInfoFillRepay.setDest(String.format(getString(R.string.day_sel_format), position + 1));
                }
                dialog.dismiss();
            }
        });
    }
}
