package teambutterflower.co.kr.myportfolio.views.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import teambutterflower.co.kr.myportfolio.R;


public class NavigationDrawerFragment extends Fragment{

    private DrawerLayout mDrawerLaout;
    private View mFragmentContainerView;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout drawerContent = (LinearLayout) inflater.inflate(R.layout.fragment_navigation_drawer, container, false);

        return drawerContent;
    }

    public void setup(int fragmentId, DrawerLayout drawerLayout){
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLaout = drawerLayout;

        mDrawerLaout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

    }

    public void openFragment(){
        mDrawerLaout.openDrawer(mFragmentContainerView);
    }
}