package edu.scau.buymesth.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import edu.scau.buymesth.R;
import edu.scau.buymesth.user.setting.UserSettingActivity;

/**
 * Created by Jammy on 2016/8/1.
 */
public class UserFragment extends Fragment implements UserContract.View{


    UserPresenter presenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user,container,false);
        view.findViewById(R.id.btn_setting).setOnClickListener(v->{
            Intent intent=new Intent(getActivity(), UserSettingActivity.class);
            getActivity().startActivity(intent);
        });
        presenter = new UserPresenter();
        return view;
    }
}
