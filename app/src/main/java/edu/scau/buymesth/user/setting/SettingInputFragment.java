package edu.scau.buymesth.user.setting;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import base.BaseFragment;
import edu.scau.buymesth.R;

/**
 * Created by John on 2016/9/11.
 */

public class SettingInputFragment extends BaseFragment {
    EditText mInputEt;
    OnInputCompletedListener mCallback;
    interface OnInputCompletedListener{
        void onInputCompleted(String input);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback=(OnInputCompletedListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnItemClickedListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_setting_input,container,false);
        mInputEt= (EditText) view.findViewById(R.id.et_input);
        ((UserSettingActivity)getActivity()).mSubmitBtn.setOnClickListener(v-> mCallback.onInputCompleted(mInputEt.getText().toString()));
        return view;
    }
}
