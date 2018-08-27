package com.zd112.framework;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zd112.framework.listener.CusOnClickListener;
import com.zd112.framework.net.annotation.RequestStatus;
import com.zd112.framework.net.annotation.RequestType;
import com.zd112.framework.net.callback.Callback;
import com.zd112.framework.net.callback.ProgressCallback;
import com.zd112.framework.net.helper.NetInfo;
import com.zd112.framework.utils.DateUtils;
import com.zd112.framework.utils.LogUtils;
import com.zd112.framework.utils.SystemUtils;
import com.zd112.framework.utils.ViewUtils;
import com.zd112.framework.view.DialogView;
import com.zd112.framework.view.NavigationView;
import com.zd112.framework.view.refresh.RefreshView;
import com.zd112.framework.view.refresh.interfaces.OnLoadMoreListener;
import com.zd112.framework.view.refresh.interfaces.OnRefreshListener;
import com.zd112.framework.view.refresh.interfaces.RefreshLayout;

import java.util.HashMap;

public abstract class BaseActivity extends AppCompatActivity implements CusOnClickListener, Callback, OnRefreshListener, OnLoadMoreListener {

    protected FragmentManager mFragmentManager;
    protected FragmentTransaction mFragmentTransaction;
    protected View mView;
    public NavigationView mNavigationBar;
    protected RefreshView mRefreshView;
    private DateUtils mDateUtils;
    private boolean mIsLoadMore;
    private NetInfo.Builder mBuilder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFragmentManager = getSupportFragmentManager();
        mNavigationBar = new NavigationView(this);
        if (mView == null) {
            initView(savedInstanceState);
            setListener();
//            processLogic(savedInstanceState);
        } else {
            ViewGroup parent = (ViewGroup) mView.getParent();
            if (parent != null) {
                parent.removeView(mView);
            }
        }
        processLogic(savedInstanceState);
    }

    protected void setTitle(int leftResId, String name, int resId, String title) {
        if (mView == null) return;
        mView.findViewById(leftResId).setOnClickListener(this);
        ((TextView) mView.findViewById(resId)).setText(title);
    }

    protected RefreshView setView(@LayoutRes int layout, Object object) {
        setView(layout, object, false);
        return mRefreshView;
    }

    protected RefreshView setView(@LayoutRes int layout, Object object, boolean isRefresh) {
        setView(LayoutInflater.from(this).inflate(layout, null), object, isRefresh);
        return mRefreshView;
    }

    protected RefreshView setView(View view, Object object) {
        setView(view, object, false);
        return mRefreshView;
    }

    protected void setView(View view, Object object, boolean isRefresh) {
        mRefreshView = new RefreshView(this);
        if (isRefresh) {
            mRefreshView.setOnRefreshListener(this);
            mRefreshView.setOnLoadMoreListener(this);
            mRefreshView.addView(view);
            mView = mRefreshView;
        } else {
            mView = view;
        }
        setContentView(mView);
        ViewUtils.inject(object, mView);
        SystemUtils.setNoStatusBarFullMode(this, false);
    }

    public void setEnableMore(int resultSize) {
        if (mRefreshView == null) return;
//        mRefreshView.setNoMoreData(mDefaultPageSize > resultSize);
    }

    public int getResColor(int resColor) {
        return getResources().getColor(resColor);
    }

    public String[] getResArrStr(int resArrStr) {
        return getResources().getStringArray(resArrStr);
    }

    public void push(Fragment fragment) {
        push(fragment, null);
    }

    public void push(Fragment fragment, Bundle bundle) {
        fragment.setArguments(bundle);
        mFragmentTransaction = mFragmentManager.beginTransaction();
        mFragmentTransaction.replace(NavigationView.MAIN, fragment);
        mFragmentTransaction.commit();
    }

    public DialogView loading(String msg) {
        return BaseApplication.mApplication.loading(this, msg);
    }

    public DialogView loading(int layout) {
        return BaseApplication.mApplication.loading(this, layout);
    }

    public DialogView loading(int layout, DialogView.DialogViewListener dialogViewListener) {
        return BaseApplication.mApplication.loading(this, layout, dialogViewListener);
    }

    public void request(String action) {
        this.request(action, null, this, null, true);
    }

    public void request(String action, Class _class) {
        this.request(action, null, this, _class, true);
    }

    public void request(String action, Callback callback) {
        this.request(action, null, callback, null, true);
    }

    public void request(String action, Callback callback, Class _class) {
        this.request(action, null, callback, _class, true);
    }

    public void request(String action, boolean isLoading) {
        this.request(action, null, this, null, isLoading);
    }

    public void request(String action, Class _class, boolean isLoading) {
        this.request(action, null, this, _class, isLoading);
    }

    public void request(String action, Callback callback, boolean isLoading) {
        this.request(action, null, callback, null, isLoading);
    }

    public void request(String action, Callback callback, Class _class, boolean isLoading) {
        this.request(action, null, callback, _class, isLoading);
    }

    public void request(String action, HashMap<String, String> params) {
        this.request(action, params, this, null, true);
    }

    public void request(String action, HashMap<String, String> params, Class _class) {
        this.request(action, params, this, _class, true);
    }

    public void request(String action, HashMap<String, String> params, Callback callback) {
        this.request(action, params, callback, null, true);
    }

    public void request(String action, HashMap<String, String> params, Callback callback, Class _class) {
        this.request(action, params, callback, _class, true);
    }

    public void request(String action, HashMap<String, String> params, boolean isLoading) {
        this.request(action, params, this, null, isLoading);
    }

    public void request(String action, HashMap<String, String> params, Class _class, boolean isLoading) {
        this.request(action, params, this, _class, isLoading);
    }

    public void request(String action, HashMap<String, String> params, Callback callback, Class _class, boolean isLoading) {
        this.request(RequestType.GET, action, params, callback, _class, isLoading);
    }

    public void request(int requestType, String action, HashMap<String, String> params, Callback callback, Class _class, boolean isLoading) {
        BaseApplication.mApplication.request(this, BaseApplication.mApplication.build(requestType, action, params, _class), callback, isLoading);
    }

    public void download(String url, ProgressCallback progressCallback) {
        this.download(url, progressCallback,null);
    }

    public void download(String url, ProgressCallback progressCallback, Object tag) {
        this.download(url,null, progressCallback, tag);
    }

    public void download(String url, String saveFileName, ProgressCallback progressCallback, Object tag) {
        BaseApplication.mApplication.request(BaseApplication.mApplication.build(url, saveFileName, progressCallback), tag);
    }

    @Override
    public void onSuccess(NetInfo info) {
        cancelRefresh();
        mBuilder = info.getBuild();
        LogUtils.e("---------mBuilder:",mBuilder);
    }

    @Override
    public void onFailure(NetInfo info) {
        cancelRefresh();
        mBuilder = info.getBuild();
    }

    private void cancelRefresh() {
        if (mRefreshView != null) {
            mRefreshView.finishRefresh();
            mRefreshView.finishLoadMore();
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (mRefreshView != null) {
            mRefreshView.setNoMoreData(false);
        }
        BaseApplication.mApplication.mNetBuilder.build().doAsync(mBuilder.setStatus(RequestStatus.REFRESH).build(), this);
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        mIsLoadMore = true;
        BaseApplication.mApplication.mNetBuilder.build().doAsync(mBuilder.setStatus(RequestStatus.MORE).build(), this);
    }

    protected void scrollTxt(TextView textView, String txt) {
        textView.setText(TextUtils.isEmpty(txt) ? "" : txt);
        textView.setEllipsize(TextUtils.TruncateAt.MARQUEE);
        textView.setSingleLine(true);
        textView.setSelected(true);
        textView.setFocusable(true);
        textView.setFocusableInTouchMode(true);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public void onClick(View v, Bundle bundle) {
    }

    public DateUtils countDown(long second) {
        return countDown(second * 1000, 1000);
    }

    protected DateUtils countDown(long millisInFuture, long countDownInterval) {
        cancelTime();
        return mDateUtils = new DateUtils(millisInFuture, countDownInterval);
    }

    protected void cancelTime() {
        if (mDateUtils != null) {
            mDateUtils.cancel();
            mDateUtils = null;
        }
    }

    protected abstract void initView(Bundle savedInstanceState);

    protected void setListener() {
    }

    protected abstract void processLogic(Bundle savedInstanceState);

    @Override
    protected void onDestroy() {
        cancelTime();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        new DialogView(this, R.style.DialogTheme).setOutsideClose(false).setListener(new DialogView.DialogOnClickListener() {
            @Override
            public void onDialogClick(boolean isCancel) {
                if (!isCancel) {
                    BaseActivity.super.onBackPressed();
                }
            }
        }).show();
    }
}
