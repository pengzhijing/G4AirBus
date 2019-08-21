package com.shima.smartbushome;


import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.shima.smartbushome.about.AboutActivity;
import com.shima.smartbushome.assist.Adapter.AreaAdapter;
import com.shima.smartbushome.assist.Adapter.RoomAdapter;
import com.shima.smartbushome.assist.Adapter.RoomIconAdapter;
import com.shima.smartbushome.assist.DraggableGridViewPager;
import com.shima.smartbushome.assist.MusicNotification;
import com.shima.smartbushome.assist.MusicNotifyReceiver;
import com.shima.smartbushome.assist.holocolorpicker.ColorPicker;
import com.shima.smartbushome.assist.holocolorpicker.SVBar;
import com.shima.smartbushome.database.SaveArea;
import com.shima.smartbushome.database.Saveroom;
import com.shima.smartbushome.mainsetting.MainSettingActivity;
import com.shima.smartbushome.util.FileUtil;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import per.goweii.anylayer.AnimHelper;
import per.goweii.anylayer.AnyLayer;

/**
 * A simple {@link Fragment} subclass.
 */
public class MainFragment extends Fragment {

    private SaveArea currPageArea=null;//当前选着的区域对象

    private GridView gv_rooms;
    android.os.Handler handle = new android.os.Handler();
    private RoomAdapter mAdapter;
    public List<Saveroom> roomdata = new ArrayList<Saveroom>();
    public static List<HashMap<String, String>> netdeviceList = new ArrayList<HashMap<String, String>>();
    private boolean deletechoose = false;
    public static Context maincontext;
    String roomicon = "room_type1", settingicon = "", roombgcolor = "";
    private AlertView maddingAlertViewExt, miconAlertViewExt, msettingAlertViewExt, mpcolorAlertViewExt;//窗口拓展例子
    int settingid = 0;
    private List<String> iconarray = new ArrayList<String>() {
        {
            add("room_type1");
            add("room_type2");
            add("room_type3");
            add("room_type4");
            add("room_type5");
            add("room_type6");
            add("room_type7");
            add("room_type8");
            add("room_type9");
            add("room_type10");
            add("room_type11");
            add("room_type12");
            add("room_type13");
            add("room_type14");
            add("room_type15");
        }
    };
    MusicNotifyReceiver mReceiver;
    View view;
    AlertView mAlertView;
    boolean exitdialogshow = false;

    public MainFragment() {
        // Required empty public constructor
    }

    public static MainFragment newInstance(String param1) {
        MainFragment fragment = new MainFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_main, container, false);
        // view.findViewById(R.id.layout_fm).setAlpha(0);//设置背景透明
        setHasOptionsMenu(true);
        gv_rooms = view.findViewById(R.id.gv_rooms);
        mAdapter = new RoomAdapter(getActivity());
        maincontext = getActivity();
        handle.postDelayed(getroomrun, 20);

        gv_rooms.setOnItemClickListener(itemclick);//点击
        gv_rooms.setOnItemLongClickListener(itemlongclick);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mGattUpdateReceiver, makeUpdateIntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }


    AnyLayer areaListDialog=null;//区域列表对话框
    AnyLayer addAreaDialog=null;//添加区域对话框

    GridView gv_areaList;
    AreaAdapter areaAdapter;
    List<SaveArea> areaList=null;

    EditText et_areaName;
    String areaBgPath="";//区域背景路径 临时保存

    //显示添加区域对话框
    public void showAddAreaDialog(){

        addAreaDialog = AnyLayer.with(getActivity())
                .contentView(R.layout.view_addarea_dialog)
                .backgroundBlurPercent(0.015F)//背景高斯模糊
                .gravity(Gravity.TOP |Gravity.CENTER)
                .cancelableOnTouchOutside(true)
                .cancelableOnClickKeyBack(true)
                .onClick(R.id.fl_dialog_no, new AnyLayer.OnLayerClickListener() {
                    @Override
                    public void onClick(AnyLayer anyLayer, View v) {
                        //关闭添加对话框
                        anyLayer.dismiss();
                    }
                })
                .onClick(R.id.fl_dialog_yes, new AnyLayer.OnLayerClickListener() {
                    @Override
                    public void onClick(AnyLayer anyLayer, View v) {
                        //添加一个区域
                        SaveArea area=new SaveArea();
                        String areaName="";
                        String areaBg="";
                        try {
                            areaName=et_areaName.getText().toString();
                            areaBg=areaBgPath;
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        area.setArea_name(areaName);
                        area.setArea_bg(areaBg);

                        MainActivity.mgr.addArea(area);
                        //刷新
                        areaList.clear();
                        areaList.addAll(MainActivity.mgr.queryArea());
                        areaAdapter.notifyDataSetChanged();


                        //关闭添加对话框
                        anyLayer.dismiss();
                    }
                })
                .contentAnim(new AnyLayer.IAnim() {
                    @Override
                    public long inAnim(View content) {
                        AnimHelper.startTopAlphaInAnim(content, 350);//设置进入动画
                        return 350;
                    }

                    @Override
                    public long outAnim(View content) {
                        AnimHelper.startTopAlphaOutAnim(content, 350);//设置退出动画
                        return 350;
                    }
                });
        et_areaName=addAreaDialog.getContentView().findViewById(R.id.et_areaName);
        iv_backgroudArea=addAreaDialog.getContentView().findViewById(R.id.iv_backgroud);
        iv_galleryArea=addAreaDialog.getContentView().findViewById(R.id.iv_gallery);
        iv_galleryArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 从相册获取背景路径 区域背景
                galleryArea();
            }
        });
        addAreaDialog.show();
    }

    //显示编辑区域对话框
    public void showUpdateAreaDialog(SaveArea area){

        final SaveArea updateArea=area;
        addAreaDialog = AnyLayer.with(getActivity())
                .contentView(R.layout.view_addarea_dialog)
                .backgroundBlurPercent(0.015F)//背景高斯模糊
                .gravity(Gravity.TOP |Gravity.CENTER)
                .cancelableOnTouchOutside(true)
                .cancelableOnClickKeyBack(true)
                .onClick(R.id.fl_dialog_delete, new AnyLayer.OnLayerClickListener() {
                    @Override
                    public void onClick(AnyLayer anyLayer, View v) {
                        //删除一个区域
                        MainActivity.mgr.deleteArea(updateArea.getId());
                        //获取该区域下的房间
                        List<Saveroom> saveroomList=MainActivity.mgr.queryRoomByAreaId(updateArea.getId());
                        //根据该区域下的房间ID进行删除房间内所有设备信息
                        for (int i=0;i<saveroomList.size();i++){
                            deleteRoomByRoomId(saveroomList.get(i).room_id);
                        }

                        //刷新
                        areaList.clear();
                        areaList.addAll(MainActivity.mgr.queryArea());
                        areaAdapter.notifyDataSetChanged();


                        //关闭添加对话框
                        anyLayer.dismiss();
                    }
                })
                .onClick(R.id.fl_dialog_no, new AnyLayer.OnLayerClickListener() {
                    @Override
                    public void onClick(AnyLayer anyLayer, View v) {
                        //关闭添加对话框
                        anyLayer.dismiss();
                    }
                })
                .onClick(R.id.fl_dialog_yes, new AnyLayer.OnLayerClickListener() {
                    @Override
                    public void onClick(AnyLayer anyLayer, View v) {
                        //修改更新一个区域
                        String areaName="";
                        String areaBg="";
                        try {
                            areaName=et_areaName.getText().toString();
                            areaBg=areaBgPath;
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        updateArea.setArea_name(areaName);
                        updateArea.setArea_bg(areaBg);

                        MainActivity.mgr.updateArea(updateArea);
                        //刷新
                        areaList.clear();
                        areaList.addAll(MainActivity.mgr.queryArea());
                        areaAdapter.notifyDataSetChanged();


                        //关闭添加对话框
                        anyLayer.dismiss();
                    }
                })
                .contentAnim(new AnyLayer.IAnim() {
                    @Override
                    public long inAnim(View content) {
                        AnimHelper.startTopAlphaInAnim(content, 350);//设置进入动画
                        return 350;
                    }

                    @Override
                    public long outAnim(View content) {
                        AnimHelper.startTopAlphaOutAnim(content, 350);//设置退出动画
                        return 350;
                    }
                });
        FrameLayout fl_dialog_delete=addAreaDialog.getContentView().findViewById(R.id.fl_dialog_delete);
        fl_dialog_delete.setVisibility(View.VISIBLE);
        TextView tv_title=addAreaDialog.getContentView().findViewById(R.id.tv_title);
        tv_title.setText("Update Area");
        et_areaName=addAreaDialog.getContentView().findViewById(R.id.et_areaName);
        et_areaName.setText(updateArea.getArea_name());
        iv_backgroudArea=addAreaDialog.getContentView().findViewById(R.id.iv_backgroud);
        iv_galleryArea=addAreaDialog.getContentView().findViewById(R.id.iv_gallery);
        iv_galleryArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 从相册获取背景路径 区域背景
                galleryArea();
            }
        });

        //设置图片圆角角度
        RoundedCorners roundedCorners= new RoundedCorners(10);
        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
        RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(500, 500);
        //加载区域背景图片
        Glide.with(getContext())
                .load(area.getArea_bg())
                .apply(options)
                .into(iv_backgroudArea);

        addAreaDialog.show();
    }


    //根据房间ID来删除
    private void deleteRoomByRoomId(int room_id){
        MainActivity.mgr.deletefounction("room", room_id);
        MainActivity.mgr.deletefc("light", room_id);
        MainActivity.mgr.deletefc("hvac", room_id);
        MainActivity.mgr.deletefc("mood", room_id);
        MainActivity.mgr.deletefc("moodbutton", room_id);
        MainActivity.mgr.deletefc("fan", room_id);
        MainActivity.mgr.deletefc("curtain", room_id);
        MainActivity.mgr.deletefc("music", room_id);
        MainActivity.mgr.deletefc("song", room_id);
        MainActivity.mgr.deletefc("radio", room_id);
        MainActivity.mgr.deletefc("floorheat", room_id);
    }

    //菜单按钮事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case R.id.menu_area:
                //显示区域编辑对话框
                 areaListDialog = AnyLayer.with(getActivity())
                        .contentView(R.layout.view_arealist_dialog)
                        .backgroundBlurPercent(0.015F)//背景高斯模糊
                        .gravity(Gravity.TOP | Gravity.CENTER)
                        .cancelableOnTouchOutside(true)
                        .cancelableOnClickKeyBack(true)
                        .onClick(R.id.fb_addArea, new AnyLayer.OnLayerClickListener() {
                            @Override
                            public void onClick(AnyLayer anyLayer, View v) {
                                // 显示添加区域对话框
                                showAddAreaDialog();
                            }
                        })
                        .contentAnim(new AnyLayer.IAnim() {
                            @Override
                            public long inAnim(View content) {
                                AnimHelper.startTopAlphaInAnim(content, 350);//设置进入动画
                                return 350;
                            }

                            @Override
                            public long outAnim(View content) {
                                AnimHelper.startTopAlphaOutAnim(content, 350);//设置退出动画
                                return 350;
                            }
                        });

                 TextView tv_areaListTitle=areaListDialog.getContentView().findViewById(R.id.tv_areaListTitle);
                tv_areaListTitle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        currPageArea=null;
                        //对话框消失
                        areaListDialog.dismiss();
                        //更新当前房间列表
                        handle.postDelayed(getroomrun, 20);
                    }
                });

                gv_areaList=areaListDialog.getContentView().findViewById(R.id.gv_areaList);
                //区域列表数据绑定与显示
                areaList=MainActivity.mgr.queryArea();
                areaAdapter=new AreaAdapter(getContext(),areaList);
                gv_areaList.setAdapter(areaAdapter);
                //区域列表子项点击事件
                gv_areaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //对话框消失
                        areaListDialog.dismiss();
                        //保存当前选择的区域对象
                        currPageArea=areaList.get(position);
                        //更新当前房间列表
                        handle.postDelayed(getroomrun, 20);


                    }
                });
                //区域列表子项长按事件
                gv_areaList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        //显示修改或者删除区域对话框
                        showUpdateAreaDialog(areaList.get(position));
                        return true;
                    }
                });

                areaListDialog.show();
                break;
            case R.id.room_add:
                maddingAlertViewExt = new AlertView("Enter Room info", null, "CANCEL",
                        null, new String[]{"SAVE"}, getActivity(), AlertView.Style.Alert, alterclick);
                ViewGroup addingextView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.setting_maininfo2, null);
                final ImageView iconchoose = (ImageView) addingextView.findViewById(R.id.mainroomicon);
                iv_gallery = addingextView.findViewById(R.id.iv_gallery);
                iv_backgroud = addingextView.findViewById(R.id.iv_backgroud);
                name = (EditText) addingextView.findViewById(R.id.mainnameeditText);
                //  setmainbgcolor=(TextView)addingextView.findViewById(R.id.mainbgcolor);

                //跳转到相册选择图片作为背景
                iv_gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        gallery();
                    }
                });



                List<Saveroom> saveroomList=MainActivity.mgr.queryroom();
                if (saveroomList.size() <= 0) {
                    name.setText("room1");
                } else {
                    name.setText("room" + (saveroomList.get(saveroomList.size() - 1).room_id + 1));
                }

                iconchoose.setImageDrawable(getResources().getDrawable(getResourdIdByResourdName(getActivity(), roomicon)));
                settingicon = roomicon;
                iconchoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        miconAlertViewExt = new AlertView("Icon Selection", null, "CANCEL",
                                null, null, getActivity(), AlertView.Style.Alert, alterclick);
                        miconAlertViewExt.setCancelable(false);
                        ViewGroup exticonView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.mood_icon_select, null);
                        GridView icongrid = (GridView) exticonView.findViewById(R.id.gridView2);
                        icongrid.setAdapter(new RoomIconAdapter(getActivity()));
                        icongrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                settingicon = iconarray.get(position);
                                iconchoose.setImageDrawable(getResources().getDrawable(getResourdIdByResourdName(getActivity(), iconarray.get(position))));
                                miconAlertViewExt.dismiss();
                            }
                        });
                        miconAlertViewExt.addExtView(exticonView);
                        miconAlertViewExt.show();
                    }
                });
//                setmainbgcolor.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        mpcolorAlertViewExt = new AlertView("Color Selection", null, "CANCEL",
//                                null,  new String[]{"SAVE"}, getActivity(), AlertView.Style.Alert, alterclick);
//                        mpcolorAlertViewExt.setCancelable(false);
//                        ViewGroup extcolorView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.view_pickcolor, null);
//                        picker = (ColorPicker) extcolorView.findViewById(R.id.view5);
//                        SVBar svBar = (SVBar)extcolorView. findViewById(R.id.view7);
//                        picker.addSVBar(svBar);
//                        ColorDrawable dr = (ColorDrawable) setmainbgcolor.getBackground();
//                        if(dr!=null){
//                            int col_int = dr.getColor();
//                            picker.setColor(col_int);
//                        }
//                        mpcolorAlertViewExt.addExtView(extcolorView);
//                        mpcolorAlertViewExt.show();
//                    }
//                });
                maddingAlertViewExt.addExtView(addingextView);

             /*   Window window = maddingAlertViewExt.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.alpha = 0.9f;
                window.setAttributes(lp);*/

                maddingAlertViewExt.show();
                break;
            case R.id.room_delete:
                if (mAdapter.getCount() > 0) {
                    showToast("pls click one item to delete");
                    deletechoose = true;
                }
                break;
            case R.id.search:
                startActivity(new Intent(getActivity(), NetDaviceListActivity.class));
                break;
            case R.id.main_setting:
                startActivity(new Intent(getActivity(), MainSettingActivity.class));
                break;
            case R.id.main_about:
                Intent about = new Intent(getActivity(), AboutActivity.class);
                startActivity(about);
                break;
            case R.id.main_exit:
                if (exitdialogshow) {
                    mAlertView.dismiss();
                    exitdialogshow = false;
                } else {
                    mAlertView = new AlertView("Warning", "Are you sure to exit?", "CANCEL",
                            new String[]{"YES"}, null, getActivity(), AlertView.Style.Alert, exitclick);
                    mAlertView.setCancelable(false);
                    mAlertView.show();
                    exitdialogshow = true;
                }
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (MainActivity.ACTION_BACKPRESS.equals(action)) {
                if (exitdialogshow) {
                    if (MusicNotification.manager != null) {
                        MusicNotification.manager.cancel(MusicNotification.NOTICE_ID_TYPE_0);
                    }
                    int currentVersion = android.os.Build.VERSION.SDK_INT;
                    if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                        System.exit(0);
                    } else {// android2.1
                        ActivityManager am = (ActivityManager) getActivity().getSystemService(getActivity().ACTIVITY_SERVICE);
                        am.restartPackage(getActivity().getPackageName());
                    }
                } else {
                    mAlertView = new AlertView("Warning", "Are you sure to exit?", "CANCEL",
                            new String[]{"YES"}, null, getActivity(), AlertView.Style.Alert, exitclick);
                    mAlertView.setCancelable(false);
                    mAlertView.show();
                    exitdialogshow = true;
                }
            }
        }
    };
    public com.bigkoo.alertview.OnItemClickListener exitclick = new com.bigkoo.alertview.OnItemClickListener() {
        public void onItemClick(Object o, int position) {
            if (position == -1) {
                mAlertView.dismiss();
                exitdialogshow = false;
            } else if (position == 0) {
                if (MusicNotification.manager != null) {
                    MusicNotification.manager.cancel(MusicNotification.NOTICE_ID_TYPE_0);
                }
                int currentVersion = android.os.Build.VERSION.SDK_INT;
                if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) {
                    Intent startMain = new Intent(Intent.ACTION_MAIN);
                    startMain.addCategory(Intent.CATEGORY_HOME);
                    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(startMain);
                    System.exit(0);
                } else {// android2.1
                    ActivityManager am = (ActivityManager) getActivity().getSystemService(getActivity().ACTIVITY_SERVICE);
                    am.restartPackage(getActivity().getPackageName());
                }
            }
        }
    };
    Runnable getroomrun = new Runnable() {
        @Override
        public void run() {
            //roomid.clear();
            if (roomdata.size() > 0) {
                roomdata.clear();
            }
            mAdapter.clear();
            try {
                if (currPageArea!=null){
                    MainActivity.locationid.setText(""+currPageArea.getArea_name());
                    roomdata = MainActivity.mgr.queryRoomByAreaId(currPageArea.getId());
                }else{
                    MainActivity.locationid.setText("");
                    roomdata = MainActivity.mgr.queryroom();
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), "your database is boarken", Toast.LENGTH_SHORT).show();
            }

            for (int i = 0; i < roomdata.size(); i++) {
                mAdapter.add(roomdata.get(i).room_name, roomdata.get(i).room_icon, roomdata.get(i).room_icon_bg);
            }
            mAdapter.setRoomList(roomdata);
            gv_rooms.setAdapter(mAdapter);

            handle.removeCallbacks(getroomrun);
        }
    };
    public AdapterView.OnItemLongClickListener itemlongclick = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            showPopupMenu(view);
            return true;
        }
    };
    ColorPicker picker;
    TextView setmainbgcolor;
    EditText name;
    ImageView iv_gallery, iv_backgroud,iv_galleryArea,iv_backgroudArea;
    Spinner sp_areaList;
    int spAreaSelectposition=0;

    private void showPopupMenu(final View popview) {
        // View当前PopupMenu显示的相对View的位置
        PopupMenu popupMenu = new PopupMenu(getActivity(), popview);
        // menu布局
        popupMenu.getMenuInflater().inflate(R.menu.main_popup_menu, popupMenu.getMenu());

        // menu的item点击事件
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.main_setting:
                        for (int i = 0; i < roomdata.size(); i++) {
                            if (roomdata.get(i).room_name.equals(((TextView) popview.findViewById(R.id.textView12)).getText().toString().trim())) {
                                settingid = i;
                                settingicon = roomdata.get(i).room_icon;
                                break;
                            }
                        }
                        //房间信息修改
                        msettingAlertViewExt = new AlertView("Settings", null, "CANCEL",
                                null, new String[]{"SAVE"}, getActivity(), AlertView.Style.Alert, alterclick);
                        ViewGroup extView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.setting_maininfo2, null);
                        final ImageView iconchoose = (ImageView) extView.findViewById(R.id.mainroomicon);
                        LinearLayout layout_selectArea=extView.findViewById(R.id.layout_selectArea);
                        layout_selectArea.setVisibility(View.VISIBLE);
                        sp_areaList=extView.findViewById(R.id.sp_areaList);
                        spAreaSelectposition=0;
                        //区域下拉列表
                        List<SaveArea> saveAreaList=MainActivity.mgr.queryArea();
                        if (saveAreaList.size()>0){
                            String[] areaNames=new String[saveAreaList.size()];
                            for (int i=0;i<saveAreaList.size();i++){
                                areaNames[i]=saveAreaList.get(i).getArea_name();
                                if (roomdata.get(settingid).area_id==saveAreaList.get(i).getId()){
                                    spAreaSelectposition=i;
                                }
                            }
                            ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_dropdown_item_1line, areaNames);
                            sp_areaList.setAdapter(adapter);
                            try {
                                sp_areaList.setSelection(spAreaSelectposition);
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }

                        sp_areaList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                spAreaSelectposition=position;
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {

                            }
                        });



                        name = (EditText) extView.findViewById(R.id.mainnameeditText);
                        iv_gallery = extView.findViewById(R.id.iv_gallery);
                        iv_backgroud = extView.findViewById(R.id.iv_backgroud);
                        //  setmainbgcolor=(TextView)extView.findViewById(R.id.mainbgcolor);
                        name.setText(roomdata.get(settingid).room_name);
                        //  setmainbgcolor.setBackgroundColor(ToColor(roomdata.get(settingid).room_icon_bg));

                        //跳转到相册选择图片作为背景
                        iv_gallery.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                gallery();
                            }
                        });

                        //设置图片圆角角度
                        RoundedCorners roundedCorners= new RoundedCorners(10);
                        //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
                        RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(300, 300);
                        //加载图片
                        Glide.with(getContext())
                                .load(roomdata.get(settingid).room_icon_bg)
                                .apply(options)
                                .into(iv_backgroud);


                        iconchoose.setImageDrawable(getResources().getDrawable(getResourdIdByResourdName(getActivity(), settingicon)));
                        iconchoose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                miconAlertViewExt = new AlertView("Icon Selection", null, "CANCEL",
                                        null, null, getActivity(), AlertView.Style.Alert, alterclick);
                                miconAlertViewExt.setCancelable(false);
                                ViewGroup exticonView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.mood_icon_select, null);
                                GridView icongrid = (GridView) exticonView.findViewById(R.id.gridView2);
                                icongrid.setAdapter(new RoomIconAdapter(getActivity()));
                                icongrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        settingicon = iconarray.get(position);
                                        iconchoose.setImageDrawable(getResources().getDrawable(getResourdIdByResourdName(getActivity(), iconarray.get(position))));
                                        miconAlertViewExt.dismiss();
                                    }
                                });
                                miconAlertViewExt.addExtView(exticonView);
                                miconAlertViewExt.show();
                            }
                        });
//                        setmainbgcolor.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                mpcolorAlertViewExt = new AlertView("Color Selection", null, "CANCEL",
//                                        null,  new String[]{"SAVE"}, getActivity(), AlertView.Style.Alert, alterclick);
//                                mpcolorAlertViewExt.setCancelable(false);
//                                ViewGroup extcolorView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.view_pickcolor, null);
//                                picker = (ColorPicker) extcolorView.findViewById(R.id.view5);
//                                SVBar svBar = (SVBar)extcolorView. findViewById(R.id.view7);
//                                picker.addSVBar(svBar);
//                                ColorDrawable dr = (ColorDrawable) setmainbgcolor.getBackground();
//                                if(dr!=null){
//                                    int col_int = dr.getColor();
//                                    picker.setColor(col_int);
//                                }
//                                mpcolorAlertViewExt.addExtView(extcolorView);
//                                mpcolorAlertViewExt.show();
//
//                            }
//                        });
                        msettingAlertViewExt.addExtView(extView);
                        msettingAlertViewExt.show();
                        break;
                }
                return false;

            }
        });
        popupMenu.show();
    }

    public com.bigkoo.alertview.OnItemClickListener alterclick = new com.bigkoo.alertview.OnItemClickListener() {
        public void onItemClick(Object o, int position) {
            if (o == mpcolorAlertViewExt) {
                if (position == -1) {
                    mpcolorAlertViewExt.dismiss();
                } else if (position == 0) {
                    //  setmainbgcolor.setBackgroundColor(picker.getColor());
                }
            } else if (o == miconAlertViewExt) {
                if (position == -1) {
                    miconAlertViewExt.dismiss();
                }
            } else if (o == msettingAlertViewExt) {
                if (position == -1) {
                    msettingAlertViewExt.dismiss();
                } else if (position == 0) {
//                    ColorDrawable dr = (ColorDrawable) setmainbgcolor.getBackground();
//                    int col_int = dr.getColor();
                    Saveroom updateroom = new Saveroom();
                    updateroom.room_id = roomdata.get(settingid).room_id;
                    updateroom.room_name = name.getText().toString().trim();
                    updateroom.room_icon = settingicon;
                    updateroom.room_icon_bg = backgroudPath;//房间背景
                    List<SaveArea> saveAreaList=MainActivity.mgr.queryArea();
                    if (saveAreaList.size()>=spAreaSelectposition){
                        updateroom.area_id=saveAreaList.get(spAreaSelectposition).getId();//区域ID
                    }else {
                        updateroom.area_id=0;
                    }

                    //修改房间
                    MainActivity.mgr.updataroominfo(updateroom);
                    handle.postDelayed(getroomrun, 20);
                }
            } else if (o == maddingAlertViewExt) {
                if (position == -1) {
                    maddingAlertViewExt.dismiss();
                } else if (position == 0) {
                    String x = name.getText().toString().trim();
                    // mAdapter.add(x);
                    ArrayList<Saveroom> tips = new ArrayList<Saveroom>();
                    int room_id;
                    List<Saveroom> saveroomList=MainActivity.mgr.queryroom();
                    if (saveroomList.size() == 0) {
                        room_id = 1;
                    } else {
                        room_id = saveroomList.get(saveroomList.size() - 1).room_id + 1;
                    }
//                    ColorDrawable dr = (ColorDrawable) setmainbgcolor.getBackground();
//                    int tvcolor = dr.getColor();
                    //Toast.makeText(MainActivity.this, Integer.toHexString(tvcolor), Toast.LENGTH_SHORT).show();
                    //添加房间
                    int area_id=0;
                    if (currPageArea!=null){
                        area_id=currPageArea.getId();
                    }
                    Saveroom savenews = new Saveroom(room_id, x, 0, 0, 0, 0, 0, 0, settingicon, backgroudPath, 0, 0, 0, 0,area_id);
                    tips.add(savenews);
                    MainActivity.mgr.addroom(tips);
                    handle.postDelayed(getroomrun, 20);
                }
            }

        }
    };

    public AdapterView.OnItemClickListener itemclick = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (deletechoose) {
                int select = 0;
                for (int i = 0; i < roomdata.size(); i++) {
                    if (((TextView) view.findViewById(R.id.textView12)).getText().toString().trim().equals(roomdata.get(i).room_name)) {
                        select = i;
                        break;
                    }
                }
                //mAdapter.remove(mAdapter.getItem(position));
                MainActivity.mgr.deletefounction("room", roomdata.get(select).room_id);
                MainActivity.mgr.deletefc("light", roomdata.get(select).room_id);
                MainActivity.mgr.deletefc("hvac", roomdata.get(select).room_id);
                MainActivity.mgr.deletefc("mood", roomdata.get(select).room_id);
                MainActivity.mgr.deletefc("moodbutton", roomdata.get(select).room_id);
                MainActivity.mgr.deletefc("fan", roomdata.get(select).room_id);
                MainActivity.mgr.deletefc("curtain", roomdata.get(select).room_id);
                MainActivity.mgr.deletefc("music", roomdata.get(select).room_id);
                MainActivity.mgr.deletefc("song", roomdata.get(select).room_id);
                MainActivity.mgr.deletefc("radio", roomdata.get(select).room_id);
                MainActivity.mgr.deletefc("floorheat", roomdata.get(select).room_id);
                deletechoose = false;
                handle.postDelayed(getroomrun, 20);
            } else {
                int select = 0;
                for (int i = 0; i < roomdata.size(); i++) {
                    if (((TextView) view.findViewById(R.id.textView12)).getText().toString().trim().equals(roomdata.get(i).room_name)) {
                        select = i;
                        break;
                    }
                }
                Intent gotoroom = new Intent();
                gotoroom.putExtra("roomname", roomdata.get(select).room_name);
                gotoroom.putExtra("roomid", roomdata.get(select).room_id);
                gotoroom.setClass(getActivity(), RoomActivity.class);
                startActivity(gotoroom);
                //showToast(new String(Integer.toString(position)));
            }

        }
    };

    public static int getResourdIdByResourdName(Context context, String ResName) {
        int resourceId = 0;
        try {
            Field field = R.mipmap.class.getField(ResName);
            field.setAccessible(true);

            try {
                resourceId = field.getInt(null);
            } catch (IllegalArgumentException e) {
                // log.showLogDebug("IllegalArgumentException:" + e.toString());
            } catch (IllegalAccessException e) {
                // log.showLogDebug("IllegalAccessException:" + e.toString());
            }
        } catch (NoSuchFieldException e) {
            //log.showLogDebug("NoSuchFieldException:" + e.toString());
        }
        return resourceId;
    }

    public int ToColor(String data) {
        int color = 0;
        int rin, gin, bin, ain;
        ain = Integer.parseInt(data.substring(0, 2), 16);
        rin = Integer.parseInt(data.substring(2, 4), 16);
        gin = Integer.parseInt(data.substring(4, 6), 16);
        bin = Integer.parseInt(data.substring(6, 8), 16);
        color = Color.argb(ain, rin, gin, bin);
        return color;
    }

    private static IntentFilter makeUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.setPriority(2147483647);
        intentFilter.addAction(MainActivity.ACTION_BACKPRESS);
        return intentFilter;
    }


    public static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    public static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择 房间背景
    public static final int PHOTO_REQUEST_GALLERY_AREA = 3;// 从相册中选择 区域背景

    /*
     * 从相册获取 房间背景
     */
    public void gallery() {
        //判断是否有获取图片和存储权限
        if (!isGrantExternalRW(getActivity())) {
            return;
        }

        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    /*
     * 从相册获取 区域背景
     */
    public void galleryArea() {
        //判断是否有获取图片和存储权限
        if (!isGrantExternalRW(getActivity())) {
            return;
        }

        // 激活系统图库，选择一张图片
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_GALLERY
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY_AREA);
    }


    //android 6.0 判断是否有存储权限
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        return true;
    }


    /*
     * 判断sdcard是否被挂载
     */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    //选择的图片
    private String backgroudPath="";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //处理房间背景图片选择
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null && data.getData() != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                uri = FileUtil.getPictureUri(getContext(), data);//解决小米手机上获取图片路径为null的情况
                //选择的图片
                backgroudPath = FileUtil.UriToFilePath(getActivity(), uri);//uri转文件绝对路径

                //保存数据库图片路径

                //设置图片圆角角度
                RoundedCorners roundedCorners= new RoundedCorners(10);
                //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
                RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(300, 300);
                //加载图片
                Glide.with(getContext())
                        .load(backgroudPath)
                        .apply(options)
                        .into(iv_backgroud);

            }

        }else if (requestCode == PHOTO_REQUEST_GALLERY_AREA){//处理区域背景图片选择
            // 从相册返回的数据
            if (data != null && data.getData() != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                uri = FileUtil.getPictureUri(getContext(), data);//解决小米手机上获取图片路径为null的情况
                //选择的图片
                areaBgPath = FileUtil.UriToFilePath(getActivity(), uri);//uri转文件绝对路径

                //保存数据库图片路径

                //设置图片圆角角度
                RoundedCorners roundedCorners= new RoundedCorners(10);
                //通过RequestOptions扩展功能,override:采样率,因为ImageView就这么大,可以压缩图片,降低内存消耗
                RequestOptions options=RequestOptions.bitmapTransform(roundedCorners).override(500, 500);
                //加载图片
                Glide.with(getContext())
                        .load(areaBgPath)
                        .apply(options)
                        .into(iv_backgroudArea);

            }
        }
    }


}


