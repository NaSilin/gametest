package com.game.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

public class GameRun extends SurfaceView implements SurfaceHolder.Callback
{
	
	public boolean active = false;
	
	private float startx;
	private float starty;
	private float endx;
	private float endy;
	
	 public class SoundManager{
		 private  SoundPool mSoundPool;
		 private  HashMap mSoundPoolMap;
		 private  AudioManager  mAudioManager;
		 private  Context mContext;
		 public boolean loaded = false;
		 
	
		 public void initSounds(Context theContext)
		 {
			 mContext = theContext;
			 mSoundPool = new SoundPool(20,AudioManager.STREAM_MUSIC,0);
			 mSoundPoolMap = new HashMap();
			 mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE);
			 mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener(){
				 @Override
				 public void onLoadComplete(SoundPool soundPool, int sampleld,int status){
					 if(mSoundPoolMap.size() == sampleld)
					 {

						 if(status ==0)loaded = true;
					 }
				 }
				 
			 });
		 }
		 public void addSound(int index, int SoundID)
		 {
		     mSoundPoolMap.put(index, mSoundPool.load(mContext, SoundID, 1));
		 }
		 
		 public void playSound(int index)
		 {
			 float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			 streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		     mSoundPool.play((Integer) mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1f);
		 }
		 
		 public void StopSound(int index)
		 {
			 mSoundPool.stop(index);
		 }
		 public void PauseSound(int index)
		 {
			 mSoundPool.pause(index);
		 }
		  
		 public void playLoopedSound(int index)
		 {
		     float streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		     streamVolume = streamVolume / mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		     mSoundPool.play((Integer) mSoundPoolMap.get(index), streamVolume, streamVolume, 1, -1, 1f);
		    
		 }
		
	 }
		
	class CObject
	{
		public static final int ETC = 0;
		public static final int ARROW = 1;
		public static final int MOB = 2;
		public static final int BOARD = 3;
		
		protected boolean isvis=true;
		protected boolean ishit = false;
		protected boolean isend = false;
		public int whatis=ETC;
		protected float positionX;
		protected float positionY;
		private float velX=0;
		private float velY=0;
		private float acelX=0;
		private float acelY=0;
		public int currentframe=0;
		public ArrayList<Bitmap> m_bit;
		
		public CObject(Bitmap bit,float posx,float posy)
		{
			m_bit = new ArrayList<Bitmap>();
			if(bit != null)m_bit.add(bit);
			positionX = posx;
			positionY = posy;
			
		}
		public CObject(Bitmap bit,float posx,float posy,float velx,float vely)
		{
			m_bit = new ArrayList<Bitmap>();
			if(bit != null)m_bit.add(bit);
			positionX = posx;
			positionY = posy;
			velX = velx;
			velY = vely;
		}
		public CObject(Bitmap bit,float posx, float posy,float velx,float vely,float acx,float acy)
		{
			m_bit = new ArrayList<Bitmap>();
			if(bit != null)m_bit.add(bit);
			positionX = posx;
			positionY = posy;
			velX = velx;
			velY = vely;
			acelX = acx;
			acelY = acy;
		}
		public float GetDistanceSqrt(CObject target)
		{
			return (target.positionX-positionX)*(target.positionX-positionX)+(target.positionY-positionY)*(target.positionY-positionY);
		}

		public void Motion()
		{
			
				velX += acelX;
				velY += acelY;
				positionX += velX;
				positionY += velY;
			
		}
		public void visable(Rect m_boundary)
		{
				if(!m_boundary.contains((int)positionX, (int)positionY))
				{
					isvis = false;
				}
				else isvis = true;
			
		}
		public boolean findend(float screenx,float screeny)
		{
			if(positionX <0 || positionY >screeny)
			{
				isend = true;
				return true;
			}
			return false;
		}
		public void Draw(Canvas can)
		{
			if(isvis)
			{
				int Width = (int) ((int)m_bit.get(currentframe).getWidth()/2);
				int Height = (int) ((int)m_bit.get(currentframe).getHeight()/2);
				can.drawBitmap(m_bit.get(currentframe), positionX-Width,
						positionY-Height,null);
				currentframe++;
				if(m_bit.size() == currentframe)currentframe = 0;
			}
		}
		public void SetPosition(float x,float y)
		{
			positionX = x;
			positionY = y;
		}
		public void SetVelocity(float x,float y)
		{
			velX = x;
			velY = y;
		}
		public void SetAcel(float x,float y)
		{
			acelX = x;
			acelY = y;
		}
	}
	
	class ArrowObject extends CObject
	{
		public ArrowObject(Bitmap bmp,float posx,float posy,float velx,float vely)
		{
			super(bmp,posx, posy,velx,vely,0,(float)0.2);
			whatis = ARROW;
		}
	}

	class MobObject extends CObject
	{
		
		public MobObject(Bitmap bmp,float posx,float posy)
		{
			super(bmp,posx,posy,-2,0,0,0);
			whatis = MOB;
		}
	}
	
	class AnimationObject extends CObject
	{
		public AnimationObject(float posx,float posy,float velx,float vely,float acelx,float acely)
		{
			super(null,posx,posy,velx,vely,acelx,acely);
			whatis = ETC;
		}
		public void addAnimation(Bitmap bit)
		{
			m_bit.add(bit);
		}
	}
	
	class EndAnimationObject extends AnimationObject
	{
		public EndAnimationObject(float posx,float posy)
		{
			super(posx,posy,0,0,0,0);			
		}

		@Override
		public void Draw(Canvas can) {

			if(isvis)
			{
				int Width = (int) ((int)m_bit.get(currentframe).getWidth()/2);
				int Height = (int) ((int)m_bit.get(currentframe).getHeight()/2);
				can.drawBitmap(m_bit.get(currentframe), positionX-Width,
						positionY-Height,null);
				currentframe++;
				if(m_bit.size() <= currentframe)
					{
						currentframe =0;
						isend = true;
					}
			}
		}
	}
	
	class MobAniObject extends AnimationObject
	{
		public int type=0;
		public MobAniObject(float posx,float posy)
		{
			super(posx,posy,-2,0,0,0);
			whatis = MOB;
		}
	}
	
	class BoardObject extends CObject
	{
		public BoardObject(Bitmap bit, float posx, float posy) {
			super(bit, posx, posy);
			whatis = BOARD;
		}
	}
	
	class LifeBoard extends BoardObject
	{
		public int SCX;
		public int SCY;
		public int life;
		
		public LifeBoard(int scx,int scy){
			super(null,0,0);
			SCX = scx;
			SCY = scy;
			whatis = BOARD;
		}
		
		public void PutLife(int mlife)
		{
			life = mlife;
		}
		
		@Override
		public void Draw(Canvas can)
		{
			android.graphics.Paint p = new android.graphics.Paint();
			p.setColor(android.graphics.Color.YELLOW);
			float ratio = (float)life/10;
			Rect liferect = new Rect(50,SCY-20,(int)(50+(ratio*(SCX-150))),SCY-10);
			Rect liferestrect = new Rect((int)(50+(ratio*(SCX-150))),SCY-20,(int)(50+(SCX-150)),SCY-10);
			Rect lifeback = new Rect(50-2,SCY-20-2,50+SCX-150+2,SCY-10+2);
			can.drawRect(lifeback, p);
			p.setColor(android.graphics.Color.RED);
			can.drawRect(liferect, p);
			p.setColor(android.graphics.Color.BLACK);
			can.drawRect(liferestrect, p);
		}
	}
	
	class ArrowBoard extends BoardObject
	{
		public int arrow;
		public int twodigit=0;
		public int onedigit =0;
		
		public ArrowBoard(float posx,float posy)
		{
			super(null,posx,posy);
			whatis = BOARD;
		}
		public void addbit(Bitmap bit)
		{
			m_bit.add(bit);
		}
		public void PutArrow(int ar)
		{
			arrow = ar;
			twodigit = arrow%100;
			onedigit = twodigit%10;
			
			twodigit = twodigit/10;
		}
		@Override
		public void Draw(Canvas can)
		{
			android.graphics.Paint p = new android.graphics.Paint();
			p.setColor(android.graphics.Color.RED);

			can.drawBitmap(m_bit.get(10),positionX,positionY, p);
			can.drawBitmap(m_bit.get(twodigit),positionX+11,positionY, p);
			can.drawBitmap(m_bit.get(onedigit),positionX+22,positionY, p);
		}
	}
	class ScoreBoard extends BoardObject
	{
		public int score;
		public int fivedigit =0;
		public int fourdigit=0;
		public int threedigit=0;
		public int twodigit=0;
		public int onedigit=0;
		public ScoreBoard(float posx,float posy)
		{
			super(null,posx,posy);
			whatis = BOARD;
		}
		public void addbit(Bitmap bit)
		{
			m_bit.add(bit);
		}
		public void PutScore(int sc)
		{
			score = sc;
			fivedigit = score%100000;
			fourdigit = fivedigit%10000;
			threedigit = fourdigit%1000;
			twodigit = threedigit%100;
			onedigit = twodigit%10;
			
			fivedigit = fivedigit/10000;
			fourdigit = fourdigit/1000;
			threedigit = threedigit/100;
			twodigit = twodigit/10;
			
		}
		
		@Override
		public void Draw(Canvas can)
		{

			android.graphics.Paint p = new android.graphics.Paint();
			p.setColor(android.graphics.Color.RED);

			can.drawBitmap(m_bit.get(fivedigit), positionX,positionY, p);
			can.drawBitmap(m_bit.get(fourdigit),positionX+10,positionY, p);
			can.drawBitmap(m_bit.get(threedigit),positionX+20,positionY, p);
			can.drawBitmap(m_bit.get(twodigit),positionX+30,positionY, p);
			can.drawBitmap(m_bit.get(onedigit),positionX+40,positionY, p);
			
			
	
			//can.drawText(String.format("score : %d",score), positionX, positionY, p);
		}
	}
	class LoadingBoard extends BoardObject
	{
		public int count;
		public static final int maxcount=2;
		public LoadingBoard(float posx,float posy)
		{
			super(null,posx,posy);
			
		}
		@Override
		public void Draw(Canvas can)
		{
			if(isvis)
			{
				android.graphics.Paint p = new android.graphics.Paint();
			
				p.setColor(android.graphics.Color.RED);
				can.drawText(String.format("loading..."), positionX,positionY , p);
			}
			if(count > maxcount)
			{
				if(isvis==true)isvis = false;
				else isvis = true;
				count = 0;
			}
			count++;
		}
	}
	class StartObject extends BoardObject
	{
		public int count;
		public static final int maxcount= 40;
		public StartObject(Bitmap bit,float x,float y)
		{
			super(bit,x,y);			
		}
		
		@Override
		public void Draw(Canvas can)
		{
			if(isvis)
			{
				int Width = (int) ((int)m_bit.get(currentframe).getWidth()/2);
				int Height = (int) ((int)m_bit.get(currentframe).getHeight()/2);
				can.drawBitmap(m_bit.get(currentframe), positionX-Width,
						positionY-Height,null);
				currentframe++;
				if(m_bit.size() == currentframe)currentframe = 0;
			}

		}
		@Override
		public void Motion()
		{
			if(count > maxcount)
			{
				if(isvis)
					{
						isvis = false;
						
					}
				if(!isvis)
					{
						isvis = true;
						
					}
				count = 0;
			}
			count++;
		}
	}
	class DragBoard extends BoardObject
	{
		public float basex;
		public float basey;
		public float endx;
		public float endy;
		public DragBoard(float bx,float by)
		{
			super(null,bx,by);
			whatis = BOARD;
			basex = bx;
			basey = by;
			endx = bx;
			endy = by;
		}
		public void PutCorrds(float ex,float ey)
		{
			endx = ex;
			if(ex>basex)endx = basex;
			
			endy = ey;
			if(ey<basey)endy = basey;
		}
		@Override
		public void Draw(Canvas can)
		{
			if(isvis)
			{
				android.graphics.Paint p = new android.graphics.Paint();
				p.setColor(android.graphics.Color.GREEN);
				can.drawLine(basex,basey, endx, endy, p);
			}
		}
	}
	class GameThread extends Thread
	{

		public static final int floading=2;
		public static final int fready = 3;
		public static final int fongame = 0;
		public static final int fscore = 1;
		public static final int frunning = 4;
		public static final int fgo=5;
		public static final int fgo2=6;
		public static final int fwait=7;
		
		private SurfaceHolder m_SurfaceHolder;
		public boolean m_bStop;
		public void SetLoop(boolean _bStop){m_bStop = _bStop;}
		private int ScreenX=533;
		private int ScreenY=300;
		private long prtime;
		private  long timeforframe=30;
		private ArrayList<CObject> obj;
		private Resources res;
		private Random rand;
		private long prmob1=0;
		private long prmob2 =0;
		private long prmob3=0;
		private long prarrow =0;
		private  long mobbyframe1 = 2000;
		private long mobbyframe2 = 5000;
		private long mobbyframe3 = 10000;
		private long arrowbyframe = 1000;
		private long phasetime1=10000;
		private long phasetime2=20000;
		private long phasetime3=30000;
		private long prphasetime1=0;
		private long prphasetime2=0;
		private long prphasetime3=0;
		public int score=0;
		public int life = 10;
		public Bitmap bground;
		public MediaPlayer bgroundmusic;
		//public Bitmap sunny;


		public SoundManager m_soundmanager;
		
		public boolean loaded = false;
		

		
		public Display display;
		public int phase=floading;
		public int arrow = 20;
		public float linex=ScreenX/4;
		public float liney=2*ScreenY/3;
		public float velratio = 5;
		
		public LifeBoard lifeboard;
		public ScoreBoard scoreboard;
		public ArrowBoard arrowboard;
		public DragBoard dragboard;
		
		public void inputline(float _posx,float _posy)
		{
		
			dragboard.PutCorrds(_posx, _posy);
		}

		public void input(float posx,float posy,float endx,float endy)
		{
			switch(phase)
			{
			case fongame:
				if(arrow >0)
				{
					float pos1x = posx;
					if(posx>ScreenX/4)pos1x = ScreenX/4;
					float pos1y = posy;
					if(posy<2*ScreenY/3)pos1y=2*ScreenY/3;
					float pos2x = endx;
					if(endx>ScreenX/4)pos2x=ScreenX/4;
					float pos2y = endy;
					if(endy<2*ScreenY/3)pos2y=2*ScreenY/3;
					obj.add(new ArrowObject(BitmapFactory.decodeResource(res, R.drawable.bubble),ScreenX/4,2*ScreenY/3,Math.abs(pos2x-pos1x)/velratio,-Math.abs(pos2y-pos1y)/velratio));
		
					m_soundmanager.playSound(1);
					arrow --;
					
				}
				
				break;
			case fwait:
				phase= fgo;
				break;
			case fscore:
					obj.clear();
					score =0;
					life = 10;
					arrow = 20;
					phase=floading;
					bgroundmusic.pause();
					break;

			default :
					
			}
			
		}
		public void AddMob(int mtype)
		{
			MobAniObject m_mob = new MobAniObject(ScreenX-1,rand.nextInt((int)2*ScreenY/3));
			//m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.ic_launcher));
			
			//obj.add(m_mob);
			//switch(rand.nextInt(3))
			switch(mtype)
			{
			case 0:
				m_mob.SetVelocity(-2, 0);
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco11));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco11));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco11));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco11));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco12));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco12));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco12));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco12));
				m_mob.type = 0;
				//obj.add(new MobObject(BitmapFactory.decodeResource(res, R.drawable.choco1),ScreenX-1,rand.nextInt((int)2*ScreenY/3)));
				break;
			case 1:
				m_mob.SetVelocity(-4, 0);
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco21));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco21));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco21));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco21));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco22));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco22));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco22));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco22));
				m_mob.type = 1;
				//obj.add(new MobObject(BitmapFactory.decodeResource(res, R.drawable.choco2),ScreenX-1,rand.nextInt((int)2*ScreenY/3)));
				break;
			case 2:
				m_mob.SetVelocity(-6, 0);
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco31));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco31));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco31));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco31));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco32));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco32));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco32));
				m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco32));
				m_mob.type = 2;
				//obj.add(new MobObject(BitmapFactory.decodeResource(res, R.drawable.choco3),ScreenX-1,rand.nextInt((int)2*ScreenY/3)));
				break;
			default :
					break;
			}
			obj.add(m_mob);
			/*
			MobAniObject m_mob = new MobAniObject(ScreenX-1,rand.nextInt((int)2*ScreenY/3));
			m_mob.addAnimation(BitmapFactory.decodeResource(res, R.drawable.ic_launcher));
			
			obj.add(m_mob);
			*/
			
		}
		public void deleteObject()
		{
			for(int i =0 ; i <obj.size();i++)
			{
				if(obj.get(i).isend == true)
				{
					obj.remove(i);
				}
				if(obj.get(i).ishit == true)
				{
					MobAniObject temp = (MobAniObject) obj.get(i);
					EndAnimationObject temp2 = new EndAnimationObject(obj.get(i).positionX,obj.get(i).positionY);
					switch(temp.type)
					{
					//EndAnimationObject temp = new EndAnimationObject(obj.get(i).positionX,obj.get(i).positionY);
	
					case 0:
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco1d1));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco1d1));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco1d2));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco1d2));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco1d3));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco1d3));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco1d4));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco1d4));
						break;
					//temp.addAnimation(BitmapFactory.decodeResource(res, R.drawable.ic_launcher));
					case 1:
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco2d1));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco2d1));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco2d2));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco2d2));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco2d3));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco2d3));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco2d4));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco2d4));
						break;
					case 2:
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco3d1));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco3d1));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco3d2));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco3d2));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco3d3));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco3d3));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco3d4));
						temp2.addAnimation(BitmapFactory.decodeResource(res, R.drawable.choco3d4));
						break;
					//obj.add(temp);
					}
					obj.add(temp2);
					obj.remove(i);

				}
			
			}
		}
		public void update()
		{
			
			long time = System.currentTimeMillis();
			if(time >prarrow+ arrowbyframe)
			{
				if(phase==fongame)arrow++;
				if(arrow > 99)arrow = 99;
				
				prarrow =time;
			}
			if(time >prmob1 + mobbyframe1)
			{
				if(phase==fongame)AddMob(0);
				prmob1 = time;
			}
			if(time>prphasetime1+40000)mobbyframe1 =1000;
			if(time > prphasetime2+phasetime2)
			{
				if(time > prmob2 + mobbyframe2)
				{
					if(phase==fongame)AddMob(1);
					prmob2 = time;
				}
			}
			if(time>prphasetime2+60000)mobbyframe2 = 2000;
			if(time>prphasetime3+phasetime3)
			{
				if(time > prmob3 + mobbyframe3)
				{
					if(phase==fongame)AddMob(2);
					prmob3 = time;
				}
			}
			if(time>prphasetime3+70000)mobbyframe3 = 5000;
			if(time>prtime+timeforframe)
			{
				
				for(int i =0 ; i < obj.size();i++)
				{
					if(obj.get(i).whatis == CObject.ARROW)
					{
						for(int j = 0;j<obj.size();j++)
						{
							if(obj.get(j).whatis == CObject.MOB)
							{
								if(obj.get(i).GetDistanceSqrt(obj.get(j))<1000)
								{
									
									obj.get(j).ishit = true;
									MobAniObject temp = (MobAniObject) obj.get(j);
									switch(temp.type)
									{
									case 0:
										score++;
										break;
									case 1:
										score += 5;
										break;
									case 2:
										score += 10;
										break;
									}
									
									scoreboard.PutScore(score);
								}
							}
						}
					}
					obj.get(i).visable(new Rect(0,0,ScreenX,ScreenY));
					
					if(obj.get(i).findend(ScreenX,ScreenY) == true && obj.get(i).whatis == CObject.MOB)
					{
						life--;
						lifeboard.PutLife(life);
					}
					obj.get(i).Motion();
					
				}
				prtime = time;
				
			}
			deleteObject();
			
			switch(phase)
			{
			case fongame:
				arrowboard.PutArrow(arrow);
				if(life<=0)
				{
					phase =fscore;
					obj.clear();
					scoreboard.PutScore(score);
					scoreboard.SetPosition(273, 164);
					obj.add(new BoardObject(BitmapFactory.decodeResource(res, R.drawable.gameover),125+(404-125)/2,90+(129-90)/2));
					obj.add(scoreboard);
					obj.add(new BoardObject(BitmapFactory.decodeResource(res, R.drawable.score),273-(263-146)/2,164-5));
				}
				break;
			case floading:
				if(m_soundmanager.loaded == true)
				{
				
					bgroundmusic.start();
					obj.clear();
					BoardObject theman = new BoardObject(BitmapFactory.decodeResource(res, R.drawable.chulsu),ScreenX-1,2*ScreenY/3+15);
					theman.SetVelocity(-10, 0);
					obj.add(theman);
					
					phase = frunning;
				}
				break;
			case frunning:
				for(int i =0 ; i < obj.size();i++)
				{
					
					if(obj.get(i).findend(ScreenX,ScreenY) == true)
					{
						obj.clear();
						obj.add(new BoardObject(BitmapFactory.decodeResource(res, R.drawable.sunny),ScreenX/4-30,2*ScreenY/3+15));
						Bitmap tempbitmap2 = BitmapFactory.decodeResource(res, R.drawable.start);
						Bitmap tempbit = BitmapFactory.decodeResource(res, R.drawable.title);
						//obj.add(new BoardObject(tempbit,ScreenX/2, ScreenY/2-tempbit.getHeight()/2));
						obj.add(new BoardObject(tempbit,98+(438-98)/2,40+(178-40)/2));
						//obj.add(new StartObject(tempbitmap2,ScreenX/2, ScreenY/2+tempbitmap2.getHeight()+30));
						obj.add(new StartObject(tempbitmap2,100+(430-100)/2,209+(260-209)/2));
						phase= fwait;
					}
				}
				break;
			case fwait:
				
					//obj.clear();
					
					//phase = fgo;
				
				break;
			case fgo:
				obj.clear();
				EndAnimationObject temp = new EndAnimationObject(ScreenX/2,ScreenY/2);
				temp.addAnimation(BitmapFactory.decodeResource(res, R.drawable.ready));
				temp.addAnimation(BitmapFactory.decodeResource(res, R.drawable.ready));
				temp.addAnimation(BitmapFactory.decodeResource(res, R.drawable.ready));
				temp.addAnimation(BitmapFactory.decodeResource(res, R.drawable.ready));
				temp.addAnimation(BitmapFactory.decodeResource(res, R.drawable.ready));
				temp.addAnimation(BitmapFactory.decodeResource(res, R.drawable.ready));
				temp.addAnimation(BitmapFactory.decodeResource(res, R.drawable.ready));
				temp.addAnimation(BitmapFactory.decodeResource(res, R.drawable.ready));
				temp.addAnimation(BitmapFactory.decodeResource(res, R.drawable.go));
				temp.addAnimation(BitmapFactory.decodeResource(res, R.drawable.go));
				temp.addAnimation(BitmapFactory.decodeResource(res, R.drawable.go));
				temp.addAnimation(BitmapFactory.decodeResource(res, R.drawable.go));
				temp.addAnimation(BitmapFactory.decodeResource(res, R.drawable.go));
				temp.addAnimation(BitmapFactory.decodeResource(res, R.drawable.go));
				temp.addAnimation(BitmapFactory.decodeResource(res, R.drawable.go));
				temp.addAnimation(BitmapFactory.decodeResource(res, R.drawable.go));
				obj.add(temp);
				phase = fgo2;
				break;
			case fgo2:
				if(obj.size() == 0)phase = fready;
				break;
			case fready:
				obj.clear();
				obj.add(new BoardObject(BitmapFactory.decodeResource(res, R.drawable.sunny),ScreenX/4-30,2*ScreenY/3+15));
				lifeboard.PutLife(10);
				obj.add(lifeboard);
				
				scoreboard.PutScore(0);
				scoreboard.SetPosition(438, 266);
				obj.add(scoreboard);
				arrowboard.PutArrow(20);
				obj.add(arrowboard);
				obj.add(dragboard);
				
				prtime = System.currentTimeMillis();
				prphasetime1 = prtime;
				prphasetime2 = prtime;
				prphasetime3 = prtime;
				prmob1 = prtime;
				prmob2 = prtime;
				prmob3 = prtime;
				prarrow = prtime;
				
				phase = fongame;
				break;
				
			}
		}
		
		public GameThread(SurfaceHolder surfaceHolder,Context context)
		{
			m_SurfaceHolder = surfaceHolder;
			res = context.getResources();
			obj = new ArrayList<CObject>();
		
			m_bStop  = false;
			display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
			//ScreenX = display.getWidth();
			//ScreenY = display.getHeight();
		
			lifeboard = new LifeBoard(ScreenX,ScreenY);
			scoreboard = new ScoreBoard(438, 266);
			scoreboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n0));
			scoreboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n1));
			scoreboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n2));
			scoreboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n3));
			scoreboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n4));
			scoreboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n5));
			scoreboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n6));
			scoreboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n7));
			scoreboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n8));
			scoreboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n9));
			
			arrowboard = new ArrowBoard(10, 266);
			arrowboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n0));
			arrowboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n1));
			arrowboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n2));
			arrowboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n3));
			arrowboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n4));
			arrowboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n5));
			arrowboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n6));
			arrowboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n7));
			arrowboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n8));
			arrowboard.addbit(BitmapFactory.decodeResource(res, R.drawable.n9));
			arrowboard.addbit(BitmapFactory.decodeResource(res, R.drawable.nx));
			
			dragboard = new DragBoard(ScreenX/4,2*ScreenY/3);
			rand = new Random();
		
			
			m_soundmanager = new SoundManager();
			m_soundmanager.initSounds(context);
			m_soundmanager.addSound(1, R.raw.arrow);
			//m_soundmanager.addSound(2, R.raw.bg);
			//m_soundmanager.addSound(3, R.raw.bg2);
			
			bgroundmusic = MediaPlayer.create(context, R.raw.bg);
			bgroundmusic.setLooping(true);
			bground = BitmapFactory.decodeResource(res, R.drawable.bg);
			//sunny = BitmapFactory.decodeResource(res, R.drawable.sunny);
			//m_soundmanager.playLoopedSound(2);
			Bitmap tempbitmap = BitmapFactory.decodeResource(res, R.drawable.title);
			
			obj.add(new BoardObject(tempbitmap, 98+(438-98)/2,40+(178-40)/2));
			obj.add(new LoadingBoard(98, 118));
			
			
			
		}
		
		public void run()
		{
			super.run();
			
			while(!m_bStop)
			{
				android.graphics.Canvas c = null;
				
				try
				{
					update();
					c = m_SurfaceHolder.lockCanvas(null);
					synchronized(m_SurfaceHolder)
					{
						android.graphics.Paint p = new android.graphics.Paint();
						p.setColor(android.graphics.Color.BLACK);
						c.drawRect(0,0,display.getWidth(),display.getHeight(), p);
						c.drawBitmap(bground, 0, 0, p);
						
						
							
							for(int i = 0 ; i <obj.size();i++)
							{
								obj.get(i).Draw(c);
							}
							
						
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				finally
				{
					if(c != null)
					{
						m_SurfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}
	}
	
	private GameThread m_gameThread;
	
	public GameRun(Context context,AttributeSet attrs){
		
		super(context,attrs);
		
		SurfaceHolder holder = getHolder();
		holder.addCallback(this);
		m_gameThread = new GameThread(holder,context);
	}
	
	public void surfaceCreated(SurfaceHolder holder)
	{
		m_gameThread.start();
	}
	
	public void surfaceChanged(SurfaceHolder holder,int format, int width,int height)
	{
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		boolean retry = true;
		m_gameThread.SetLoop(true);
		
		while(retry)
		{
			try
			{
				m_gameThread.bgroundmusic.stop();
				m_gameThread.join();
				retry = false;
			}
			catch(InterruptedException e)
			{
				e.getStackTrace();
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		int action = event.getAction();
		if(action == MotionEvent.ACTION_DOWN)
		{
			startx = event.getX();
			starty = event.getY();	
			
		}
		
		if(action == MotionEvent.ACTION_MOVE)
		{
			float x = event.getX();
			float y = event.getY();
			m_gameThread.inputline(x, y);
		}
		
		if(action == MotionEvent.ACTION_UP)
		{
			endx = event.getX();
			endy = event.getY();
			m_gameThread.input(startx, starty, endx,endy);
		}
		return true;
	}
	
	
}
