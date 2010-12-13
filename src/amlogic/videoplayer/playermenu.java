package amlogic.videoplayer;
import android.os.storage.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.subtitleparser.Subtitle;
import com.subtitleparser.SubtitleUtils;
import com.subtitleview.SubtitleView;
import android.content.Context;
import amlogic.playerservice.Errorno;
import amlogic.playerservice.MediaInfo;
import amlogic.playerservice.Player;
import amlogic.playerservice.ResumePlay;
import amlogic.playerservice.SettingsVP;
import amlogic.playerservice.VideoInfo;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.util.Log;
import android.view.*;
import android.widget.*;

public class playermenu extends Activity {
	private static String TAG = "playermenu";
	private static String codec_mips;
	private static String InputFile = "/sys/class/audiodsp/codec_mips";
	private static String OutputFile = "/sys/devices/system/cpu/cpu0/cpufreq/scaling_min_freq";
	
    /** Called when the activity is first created. */
	private int totaltime = 0;
	private int curtime = 0;
	private int position = 0;
	private int cur_audio_stream = 0;
	private int total_audio_num = 0;
	private int ScreenOffTimeoutValue = 0;
	private boolean backToFileList = false;
	private boolean progressSliding = false;
	private boolean INITOK = false;
	private boolean FF_FLAG = false;
	private boolean NOT_FIRSTTIME = false;
    
    //for repeat mode;
    private static int m_playmode = 1;
    private static final int REPEATLIST = 1;
    private static final int REPEATONE = 2;
  
    private SeekBar myProgressBar = null;
    private ImageButton play = null;
	private TextView cur_time = null;
	private TextView total_time = null;
	private LinearLayout infobar = null;
	private LinearLayout morbar = null;
	private LinearLayout subbar = null;
	private LinearLayout otherbar = null;
	
	Timer timer = new Timer();
	Toast toast = null;
	public Handler myHandler;
	public MediaInfo bMediaInfo = null;
	private static int PRE_NEXT_FLAG = 0;
	private int player_status = VideoInfo.PLAYER_UNKNOWN;
	
	//for subtitle
	private SubtitleUtils subMange = null;
	private SubtitleView  subTitleView = null;
	private subview_set   sub_para = null;
	private int sub_switch_state = 0;
	private int sub_font_state = 0;
	private int sub_color_state = 0;
	private TextView t_subswitch =null ;
	private TextView t_subsfont=null ;
	private TextView t_subscolor=null ;
	private TextView morebar_tileText =null;
	private String color_text[]={ "white","yellow","blue"};
	private String[] m_display = {"4:3","16:9","",""};
	private String[] m_brightness= {"1","2","3","4"};		
	private String[] m_repeat= {"repeat list ","repeat one","",""};	
	
    private void videobar() {
    		
    		setContentView(R.layout.layout_imagebutton);
    		
    		subTitleView = (SubtitleView) findViewById(R.id.subTitle_more);
        	subTitleView.setTextColor(sub_para.color);
        	subTitleView.setTextSize(sub_para.font);
        	openFile(sub_para.filepath);
        	
    		subbar = (LinearLayout)findViewById(R.id.LinearLayout_sub);
    		subbar.setVisibility(View.GONE);
    		
    		otherbar = (LinearLayout)findViewById(R.id.LinearLayout_other);
    		morebar_tileText = (TextView)findViewById(R.id.more_title);
    		otherbar.setVisibility(View.GONE);
    		
    		morbar = (LinearLayout)findViewById(R.id.morebarLayout);
    		ImageButton panelortv = (ImageButton) findViewById(R.id.ImageButton01);
            panelortv.setOnClickListener(new View.OnClickListener() 
    	    {
                public void onClick(View v) 
                {
                	sendBroadcast( new Intent("com.amlogic.HdmiSwitch.FAST_SWITCH"));
                } 
    	    });
            
            ImageButton playmode = (ImageButton) findViewById(R.id.ImageButton02);
            playmode.setOnClickListener(new View.OnClickListener() 
    	    {
                public void onClick(View v) 
                {
                	otherbar.setVisibility(View.VISIBLE);
                	morbar.setVisibility(View.GONE);
                	morebar_tileText.setText("play mode");
                	ListView listView = (ListView)findViewById(R.id.AudioListView);
                    listView.setAdapter(new ArrayAdapter<String>(playermenu.this, 
                    		R.layout.list_row,m_repeat));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                    	 public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    	{
                    		 if (position == 0)
                        		 m_playmode = REPEATLIST;
                        	 else if (position == 1)
                        		 m_playmode = REPEATONE;
                    		 
                    		 otherbar.setVisibility(View.GONE);
                    		 morbar.setVisibility(View.VISIBLE);
                    	}
                    });
                } 
    	    });
            ImageButton audiotrace = (ImageButton) findViewById(R.id.ImageButton03);
            audiotrace.setOnClickListener(new View.OnClickListener() 
    	    {
                public void onClick(View v) 
                {
                	otherbar.setVisibility(View.VISIBLE);
                	morbar.setVisibility(View.GONE);
                	
                	morebar_tileText.setText("audio trace");
                	ListView listView = (ListView)findViewById(R.id.AudioListView);
                	if (AudioTrackOperation.AudioStreamFormat.size() < bMediaInfo.getAudioTrackCount())
                		AudioTrackOperation.setAudioStream(bMediaInfo);
                    listView.setAdapter(new ArrayAdapter<String>(playermenu.this, 
                    		R.layout.list_row,
                    		AudioTrackOperation.AudioStreamFormat));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                    	public void onItemClick(AdapterView<?> arg0, View arg1,
            					int arg2, long arg3) {
            				// TODO Auto-generated method stub
                    		try {
                    			m_Amplayer.SwitchAID(AudioTrackOperation.AudioStreamInfo.get(arg2).audio_id);
                    			Log.d("audiostream","change audio stream to: " + arg2);
                    			
                			} catch (RemoteException e) {
                				e.printStackTrace();
                			}
                    		try {
                    			m_Amplayer.GetMediaInfo();
                    		} catch (RemoteException e) {
                				e.printStackTrace();
                			}
                    		
                    		otherbar.setVisibility(View.GONE);
                        	morbar.setVisibility(View.VISIBLE);
            			}	
                    });
                } 
    	    });
            ImageButton sutitle = (ImageButton) findViewById(R.id.ImageButton04);
            sutitle.setOnClickListener(new View.OnClickListener() 
    	    {
                public void onClick(View v) 
                {
                	if(sub_para.totalnum<=0)
                	{
                		Toast toast =Toast.makeText(playermenu.this, "No subtitle!",Toast.LENGTH_SHORT );
                		toast.setGravity(Gravity.BOTTOM,110,0);
                		toast.setDuration(0x00000001);
                		toast.show();
                	    return;
                	}
                	subbar.setVisibility(View.VISIBLE);
                	morbar.setVisibility(View.GONE);
                	subtitle_control();
                }

				private void subtitle_control() {
					t_subswitch =(TextView)findViewById(R.id.sub_swith111);
  					t_subsfont =(TextView)findViewById(R.id.sub_font111);
  					t_subscolor =(TextView)findViewById(R.id.sub_color111);
  					
  					sub_switch_state = sub_para.curid;
	                sub_font_state = sub_para.font;
	                
	                if(sub_para.color==android.graphics.Color.WHITE)
	                   sub_color_state =0;
	                else if(sub_para.color==android.graphics.Color.YELLOW)
	                   sub_color_state =1;
	                else
	                	sub_color_state =2;
	                	
	                
	                if(sub_para.curid==sub_para.totalnum)
							t_subswitch.setText("off");
						 else
							t_subswitch.setText(String.valueOf(sub_para.curid+1)+"/"+String.valueOf(sub_para.totalnum));
  					
  					t_subsfont.setText(String.valueOf(sub_font_state));
  					t_subscolor.setText(color_text[sub_color_state]);
  					
  					Button ok = (Button) findViewById(R.id.button_ok);
  					ok.setOnClickListener(new View.OnClickListener() 
  		    	    {
  		                public void onClick(View v) 
  		                {
  		                	sub_para.curid = sub_switch_state;
  		                	sub_para.font = sub_font_state;
  		                	
  		             	  if(sub_para.curid==sub_para.totalnum )
  		          		       sub_para.filepath =null;
  		             	  else
  		             		   sub_para.filepath =subMange.getSubPath(sub_para.curid);
  		             	  
  		             	  if(sub_color_state==0)
  		             		    sub_para.color =android.graphics.Color.WHITE;
  		             	  else if(sub_color_state==1) 
  		             		  	sub_para.color =android.graphics.Color.YELLOW;
  		             	  else
  		             		  	sub_para.color =android.graphics.Color.BLUE;
  		             	  
  		                	subbar.setVisibility(View.GONE);
  		                	videobar();
  		                } 
  		    	    });
  					Button cancel = (Button) findViewById(R.id.button_canncel);
  					cancel.setOnClickListener(new View.OnClickListener() 
  		    	    {
  		                public void onClick(View v) 
  		                {
  		                	
  		                	subbar.setVisibility(View.GONE);
  		                	videobar();
  		                } 
  		    	    });
  					ImageButton Bswitch_l = (ImageButton) findViewById(R.id.switch_l);	
  					ImageButton Bswitch_r = (ImageButton) findViewById(R.id.switch_r);
  					ImageButton Bfont_l = (ImageButton) findViewById(R.id.font_l);	
  					ImageButton Bfont_r = (ImageButton) findViewById(R.id.font_r);
  					ImageButton Bcolor_l = (ImageButton) findViewById(R.id.color_l);	
  					ImageButton Bcolor_r = (ImageButton) findViewById(R.id.color_r);
  					Bswitch_l.setOnClickListener(new View.OnClickListener() 
  					{
  						 public void onClick(View v) 
   		                {
  							 if(sub_switch_state <= 0)
  								sub_switch_state =sub_para.totalnum;
   							 else
   								sub_switch_state --;
   							
  							 if(sub_switch_state==sub_para.totalnum)
  								t_subswitch.setText("off");
  							 else
  								t_subswitch.setText(String.valueOf(sub_switch_state+1)+"/"+String.valueOf(sub_para.totalnum));
   		                } 
  					});
  					Bswitch_r.setOnClickListener(new View.OnClickListener() 
  					{
  						 public void onClick(View v) 
   		                {
  							if(sub_switch_state >= sub_para.totalnum)
  								sub_switch_state =0;
   							 else
   								sub_switch_state ++;
   							
  							 if(sub_switch_state==sub_para.totalnum)
  								t_subswitch.setText("off");
  							 else
  								t_subswitch.setText(String.valueOf(sub_switch_state+1)+"/"+String.valueOf(sub_para.totalnum));;
   		                } 
  					});
  					Bfont_l.setOnClickListener(new View.OnClickListener() 
  					{
  						 public void onClick(View v) 
   		                {
  							 if(sub_font_state > 12)
  							    sub_font_state =sub_font_state-2;
  							 else
  								sub_font_state =30;
  							 
  							t_subsfont.setText(String.valueOf(sub_font_state));
  							 
   		                } 
  					});
  					Bfont_r.setOnClickListener(new View.OnClickListener() 
  					{
  						 public void onClick(View v) 
   		                {
  							if(sub_font_state < 30)
  							    sub_font_state =sub_font_state +2;
  							else
  								sub_font_state =12;
  							
  							t_subsfont.setText(String.valueOf(sub_font_state));
   		                } 
  					});
  					
  					Bcolor_l.setOnClickListener(new View.OnClickListener() 
  					{
  						 public void onClick(View v) 
   		                {
  							 if(sub_color_state<= 0)
  								sub_color_state=2;
   							 else 
   								sub_color_state-- ;
   							 
   							t_subscolor.setText(color_text[sub_color_state]);
   		                } 
  					});
  					Bcolor_r.setOnClickListener(new View.OnClickListener() 
  					{
  						 public void onClick(View v) 
   		                {
  							 if(sub_color_state>=2)
   								sub_color_state=0;
    						  else 
    							sub_color_state++ ;
    							 
  							t_subscolor.setText(color_text[sub_color_state]);
   		                } 
  					});
				
				} 
    	    });
            ImageButton display = (ImageButton) findViewById(R.id.ImageButton05);
            display.setOnClickListener(new View.OnClickListener() 
    	    {
                public void onClick(View v) 
                {
                	otherbar.setVisibility(View.VISIBLE);
                	morbar.setVisibility(View.GONE);
                	
                	morebar_tileText.setText("display mode");
                	ListView listView = (ListView)findViewById(R.id.AudioListView);
                    listView.setAdapter(new ArrayAdapter<String>(playermenu.this, 
                    		R.layout.list_row,m_display));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                    	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    	{
                    		 otherbar.setVisibility(View.GONE);
                         	 morbar.setVisibility(View.VISIBLE);
                    	}
                    });    
                } 
    	    });
            ImageButton brigtness = (ImageButton) findViewById(R.id.ImageButton06);
            brigtness.setOnClickListener(new View.OnClickListener() 
    	    {
                public void onClick(View v) 
                {
                	
                	otherbar.setVisibility(View.VISIBLE);
                	morbar.setVisibility(View.GONE);
                	morebar_tileText.setText("brightness setting");
                	ListView listView = (ListView)findViewById(R.id.AudioListView);
                    listView.setAdapter(new ArrayAdapter<String>(playermenu.this, 
                    		R.layout.list_row,m_brightness));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                    {
                    	 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    		 WindowManager.LayoutParams lp = getWindow().getAttributes();	
                        	 switch(position)
                        	 {
                        	 case 0:
                        		 lp.screenBrightness = 0.2f;
                        		 break;
                        	 case 1:
                        		 lp.screenBrightness = 0.5f;
                        		 break;
                        	 case 2:
                        		 lp.screenBrightness = 0.7f;
                        		 break;
                        	 case 3:
                        		 lp.screenBrightness = 1.0f;
                        		 break;	 
                        	 default:
                        		  break;
                        	 }
                        	 getWindow().setAttributes(lp);
                    		otherbar.setVisibility(View.GONE);
                        	morbar.setVisibility(View.VISIBLE);
                    	}
                    });
                } 
    	    }); 
            ImageButton backtovidebar = (ImageButton) findViewById(R.id.ImageButton07);
            backtovidebar.setOnClickListener(new View.OnClickListener() 
    	    {
                public void onClick(View v) 
                {
                	setContentView(R.layout.infobar);
                	initinfobar();
                	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                } 
    	    }); 
    	}
    
    public boolean onKeyDown(int keyCode, KeyEvent msg) 
    {
    	if (keyCode == KeyEvent.KEYCODE_BACK) 
    	{
    		if (morbar!=null) 
	        {
	        	morbar=null;
	        	setContentView(R.layout.infobar);
	        	initinfobar();
	        	return(true);
	          
	        }
    		else
    		{
    			if(m_Amplayer == null)
					return (true);
    			//stop play
    			Amplayer_stop();
				return super.onKeyDown(keyCode, msg);
    		}
    	}
        else
		 return super.onKeyDown(keyCode, msg);
    }
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.infobar);
        toast = Toast.makeText(playermenu.this, "", Toast.LENGTH_SHORT);
        closeScreenOffTimeout();
        SettingsVP.init(this);
		subinit();
		initinfobar();
		
        resumePlay();
    }
    
    protected void subinit()
    {
    
    	subMange = new SubtitleUtils(PlayList.getinstance().getcur());
    	sub_para= new subview_set();
         
    	sub_para.totalnum =subMange.getSubTotal();
    	//sub_para.totalnum =0;
    	sub_para.curid =0;
    	sub_para.color =android.graphics.Color.WHITE;
    	sub_para.font=20;
    	if(sub_para.totalnum>0)
    		sub_para.filepath =subMange.getSubPath(sub_para.curid);
    	else
    		sub_para.filepath =null;
    
    }
    
    protected void initinfobar()
    {
    	//set subtitle
    	subTitleView = (SubtitleView) findViewById(R.id.subTitle);
    	subTitleView.setTextColor(sub_para.color);
    	subTitleView.setTextSize(sub_para.font);
    	openFile(sub_para.filepath);
	
        ImageButton browser = (ImageButton)findViewById(R.id.BrowserBtn);
        ImageButton more = (ImageButton)findViewById(R.id.moreBtn);
        ImageButton preItem = (ImageButton)findViewById(R.id.PreBtn);
        ImageButton nextItem = (ImageButton)findViewById(R.id.NextBtn);
        play = (ImageButton)findViewById(R.id.PlayBtn);
        ImageButton fastforword = (ImageButton)findViewById(R.id.FastForward);
        ImageButton fastreverse = (ImageButton)findViewById(R.id.FastReverse);
        infobar = (LinearLayout)findViewById(R.id.infobarLayout);
        myProgressBar = (SeekBar)findViewById(R.id.SeekBar02);
    	cur_time = (TextView)findViewById(R.id.TextView03);
    	total_time = (TextView)findViewById(R.id.TextView04);
    	cur_time.setText(secToTime(curtime));
    	total_time.setText(secToTime(totaltime));
        
    	
        browser.setOnClickListener(new ImageButton.OnClickListener()
    	{
			public void onClick(View v) 
			{
			// TODO Auto-generated method stub
				Intent selectFileIntent = new Intent();
				selectFileIntent.setClass(playermenu.this, FileList.class);
					
				//stop play
				if(m_Amplayer != null)
					Amplayer_stop();
				startActivity(selectFileIntent);
				backToFileList = true;
				playermenu.this.finish();
			}
		});
        
        preItem.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!INITOK)
					return;
				ResumePlay.saveResumePara(PlayList.getinstance().getcur(), curtime);
				String filename = PlayList.getinstance().moveprev();
				toast.cancel();
				toast.setText(filename);
				toast.show();
				if(m_Amplayer == null)
					return;
				//stop play
				else
					Amplayer_stop();
				PRE_NEXT_FLAG = 1;
			}
        });
        
        nextItem.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (!INITOK)
					return;
				ResumePlay.saveResumePara(PlayList.getinstance().getcur(), curtime);
				String filename = PlayList.getinstance().movenext();
				toast.cancel();
				toast.setText(filename); 
				toast.show();
				if(m_Amplayer == null)
					return;
				else
					Amplayer_stop();
				PRE_NEXT_FLAG = 1;
			}
        });
        
        if (player_status == VideoInfo.PLAYER_RUNNING)
			play.setBackgroundResource(R.drawable.pause_button);
        play.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (player_status == VideoInfo.PLAYER_RUNNING)
				{
					try	{
						m_Amplayer.Pause();
					} catch(RemoteException e) {
						e.printStackTrace();
					}
				}
				else if (player_status == VideoInfo.PLAYER_PAUSE)
				{
					try	{
						m_Amplayer.Resume();
					} catch(RemoteException e)	{
						e.printStackTrace();
					}
				}
				else if (player_status == VideoInfo.PLAYER_SEARCHING)
				{
					try	{
						if (FF_FLAG)
							m_Amplayer.FastForward(0);
						else
							m_Amplayer.BackForward(0);
					} catch(RemoteException e) {
						e.printStackTrace();
					}
				}
			}
        });
                
        fastforword.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				if (!INITOK)
					return;
				if (player_status == VideoInfo.PLAYER_SEARCHING)
				{
					try	{
						m_Amplayer.FastForward(0);
					} catch(RemoteException e) {
						e.printStackTrace();
					}
				}
				else
				{
					try	{
						m_Amplayer.FastForward(2);
					} catch(RemoteException e) {
						e.printStackTrace();
					}
					FF_FLAG = true;
				}
			}
        });
        
        fastreverse.setOnClickListener(new Button.OnClickListener(){
			public void onClick(View v) {
				if (!INITOK)
					return;
				if (player_status == VideoInfo.PLAYER_SEARCHING)
				{
					try	{
						m_Amplayer.BackForward(0);
					} catch(RemoteException e) {
						e.printStackTrace();
					}
				}
				else
				{
					try	{
						m_Amplayer.BackForward(2);
					} catch(RemoteException e) {
						e.printStackTrace();
					}
					FF_FLAG = false;
				}
			}
        });
        
        more.setOnClickListener(new ImageButton.OnClickListener()
    	{
			public void onClick(View v) 
			{
				// TODO Auto-generated method stub
				videobar();
			}
		});
        
        if (curtime != 0)
        	myProgressBar.setProgress(curtime*100/totaltime);
        myProgressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() 
        {
    
			public void onStopTrackingTouch(SeekBar seekBar) 
			{
				// TODO Auto-generated method stub
				int dest = myProgressBar.getProgress();
				int pos = totaltime * dest / 100;
				try
				{
					m_Amplayer.Seek(pos);
				}
				catch(RemoteException e)
				{
					e.printStackTrace();
				}
				waitForHide();
				progressSliding = false;
			}
			
			public void onStartTrackingTouch(SeekBar seekBar) 
			{
				// TODO Auto-generated method stub
				timer.cancel();
				progressSliding = true;
			}
			
			public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) 
			{
				// TODO Auto-generated method stub
				
			}
		});
        
        waitForHide();
    }
	
    public static int setCodecMips()
	{
    	int tmp;
    	String buf = null;
		File file = new File(InputFile);
		if (!file.exists()) {        	
        	return 0;
        }
		file = new File(OutputFile);
		if (!file.exists()) {        	
        	return 0;
        }
		//read
		try
		{
			BufferedReader in = new BufferedReader(new FileReader(InputFile), 32);
			try
			{
				codec_mips = in.readLine();
				Log.d(TAG, "file content:"+codec_mips);
				tmp = Integer.parseInt(codec_mips)*2;
				buf = Integer.toString(tmp);
			} finally {
    			in.close();
    		} 
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException when read "+InputFile);
		} 
		
		//write
		try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(OutputFile), 32);
    		try
    		{
    			out.write(buf);    
    			Log.d(TAG, "set codec mips ok:"+buf);
    		} finally {
				out.close();
			}
			 return 1;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException when write "+OutputFile);
			return 0;
		}
	}
    
    public static int setDefCodecMips()
    {
    	File file = new File(OutputFile);
		if (!file.exists()) {        	
        	return 0;
        }
    	try
		{
			BufferedWriter out = new BufferedWriter(new FileWriter(OutputFile), 32);
    		try
    		{
    			out.write(codec_mips);    
    			Log.d(TAG, "set codec mips ok:"+codec_mips);
    		} finally {
				out.close();
			}
			 return 1;
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "IOException when write "+OutputFile);
			return 0;
		}
    }
    
    protected void closeScreenOffTimeout()
    {
    	try {
			ScreenOffTimeoutValue = Settings.System.getInt(getContentResolver(), System.SCREEN_OFF_TIMEOUT);
		} catch (SettingNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Settings.System.putInt(getContentResolver(), System.SCREEN_OFF_TIMEOUT, -1);
    }
    
    protected void openScreenOffTimeout()
    {
    	Settings.System.putInt(getContentResolver(), System.SCREEN_OFF_TIMEOUT, ScreenOffTimeoutValue);
    }
    
    protected void waitForHide()	//infobar auto hide
    {
    	final Handler handler = new Handler(){   
    		  
            public void handleMessage(Message msg) {   
                switch (msg.what) {       
                case 0x3c:       
                	hide_infobar();
                    break;       
                }       
                super.handleMessage(msg);   
            }
               
        };   
        TimerTask task = new TimerTask(){   
      
            public void run() {   
                Message message = new Message();       
                message.what = 0x3c;       
                handler.sendMessage(message);     
            }   
               
        };   
        
        timer.cancel();
        timer = new Timer();
    	timer.schedule(task, 3000);
    }
    
    protected void hide_infobar()
    {
    	infobar.setVisibility(View.GONE);
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,   
    			WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    
    protected void show_menu()
    {
    	infobar.setVisibility(View.VISIBLE);
    	getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    
    public boolean onTouchEvent (MotionEvent event)
    {
    	super.onTouchEvent(event);
    	if (event.getAction() == MotionEvent.ACTION_DOWN)
    	{
	    	if (infobar.getVisibility() == View.VISIBLE)
	    		hide_infobar();
	    	else {
		    	show_menu();
		    	waitForHide();
	    	}
    	}
    	return true;
    }
    
    private String secToTime(int i)
	{
		String retStr = null;
		int hour = 0;
		int minute = 0;
		int second = 0;
		if (i <= 0)
			return "00:00:00";
		else
		{
			minute = i/60;
			if (minute < 60)
			{
				second = i%60;
				retStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
			}
			else
			{
				hour = minute/60;
				if (hour > 99)
					return "99:59:59";
				minute = minute%60;
				second = i%60;
				retStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
			}
		}
		return retStr;
	}
	
	private String unitFormat(int i)
	{
		String retStr = null;
		if (i >= 0 && i < 10)
			retStr = "0" + Integer.toString(i);
		else
			retStr = Integer.toString(i);
		return retStr;
    }
	
	@Override
    public void onDestroy() {
        ResumePlay.saveResumePara(PlayList.getinstance().getcur(), curtime);
        Amplayer_stop();
        StopPlayerService();
        setDefCodecMips();
        openScreenOffTimeout();
        
        super.onDestroy();
    }

	@Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "...........................onstop.........1237................");
        StorageManager m_storagemgr = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        m_storagemgr.unregisterListener(mListener);
        if (!backToFileList)
        	PlayList.getinstance().rootPath =null;
        finish();
    }
    
	//=========================================================
    private Messenger m_PlayerMsg = new Messenger(new Handler()
    {
    	public void handleMessage(Message msg)
    	{
    		switch(msg.what)
    		{
    			case VideoInfo.TIME_INFO_MSG:
    				Log.i(TAG,"get time "+secToTime((msg.arg1)/90000));
    		    	cur_time.setText(secToTime((msg.arg1)/90000));
    		    	total_time.setText(secToTime(msg.arg2));
    		    	curtime = msg.arg1;
    		    	totaltime = msg.arg2;
    		    	
    		    	//for subtitle tick;
    		    	if (player_status == VideoInfo.PLAYER_RUNNING)
    		    	{
    		    		
    		    		if(subTitleView!=null&&sub_para.filepath!=null)
    		    		subTitleView.tick(curtime/90);
    		    		
    		    	}
    		    	if (totaltime == 0)
						myProgressBar.setProgress(0);
					else {
						if (!progressSliding)
							myProgressBar.setProgress(msg.arg1/90000*100/totaltime);
					}
    				break;
    			case VideoInfo.STATUS_CHANGED_INFO_MSG:
    				player_status = msg.arg1;
    				
    				switch(player_status)
    				{
					case VideoInfo.PLAYER_RUNNING:
						NOT_FIRSTTIME = true;
						try {
							bMediaInfo = m_Amplayer.GetMediaInfo();
						} catch(RemoteException e) {
							e.printStackTrace();
						}
						play.setBackgroundResource(R.drawable.pause_button);
						break;
					case VideoInfo.PLAYER_PAUSE:
						play.setBackgroundResource(R.drawable.play_button);
						break;
					case VideoInfo.PLAYER_EXIT:						
						if (PRE_NEXT_FLAG == 1)
    					{
    						Log.d(TAG,"to play another file!");
							new PlayThread().start();
							if (resumePlay() == 0)
								Amplayer_play();
    						PRE_NEXT_FLAG = 0;
    					}
						break;
					case VideoInfo.PLAYER_STOPED:
						/*new PlayThread().start();
						if (PRE_NEXT_FLAG == 1)
    					{
    						Log.d(TAG,"to play another file!");
    						//Amplayer_play();
    						PRE_NEXT_FLAG = 0;
    					}*/
						break;
					case VideoInfo.PLAYER_PLAYEND:
						try	{
							m_Amplayer.Close();
						} catch(RemoteException e) {
							e.printStackTrace();
						}
						if (m_playmode == REPEATLIST)
							PlayList.getinstance().movenext();
						AudioTrackOperation.AudioStreamFormat.clear();
						AudioTrackOperation.AudioStreamInfo.clear();
						INITOK = false;
						PRE_NEXT_FLAG = 1;
						//Amplayer_play();
						break;
					case VideoInfo.PLAYER_ERROR:
						String InfoStr = null;
						InfoStr = getErrorInfo(msg.arg2);
						Toast.makeText(playermenu.this, "Status Error:"+InfoStr, Toast.LENGTH_LONG)
							.show();
						break;
					case VideoInfo.PLAYER_INITOK:
						INITOK = true;
						if (setCodecMips() == 0)
				        	Log.d(TAG, "setCodecMips Failed");
						break;
					default:
						break;
    				}
    				break;
    			case VideoInfo.AUDIO_CHANGED_INFO_MSG:
    				total_audio_num = msg.arg1;
    				cur_audio_stream = msg.arg2;
    				break;
    			case VideoInfo.HAS_ERROR_MSG:
					String errStr = null;
					errStr = getErrorInfo(msg.arg2);
					Toast.makeText(playermenu.this, errStr, Toast.LENGTH_LONG)
						.show();
    				break;
    			default:
    				super.handleMessage(msg);
    				break;
    		}
    	}
    });
    
    private String getErrorInfo(int errID)
    {
    	String errStr = null;
    	switch (errID)
    	{
			case Errorno.PLAYER_UNSUPPORT:
				errStr = "Unsupport Video and Audio format";
				break;
			case Errorno.PLAYER_UNSUPPORT_VIDEO:
				errStr = "Unsupport Video format";
				break;
			case Errorno.PLAYER_UNSUPPORT_AUDIO:
				errStr = "Unsupport Audio format";
				break;
			case Errorno.FFMPEG_OPEN_FAILED:
				errStr = "Open file ( "+PlayList.getinstance().getcur()+" ) failed";
				break;	
			case  Errorno.FFMPEG_PARSE_FAILED:
				errStr = "Parser file ( "+PlayList.getinstance().getcur()+" ) failed";
				break;
			case  Errorno.DECODER_INIT_FAILED:
				errStr = "Decode Init failed";
				break;
			case  Errorno.PLAYER_NO_VIDEO:
				errStr = "file have no video";
				break;
			case  Errorno.PLAYER_NO_AUDIO:
				errStr = "file have no audio";
				break;
			case  Errorno.PLAYER_SET_NOVIDEO:
				errStr = "set playback without video";
				break;
			case  Errorno.PLAYER_SET_NOAUDIO:
				errStr = "set playback without audio";
				break;
			default:
				errStr = "Unknow Error";
				break;
    	}
    	return errStr;
    }
	
    public Player m_Amplayer = null;
    private void Amplayer_play()
    {
    	try
		{
    		//if otherbar is visable;hide it 
    		if(otherbar!=null&&morbar!=null)
    		{	
    			if(otherbar.getVisibility() == View.VISIBLE)
    				otherbar.setVisibility(View.GONE);
    			if(subbar.getVisibility() == View.VISIBLE)
    				subbar.setVisibility(View.GONE);
    			
	            morbar.setVisibility(View.VISIBLE);
    		}
    	
			m_Amplayer.Open(PlayList.getinstance().getcur(), position);
			//reset sub;
			subTitleView.setText("");
			subinit();
			subTitleView.setTextColor(sub_para.color);
	    	subTitleView.setTextSize(sub_para.font);
	    	openFile(sub_para.filepath);
	    	
	    	
		}
		catch(RemoteException e)
		{
			e.printStackTrace();
		}
		try
		{
			m_Amplayer.Play();
		}
		catch(RemoteException e)
		{
			e.printStackTrace();
		}
    }
    
    private void Amplayer_stop()
    {
    	try {
			m_Amplayer.Stop();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		try {
			m_Amplayer.Close();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		AudioTrackOperation.AudioStreamFormat.clear();
		AudioTrackOperation.AudioStreamInfo.clear();
		INITOK = false;
    }
    
    ServiceConnection m_PlayerConn = new ServiceConnection()
    {
		public void onServiceConnected(ComponentName name, IBinder service) {
			m_Amplayer = Player.Stub.asInterface(service);

			try {
				m_Amplayer.Init();
			} catch (RemoteException e) {
				e.printStackTrace();
				Log.d(TAG,"init fail!");
			}
			try {
				m_Amplayer.RegisterClientMessager(m_PlayerMsg.getBinder());
			} catch (RemoteException e) {
				e.printStackTrace();
				Log.e(TAG, "set client fail!");
			}
			
			//auto play
			Log.d(TAG,"to play files!");
			try
			{
				final short color = ((0x8 >> 3) << 11) 
									| ((0x30 >> 2) << 5) 
									| ((0x8 >> 3) << 0);
				m_Amplayer.SetColorKey(color);
				Log.d(TAG, "set colorkey() color=" + color);
			}
			catch(RemoteException e)
			{
				e.printStackTrace();
			}
			Amplayer_play();
		}

		public void onServiceDisconnected(ComponentName name) {
			try {
				m_Amplayer.Stop();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			try {
				m_Amplayer.Close();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			m_Amplayer = null;
		}
    };
    public void StartPlayerService()
    {
    	Intent intent = new Intent();
    	ComponentName hcomponet = new ComponentName("amlogic.videoplayer","amlogic.playerservice.AmPlayer");
    	intent.setComponent(hcomponet);
    	this.startService(intent);
    	this.bindService(intent, m_PlayerConn, BIND_AUTO_CREATE);
    }
    
    public void StopPlayerService()
    {
    	this.unbindService(m_PlayerConn);
    	Intent intent = new Intent();
    	ComponentName hcomponet = new ComponentName("amlogic.videoplayer","amlogic.playerservice.AmPlayer");
    	intent.setComponent(hcomponet);
    	this.stopService(intent);
    	m_Amplayer = null;
    }
    
	private void openFile(String filepath)  {
		
		
		if(filepath==null)
		{
			Log.d(TAG, "----------------sub filepath is null----------------");
			return;
		}
		
		try {
			
			if(subTitleView.setFile(filepath, "GBK")==Subtitle.SUBTYPE.SUB_INVALID)
			{
			
				return;
			}
		} catch (Exception e) {
			
			Log.d(TAG, "open:errrrrrrrrrrrrrrr");

			e.printStackTrace();
		}
	
	}
	
	private int resumePlay()
	{
		final int pos = ResumePlay.check(PlayList.getinstance().getcur());
		Log.d(TAG, "resumePlay() pos is :"+pos);
		if (pos > 0)
		{
			AlertDialog.Builder resumeBuilder = new AlertDialog.Builder(this);
			resumeBuilder.setTitle("VideoPlayer")  
				.setMessage("Whether to resume this video from last position?") 
				.setPositiveButton("OK",  
					new DialogInterface.OnClickListener() {  
			            public void onClick(DialogInterface dialog, int whichButton) {  
			                position = pos;
			                if (!NOT_FIRSTTIME)
			        			StartPlayerService();
			                else
			                	Amplayer_play();
			            }  
			        })  
			    .setNegativeButton("Cancel",  
				    new DialogInterface.OnClickListener() {  
				        public void onClick(DialogInterface dialog, int whichButton) {  
				        	position = 0;
				        	if (!NOT_FIRSTTIME)
				    			StartPlayerService();
				        	else
				        		Amplayer_play();
				        }  
				    })  
			    .show(); 
			return pos;
		}
		if (!NOT_FIRSTTIME)
			StartPlayerService();
		return pos;
	}
	
	public class PlayThread extends Thread
	{
		public void run()
		{
			super.run();
			try
        	{
        		Thread.sleep(600);
        	}
        	catch (InterruptedException e)
        	{
        		e.printStackTrace();
        	}
		}
	}

	 private final StorageEventListener mListener = new StorageEventListener() {
	        public void onUsbMassStorageConnectionChanged(boolean connected)
	        {
	        	//this is the action when connect to pc
	        	return ;
	        }
	        public void onStorageStateChanged(String path, String oldState, String newState)
	        {
	        	Log.d(TAG, "..............onStorageStateChanged...................."+path+newState);
	        	if (newState == null || path == null) 
	        		return;
	        	
	        	if(newState.compareTo("unmounted") == 0||newState.compareTo("removed") == 0)
	        	{
	        		Log.d(TAG, "...........................unmounted........................."+path);
	        		if(PlayList.getinstance().rootPath.startsWith(path)|PlayList.getinstance().rootPath.equals(path))
	        		{
	        			Intent selectFileIntent = new Intent();
	    				selectFileIntent.setClass(playermenu.this, FileList.class);
	    					
	    				//stop play
	    				if(m_Amplayer != null)
	    					Amplayer_stop();
	    				PlayList.getinstance().rootPath=null;
	    				startActivity(selectFileIntent);
	    				backToFileList = true;
	    				playermenu.this.finish();
	        		}
	        	}
	        }
	        
	    };
	    
	    @Override
	    public void onResume() {
	    	Log.d(TAG, "...........................onResume.........1237................");
	        super.onResume();
	        StorageManager m_storagemgr = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
			m_storagemgr.registerListener(mListener);
	    }
	    
}

class subview_set{
	public int totalnum; 
	public int curid;
	public int color;
	public int font; 
	public String filepath;
}
