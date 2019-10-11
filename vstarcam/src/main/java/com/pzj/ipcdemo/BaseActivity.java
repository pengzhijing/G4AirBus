package com.pzj.ipcdemo;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class BaseActivity extends Activity {
	  @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	  
	  public void showToast(String content){
			Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
		}
		public void showToast(int rid){
			Toast.makeText(this, getResources().getString(rid), Toast.LENGTH_LONG).show();
		}
	}
