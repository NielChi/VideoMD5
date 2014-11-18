package com.niel.code.videomd5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.niel.code.Tool.FileManager;
import com.niel.code.Tool.ParseMD5;
import com.niel.code.Tool.ParseMD5.OnParseListener;
import com.niel.code.Util.FileControl;
import com.niel.code.widget.MD5Adapter;
import com.niel.code.widget.MD5Structure;

public class VideoMD5 extends Activity {
	
	private final String TAG = "VIDEOMD5";
	private final String TEST_PATH = "/sdcard/DCIM/Camera/";
	private final String ROOT = "/sdcard";
	
	private int position = 0;
	
	private Context mContext;
	private ArrayList<MD5Structure> mp4;
	
	private ParseMD5 parseMD5;
	
	private Status checkStatus;
	enum Status {
		START, PAUSE
	}
	
	private Mode mMode;
	enum Mode {
		LIST, SEARCH
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_md5_main);
		mContext = this;
		if(mp4 == null) {
			mp4 = new ArrayList<MD5Structure>();
		}
		checkStatus = Status.PAUSE;
		mMode = Mode.SEARCH;
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		resumeLayout();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	private void resumeLayout() {
		((ImageButton) findViewById(R.id.videomd5_Search_Button)).setOnClickListener(onClick);
		((ImageButton) findViewById(R.id.videomd5_List_Button)).setOnClickListener(onClick);
		((ImageButton) findViewById(R.id.videomd5_Start_Button)).setOnClickListener(onClick);
		((EditText) findViewById(R.id.videomd5_Edittext)).setOnClickListener(onClick);
		if( ((ListView) findViewById(R.id.videomd5_ListView)).getAdapter() == null) {
			if(mp4 == null) {
				mp4 = new ArrayList<MD5Structure>();
			}
			((ListView) findViewById(R.id.videomd5_ListView)).setAdapter(new MD5Adapter(this, mp4));
		}
	}
	
	private OnClickListener onClick = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.videomd5_Edittext:
				mMode = Mode.SEARCH;
				break;
			case R.id.videomd5_Search_Button:
				mMode = Mode.SEARCH;
				position = 0;
				String video_Path = ((EditText) findViewById(R.id.videomd5_Edittext)).getText().toString().length() == 0 ? null : ((EditText) findViewById(R.id.videomd5_Edittext)).getText().toString();
				ArrayList<HashMap<String, String>> list = FileManager.getMP4List(video_Path);
				if(list.size() == 0) {
					Toast.makeText(mContext, "Sorry not search any MP4 Video.", Toast.LENGTH_SHORT).show();
					return;
				}
				if(((ListView) findViewById(R.id.videomd5_ListView)).getAdapter() != null) {
					((ListView) findViewById(R.id.videomd5_ListView)).setAdapter(new MD5Adapter(mContext, mp4));
				}
				if(mp4.size() != 0) {
					mp4.clear();
					((MD5Adapter) ((ListView) findViewById(R.id.videomd5_ListView)).getAdapter()).notifyDataSetChanged();
				}
				for(HashMap<String, String> item : list) {
					String path = null;
					String fileName = null;
					for(Entry<String, String> entry : item.entrySet()) {
						if(entry.getKey().contains("path")) path = entry.getValue();
						if(entry.getKey().contains("name")) fileName = entry.getValue();
					}
					String md5 = null;
					try {
						md5 = FileControl.readFile(path.split("\\.")[0]+".xml") == null ? null : FileControl.readFile(path.split("\\.")[0]+".xml");
					} catch (IOException e) {
						md5 = null;
					}
					MD5Structure file = new MD5Structure(path, fileName, md5, md5 == null ? "0" : "100");
					file.setType(MD5Structure.TYPE_FILE);
					mp4.add(file);
				}
				((MD5Adapter) ((ListView) findViewById(R.id.videomd5_ListView)).getAdapter()).setAdapterItem(mp4);
				break;
			case R.id.videomd5_Start_Button:
				if(checkStatus == Status.PAUSE) {
					if( (mp4.size() == 0) || (position >= mp4.size()) ) return;
					while( (mp4.get(position).getStatus() == MD5Structure.MD5_FINISH) || (mp4.get(position).getType() != MD5Structure.TYPE_FILE) ) {
						position = position + 1;
						if(position >= mp4.size()) {
							((ImageButton) findViewById(R.id.videomd5_Start_Button)).setImageResource(android.R.drawable.ic_media_play);
							parseMD5 = null;
							checkStatus = Status.PAUSE;
							return;
						}
					}
					checkStatus = Status.START;
					((ImageButton) findViewById(R.id.videomd5_Start_Button)).setImageResource(android.R.drawable.ic_media_pause);
					if(parseMD5 == null) {
						parseMD5 = (ParseMD5) new ParseMD5().execute(mp4.get(position).getPath(), position+"");
						parseMD5.setOnParseListener(parse);
					} else {
						parseMD5.start();
					}
				} else if(checkStatus == Status.START){
					checkStatus = Status.PAUSE;
					((ImageButton) findViewById(R.id.videomd5_Start_Button)).setImageResource(android.R.drawable.ic_media_play);
					if(parse != null)
						parseMD5.pause();
				}
				break;
			case R.id.videomd5_List_Button:
				mMode = Mode.LIST;
				position = 0;
				((ListView) findViewById(R.id.videomd5_ListView)).setOnItemClickListener(itemClick);
				mp4.clear();
				mp4 = FileManager.getList(null);
				((MD5Adapter) ((ListView) findViewById(R.id.videomd5_ListView)).getAdapter()).setAdapterItem(mp4);
				break;
			}
		}
	};
	
	private OnItemClickListener itemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapter, View view, int mPosition,
				long id) {
			if(mMode == Mode.LIST) {
				if(((MD5Structure)((MD5Adapter)adapter.getAdapter()).getItem(mPosition)).getType() == MD5Structure.TYPE_FOLDER) {
					mp4.clear();
					mp4 = FileManager.getList(((MD5Structure)((MD5Adapter)adapter.getAdapter()).getItem(mPosition)).getPath());
					((MD5Adapter) ((ListView) findViewById(R.id.videomd5_ListView)).getAdapter()).setAdapterItem(mp4);
					position = 0;
				} else {
					
				}
			}
		}
	};
	
	private OnParseListener parse = new OnParseListener() {
		
		@Override
		public void onProgressUpdate(int index, int percentage) {
			mp4.get(index).setPercentage(percentage);
			((MD5Adapter)((ListView) findViewById(R.id.videomd5_ListView)).getAdapter()).notifyDataSetChanged();
		}
		
		@Override
		public void onMD5Checksum(int index, String md5) {
			mp4.get(index).setMD5(md5);
			String[] split =  mp4.get(index).getPath().split("\\.");
			String name = split[0]+".xml";
			FileControl.writeFile(name, md5);
			((MD5Adapter)((ListView) findViewById(R.id.videomd5_ListView)).getAdapter()).notifyDataSetChanged();
			while( (mp4.get(position).getStatus() == MD5Structure.MD5_FINISH) || (mp4.get(position).getType() != MD5Structure.TYPE_FILE) ) {
				position = position + 1;
				if(position >= mp4.size()) {
					((ImageButton) findViewById(R.id.videomd5_Start_Button)).setImageResource(android.R.drawable.ic_media_play);
					parseMD5 = null;
					checkStatus = Status.PAUSE;
					return;
				}
			}
			parseMD5 = (ParseMD5) new ParseMD5().execute(mp4.get(position).getPath(), position+"");
			parseMD5.setOnParseListener(parse);
		}
	};
}