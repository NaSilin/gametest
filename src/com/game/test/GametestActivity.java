package com.game.test;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

public class GametestActivity extends Activity {
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        setContentView(R.layout.gamerun);
        
        	//setContentView(R.layout.main);
        /*
        	ImageView imageview = (ImageView)findViewById(R.id.imageView1);
        	imageview.setOnClickListener(new View.OnClickListener(){
        		public void onClick(View view){
        			
        			setContentView(R.layout.gamerun);
        		}
        	});
       */
    }

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}
}