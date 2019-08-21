package com.shima.smartbushome.mainsetting;


import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.shima.smartbushome.R;
import com.shima.smartbushome.assist.Adapter.MusicBGAdapter;
import com.shima.smartbushome.assist.holocolorpicker.ColorPicker;
import com.shima.smartbushome.assist.holocolorpicker.SVBar;

import java.lang.reflect.Field;

/**
 * A simple {@link Fragment} subclass.
 */
public class ThemeFragment extends Fragment implements View.OnClickListener{

    TextView mainbgcolor,selecttextview,roombgcolor,lightbgcolor,hvacbgcolor,fanbgcolor,
            curtainbgcolor,musicbgcolor,moodbgcolor,otherbgcolor,mediabgcolor,niobgcolor;
    Button themesave;
    View view;
    private AlertView mpcolorAlertViewExt;
    ColorPicker picker;
    int color[]=new int[11];
    String musicbg="";
    public ThemeFragment() {
        // Required empty public constructor
    }

    public static ThemeFragment newInstance() {
        ThemeFragment pageFragment = new ThemeFragment();
        return pageFragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view=inflater.inflate(R.layout.fragment_theme, container, false);
        mainbgcolor=(TextView)view.findViewById(R.id.mainbgcolor);
        roombgcolor=(TextView)view.findViewById(R.id.roombgcolor);
        lightbgcolor=(TextView)view.findViewById(R.id.lightbgcolor);
        hvacbgcolor=(TextView)view.findViewById(R.id.hvacbgcolor);
        fanbgcolor=(TextView)view.findViewById(R.id.fanbgcolor);
        curtainbgcolor=(TextView)view.findViewById(R.id.curtainbgcolor);
        musicbgcolor=(TextView)view.findViewById(R.id.musicbgcolor);
        moodbgcolor=(TextView)view.findViewById(R.id.moodbgcolor);
        otherbgcolor=(TextView)view.findViewById(R.id.otherbgcolor);
        mediabgcolor=(TextView)view.findViewById(R.id.mediabgcolor);
        niobgcolor=(TextView)view.findViewById(R.id.niobgcolor);
        themesave=(Button)view.findViewById(R.id.themesave);

        SharedPreferences sharedPre = getActivity().getSharedPreferences("pagesbgcolor", getActivity().MODE_PRIVATE);
        color[0] = sharedPre.getInt("mainbgcolor", 0xFF000000);
        color[1] = sharedPre.getInt("roombgcolor", 0xFF000000);
        color[2] = sharedPre.getInt("lightbgcolor", 0xFF000000);
        color[3] = sharedPre.getInt("hvacbgcolor", 0xFF000000);
        color[4] = sharedPre.getInt("fanbgcolor", 0xFF000000);
        color[5] = sharedPre.getInt("curtainbgcolor", 0xFF000000);
        musicbg = sharedPre.getString("musicbg", "music_bg10");
        color[7] = sharedPre.getInt("moodbgcolor", 0xFF000000);
        color[8] = sharedPre.getInt("otherbgcolor", 0xFF000000);
        color[9] = sharedPre.getInt("mediabgcolor", 0xFF000000);
        color[10] = sharedPre.getInt("niobgcolor", 0xFF000000);


        mainbgcolor.setBackgroundColor(color[0]);
        roombgcolor.setBackgroundColor(color[1]);
        lightbgcolor.setBackgroundColor(color[2]);
        hvacbgcolor.setBackgroundColor(color[3]);
        fanbgcolor.setBackgroundColor(color[4]);
        curtainbgcolor.setBackgroundColor(color[5]);
        if(Build.VERSION.SDK_INT >= 16){
            musicbgcolor.setBackground(getResources().getDrawable(getResourdIdByResourdName(musicbg)));
        }else{
            musicbgcolor.setBackgroundDrawable(getResources().getDrawable(getResourdIdByResourdName(musicbg)));
        }
        moodbgcolor.setBackgroundColor(color[7]);
        otherbgcolor.setBackgroundColor(color[8]);
        mediabgcolor.setBackgroundColor(color[9]);
        niobgcolor.setBackgroundColor(color[10]);

        mainbgcolor.setOnClickListener(this);
        roombgcolor.setOnClickListener(this);
        lightbgcolor.setOnClickListener(this);
        hvacbgcolor.setOnClickListener(this);
        fanbgcolor.setOnClickListener(this);
        curtainbgcolor.setOnClickListener(this);
        musicbgcolor.setOnClickListener(this);
        moodbgcolor.setOnClickListener(this);
        otherbgcolor.setOnClickListener(this);
        mediabgcolor.setOnClickListener(this);
        niobgcolor.setOnClickListener(this);

        themesave.setOnClickListener(this);
        return view;
    }

    AlertView iconalter;
    int bg_position=0;
    public void onClick(View v){
        switch (v.getId()){
            case R.id.themesave:
                savecolorInfo(getActivity(),color);
                savemusicbgInfo(getContext(),musicbg);
                Toast.makeText(getActivity(), "saved", Toast.LENGTH_SHORT).show();
                break;
            case R.id.musicbgcolor:
                LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                iconalter = new AlertView("BG Selection", null, "CANCEL", new String[]{"SAVE"}, null, getContext(), AlertView.Style.Alert,
                        null);
                View selfviewx = inflater.inflate(R.layout.mood_icon_select, null);
                GridView icongrid = (GridView) selfviewx.findViewById(R.id.gridView2);
                icongrid.setAdapter(new MusicBGAdapter(getContext()));
                icongrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        bg_position=position+1;
                        musicbg="music_bg"+bg_position;
                        if(Build.VERSION.SDK_INT >= 16){
                            musicbgcolor.setBackground(getResources().getDrawable(getResourdIdByResourdName(musicbg)));
                        }else{
                            musicbgcolor.setBackgroundDrawable(getResources().getDrawable(getResourdIdByResourdName(musicbg)));
                        }
                        //Toast.makeText(getContext(), "select BG "+(bg_position), Toast.LENGTH_SHORT).show();
                        iconalter.dismiss();
                    }
                });
                iconalter.addExtView(selfviewx);
                iconalter.show();
                break;
            default:
                selecttextview=(TextView)view.findViewById(v.getId());
                mpcolorAlertViewExt = new AlertView("Color Selection", null, "CANCEL",
                        null,  new String[]{"SAVE"}, getActivity(), AlertView.Style.Alert, alterclick);
                mpcolorAlertViewExt.setCancelable(false);
                ViewGroup extcolorView = (ViewGroup) LayoutInflater.from(getActivity()).inflate(R.layout.view_pickcolor, null);
                picker = (ColorPicker) extcolorView.findViewById(R.id.view5);
                SVBar svBar = (SVBar)extcolorView. findViewById(R.id.view7);
                picker.addSVBar(svBar);
                ColorDrawable dr = (ColorDrawable) (selecttextview).getBackground();
                if(dr!=null){
                    int col_int = dr.getColor();
                    picker.setColor(col_int);
                }
                mpcolorAlertViewExt.addExtView(extcolorView);
                mpcolorAlertViewExt.show();
                break;
        }
    }

    public com.bigkoo.alertview.OnItemClickListener alterclick=new com.bigkoo.alertview.OnItemClickListener(){
        public void onItemClick(Object o,int position) {
            if(o==mpcolorAlertViewExt){
                if(position==-1){
                    mpcolorAlertViewExt.dismiss();
                }else if(position==0){
                    setcolor(picker.getColor(),selecttextview.getId());
                    selecttextview.setBackgroundColor(picker.getColor());
                }
            }
        }
    };

    public void setcolor(int value,int viewid){
        int position=0;
        switch (viewid){
            case R.id.mainbgcolor:
                position=0;
                break;
            case R.id.roombgcolor:
                position=1;
                break;
            case R.id.lightbgcolor:
                position=2;
                break;
            case R.id.hvacbgcolor:
                position=3;
                break;
            case R.id.fanbgcolor:
                position=4;
                break;
            case R.id.curtainbgcolor:
                position=5;
                break;
            case R.id.musicbgcolor:
                position=6;
                break;
            case R.id.moodbgcolor:
                position=7;
                break;
            case R.id.otherbgcolor:
                position=8;
                break;
            case R.id.mediabgcolor:
                position=9;
                break;
            case R.id.niobgcolor:
                position=10;
                break;

        }
        color[position]=value;
    }

    public void savecolorInfo(Context context,int value[]){
        //获取SharedPreferences对象
        SharedPreferences sharedPre=context.getSharedPreferences("pagesbgcolor", context.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor=sharedPre.edit();
        //设置参数
        editor.putInt("mainbgcolor", value[0]);
        editor.putInt("roombgcolor", value[1]);
        editor.putInt("lightbgcolor", value[2]);
        editor.putInt("hvacbgcolor", value[3]);
        editor.putInt("fanbgcolor", value[4]);
        editor.putInt("curtainbgcolor", value[5]);
        editor.putInt("moodbgcolor", value[7]);
        editor.putInt("otherbgcolor", value[8]);
        editor.putInt("mediabgcolor", value[9]);
        editor.putInt("niobgcolor", value[10]);
        //提交
        editor.commit();
    }
    public void savemusicbgInfo(Context context,String bgname){
        //获取SharedPreferences对象
        SharedPreferences sharedPre=context.getSharedPreferences("pagesbgcolor", context.MODE_PRIVATE);
        //获取Editor对象
        SharedPreferences.Editor editor=sharedPre.edit();
        //设置参数
        editor.putString("musicbg", bgname);
        //提交
        editor.commit();
    }
    public int getResourdIdByResourdName(String ResName){
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
}
