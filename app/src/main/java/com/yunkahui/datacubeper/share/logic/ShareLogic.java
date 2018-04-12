package com.yunkahui.datacubeper.share.logic;

import android.content.Context;

import com.google.gson.JsonObject;
import com.hellokiki.rrorequest.HttpManager;
import com.hellokiki.rrorequest.SimpleCallBack;
import com.yunkahui.datacubeper.common.api.ApiService;
import com.yunkahui.datacubeper.common.utils.RequestUtils;

import java.util.Map;

/**
 * Created by YD1 on 2018/4/11
 */
public class ShareLogic {

    public void getSharePageInfo(Context context, SimpleCallBack<JsonObject> callBack){
        HttpManager.getInstance().create(ApiService.class)
                .requestSharePageInfo(RequestUtils.newParams(context).create())
                .compose(HttpManager.<JsonObject>applySchedulers())
                .subscribe(callBack);
    }

    public void createActivationCode(Context context, SimpleCallBack<JsonObject> callBack){
        HttpManager.getInstance().create(ApiService.class)
                .produceActivationCode(RequestUtils.newParams(context).create())
                .compose(HttpManager.<JsonObject>applySchedulers())
                .subscribe(callBack);
    }
}
