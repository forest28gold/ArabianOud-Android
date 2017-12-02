package com.ecommerce.dell.mcommerce.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.ecommerce.dell.mcommerce.R;
import com.ecommerce.dell.mcommerce.activity.login.LoginActivity;
import com.ecommerce.dell.mcommerce.activity.product.CategoryActivity;
import com.ecommerce.dell.mcommerce.activity.profile.ContactUsActivity;
import com.ecommerce.dell.mcommerce.activity.profile.ProfileActivity;
import com.ecommerce.dell.mcommerce.activity.profile.StoreLocatorActivity;

public class NavigationDrawerFragment extends Fragment {

    public MyAdapter myadapter ;
    public String[] groups = new String[6];
    public String[][] children = {};

    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    public ExpandableListView mDrawerListView;
    private View mFragmentContainerView;
    private LinearLayout containerView;

    private int mCurrentSelectedPosition = 0;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    TextView username;

    public NavigationDrawerFragment() {
        myadapter = new MyAdapter();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setHomeAsUpIndicator(R.drawable.ic_drawer);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        selectItem(-1);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        containerView = (LinearLayout)inflater.inflate(R.layout.side_menu, container, false);

        mDrawerListView = (ExpandableListView) containerView.findViewById(R.id.drawerList);
        username = (TextView) containerView.findViewById(R.id.username);

        try {
            SharedPreferences s = getActivity().getSharedPreferences("user", 0);
            String status = s.getString("status", "");
            if (status.equals("1")){
                username.setText(s.getString("firstname",""));
            } else {
                username.setText("");
            }

        } catch (Exception ex) {
            Toast.makeText(getActivity(), ex.getStackTrace().toString() , Toast.LENGTH_LONG).show();
        }

        groups = getActivity().getResources().getStringArray(R.array.side_menu);
        children = new String[][]{
                getActivity().getResources().getStringArray(R.array.side_menu_sub1),
                getActivity().getResources().getStringArray(R.array.side_menu_sub2),
                getActivity().getResources().getStringArray(R.array.side_menu_sub3),
                {},
                {},
                {}
        };

        mDrawerListView.setGroupIndicator(null);
        mDrawerListView.setAdapter(myadapter);

        LinearLayout contactus = (LinearLayout) containerView.findViewById(R.id.contactus);
        LinearLayout storeLocator = (LinearLayout) containerView.findViewById(R.id.storeLocator);
        LinearLayout account = (LinearLayout) containerView.findViewById(R.id.account);
        LinearLayout changeLanguage = (LinearLayout) containerView.findViewById(R.id.changeLanguage);

        changeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final SharedPreferences sharedPreferences = getActivity().getSharedPreferences("setting", 0);
                final String language = sharedPreferences.getString("language", "");

                new AlertView(getString(R.string.changeLanguageTitle),
                        getString(R.string.changeLanguageAndRestart),
                        getString(R.string.cancel),
                        new String[]{getString(R.string.ok)},
                        null, getActivity(), AlertView.Style.Alert, (new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        if(position != AlertView.CANCELPOSITION) {

                            SharedPreferences.Editor edit = sharedPreferences.edit();
                            if (language.equals("ar")) {
                                edit.putString("language","en");
                            } else if (language.equals("en")){
                                edit.putString("language","ar");
                            }
                            edit.apply();

                            Intent i = getActivity().getPackageManager().getLaunchIntentForPackage( getActivity().getPackageName() );
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            System.exit(1);
                        }
                    }
                })).setCancelable(true).show();
            }


        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(mFragmentContainerView);
                SharedPreferences s = getActivity().getSharedPreferences("user", 0);

                String status = s.getString("status" , "");
                if (status.equals("1")){
                    Intent myIntent = new Intent(getActivity(), ProfileActivity.class);
                    startActivity(myIntent);
                } else {
                    Intent myIntent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(myIntent);
                }
            }
        });

        contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(mFragmentContainerView);
                Intent i = new Intent(getActivity() , ContactUsActivity.class);
                startActivity(i);
            }
        });

        storeLocator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(mFragmentContainerView);
                Intent i = new Intent(getActivity(), StoreLocatorActivity.class);
                startActivity(i);
            }
        });

        return containerView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public int pxToDp(int px) {
        final float scale = getActivity().getResources().getDisplayMetrics().density;
        int pixels = (int) (px * scale + 0.5f);

        return pixels;
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

        final ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }
                actionBar.setTitle(getActivity().getTitle());
                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                SharedPreferences s = getActivity().getSharedPreferences("user", 0);
                String status = s.getString("status", "");

                if (status.equals("1")){
                    username.setText(s.getString("firstname",""));
                } else {
                    username.setText("");
                }

                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

//        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
//            mDrawerLayout.openDrawer(mFragmentContainerView);
//        }

        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);

    }

    private void selectItem(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, true);
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences s = getActivity().getSharedPreferences("user", 0);

        String status = s.getString("status", "");
        if (status.equals("1")){
            username.setText(s.getString("firstname",""));
        } else {
            username.setText("");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            //  inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    public  class MyAdapter extends BaseExpandableListAdapter {

        public MyAdapter() {

        }

        @Override
        public int getGroupCount() {
            // Get header size
            return groups.length;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            // return children count
            return children[groupPosition].length;
        }

        @Override
        public Object getGroup(int groupPosition) {
            // Get header position
            return groups[groupPosition];
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            // This will return the child
            return children[groupPosition][childPosition];
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.side_menu_main_item , parent , false);
            }

            groups = getActivity().getResources().getStringArray(R.array.side_menu);

            ImageView image  = (ImageView) convertView.findViewById(R.id.image);
            TextView textView = (TextView) convertView.findViewById(R.id.item);
            ImageView btnExpand = (ImageView) convertView.findViewById(R.id.imageView_expand);

            if (isExpanded) {
                btnExpand.setBackground(getResources().getDrawable(R.drawable.minus));
            } else {
                btnExpand.setBackground(getResources().getDrawable(R.drawable.plus));
            }

            switch (groupPosition){
                case 0 :
                    image.setImageResource(R.drawable.perfume_icon2);
                    textView.setText(groups[1]);
                    btnExpand.setVisibility(View.VISIBLE);
                    break;
                case 1 :
                    image.setImageResource(R.drawable.perfume_icon1);
                    textView.setText(groups[0]);
                    btnExpand.setVisibility(View.VISIBLE);
                    break;
                case 2 :
                    image.setImageResource(R.drawable.perfume_icon3);
                    textView.setText(groups[2]);
                    btnExpand.setVisibility(View.VISIBLE);
                    break;
                case 3 :
                    image.setImageResource(R.drawable.perfume_icon5);
                    textView.setText(groups[4]);
                    btnExpand.setVisibility(View.INVISIBLE);
                    break;
                case 4 :
                    image.setImageResource(R.drawable.perfume_icon4);
                    textView.setText(groups[3]);
                    btnExpand.setVisibility(View.INVISIBLE);
                    break;
                case 5 :
                    image.setImageResource(R.drawable.perfume_icon6);
                    textView.setText(groups[5]);
                    btnExpand.setVisibility(View.INVISIBLE);
                    break;
            }

            return convertView;
        }

        @Override
        public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.side_menu_sub_item, parent, false);
            }

            TextView child_text = (TextView) convertView.findViewById(R.id.subitem);
            child_text.setText(children[groupPosition][childPosition]);

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    mDrawerLayout.closeDrawer(mFragmentContainerView);
                    Intent myIntent = new Intent(getActivity(), CategoryActivity.class);

                    if (groupPosition == 0) {
                        switch (childPosition) {
                            case 0:
                                myIntent.putExtra("categoryID","32");
                                break;
                            case 1:
                                myIntent.putExtra("categoryID","27");
                                break;
                            case 2:
                                myIntent.putExtra("categoryID","26");
                                break;
                            case 3:
                                myIntent.putExtra("categoryID","28");
                                break;
                            case 4:
                                myIntent.putExtra("categoryID","30");
                                break;
                            case 5:
                                myIntent.putExtra("categoryID","29");
                                break;
                            case 6:
                                myIntent.putExtra("categoryID","31");
                                break;
                        }
                    } else if (groupPosition == 1) {
                        switch (childPosition) {
                            case 0:
                                myIntent.putExtra("categoryID","34");
                                break;
                            case 1:
                                myIntent.putExtra("categoryID","36");
                                break;
                            case 2:
                                myIntent.putExtra("categoryID","37");
                                break;
                            case 3:
                                myIntent.putExtra("categoryID","38");
                                break;
                        }
                    } else if (groupPosition == 2) {
                        switch (childPosition) {
                            case 0:
                                myIntent.putExtra("categoryID","35");
                                break;
                            case 1:
                                myIntent.putExtra("categoryID","7");
                                break;
                            case 2:
                                myIntent.putExtra("categoryID","24");
                                break;
                            case 3:
                                myIntent.putExtra("categoryID","50");
                                break;
                        }
                    }

                    myIntent.putExtra("mTitle", children[groupPosition][childPosition]);
                    startActivity(myIntent);
                }
            });

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

        @Override
        public void onGroupExpanded(int groupPosition) {
            super.onGroupExpanded(groupPosition);
            Intent myIntent;

            switch (groupPosition) {
                case 3:
                    mDrawerLayout.closeDrawer(mFragmentContainerView);
                    myIntent = new Intent(getActivity(), CategoryActivity.class);
                    myIntent.putExtra("categoryID","4");
                    myIntent.putExtra("mTitle", getResources().getString(R.string.title_section5));
                    startActivity(myIntent);
                    break;

                case 4:
                    mDrawerLayout.closeDrawer(mFragmentContainerView);
                    myIntent = new Intent(getActivity(), CategoryActivity.class);
                    myIntent.putExtra("categoryID","61");
                    myIntent.putExtra("mTitle", getResources().getString(R.string.title_section4));
                    startActivity(myIntent);
                    break;

                case 5:
                    mDrawerLayout.closeDrawer(mFragmentContainerView);
                    myIntent = new Intent(getActivity(), CategoryActivity.class);
                    myIntent.putExtra("categoryID","58");
                    myIntent.putExtra("mTitle", getResources().getString(R.string.title_section6));
                    startActivity(myIntent);
                    break;

            }
        }

    }

}
