package net.oschina.app.improve.tweet.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import net.oschina.app.improve.base.activities.BaseActivity;
import net.oschina.app.improve.media.SelectImageActivity;
import net.oschina.app.improve.media.config.ImageConfig;
import net.oschina.app.improve.media.config.SelectedCallBack;
import net.oschina.app.improve.tweet.adapter.TweetSelectImageAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by JuQiu
 * on 16/7/18.
 * <p>
 * 动弹发布界面, 图片预览器
 * <p>
 * 提供图片预览/图片操作 返回选中图片等功能
 */

public class TweetPicturesPreviewer extends RecyclerView implements TweetSelectImageAdapter.Callback, SelectedCallBack {
    private TweetSelectImageAdapter mImageAdapter;
    private ItemTouchHelper mItemTouchHelper;
    private RequestManager mCurImageLoader;

    public TweetPicturesPreviewer(Context context) {
        super(context);
        init();
    }

    public TweetPicturesPreviewer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TweetPicturesPreviewer(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        mImageAdapter = new TweetSelectImageAdapter(this);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 3);
        this.setLayoutManager(layoutManager);
        this.setAdapter(mImageAdapter);
        this.setOverScrollMode(View.OVER_SCROLL_NEVER);

        ItemTouchHelper.Callback callback = new TweetPicturesPreviewerItemTouchCallback(mImageAdapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(this);
    }

    public void set(List<String> paths) {
        mImageAdapter.clear();
        for (String path : paths) {
            mImageAdapter.add(path);
        }
        mImageAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadMoreClick() {
        ImageConfig config = ImageConfig.Build();
        config.selectMode(ImageConfig.SelectMode.MULTI_MODE);
        config.selectCount(9);
        config.mediaMode(ImageConfig.MediaMode.HAVE_CAM_MODE);
        config.selectedImages(mImageAdapter.getPaths());
        config.callBack(this);
        SelectImageActivity.showImage(getContext(), config);

    }

    @Override
    public RequestManager getImgLoader() {
        if (mCurImageLoader == null) {
            Context context = getContext();
            if (context != null && context instanceof BaseActivity) {
                mCurImageLoader = ((BaseActivity) context).getImageLoader();
            } else {
                mCurImageLoader = Glide.with(getContext());
            }
        }
        return mCurImageLoader;
    }

    @Override
    public void onStartDrag(ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public ArrayList<String> getPaths() {
        return mImageAdapter.getPaths();
    }

    public void destroy() {
        Context context = getContext();
        if (!(context != null && context instanceof BaseActivity)) {
            mCurImageLoader.onDestroy();
        }
        mCurImageLoader = null;
    }

    @Override
    public void doBack(ArrayList<String> images) {
        set(images);
    }
}
