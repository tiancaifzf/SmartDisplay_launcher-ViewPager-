package com.smarter.pictureview;

import android.app.Application;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.smarter.pictureview.view.PictureView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    private ViewPager viewPager;
    //private int[] images ;
    public String fullname;
    private ImageView[] imageViews ;
    public ArrayList<HashMap<String, Object>> applist;

    private int position1=0;
    private boolean isClick = false;
    private static int scrollState;
    private static int preSelectedPage = 0;
    public static String application_name;
    public static String packageName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //设置无标题
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.view_pager);
        final PackageManager pm = getApplicationContext().getPackageManager();
        viewPager = (ViewPager)findViewById(R.id.view_pager);
        applist = new ArrayList<HashMap<String, Object>>();
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            AppInfo tmpinfo = new AppInfo();
            HashMap<String, Object> map = new HashMap<String, Object>();
            //过滤掉系统程序
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM)
            {
                // Log.d("System software", "We met System " + i + "th software: " + packageInfo.packageName);
                continue;
            }

            PackageInfo packageinfo = null;
            try {
                packageinfo = getPackageManager().getPackageInfo(packageInfo.packageName.toString(), 0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (packageinfo == null) {
                return;
            }
            // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
            Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
            resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            resolveIntent.setPackage(packageinfo.packageName);

            // 通过getPackageManager()的queryIntentActivities方法遍历
            List<ResolveInfo> resolveinfoList = getPackageManager()
                    .queryIntentActivities(resolveIntent, 0);

            ResolveInfo resolveinfo = resolveinfoList.iterator().next();

            if (resolveinfo != null) {
                // packagename = 参数packname
                packageName = resolveinfo.activityInfo.packageName;
                // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
                fullname = resolveinfo.activityInfo.name;
                Log.d("##############","Activityname:"+fullname);
               // map.put("full_packagename",fullname);
                try {
                    map.put("application_name",getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(packageName,0)));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                map.put("packageName",packageName);


                map.put("appicon", packageInfo.applicationInfo.loadIcon(getPackageManager()).getCurrent());
                //map.put("application_name",packageinfo.applicationInfo.loadDescription())
                map.put("appname", tmpinfo.appName = packageInfo.applicationInfo.loadLabel(getPackageManager()).toString());
                // String fullname=packageInfo.packageName.toString()+packageInfo.applicationInfo.className.toString();
                //map.put("packagename", tmpinfo.packagename = packageInfo.applicationInfo.className.toString());
                //map.put("ItemButton",R.drawable.android_logo);
                try {
                    ApplicationInfo fzf=getPackageManager().getApplicationInfo(packageName,PackageManager.GET_META_DATA);
                    if(fzf.metaData!=null) {
                        if(fzf.metaData.getString("Smart_Display")!=null) {
                            Log.d("FZF", "EEE: YES!!!!");
                            String an=getPackageManager().getApplicationLabel(getPackageManager().getApplicationInfo(packageName,0)).toString();
                            if(an.equals("Smart Display")){
                                Log.d("FZF","Ignore self App!!");
                            }
                            else {
                                applist.add(map);
                            }
                        }
                    }
                  // if(fzf.metaData.getBoolean("Smart_Display")){
                      // applist.add(map);
                  // }

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                    Log.d("FZF","Have no SD meta-data");
                }
              //  applist.add(map);
            }

        }
        HashMap<String, Object> appInfotmp=applist.get(0);
        application_name=(String)appInfotmp.get("application_name");
        packageName=(String)appInfotmp.get("packageName");
        imageViews= new ImageView[applist.size()];

        viewPager.setAdapter(new PagerAdapter()
        {
            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                final HashMap<String, Object> appInfo = applist.get(position);

                PictureView pictureView = new PictureView(getApplicationContext());
                //pictureView.setImageResource();
                pictureView.setImageDrawable((Drawable) appInfo.get("appicon"));
                container.addView(pictureView);
                imageViews[position] = pictureView;
                return pictureView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(imageViews[position]);
            }

            @Override
            public int getCount() {
                return imageViews.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (positionOffsetPixels!=0) {
                    if (scrollState == 1) {//手指按下
//						if(preSelectedPage == arg0){//表示往左拉，相应的tab往右走
//							Log.i(TAG, "ux==--> 手指左滑 整体页面--> ");
//						}else {
//							Log.i(TAG, "ux==--> 手指向右 整体页面<--");
//						}
                    }else if (scrollState==2) {
                        if(preSelectedPage == position){//往左拉
                         ///   Log.i("FZF", "ux==--> 手指左滑 整体页面--> 页面向右");
                        }else{//表示往右拉
                        ///    Log.i("FZF", "ux==--> 手指右滑 整体页面-->  页面向左");
                        }
                    }
                }
            }

            @Override
            public void onPageSelected(int position) {
                position1=position;
                Log.d("FZF","Position:"+position);
                final HashMap<String, Object> appInfo1 = applist.get(position);
                application_name=(String)appInfo1.get("application_name");
                packageName=(String)appInfo1.get("packageName");
               // Log.d("FZF","Package Name:"+packagename);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(!isClick){
                    scrollState = state;
                    preSelectedPage = position1;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public int Arraylist_size(ArrayList arraylist){
        return arraylist.size();
    }
}
