package com.yunkahui.datacubeper.share.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.hellokiki.rrorequest.SimpleCallBack;
import com.yunkahui.datacubeper.R;
import com.yunkahui.datacubeper.common.bean.BaseBean;
import com.yunkahui.datacubeper.common.bean.TradeRecordSummary;
import com.yunkahui.datacubeper.common.utils.LogUtils;
import com.yunkahui.datacubeper.common.utils.RequestUtils;
import com.yunkahui.datacubeper.common.utils.TimeUtils;
import com.yunkahui.datacubeper.common.utils.ToastUtils;
import com.yunkahui.datacubeper.share.adapter.RecordMultListAdapter;
import com.yunkahui.datacubeper.share.logic.RecordListLogic;
import com.yunkahui.datacubeper.share.logic.RecordType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * 明细分组列表(新)
 */
public class RecordListNewFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private List<MultiItemEntity> mItemEntities;
    private RecordMultListAdapter mAdapter;
    private RecordListLogic mLogic;
    private RecordType mRecordType;

    private int mCurrentPage = 1;
    private int mPageSize = 20;

    private long mStartTime;
    private long mEndTime;
    private int mLostSummaryPosition;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_list_new, container, false);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mLogic = new RecordListLogic();
        initRecycler();
        update();
        return view;
    }

    private void initRecycler() {
        mItemEntities = new ArrayList<>();
        mAdapter = new RecordMultListAdapter(mItemEntities);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                loadData();
            }
        }, mRecyclerView);
        mAdapter.disableLoadMoreIfNotFullPage();
        mAdapter.setEnableLoadMore(true);
        mAdapter.setEmptyView(R.layout.layout_no_data);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void update() {
        mCurrentPage = 1;
        mLogic.update();
        mRecordType = ((RecordListActivity) getActivity()).getRecordType();
        mStartTime = ((RecordListActivity) getActivity()).getStartTime();
        mEndTime = ((RecordListActivity) getActivity()).getEndTime();
        loadData();
    }

    private void loadData() {

        switch (mRecordType) {
            case MyWallet_come:
            case online_come:
                mLogic.loadProfitIncome(getActivity(), mPageSize, mCurrentPage, mRecordType.getType(), mStartTime, mEndTime, new InnerCallBack());
                break;
            case pos_come:
                mLogic.loadPosFenRunData(getActivity(), mPageSize, mCurrentPage, mRecordType.getType(), mStartTime, mEndTime, new InnerCallBack());
                break;
            case balance_all:
                mLogic.loadTradeDetail(getActivity(), mPageSize, mCurrentPage, mRecordType.getType(), mStartTime, mEndTime, new InnerCallBack());
                break;
        }

    }


    private class InnerCallBack extends SimpleCallBack<BaseBean> {

        @Override
        public void onSuccess(BaseBean baseBean) {
            LogUtils.e("loadProfitIncome->" + baseBean.getJsonObject().toString());
            if (RequestUtils.SUCCESS.equals(baseBean.getRespCode())) {
                List<MultiItemEntity> entityList = mLogic.parsingJSONForProfitIncome(baseBean);
                int allPage = baseBean.getJsonObject().optJSONObject("respData").optInt("pages");
                mItemEntities.clear();
                mItemEntities.addAll(entityList);
                mAdapter.notifyDataSetChanged();
                mAdapter.expandAll();
                if (mCurrentPage >= allPage) {
                    mAdapter.loadMoreEnd();
                } else {
                    mAdapter.loadMoreComplete();
                }
                mCurrentPage++;

                for (int i = mItemEntities.size() - 1; i >= 0; i--) {
                    if (mItemEntities.get(i) instanceof TradeRecordSummary) {
                        mLostSummaryPosition = i;
                        loadStatisticalMoney(mItemEntities.size() > 0 ? (TradeRecordSummary) mItemEntities.get(i) : null);
                        break;
                    }
                }
            }
        }

        @Override
        public void onFailure(Throwable throwable) {
            mAdapter.loadMoreFail();
            ToastUtils.show(getActivity(), "请求失败 " + throwable.toString());
        }
    }

    //获取统计收入/支出
    private void loadStatisticalMoney(TradeRecordSummary summary) {
        if (summary == null || !TextUtils.isEmpty(summary.getBack()) || !TextUtils.isEmpty(summary.getPay())) {
            return;
        }
        mLogic.loadStatisticalMoney(getActivity(), summary.getYear(), summary.getMonth(), mRecordType.getType(), "all", new SimpleCallBack<BaseBean>() {
            @Override
            public void onSuccess(BaseBean baseBean) {
                LogUtils.e("统计收入-->" + baseBean.toString());
                if (RequestUtils.SUCCESS.equals(baseBean.getRespCode())) {
                    try {
                        JSONArray array = new JSONObject(baseBean.getJsonObject().toString()).optJSONArray("respData");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.optJSONObject(i);
                            if ("in".equals(object.optString("static_type"))) {
                                ((TradeRecordSummary) mItemEntities.get(mLostSummaryPosition)).setBack(object.optString("amount"));
                            } else {
                                ((TradeRecordSummary) mItemEntities.get(mLostSummaryPosition)).setPay(object.optString("amount"));
                            }
                        }
                        mAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Throwable throwable) {
                LogUtils.e("统计收入-->" + "请求失败 " + throwable.toString());
            }
        });
    }


}
