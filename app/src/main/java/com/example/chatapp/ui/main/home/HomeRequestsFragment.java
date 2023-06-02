package com.example.chatapp.ui.main.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapp.R;
import com.example.chatapp.databinding.FragmentHomeMessagesBinding;
import com.example.chatapp.databinding.FragmentHomeRequestsBinding;
import com.example.chatapp.ui.main.contacts.ContactCard;
import com.example.chatapp.ui.main.contacts.ContactsViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class HomeRequestsFragment extends Fragment {


    private FragmentHomeRequestsBinding mBinding;

    private HomeRequestsItemViewModel mHomeRequestsItemViewModel;

    private ContactsViewModel mContactsViewModel;


    public HomeRequestsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_home_messages, container, false);
        mBinding = FragmentHomeRequestsBinding.inflate(inflater);
        mHomeRequestsItemViewModel = new ViewModelProvider(getActivity()).get(HomeRequestsItemViewModel.class);
        mContactsViewModel = new ViewModelProvider(getActivity()).get(ContactsViewModel.class);

        return mBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        mBinding.homeRequestCard.setOnClickListener(button -> {
//            BottomNavigationView temp = ((AppCompatActivity) getActivity()).findViewById(R.id.nav_view);
//            temp.setSelectedItemId(R.id.navigation_connections);
//        });


/*        mHomeRequestsItemViewModel.addHomeRequestObserver(getViewLifecycleOwner(), List -> {
            Log.i("HomeRequestsFragment", "HomeRequestsObserver");

            mBinding.rootRecycler.getAdapter().notifyDataSetChanged();
        });*/

        mContactsViewModel.addContactsObserver(getViewLifecycleOwner(), List -> {


            Log.i("HomeRequestsFragment", "ContactsObserver");
            getContactRequests();
            mBinding.rootRecycler.setAdapter(new HomeRequestsAdapter(
                    mHomeRequestsItemViewModel.mHomeRequestList.getValue(),
                    mHomeRequestsItemViewModel,
                    (AppCompatActivity) getActivity()));


        });


        //test to see notif

//        List<HomeRequestsItem> items = new ArrayList<>();
//        items.add(new HomeRequestsItem("philip", "11"));
//        items.add(new HomeRequestsItem("philip", "11"));
//        items.add(new HomeRequestsItem("philip", "11"));
//        items.add(new HomeRequestsItem("philip", "11"));
//        items.add(new HomeRequestsItem("philip", "11"));
//
//        mBinding.rootRecycler.setAdapter(new HomeRequestsAdapter(
//                items,
//                mHomeRequestsItemViewModel,
//                (AppCompatActivity) getActivity()));
////////////////////////////////////////////////////////////////
        //uncomment
/*        mBinding.rootRecycler.setAdapter(new HomeRequestsAdapter(
                mHomeRequestsItemViewModel.mHomeRequestList.getValue(),
                mHomeRequestsItemViewModel,
                (AppCompatActivity) getActivity()));*/


    }


    private void getContactRequests() {
        List<ContactCard> contacts = mContactsViewModel.getContacts();
        ArrayList<HomeRequestsItem> requests = new ArrayList<>();
        for (ContactCard contact : contacts) {
             if(contact.getIncoming()){
                    requests.add(new HomeRequestsItem(contact.getName(), "11"));

             }

        }
        mHomeRequestsItemViewModel.mHomeRequestList.setValue(requests);

    }
}