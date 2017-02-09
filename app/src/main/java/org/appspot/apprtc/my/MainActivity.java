/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.appspot.apprtc.my;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.appspot.apprtc.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */
public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    FloatingActionButton fab;
    MyApplication myApplication;// = (MyApplication)getApplicationContext();

    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("org.appspot.apprtc.my.ClientSocketService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Intent intent = new Intent(this, ClientSocketService.class);
        //stopService(intent);
    }
    private static final int REQUEST_ACT = 123;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(MainActivity.this, "결과가 성공이 아님.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (requestCode == REQUEST_ACT) {
            String resultMsg = data.getStringExtra("result_msg");
            //textView.setText(resultMsg);
//        Glide.with(avatar.getContext())
//                .load(Uri.fromFile(new File(avatarName)))
//                .fitCenter()
//                .into(avatar);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    avatar.setImageURI(Uri.fromFile(new File(avatarName)));
                }
            });
            Toast.makeText(MainActivity.this, "결과 : " + resultMsg, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "REQUEST_ACT가 아님", Toast.LENGTH_SHORT).show();
        }
    }
    String avatarName = null;
    ImageView avatar = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        myApplication = (MyApplication)getApplicationContext();

        //레디스 디비에서 친구목록 -> 로컬디비에 삽입
        //쉐어드프리퍼런스 친구리스트 플래그 트루
        //처음 깔았을떄만 갱신

        if (!isServiceRunningCheck()){
            Intent intent = new Intent(this, ClientSocketService.class);
            startService(intent);
//            DataOutputStream out;
//            try {
//                out = new DataOutputStream(myApplication.getSocket().getOutputStream());
//                out.write(("{ \"status\": \"login\", \"data\": \""+myApplication.getId()+"\" }").getBytes("UTF-8"));
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //상단바 셋팅 서랍장있는 바
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setTitle(myApplication.getId());
        //좌측 3줄 서랍장 버튼
        ab.setDisplayHomeAsUpEnabled(true);
        //위로 올릴떄 보임

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        //서럽장 레이아웃
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        /**/
        avatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.avatarNav);
        avatarName = Environment.getExternalStorageDirectory().getAbsolutePath()+"/ChatRTC/Downloads/myAvatar.png";
//        Glide.with(avatar.getContext()).load(Uri.fromFile(new File(avatarName))).bitmapTransform(new BlurTransformation(getApplicationContext(), 25, 2), new CropCircleTransformation(getApplicationContext)).into(avatar);
//        Glide.with(avatar.getContext())
//                .load(Uri.fromFile(new File(avatarName)))
//                .fitCenter()
//                  .into(avatar);
        File directory_ChatRTC = new File(avatarName);
        if(directory_ChatRTC.exists()){ // 있으면
//            Glide.with(this).load(avatarName).bitmapTransform(new CropCircleTransformation(this)).into(avatar);
//            Glide.with(this).load(avatarName).centerCrop().into(avatar);
            avatar.setImageURI(Uri.fromFile(new File(avatarName)));
        }else{
            avatar.setImageResource(R.drawable.ic_account_circle_black_24dp);
        }
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"아바타",Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(MainActivity.this,FaceDetectActivity.class);
                Intent intent = new Intent(MainActivity.this,FdActivity.class);
                startActivityForResult(intent,REQUEST_ACT);
            }
        });

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        if (viewPager != null) {
            setupViewPager(viewPager);
            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    switch (position) {
                        case 0:
                            fab.show();
                            break;
                        case 1:
                            fab.hide();
                            break;
                        default:
                            fab.hide();
                            break;
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Here's a Snackbar", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//                Intent intent = new Intent(MainActivity.this,AddFriendsActivity.class);
//                intent.putExtra("user_id",myApplication.getId());
//                startActivity(intent);
                int tag = viewPager.getCurrentItem();
                //String tag = currentFragment.getTag();
                switch (tag) {
                    case 0:
                        Intent intent = new Intent(MainActivity.this, AddFriendsActivity.class);
                        intent.putExtra("user_id",myApplication.getId());
                        startActivity(intent);
                        break;
                    case 1:
                        Snackbar.make(view, "Task creation is...under construction", Snackbar.LENGTH_LONG).show();
                        break;
                    default:
                        Log.e("Unhandled", "Unhandled FAB fragment tag " + tag);
                        Snackbar.make(view, "Not sure what to do...my bad", Snackbar.LENGTH_SHORT).show();
                        break;
                }

            }
        });

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sample_actions, menu);
        return true;
    }

    //셋업 세로로 점 3개 눌렀을떄
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        switch (AppCompatDelegate.getDefaultNightMode()) {
            case AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM:
                menu.findItem(R.id.menu_night_mode_system).setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_AUTO:
                menu.findItem(R.id.menu_night_mode_auto).setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_YES:
                menu.findItem(R.id.menu_night_mode_night).setChecked(true);
                break;
            case AppCompatDelegate.MODE_NIGHT_NO:
                menu.findItem(R.id.menu_night_mode_day).setChecked(true);
                break;
        }
        return true;
    }
    //셋업 세로로 점 3개 내용 눌렀을떄
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.menu_night_mode_system:
                setNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case R.id.menu_night_mode_day:
                setNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case R.id.menu_night_mode_night:
                setNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case R.id.menu_night_mode_auto:
                setNightMode(AppCompatDelegate.MODE_NIGHT_AUTO);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setNightMode(@AppCompatDelegate.NightMode int nightMode) {
        AppCompatDelegate.setDefaultNightMode(nightMode);

        if (Build.VERSION.SDK_INT >= 11) {
            recreate();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new CheeseListFragment(), "친구목록");
//        adapter.addFragment(new CheeseListFragment(), "Category 2");
        adapter.addFragment(new RoomListFragment(), "방목록");
        viewPager.setAdapter(adapter);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
//                menuItem.getItemId();
                Log.d("눌러지지??", String.valueOf(menuItem.getItemId()));
                Log.d("눌러지지??","눌러지지??");
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
