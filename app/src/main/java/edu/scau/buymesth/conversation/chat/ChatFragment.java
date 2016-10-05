package edu.scau.buymesth.conversation.chat;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import base.util.ToastUtil;
import cn.bmob.newim.BmobIM;
import cn.bmob.newim.bean.BmobIMConversation;
import cn.bmob.newim.bean.BmobIMMessage;
import cn.bmob.newim.event.MessageEvent;
import cn.bmob.newim.listener.MessageListHandler;
import cn.bmob.v3.exception.BmobException;
import edu.scau.Constant;
import edu.scau.buymesth.R;
import edu.scau.buymesth.discover.publish.SelectActivity;
import edu.scau.buymesth.fragment.BackPressHandle;
import edu.scau.buymesth.util.CompressHelper;
import edu.scau.buymesth.util.InputMethodHelper;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.StoreHouseHeader;
import me.iwf.photopicker.PhotoPicker;
import util.DensityUtil;
import util.FileUtils;

/**
 * Created by ！ on 2016/9/18.
 */
public class ChatFragment extends Fragment implements ChatContract.View,MessageListHandler,View.OnClickListener,BackPressHandle {
    private RecyclerView mRecyclerView;
    private ChatAdapter mChatAdapter;
    private ChatPresenter mPresenter;
    private PtrFrameLayout mPtrFrameLayout;
    private Button mButtonSend;
    private EditText mInputMessage;
    private View rootView;
    private RecyclerView mFaceRecyclerView;
    private FaceAdapter mFaceAdapter;
    private boolean mFaceRvClosed = true;

    private String mPhotoToSend;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_chat_n,container,false);
        mRecyclerView= (RecyclerView) rootView.findViewById(R.id.rv_discover_fragment);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(layoutManager);
        //初始化代理人
        mPresenter=new ChatPresenter(getActivity().getBaseContext());
        BmobIMConversation bmobIMConversation = (BmobIMConversation) getArguments().getSerializable(BmobIMConversation.class.getName());
        mPresenter.setVM(this,new ChatModel(bmobIMConversation));
        initAdapter();
        initStoreHouse(rootView);
        initInput(rootView);
        initInputToolBar(rootView);
        initFaceRecyclerView();
        getActivity().setTitle(((BmobIMConversation) getArguments().getSerializable(BmobIMConversation.class.getName()))
                        .getConversationTitle());
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void initInput(View view){
        mButtonSend = (Button) view.findViewById(R.id.bt_send_message);
        mInputMessage = (EditText)view.findViewById(R.id.input_message);

        mButtonSend.setOnClickListener(this);
        mInputMessage.setOnFocusChangeListener((v, hasFocus) -> onFaceButtonClick(hasFocus));
        mInputMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String input = mInputMessage.getText().toString();
                if(input.trim().length()>0) mButtonSend.setEnabled(true);
                else mButtonSend.setEnabled(false);
            }
        });
    }

    private void initInputToolBar(View view){
        view.findViewById(R.id.iv_photo).setOnClickListener(this);
        view.findViewById(R.id.iv_file).setOnClickListener(this);
        view.findViewById(R.id.iv_face).setOnClickListener(this);
        view.findViewById(R.id.iv_location).setOnClickListener(this);
        view.findViewById(R.id.iv_picture).setOnClickListener(this);
    }

    private void initAdapter(){
        mChatAdapter = new ChatAdapter(getActivity(),mPresenter.mModel.getDatas());
        mRecyclerView.setAdapter(mChatAdapter);
        ((SimpleItemAnimator)mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false);
        mChatAdapter.setOnRecyclerViewItemClickListener((view, position) -> {
            if(mPtrFrameLayout.isRefreshing()) return;
            BmobIMMessage msg = mChatAdapter.getItem(position);
            if(msg.getSendStatus()==3){
                new AlertDialog.Builder(getActivity())
                        .setMessage("是否重发该消息？")
                        .setPositiveButton("是", (dialog, which) -> mPresenter.resendMessage(msg))
                        .setNegativeButton("否",null);
            }
        });
    }

    private void initStoreHouse(View view) {
        mPtrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.store_house_ptr_frame);
        final StoreHouseHeader header = new StoreHouseHeader(getActivity());
        header.setPadding(0, 80, 0,50);
        header.initWithString("Buy Me Sth");
        header.setTextColor(Color.BLACK);
        mPtrFrameLayout.setDurationToCloseHeader(1500);
        mPtrFrameLayout.setHeaderView(header);
        mPtrFrameLayout.addPtrUIHandler(header);
        mPtrFrameLayout.post(() -> mPresenter.loadMore());
        mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return  !mRecyclerView.canScrollVertically(-1);
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                mPresenter.loadMore();
            }
        });
        //
    }

    private void initFaceRecyclerView(){
        mFaceRecyclerView= (RecyclerView) rootView.findViewById(R.id.rv_face);
        mFaceRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(4,
                StaggeredGridLayoutManager.HORIZONTAL));
        String fff = "☺\n" +
                "\uD83D\uDE01\n" +
                "\uD83D\uDE04\n" +
                "\uD83D\uDE07\n" +
                "\uD83D\uDE2F\n" +
                "\uD83D\uDE15\n" +
                "\uD83D\uDE0A\n" +
                "\uD83D\uDE02\n" +
                "\uD83D\uDE05\n" +
                "\uD83D\uDE08\n" +
                "\uD83D\uDE10\n" +
                "\uD83D\uDE20\n" +
                "\uD83D\uDE00\n" +
                "\uD83D\uDE03\n" +
                "\uD83D\uDE06\n" +
                "\uD83D\uDE09\n" +
                "\uD83D\uDE11\n" +
                "\uD83D\uDE2C\n" +
                "\uD83D\uDE21\n" +
                "\uD83D\uDE2E\n" +
                "\uD83D\uDE25\n" +
                "\uD83D\uDE28\n" +
                "\uD83D\uDE1F\n" +
                "\uD83D\uDE33\n" +
                "\uD83D\uDE22\n" +
                "\uD83D\uDE23\n" +
                "\uD83D\uDE26\n" +
                "\uD83D\uDE29\n" +
                "\uD83D\uDE31\n" +
                "\uD83D\uDE35\n" +
                "\uD83D\uDE34\n" +
                "\uD83D\uDE24\n" +
                "\uD83D\uDE27\n" +
                "\uD83D\uDE30\n" +
                "\uD83D\uDE32\n" +
                "\uD83D\uDE36\n" +
                "\uD83D\uDE37\n" +
                "\uD83D\uDE0D\n" +
                "\uD83D\uDE1D\n" +
                "\uD83D\uDE19\n" +
                "\uD83D\uDE0E\n" +
                "\uD83D\uDE16\n" +
                "\uD83D\uDE1E\n" +
                "\uD83D\uDE1B\n" +
                "\uD83D\uDE0B\n" +
                "\uD83D\uDE18\n" +
                "\uD83D\uDE2D\n" +
                "\uD83D\uDE14\n" +
                "\uD83D\uDE12\n" +
                "\uD83D\uDE1C\n" +
                "\uD83D\uDE17\n" +
                "\uD83D\uDE1A\n" +
                "\uD83D\uDE0C\n" +
                "\uD83D\uDE2A\n" +
                "\uD83D\uDE0F";
        List<String> faces = Arrays.asList(fff.split("\n"));
        mFaceAdapter = new FaceAdapter(faces);
        mFaceAdapter.setOnRecyclerViewItemClickListener((view, position) -> mInputMessage.append(mFaceAdapter.getItem(position)));
        mFaceRecyclerView.setAdapter(mFaceAdapter);
    }

    @Override
    public void onLoadMoreSuccess(List<BmobIMMessage> list) {

    }

    @Override
    public void onError(Throwable throwable, String msg) {

    }

    @Override
    public void onRefreshComplete(List<BmobIMMessage> list) {
        mChatAdapter.notifyDataSetChanged();
        mRecyclerView.smoothScrollToPosition(mChatAdapter.getItemCount());
    }

    @Override
    public void onRefreshInterrupt() {

    }

    @Override
    public void onLoadMoreComplete(List<BmobIMMessage> list) {
        mChatAdapter.notifyDataSetChanged();
        mPtrFrameLayout.refreshComplete();
    }

    @Override
    public void onLoadMoreInterrupt() {

    }

    @Override
    public void onDeleteSuccess(String msg, int position) {

    }

    @Override
    public void onMessageSended(int location ,BmobIMMessage msg) {
        mChatAdapter.notifyItemInserted(location);
        mRecyclerView.smoothScrollToPosition(location);
    }

    @Override
    public void onMessageSendSuccess(int location ,BmobIMMessage msg) {
        mChatAdapter.notifyItemChanged(location);
    }

    @Override
    public void onMessageSendFail(int location , BmobIMMessage msg, BmobException e) {
        mChatAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMessageReceive(List<MessageEvent> list) {
        for(MessageEvent ev:list){
            if(ev.getConversation().getConversationId().equals(mPresenter.mModel.getConversation().getConversationId())){
                mPresenter.refresh();
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BmobIM.getInstance().addMessageListHandler(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        BmobIM.getInstance().removeMessageListHandler(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_send_message:
                try {
                    mPresenter.sendTextMessage(mInputMessage.getText().toString());
                } catch (JSONException e) {
                    ToastUtil.show("something went wrong");
                }
                mInputMessage.setText("");
                break;
            case R.id.iv_picture:
                PhotoPicker.builder()
                        .setPhotoCount(1)
                        .setShowCamera(true)
                        .setShowGif(false)
                        .setPreviewEnabled(true)
                        .start(getActivity(), PhotoPicker.REQUEST_CODE);
                break;
            case R.id.iv_photo:
                Intent intent = new Intent(getActivity(),SelectActivity.class);
                intent.putExtra("selectRequest",true);
                getActivity().startActivityForResult(intent,222);
                break;
            case R.id.iv_file:
                showFileChooser();
                break;
            case R.id.iv_face:
                onFaceButtonClick(false);
                break;
            case R.id.iv_location:
                break;
            default:break;
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            getActivity().startActivityForResult( Intent.createChooser(intent, "Select a File to Upload"), Constant.FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            ToastUtil.show("Please install a File Manager.");
        }
    }

    private void onFaceButtonClick(boolean close){
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mFaceRecyclerView.getLayoutParams();
        if(!close && mFaceRvClosed){
            lp.bottomMargin = (int) DensityUtil.getDipSize(getContext(),0);
            InputMethodHelper.close(getContext(),getActivity());
        }
        else lp.bottomMargin = (int) DensityUtil.getDipSize(getContext(),-250);
        mFaceRvClosed = !mFaceRvClosed;
        mFaceRecyclerView.setLayoutParams(lp);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK && requestCode == 222){
            String id = data.getStringExtra("requestId");
            if(id==null)return;
            try {
                mPresenter.sendRequestMessage(id);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if (resultCode == Activity.RESULT_OK && requestCode == PhotoPicker.REQUEST_CODE) {
            if (data != null) {
                mPhotoToSend = data.getStringArrayListExtra(PhotoPicker.KEY_SELECTED_PHOTOS).get(0);
                //
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("发送图片");
                builder.setPositiveButton("压缩", (dialog, which) -> new Thread(() -> {
                    CompressHelper compressHelper = new CompressHelper(getContext());
                    mPhotoToSend = compressHelper.thirdCompress(new File(mPhotoToSend));
                    try {
                        mPresenter.sendImageMessage(mPhotoToSend);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }).start())
                        .setNegativeButton("原图", (dialog, which) -> {
                            try {
                                mPresenter.sendImageMessage(mPhotoToSend);
                            } catch (JSONException e) {}
                        })
                        .setNeutralButton("取消",null);
                final AlertDialog dialog = builder.create();
                dialog.setView(getActivity().getLayoutInflater().inflate(R.layout.dialog_image_choice, null));
                dialog.setOnShowListener(d -> {
                    Glide.with(getActivity()).load(mPhotoToSend).into((ImageView) dialog.findViewById(R.id.iv_image_in_dialog));
                    ((TextView)dialog.findViewById(R.id.tv_image_size)).setText(FileUtils.convert(new File(mPhotoToSend).length()));
                });
                dialog.show();
            }
        }
        if(requestCode == Constant.FILE_SELECT_CODE &&resultCode == Activity.RESULT_OK){
            // Get the Uri of the selected file
            Uri uri = data.getData();
            String path = FileUtils.getPath(getContext(), uri);
            File file = new File(path);
            new AlertDialog.Builder(getContext()).setTitle("发送文件")
                    .setMessage(file.getName()+" 文件大小:"+FileUtils.convert(file.length()))
                    .setPositiveButton("发送", (dialog, which) -> {
                try {
                    mPresenter.sendFileMessage(path);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            })
                    .setNegativeButton("取消", null).show();
        }
    }

    @Override
    public boolean onBackPressed() {
        if(!mFaceRvClosed) {
            onFaceButtonClick(false);
            return true;
        }
        else return false;
    }
}
