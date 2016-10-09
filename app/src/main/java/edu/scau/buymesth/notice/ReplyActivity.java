package edu.scau.buymesth.notice;

import android.app.Activity;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import base.BaseActivity;
import butterknife.Bind;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Evaluate;

/**
 * Created by Jammy on 2016/10/6.
 */
public class ReplyActivity extends BaseActivity {
    Evaluate evaluate;
    public static final int REPLY_SUCCESS = 10086;
    @Bind(R.id.btn_go)
    Button btnGo;
    @Bind(R.id.et)
    EditText et;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reply;
    }

    @Override
    public void initView()
    {
        evaluate = (Evaluate) getIntent().getSerializableExtra("evaluate");
        btnGo.setOnClickListener(v -> {
            if (et.getText().toString().equals("")) {
                Toast.makeText(this, "请输入评价", Toast.LENGTH_SHORT).show();
                return;
            }
            evaluate.setReply(et.getText().toString());
            showLoadingDialog();
            evaluate.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    Intent data = new Intent();
                    data.putExtra("evaluate", evaluate);
                    setResult(REPLY_SUCCESS, data);
                    closeLoadingDialog();
                    finish();
                }
            });
        });
    }

    @Override
    public boolean canSwipeBack() {
        return true;
    }

    @Override
    protected int getToolBarId() {
        return R.id.toolbar;
    }

    public static void navigateForResult(Activity activity, Evaluate evaluate) {
        Intent intent = new Intent(activity, ReplyActivity.class);
        intent.putExtra("evaluate", evaluate);
        activity.startActivityForResult(intent, REPLY_SUCCESS);
    }

    @Override
    public int getStatusColorResources() {
        return R.color.colorPrimaryDark;
    }
}
