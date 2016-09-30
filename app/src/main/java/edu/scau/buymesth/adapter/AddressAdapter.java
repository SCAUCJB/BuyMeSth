package edu.scau.buymesth.adapter;

import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.List;

import adpater.BaseQuickAdapter;
import adpater.BaseViewHolder;
import edu.scau.buymesth.R;
import edu.scau.buymesth.data.bean.Address;
import edu.scau.buymesth.user.address.editaddress.EditAddressActivity;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by John on 2016/9/30.
 */

public class AddressAdapter extends BaseQuickAdapter<Address> {
    public AddressAdapter(int layoutResId, List<Address> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Address item) {
        helper.setText(R.id.tv_recipient, item.getRecipient())
                .setText(R.id.tv_phone, item.getPhone())
                .setText(R.id.tv_address, item.getRegion() + item.getSpecific());
        helper.setOnClickListener(R.id.edit, (view) -> EditAddressActivity.navigate(mContext, item));
        helper.setOnClickListener(R.id.delete, (view) -> {
            new AlertDialog.Builder(mContext)
                    .setTitle(mContext.getResources().getString(R.string.text_delete))
                    .setMessage("delete ?")
                    .setPositiveButton("yes", (dialog, which) -> {
                        item.deleteObservable(item.getObjectId()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Void>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable throwable) {
                                Toast.makeText(mContext,"网络错误",Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onNext(Void aVoid) {
                                remove(helper.getAdapterPosition());
                            }
                        });
                    })
                    .setNegativeButton("no", null)
                    .show();

        });

    }

}
