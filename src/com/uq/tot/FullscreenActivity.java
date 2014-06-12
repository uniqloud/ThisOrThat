package com.uq.tot;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;

import com.uq.tot.util.SystemUiHider;
import android.app.Activity;
import android.content.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.AutoCompleteTextView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
	
    private static final String TAG ="ThisOrThat: ";
    private static final int TAKE_SNAPSHOT = 1;
    private File picOne;
	private File picTwo;
	private ImageView imgThis;
	private ImageView imgThat;
	
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;

    /**
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

    
    
    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    Button btnTakeSnapshot;
    Button btnSubmitQuestion;
    AutoCompleteTextView txtInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Hide the window title.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.activity_fullscreen);
        
    	addListenerOnButton();
    	

    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
    	Log.d(TAG, "onActivityResult thrown");
        if (resultCode == Activity.RESULT_OK && data != null) {
          picOne = (File) data.getExtras().get("FilePicOne");
          picTwo = (File) data.getExtras().get("FilePicTwo");

          Log.d(TAG, "!!!!Received files!!!!");
          refreshImages();
        }
        else {
        	Log.d(TAG,"?? Looks like data is empty :(");
        }
        
        super.onActivityResult(requestCode, resultCode, data);
        	
    }
    
    private void refreshImages() {
		if (picOne.exists()){
		    Bitmap myBitmap = BitmapFactory.decodeFile(picOne.getAbsolutePath());
		    imgThis.setImageBitmap(myBitmap);
		}
		if (picTwo.exists()){
		    Bitmap myBitmap = BitmapFactory.decodeFile(picTwo.getAbsolutePath());
		    imgThat.setImageBitmap(myBitmap);
		}
		
	}

	public void addListenerOnButton(){
    	btnTakeSnapshot = (Button) findViewById(R.id.btnTakePic);
    	btnSubmitQuestion = (Button) findViewById(R.id.btnSubmit);
    	txtInput = (AutoCompleteTextView) findViewById(R.id.txtQuestion1);
    	imgThis= (ImageView) findViewById(R.id.imageView1);
    	imgThat = (ImageView) findViewById(R.id.imageView2);
    	
    	
    	btnTakeSnapshot.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				takeSnapshot();
				Log.d(TAG, "Returned from Snapshot!");
			}
    		
    		
    	});
    	
    	btnSubmitQuestion.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				
				TotQuestion t = new TotQuestion(picOne, picTwo, txtInput.getText().toString(), "default_user_1");
				Log.d(TAG, "Created TotQuestion!");
				
				SrvConnector s = new SrvConnector();
				s.uploadQuestion(t);
				
				
				
			}
    		
    		
    	});
      	
    			
    }
    
	   public static boolean createDirIfNotExists(String path) {
	        boolean ret = true;

	        File file = new File(Environment.getExternalStorageDirectory(), path);
	        if (!file.exists()) {
	        	Log.d(TAG, "Directory " + path + " doesn't exist");
	        	boolean dirCreated = file.mkdirs();
	            Log.d(TAG, "Has the directory been created?");
	            if (dirCreated){
	            	Log.d(TAG, "yes");
	            } else {
	            	Log.d(TAG, "no");
	            }
	            if (!dirCreated) {
	                Log.e(TAG, "Problem creating folder " + path);
	                ret = false;
	            }

	        }
	        return ret;
	    }

	protected void takeSnapshot() {
		
		Intent intent = new Intent(this, Snapshot2.class);
		startActivityForResult(intent, TAKE_SNAPSHOT);
		
	}



  


}
