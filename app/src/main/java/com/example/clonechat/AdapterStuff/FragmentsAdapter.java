package com.example.clonechat.AdapterStuff;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.clonechat.UtilFragments.CallsFragment;
import com.example.clonechat.UtilFragments.ChatsFragment;
import com.example.clonechat.UtilFragments.StatusFragment;

public class FragmentsAdapter extends FragmentPagerAdapter {
    public FragmentsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        //getting the fragments via position dynamically
        switch (position){

            case 1:
                return new StatusFragment();
//                break;
            case 2:
                return new CallsFragment();
//                break;
            default:
                return new ChatsFragment();
        }

//        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    //setting the page tittle


    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {

        String tittle = null;

        if(position == 0){
            tittle = "CHATS";
        }else if(position == 1){
            tittle = "STATUS";
        }else if(position == 2){
            tittle = "CALLS";
        }

        return tittle;

    }
}
