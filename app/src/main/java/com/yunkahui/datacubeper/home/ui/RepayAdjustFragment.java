package com.yunkahui.datacubeper.home.ui;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.hellokiki.rrorequest.SimpleCallBack;
import com.yunkahui.datacubeper.R;
import com.yunkahui.datacubeper.base.BaseFragment;
import com.yunkahui.datacubeper.common.view.LoadingViewDialog;
import com.yunkahui.datacubeper.home.logic.ExpenseAdjustLogic;
import com.yunkahui.datacubeper.home.logic.RepayAdjustLogic;

import org.json.JSONException;
import org.json.JSONObject;

public class RepayAdjustFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "RepayAdjustFragment";
    private EditText mEditTextInputMoney;
    private RepayAdjustLogic mLogic;

    @Override
    public void initData() {
        mLogic = new RepayAdjustLogic();
    }

    @Override
    public void initView(View view) {
        mEditTextInputMoney = view.findViewById(R.id.edit_text_input_money);
        view.findViewById(R.id.button_submit).setOnClickListener(this);
        mEditTextInputMoney.setText(((AdjustPlanActivity) mActivity).getAmount());
    }

    private void submit() {
        LoadingViewDialog.getInstance().show(mActivity);
        mLogic.updatePlanningInfo(mActivity, ((AdjustPlanActivity) getActivity()).getId(), "", mEditTextInputMoney.getText().toString(), new SimpleCallBack<JsonObject>() {
            @Override
            public void onSuccess(JsonObject jsonObject) {
                LoadingViewDialog.getInstance().dismiss();
                Intent intent = new Intent()
                        .putExtra("amount", mEditTextInputMoney.getText().toString())
                        .putExtra("type", "repay");
                mActivity.setResult(Activity.RESULT_OK, intent);
                mActivity.finish();
            }

            @Override
            public void onFailure(Throwable throwable) {
                LoadingViewDialog.getInstance().dismiss();
                Log.e(TAG, "onFailure: " + throwable.getMessage());
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.button_submit:
                if (TextUtils.isEmpty(mEditTextInputMoney.getText().toString())) {
                    Toast.makeText(getActivity(), "请输入还款金额", Toast.LENGTH_SHORT).show();
                    return;
                }
                submit();
                break;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_repay_adjust;
    }
}