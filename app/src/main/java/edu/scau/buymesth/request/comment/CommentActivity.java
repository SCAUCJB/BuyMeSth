package edu.scau.buymesth.request.comment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import base.BaseActivity;
import butterknife.Bind;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Comment;
import edu.scau.buymesth.data.bean.Request;
import edu.scau.buymesth.data.bean.User;

/**
 * Created by John on 2016/9/6.
 */

public class CommentActivity extends BaseActivity {
    @Bind(R.id.btn_submit)
    Button mSubmitBtn;
    @Bind(R.id.et_comment)
    EditText mCommentEt;
    Request mRequest;
    private ProgressDialog mDialog;

    public static void navigateTo(Context context,Request request){
        Intent intent = new Intent(context, CommentActivity.class);
        intent.putExtra(Constant.EXTRA_REQUEST, request);
        context.startActivity(intent);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_comment;
    }

    @Override
    protected void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(getToolBarId());
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener((v) -> onBackPressed());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    protected int getToolBarId() {
        return R.id.tb_comment;
    }

    @Override
    public void initView() {
        mRequest=(Request) getIntent().getSerializableExtra(Constant.EXTRA_REQUEST);
    }

    @Override
    protected void setListener() {
        mSubmitBtn.setOnClickListener(v->{submit();});
    }

    private void submit() {
        Comment comment=new Comment();
        showLoadingDialog("正在提交...");
        comment.setAuthor(BmobUser.getCurrentUser(User.class));
        comment.setContent(mCommentEt.getText().toString());
        comment.setRequest(mRequest);
        comment.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                closeLoadingDialog();
                finish();
            }
        });
    }
    public void showLoadingDialog(String msg) {
        if (mDialog == null) {
            mDialog = new ProgressDialog(mContext);
            mDialog.setCancelable(false);
        }
        mDialog.setMessage(msg);
        mDialog.show();
    }

    public void closeLoadingDialog() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }
    @Override
    public boolean canSwipeBack() {
        return true;
    }
}
