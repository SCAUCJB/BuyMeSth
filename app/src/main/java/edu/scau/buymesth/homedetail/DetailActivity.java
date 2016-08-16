package edu.scau.buymesth.homedetail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import base.BaseActivity;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Request;

/**
 * Created by John on 2016/8/15.
 */

public class DetailActivity extends BaseActivity {
    public static final String EXTRA_IMAGE = "edu.scau.buymesth.homedetail.extraImage";
    public static final String EXTRA_TITLE = "edu.scau.buymesth.homedetail.extraTitle";
    public static final String EXTRA_REQUEST = "edu.scau.buymesth.homedetail.request";
    public static final String EXTRA_CONTENT = "edu.scau.buymesth.homedetail.content";
    private CollapsingToolbarLayout collapsingToolbarLayout;
    public static void navigate(AppCompatActivity activity, View transitionImage, Request request) {
        Intent intent = new Intent(activity, DetailActivity.class);
        if(!request.getUrls().isEmpty())
        intent.putExtra(EXTRA_IMAGE, request.getUrls().get(0));
        intent.putExtra(EXTRA_TITLE,request.getTitle());
        intent.putExtra(EXTRA_CONTENT,request.getContent());
        intent.putExtra(EXTRA_REQUEST,request);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, transitionImage, EXTRA_IMAGE);
        ActivityCompat.startActivity(activity, intent, options.toBundle());
    }




    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    public void initView() {
        initActivityTransitions();
        ViewCompat.setTransitionName(findViewById(R.id.app_bar_layout), EXTRA_IMAGE);
        supportPostponeEnterTransition();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String itemTitle = getIntent().getStringExtra(EXTRA_TITLE);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(itemTitle);
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));

        final ImageView image = (ImageView) findViewById(R.id.image);
        if(!getIntent().getStringExtra(EXTRA_IMAGE).equals(""))
        Glide.with(this).load(getIntent().getStringExtra(EXTRA_IMAGE)).centerCrop().into(image);
        else
        image.setVisibility(View.INVISIBLE);
        TextView title = (TextView) findViewById(R.id.title);
        title.setText(getIntent().getStringExtra(EXTRA_TITLE));
        TextView content= (TextView) findViewById(R.id.description);
        content.setText(getIntent().getStringExtra(EXTRA_CONTENT));
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    private void initActivityTransitions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide transition = new Slide();
            transition.excludeTarget(android.R.id.statusBarBackground, true);
            getWindow().setEnterTransition(transition);
            getWindow().setReturnTransition(transition);
        }
    }
    private void applyPalette(Palette palette) {
        int primaryDark = getResources().getColor(R.color.primary_dark);
        int primary = getResources().getColor(R.color.primary);
        collapsingToolbarLayout.setContentScrimColor(palette.getMutedColor(primary));
        collapsingToolbarLayout.setStatusBarScrimColor(palette.getDarkMutedColor(primaryDark));
        supportStartPostponedEnterTransition();
    }
}
