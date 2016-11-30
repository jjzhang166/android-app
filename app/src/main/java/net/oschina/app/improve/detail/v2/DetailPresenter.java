package net.oschina.app.improve.detail.v2;

import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.TextHttpResponseHandler;

import net.oschina.app.R;
import net.oschina.app.api.remote.OSChinaApi;
import net.oschina.app.improve.app.AppOperator;
import net.oschina.app.improve.bean.SubBean;
import net.oschina.app.improve.bean.base.ResultBean;

import java.lang.reflect.Type;

import cz.msebera.android.httpclient.Header;

/**
 * Created by haibin
 * on 2016/11/30.
 */

public class DetailPresenter implements DetailContract.Presenter {
    private final DetailContract.View mView;
    private final DetailContract.EmptyView mEmptyView;
    private SubBean mBean;

    public DetailPresenter(DetailContract.View mView, DetailContract.EmptyView mEmptyView, SubBean bean) {
        this.mView = mView;
        this.mBean = bean;
        this.mEmptyView = mEmptyView;
        this.mView.setPresenter(this);
    }

    @Override
    public void getDetail() {
        OSChinaApi.getDetail(mBean.getType(), mBean.getId(), new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                mView.showNetworkError(R.string.tip_network_error);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                try {
                    Type type = new TypeToken<ResultBean<SubBean>>() {
                    }.getType();
                    ResultBean<SubBean> bean = AppOperator.createGson().fromJson(responseString, type);
                    if (bean.isSuccess()) {
                        mBean = bean.getResult();
                        mView.showGetDetailSuccess(mBean);
                    } else {
                        mView.showGetDetailError(bean.getMessage());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
