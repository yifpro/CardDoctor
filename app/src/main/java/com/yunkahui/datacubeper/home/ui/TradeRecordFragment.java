package com.yunkahui.datacubeper.home.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hellokiki.rrorequest.SimpleCallBack;
import com.yunkahui.datacubeper.R;
import com.yunkahui.datacubeper.base.BaseFragment;
import com.yunkahui.datacubeper.common.bean.BaseBean;
import com.yunkahui.datacubeper.common.bean.RechargeRecord;
import com.yunkahui.datacubeper.common.bean.WithdrawRecord;
import com.yunkahui.datacubeper.common.utils.TimeUtils;
import com.yunkahui.datacubeper.home.adapter.RechargeRecordAdapter;
import com.yunkahui.datacubeper.home.adapter.WithdrawRecordAdapter;
import com.yunkahui.datacubeper.home.logic.TradeRecordLogic;

import java.util.ArrayList;
import java.util.List;

public class TradeRecordFragment extends BaseFragment {

    private static final String TAG = "TradeRecordFragment";
    private TradeRecordLogic mLogic;
    private RecyclerView mRecyclerView;
    private List<RechargeRecord.RechargeDetail> mRechargeDetails;
    private List<WithdrawRecord.WithdrawDetail> mWithdrawDetails;
    private BaseQuickAdapter mAdapter;
    private int mCurrentPage;
    private int mAllPages;

    public static Fragment newInstance(int kind) {
        TradeRecordFragment fragment = new TradeRecordFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("kind", kind);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void initData() {
        mLogic = new TradeRecordLogic();
        mRechargeDetails = new ArrayList<>();
        mWithdrawDetails = new ArrayList<>();
        int kind = getArguments().getInt("kind");
        switch (kind) {
            case 0:
                getData("recharge", 20, 1);
                break;
            case 1:
                getData("withdraw", 20, 1);
                break;
        }
        initRecyclerView();
    }

    public void getData(final String pdType, int pageSize, int pageNum) {
        if ("recharge".equals(pdType)) {
            mLogic.getRechargeRecord(mActivity, pdType, pageSize, pageNum, new SimpleCallBack<BaseBean<RechargeRecord>>() {
                @Override
                public void onSuccess(BaseBean<RechargeRecord> baseBean) {
                    mCurrentPage = baseBean.getRespData().getPageNum();
                    mAllPages = baseBean.getRespData().getPages();
                    mRechargeDetails.clear();
                    mRechargeDetails.addAll(baseBean.getRespData().getList());
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.e(TAG, "getRechargeRecord onFailure: " + throwable.getMessage());
                }
            });
        } else {
            mLogic.getWithdrawRecord(mActivity, pdType, pageSize, pageNum, new SimpleCallBack<BaseBean<WithdrawRecord>>() {
                @Override
                public void onSuccess(BaseBean<WithdrawRecord> baseBean) {
                    mCurrentPage = baseBean.getRespData().getPageNum();
                    mAllPages = baseBean.getRespData().getPages();
                    mWithdrawDetails.clear();
                    mWithdrawDetails.addAll(baseBean.getRespData().getList());
                    if (mAdapter != null) {
                        mAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.e(TAG, "getWithdrawRecord onFailure: " + throwable.getMessage());
                }
            });
        }
    }

    private void initRecyclerView() {
        if (0 == getArguments().getInt("kind")) {
            mAdapter = new RechargeRecordAdapter(R.layout.layout_list_item_trade_record, mRechargeDetails);
        } else {
            mAdapter = new WithdrawRecordAdapter(R.layout.layout_list_item_trade_record, mWithdrawDetails);
        }
        mAdapter.bindToRecyclerView(mRecyclerView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                String action = null, time = null, money = null, status = null;
                if (0 == getArguments().getInt("kind")) {
                    RechargeRecord.RechargeDetail detail = mRechargeDetails.get(position);
                    action = "账户充值";
                    time = TimeUtils.format("yyyy-MM-dd hh:mm:ss", detail.getCreate_time());
                    money = String.valueOf(detail.getAmount());
                    status = getRechargeStatus(detail.getOrder_state());
                } else {
                    WithdrawRecord.WithdrawDetail detail = mWithdrawDetails.get(position);
                    action = "账户提现";
                    time = TimeUtils.format("yyyy-MM-dd hh:mm:ss", detail.getCreate_time());
                    money = String.valueOf(detail.getWithdraw_amount());
                    status = getWithdrawStatus(detail.getOrder_state());
                }
                Intent intent = new Intent(mActivity, SingleRecordActivity.class)
                        .putExtra("time", time)
                        .putExtra("money", money)
                        .putExtra("status", status)
                        .putExtra("action", action);
                startActivity(intent);
            }
        });
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                if (mCurrentPage >= mAllPages) {
                    mAdapter.loadMoreEnd();
                } else {
                    if (0 == getArguments().getInt("kind")) {
                        getData("recharge", 20, 1);
                    } else {
                        getData("withdraw", 20, 1);
                    }
                }
            }
        });
        mAdapter.disableLoadMoreIfNotFullPage();
        mAdapter.setEnableLoadMore(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(mAdapter);
    }

    public String getWithdrawStatus(String state) {
        String status;
        switch (state) {
            case "0":
                status = "提现初始化";
                break;
            case "1":
                status = "提现成功";
                break;
            case "2":
                status = "提现失败";
                break;
            default:
                status = "提现处理中";
                break;
        }
        return status;
    }

    public String getRechargeStatus(String state) {
        String status;
        switch (state) {
            case "0":
                status = "充值初始化";
                break;
            case "1":
                status = "充值成功";
                break;
            case "2":
                status = "充值失败";
                break;
            default:
                status = "充值处理中";
                break;
        }
        return status;
    }

    @Override
    public void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_trade_record;
    }
}