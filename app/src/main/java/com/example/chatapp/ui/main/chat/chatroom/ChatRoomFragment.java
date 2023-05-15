package com.example.chatapp.ui.main.chat.chatroom;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.R;
import com.example.chatapp.databinding.FragmentChatRoomBinding;
import com.example.chatapp.model.UserInfoViewModel;

public class ChatRoomFragment extends Fragment {
    private ChatRoomItemsViewModel mItemsModel;
    private FragmentChatRoomBinding mBinding;
    private UserInfoViewModel mUserInfoModel;
    private ChatRoomSendViewModel mSendModel;
    private int HARD_CODED_CHAT_ID = 1; //TODO REMOVE

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewModelProvider provider = new ViewModelProvider(getActivity());

        mUserInfoModel = provider.get(UserInfoViewModel.class);

        mItemsModel = provider.get(ChatRoomItemsViewModel.class);
        mItemsModel.getFirstMessages(HARD_CODED_CHAT_ID, mUserInfoModel.getJwt()); //CHANGE CHAT ID

        mSendModel = provider.get(ChatRoomSendViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mBinding = FragmentChatRoomBinding.inflate(inflater);
        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //Swipe refresh Container
        mBinding.swipeContainer.setRefreshing(true); //SetRefreshing shows the internal Swiper view progress bar. Show this until messages load
        //When the user scrolls to the top of the RV, the swiper list will "refresh"
        //The user is out of messages, go out to the service and get more
        mBinding.swipeContainer.setOnRefreshListener(() -> {
            mItemsModel.getNextMessages(HARD_CODED_CHAT_ID, mUserInfoModel.getJwt());
        });

        //recycler //TODO listen for ArrayList change
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        mBinding.recyclerBubbles.setLayoutManager(linearLayoutManager);
        mBinding.recyclerBubbles.setAdapter(new ChatRoomAdapter(mItemsModel.getMessageListByChatId(HARD_CODED_CHAT_ID), mUserInfoModel.getUsername()));
//        mBinding.recyclerBubbles.getAdapter().setStateRestorationPolicy(RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY);

        //Show scroll to bottom button when not at bottom
        mBinding.actionScrollToBottom.setVisibility(View.GONE); //hide initially
        mBinding.actionScrollToBottom.setOnClickListener(button -> {
            mBinding.recyclerBubbles.smoothScrollToPosition(mBinding.recyclerBubbles.getAdapter().getItemCount() - 1); //scroll to end
            mBinding.actionScrollToBottom.setVisibility(View.GONE); //hide self
        });
        mBinding.recyclerBubbles.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (recyclerView.canScrollVertically(1)) { //if can scroll down
                    mBinding.actionScrollToBottom.setVisibility(View.VISIBLE);
                } else {
                    mBinding.actionScrollToBottom.setVisibility(View.GONE);
                }
            }
        });

        //Change in items/message listener
        mItemsModel.addMessageObserver(HARD_CODED_CHAT_ID, getViewLifecycleOwner(),
                list -> {
//                    Parcelable recyclerViewState = mBinding.recyclerBubbles.getLayoutManager().onSaveInstanceState();
                    mBinding.recyclerBubbles.getAdapter().notifyDataSetChanged(); //tell recycler to update
                    mBinding.swipeContainer.setRefreshing(false);
//                    mBinding.recyclerBubbles.getLayoutManager().onRestoreInstanceState(recyclerViewState);
                });

        //Send Button
        //Send button was clicked. Send the message via the SendViewModel
        mBinding.actionSend.setOnClickListener(button -> {
            mSendModel.sendMessage(HARD_CODED_CHAT_ID,
                    mUserInfoModel.getJwt(),
                    mBinding.textMessageInput.getText().toString());
        });
        //when we get the response back from the server, clear the edittext
        mSendModel.addResponseObserver(getViewLifecycleOwner(), response -> {
            mBinding.textMessageInput.setText("");
            mBinding.recyclerBubbles.smoothScrollToPosition(mBinding.recyclerBubbles.getAdapter().getItemCount() - 1);
        });
    }

    //Hides bottom menu bar
    @Override
    public void onResume() {
        super.onResume();
        ((AppCompatActivity) getActivity()).findViewById(R.id.nav_view).setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).findViewById(R.id.nav_view).setVisibility(View.VISIBLE);
    }

}